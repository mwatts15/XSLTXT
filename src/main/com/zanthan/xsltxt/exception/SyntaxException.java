/**
 * XSLTXT - An alternative syntax for xslt
 * Copyright (C) 2002 Alex Moffat
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.zanthan.xsltxt.exception;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import com.zanthan.xsltxt.Token;

public class SyntaxException
    extends Exception {

    private static final Category logCat =
	Logger.getLogger(SyntaxException.class);

    Exception wrappedException = null;
    Token token = null;
    String containingStatement = null;
    int lineNumber = -1;
    int indent = -1;

    public SyntaxException() {
	if (logCat.isDebugEnabled())
	    logCat.debug("SyntaxException()");
    }

    public SyntaxException(Exception e) {
	this.wrappedException = e;
	if (logCat.isDebugEnabled())
	    logCat.debug("SyntaxException(" +
			 e +
			 ")");
    }

    public SyntaxException(Exception e, Token token) {
	this.wrappedException = e;
	setToken(token);
	if (logCat.isDebugEnabled())
	    logCat.debug("SyntaxException(" +
			 e +
			 ")");
    }

    public void setToken(Token token) {
	if (this.token != null)
	    return;
	this.token = token;
	setLineNumber(token.getLine());
	setIndent(token.getIndent());
    }
    
    public void setContainingStatement(String containingStatement) {
	if (this.containingStatement == null)
	    this.containingStatement = containingStatement;
    }

    public void setLineNumber(int lineNumber) {
	this.lineNumber = lineNumber;
    }

    public void setIndent(int indent) {
	this.indent = indent;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	if ((lineNumber != -1) && (indent != -1)) {
	    sb.append("Location - Line ");
	    sb.append(lineNumber);
	    sb.append(" Indent ");
	    sb.append(indent);
	    sb.append(": ");
	}
	    
	if (wrappedException == null) {
	    sb.append(super.toString());
	} else {
	    sb.append(super.toString());
	    sb.append(" wrapping ");
	    sb.append(wrappedException.toString());
	}
	return sb.toString();
    }
}
