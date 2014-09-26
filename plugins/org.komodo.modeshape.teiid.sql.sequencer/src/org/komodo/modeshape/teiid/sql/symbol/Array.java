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

package org.komodo.modeshape.teiid.sql.symbol;

import java.util.List;
import org.komodo.modeshape.teiid.cnd.TeiidSqlLexicon;
import org.komodo.modeshape.teiid.parser.LanguageVisitor;
import org.komodo.modeshape.teiid.parser.TeiidParser;
import org.komodo.modeshape.teiid.sql.lang.ASTNode;
import org.komodo.spi.type.IDataTypeManagerService.DataTypeName;
import org.komodo.utils.ArgCheck;

public class Array extends ASTNode implements Expression {

    public Array(TeiidParser p, int id) {
        super(p, id);
    }

    @Override
    public Class<?> getType() {
        return convertTypeClassPropertyToClass(TeiidSqlLexicon.Expression.TYPE_CLASS_PROP_NAME);
    }

    public void setType(Class<?> type) {
        if (type != null) {
            ArgCheck.isTrue(type.isArray(), "type not array"); //$NON-NLS-1$
        }
        DataTypeName dataTypeName = getDataTypeService().retrieveDataTypeName(type);
        setProperty(TeiidSqlLexicon.Expression.TYPE_CLASS_PROP_NAME, dataTypeName.name());
    }

    private List<Expression> getExpressions() {
        return getChildrenforIdentifierAndRefType(
                                                  TeiidSqlLexicon.Array.EXPRESSIONS_REF_NAME, Expression.class);
    }

    public void setExpressions(List<Expression> arrayExpressions) {
        setChildren(TeiidSqlLexicon.Array.EXPRESSIONS_REF_NAME, arrayExpressions);
    }

    public boolean isImplicit() {
        Object property = getProperty(TeiidSqlLexicon.Array.IMPLICIT_PROP_NAME);
        return property == null ? false : Boolean.parseBoolean(property.toString());
    }

    public void setImplicit(boolean implicit) {
        setProperty(TeiidSqlLexicon.Array.IMPLICIT_PROP_NAME, implicit);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.getExpressions() == null) ? 0 : this.getExpressions().hashCode());
        result = prime * result + (this.isImplicit() ? 1231 : 1237);
        result = prime * result + ((this.getType() == null) ? 0 : this.getType().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Array other = (Array)obj;
        if (this.getExpressions() == null) {
            if (other.getExpressions() != null)
                return false;
        } else if (!this.getExpressions().equals(other.getExpressions()))
            return false;
        if (this.isImplicit() != other.isImplicit())
            return false;
        if (this.getType() == null) {
            if (other.getType() != null)
                return false;
        } else if (!this.getType().equals(other.getType()))
            return false;
        return true;
    }

    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Array clone() {
        Array clone = new Array(this.getTeiidParser(), this.getId());

        if(getExpressions() != null)
            clone.setExpressions(cloneList(getExpressions()));

        clone.setImplicit(isImplicit());

        return clone;
    }	

}