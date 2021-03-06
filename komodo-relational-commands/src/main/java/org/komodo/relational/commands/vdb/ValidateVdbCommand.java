/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.komodo.relational.commands.vdb;

import static org.komodo.shell.CompletionConstants.MESSAGE_INDENT;
import org.komodo.relational.vdb.Vdb;
import org.komodo.shell.CommandResultImpl;
import org.komodo.shell.api.CommandResult;
import org.komodo.shell.api.WorkspaceStatus;
import org.komodo.spi.outcome.Outcome;
import org.komodo.spi.repository.validation.Result;
import org.komodo.utils.i18n.I18n;

/**
 * A shell command to validate the VDB.
 */
public final class ValidateVdbCommand extends VdbShellCommand {

    static final String NAME = "validate-vdb"; //$NON-NLS-1$

    /**
     * @param status
     *        the shell's workspace status (cannot be <code>null</code>)
     */
    public ValidateVdbCommand( final WorkspaceStatus status ) {
        super( NAME, status );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#doExecute()
     */
    @Override
    protected CommandResult doExecute() {
        
        // Default is to do a 'full' validation (validate the node and all its ancestors).  Can specify to validate this node only.
        final boolean fullValidation = Boolean.getBoolean( optionalArgument( 0, "true" ) ); //$NON-NLS-1$
        
        try {
            Vdb vdb = getVdb();
            String vdbName = vdb.getName(getTransaction());
            final Result[] results = getRepository().getValidationManager().evaluate(getTransaction(), vdb, fullValidation);

            // Determine if any Errors or Warnings
            boolean hasErrors = hasLevel(results,Outcome.Level.ERROR);
            boolean hasWarnings = hasLevel(results,Outcome.Level.WARNING);
            
            // If none, print success message
            if( !hasErrors && !hasWarnings ) {
                print( MESSAGE_INDENT, I18n.bind( VdbCommandsI18n.vdbValidationSuccessMsg, vdbName ) );
                return CommandResult.SUCCESS;
            }
            
            // Print validation errors
            if( hasErrors ) {
                print( MESSAGE_INDENT, I18n.bind( VdbCommandsI18n.vdbValidationErrorsHeader, vdbName ) );
                for(Result result : results) {
                    if( result.getLevel() == Outcome.Level.ERROR ) {
                        print( MESSAGE_INDENT, result.getMessage() );
                    }
                }
            }

            // Print validation warnings
            if( hasWarnings ) {
                print( MESSAGE_INDENT, I18n.bind( VdbCommandsI18n.vdbValidationWarningsHeader, vdbName ) );
                for(Result result : results) {
                    if( result.getLevel() == Outcome.Level.ERROR ) {
                        print( MESSAGE_INDENT, result.getMessage() );
                    }
                }
            }
            
            return CommandResult.SUCCESS;
        } catch ( final Exception e ) {
            return new CommandResultImpl( e );
        }
    }

    private boolean hasLevel( final Result[] results, Outcome.Level outcome ) {
        for( Result result : results ) {
            if(result.getLevel() == outcome) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#getMaxArgCount()
     */
    @Override
    protected int getMaxArgCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#printHelpDescription(int)
     */
    @Override
    protected void printHelpDescription( final int indent ) {
        print( indent, I18n.bind( VdbCommandsI18n.validateVdbHelp, getName() ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#printHelpExamples(int)
     */
    @Override
    protected void printHelpExamples( final int indent ) {
        print( indent, I18n.bind( VdbCommandsI18n.validateVdbExamples ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#printHelpUsage(int)
     */
    @Override
    protected void printHelpUsage( final int indent ) {
        print( indent, I18n.bind( VdbCommandsI18n.validateVdbUsage ) );
    }

}
