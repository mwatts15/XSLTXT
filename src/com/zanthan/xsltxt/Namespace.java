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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.Element;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.zanthan.xsltxt.exception.SyntaxException;

public class Namespace
    extends Statement 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(Namespace.class);


    final static String IDENT = NAMESPACE;
    
    static StatementDescriptor desc = new StatementDescriptor();

    static {
	desc.setTXTName(IDENT);
    }
    
    private String prefix = null;
    private String uri = null;
    
    Namespace() {
	super(desc);
    }

    Namespace(String prefix, String uri) {
	super(desc);
	this.prefix = prefix;
	this.uri = uri;
    }
    
    Namespace(org.jdom.Namespace n) 
	throws SyntaxException {

	super(desc);
	
	prefix = n.getPrefix();
	uri = n.getURI();
    }

    void init(Token tok, Lexer lex)
	throws IOException, SyntaxException {

	int startingIndent = tok.getIndent();

	try {
	    prefix = lex.next(startingIndent).getValue();
	    if (lex.hasNext(startingIndent)) {
		uri = lex.next(startingIndent).getValue();
	    } else {
		uri = prefix;
		prefix = null;
	    }
	}  catch (SyntaxException e) {
	    e.setToken(tok);
	    e.setContainingStatement(IDENT);
	    throw e;
	}
    }
    
    public void outputTXT(Outputter out) {
	out.output(IDENT);
	out.indent();
	out.maybeBreak();

	out.outputPaddedWithBreak(fqs(prefix));

	out.outputPaddedWithBreak(fqs(uri));

	out.dedent();
	out.newline();
    }

    public void outputXML(Outputter out) {
	out.pushPrefixMapping(prefix, uri);
// 	if (prefix.equals(""))
// 	    out.outputAttribute("xmlns",
// 				uri);
// 	else
// 	    out.outputAttribute("xmlns:" + prefix,
// 				uri);
    }

    void startPrefixMapping(ContentHandler handler,
			    NamespaceTracker tracker)
	throws SyntaxException {

	tracker.pushPrefixMapping(prefix, uri);
	try {
	    handler.startPrefixMapping(prefix, uri);
	} catch (SAXException se) {
	    logCat.warn("SAXException se");
	    throw new SyntaxException(se);
	}
    }

    void endPrefixMapping(ContentHandler handler,
			  NamespaceTracker tracker) 
	throws SyntaxException {

	tracker.popPrefixMapping(prefix);
	try {
	    handler.endPrefixMapping(prefix);
	} catch (SAXException se) {
	    logCat.warn("SAXException se");
	    throw new SyntaxException(se);
	}
    }
    
    String getPrefix() {
	return prefix;
    }

    String getURI() {
	return uri;
    }

    public Object clone() 
	throws CloneNotSupportedException {
	
	Namespace n = (Namespace)super.clone();
	n.prefix = null;
	n.uri = null;
	return n;
    }

    public String toString() {
	return "Namespace " + hashCode() + " prefix " + prefix + " uri " + uri;
    }
}
