/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.relational.commands.vdb;

import static org.komodo.relational.commands.vdb.VdbCommandMessages.UploadModelCommand.MISSING_INPUT_MODEL_FILE_PATH;
import static org.komodo.relational.commands.vdb.VdbCommandMessages.UploadModelCommand.MISSING_MODEL_NAME;
import static org.komodo.relational.commands.vdb.VdbCommandMessages.UploadModelCommand.MISSING_MODEL_TYPE;
import static org.komodo.relational.commands.vdb.VdbCommandMessages.UploadModelCommand.MODEL_TYPE_ERROR;
import static org.komodo.relational.commands.vdb.VdbCommandMessages.UploadModelCommand.MODEL_INPUT_FILE_IS_EMPTY;
import static org.komodo.relational.commands.vdb.VdbCommandMessages.UploadModelCommand.MODEL_OVERWRITE_DISABLED;
import static org.komodo.relational.commands.vdb.VdbCommandMessages.UploadModelCommand.MODEL_UPLOADED;
import static org.komodo.relational.commands.workspace.WorkspaceCommandMessages.General.INPUT_FILE_ERROR;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.komodo.relational.model.Model;
import org.komodo.relational.vdb.Vdb;
import org.komodo.shell.CommandResultImpl;
import org.komodo.shell.CompletionConstants;
import org.komodo.shell.api.Arguments;
import org.komodo.shell.api.CommandResult;
import org.komodo.shell.api.WorkspaceStatus;
import org.komodo.spi.repository.Repository;
import org.komodo.utils.StringUtils;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlParser;

/**
 * Loads a {@link Model MODEL} from a local file.
 */
public final class UploadModelCommand extends VdbShellCommand {

    static final String NAME = "upload-model"; //$NON-NLS-1$

    private static final List< String > VALID_ARGS = Arrays.asList( new String[] { "-o", "--overwrite" } ); //$NON-NLS-1$ //$NON-NLS-2$;

    /**
     * @param status
     *        the shell's workspace status (cannot be <code>null</code>)
     */
    public UploadModelCommand( final WorkspaceStatus status ) {
        super( NAME, status );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#doExecute()
     */
    @Override
    protected CommandResult doExecute() {
        try {
            final String modelName = requiredArgument( 0, getMessage( MISSING_MODEL_NAME ) );
            final String modelType = requiredArgument( 1, getMessage( MISSING_MODEL_TYPE ) );
            final String fileName = requiredArgument( 2, getMessage( MISSING_INPUT_MODEL_FILE_PATH ) );
            final String overwriteArg = optionalArgument( 3, null );
            final boolean overwrite = !StringUtils.isBlank( overwriteArg );

            // make sure ModelType arg is valid
            if(!modelType.equals(Model.Type.PHYSICAL.name()) && !modelType.equals(Model.Type.VIRTUAL.name())) {
                return new CommandResultImpl( false, getMessage( MODEL_TYPE_ERROR, modelType ), null );
            }
            
            // make sure overwrite arg is valid
            if ( overwrite && !VALID_ARGS.contains( overwriteArg ) ) {
                return new CommandResultImpl( false, getMessage( INPUT_FILE_ERROR, overwriteArg ), null );
            }

            { // Validates the supplied fileNameArg is a valid, readable file, and has property extension
                final String validationResult = validateReadableFileArg( fileName );

                if ( !CompletionConstants.OK.equals( validationResult ) ) {
                    return new CommandResultImpl( false, getMessage( INPUT_FILE_ERROR, fileName, validationResult ), null );
                }
            }

            // read file
            final String content = new String( Files.readAllBytes( Paths.get( fileName ) ) );

            if ( StringUtils.isEmpty( content ) ) {
                return new CommandResultImpl( false, getMessage( MODEL_INPUT_FILE_IS_EMPTY, fileName ), null );
            }

            final Repository.UnitOfWork uow = getTransaction();

            // make sure we can overwrite
            Vdb vdbContext = (Vdb)getContext();
            Model[] allModels = vdbContext.getModels(uow);
            boolean hasModel = false;
            for(Model theModel : allModels) {
                if(modelName.equals(theModel.getName(uow))) {
                    hasModel = true;
                    break;
                }
            }
            if ( hasModel && !overwrite ) {
                return new CommandResultImpl( false, getMessage( MODEL_OVERWRITE_DISABLED, fileName ), null );
            }

            // create Model
            final Model model = vdbContext.addModel( uow, modelName );
            
            model.setModelType(uow, Model.Type.valueOf(modelType));
            model.setModelDefinition(uow, content);
            model.setProperty(uow, StandardDdlLexicon.PARSER_ID, TeiidDdlParser.ID);

            return new CommandResultImpl( getMessage( MODEL_UPLOADED, modelName ) );
        } catch ( final Exception e ) {
            return new CommandResultImpl( e );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#getMaxArgCount()
     */
    @Override
    protected int getMaxArgCount() {
        return 4;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#tabCompletion(java.lang.String, java.util.List)
     */
    @Override
    public int tabCompletion( final String lastArgument,
                              final List< CharSequence > candidates ) throws Exception {
        final Arguments args = getArguments();

        if ( ( args.size() == 1 ) ) {
            if(lastArgument ==null) {
                candidates.add( Model.Type.PHYSICAL.name() );
                candidates.add( Model.Type.VIRTUAL.name() );
            } else {
                if( Model.Type.PHYSICAL.name().toUpperCase().startsWith(lastArgument.toUpperCase()) ) {
                    candidates.add(Model.Type.PHYSICAL.name());
                } else if ( Model.Type.VIRTUAL.name().toUpperCase().startsWith(lastArgument.toUpperCase()) ) {
                    candidates.add(Model.Type.VIRTUAL.name());
                }
            }

            return ( candidates.isEmpty() ? -1 : ( StringUtils.isBlank( lastArgument ) ? 0 : ( toString().length() + 1 ) ) );
        }

        // no tab completion
        return -1;
    }

}