/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v85;

import org.komodo.spi.runtime.version.ITeiidVersion;
import org.komodo.spi.runtime.version.TeiidVersion.Version;
import org.teiid.query.validator.v8.Test8FunctionMetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test85FunctionMetadataValidator extends Test8FunctionMetadataValidator {

    protected Test85FunctionMetadataValidator(ITeiidVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test85FunctionMetadataValidator() {
        this(Version.TEIID_8_5.get());
    }

}
