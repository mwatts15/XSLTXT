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
package com.zanthan.xsltxt;

import java.io.IOException;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import com.zanthan.xsltxt.exception.SyntaxException;

public class Message
    extends StatementWithContent 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(Message.class);

    final static String IDENT = MESSAGE;

    final static StatementDescriptor desc = new StatementDescriptor();
    
    static {
	desc.setTXTName(IDENT);
	desc.setXMLName("message");

	desc.addOptionalField("terminate");

	desc.setAllowedChildren(templateChildren);
    }
    
    Message() {
	super(desc);
    }

    void init(Token tok, Lexer lex)
	throws IOException, SyntaxException {

	if (tok.getValue().equals(MESSAGE_FATAL))
	    setOptionalValue(TXT, "terminate", "yes");

	super.init(tok, lex);
    }
    
    String getTXTName()
	throws SyntaxException {
	
	String v = getOptionalValue(TXT, "terminate");
	if ((v != null) &&
	    v.equals("yes"))
	    return MESSAGE_FATAL;
	else
	    return MESSAGE;
    }
}
