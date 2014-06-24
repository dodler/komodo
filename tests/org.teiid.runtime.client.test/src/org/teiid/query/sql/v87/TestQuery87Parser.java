/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v87;

import org.junit.Test;
import org.komodo.spi.runtime.version.ITeiidVersion;
import org.komodo.spi.runtime.version.TeiidVersion.Version;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.v85.TestQuery85Parser;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class TestQuery87Parser extends TestQuery85Parser {

    protected TestQuery87Parser(ITeiidVersion teiidVersion) {
        super(teiidVersion);
    }

    public TestQuery87Parser() {
        this(Version.TEIID_8_7.get());
    }

    @Override
    @Test
    public void testStoredQuery2SanityCheck() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setProcedureName("proc1");
        SPParameter parameter = getFactory().newSPParameter(1, getFactory().newConstant("param1"));
        storedQuery.setParameter(parameter);
        From from = getFactory().newFrom();
        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("x", storedQuery);
        from.addClause(sfc);

        helpTest("exec proc1('param1')", "EXEC proc1('param1')", storedQuery);
    }
}
