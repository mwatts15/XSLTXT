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

public class EndOfBlockException
    extends SyntaxException {

    private static final Category logCat =
	Logger.getLogger(EndOfBlockException.class);

    private String sourceIdent = null;
    private Token tok = null;
    private int indent = 0;
    
    public EndOfBlockException(String sourceIdent,
			       Token tok,
			       int indent) {
	this.sourceIdent = sourceIdent;
	this.tok = tok;
	this.indent = indent;
	if (logCat.isDebugEnabled())
	    logCat.debug("EndOfBlockException(" +
			 sourceIdent +
			 ", " +
			 tok +
			 ", " +
			 indent +
			 ")");
    }

    public String getMessage() {
	return "in " + sourceIdent +
	    " found " + tok.toString() +
	    " indent should be >= " + indent;
    }
    
    public String toString() {
	return "EndOfBlockException " +
	    getMessage();
    }

}
