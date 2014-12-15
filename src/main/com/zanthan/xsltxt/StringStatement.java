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

import org.jdom.CDATA;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;

import com.zanthan.xsltxt.exception.SyntaxException;
import com.zanthan.xsltxt.exception.TokenNotAllowedException;

public class StringStatement
    extends Statement
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(StringStatement.class);

    static StatementDescriptor desc = new StatementDescriptor();

    static {
	desc.setXMLName("text");
    }
    
    private boolean isCDATA = false;
    private String s = null;
    
    StringStatement() {
	super(desc);
    }

    StringStatement(String s) {

	super(desc);

	this.s = doubleSlash(s);
	isCDATA = false;
    }

    StringStatement(CDATA c) {

	super(desc);

	s = doubleSlash(c.getText());
	isCDATA = true;
    }

    void init(Token tok, Lexer lex)
	throws IOException, SyntaxException {

	if (tok.getValue().equals(CDATA)) {
	    isCDATA = true;
	    int startingIndent = tok.getIndent();
	    tok = lex.next(startingIndent);
	}
	if (!(tok instanceof QuotedToken)) {
	    logCat.warn("TokenNotAllowedException");
	    throw new TokenNotAllowedException(lex.getSourceIdent(),
					       tok,
					       "Quoted String");
	}
	s = tok.getValue();
    }
    
    public void outputTXT(Outputter out) {
	if (isCDATA) {
	    out.output(CDATA);
	    out.output(" ");
	}
	out.output(fqs(s));
	out.newline();
    }

    public void outputXML(Outputter out) {
	if (logCat.isDebugEnabled())
	    logCat.debug("outputXML(...) s is "  +
			 s);
	out.output("<");
	out.output(getXMLQualifiedName());
	out.output(">");
	if (isCDATA) {
	    out.output("<![CDATA[");
	    out.output(s);
	    out.output("]]>");
	} else {
	    out.output(es(s));
	}
	out.output("</");
	out.output(getXMLQualifiedName());
	out.output(">");
	out.newline();
    }

    void outputXMLChildren(ContentHandler handler,
			   NamespaceTracker tracker)
	throws SyntaxException {
	
	char[] cs = s.toCharArray();
	try {
	    handler.characters(cs, 0, cs.length);
	} catch (SAXException se) {
	    logCat.warn("SAXException se");
	    throw new SyntaxException(se);
	}
    }
    
    boolean hasChildren() {
	return (s != null);
    }
    
    String getString() {
	return s;
    }

    public Object clone()
	throws CloneNotSupportedException {
	
	StringStatement ss = (StringStatement)super.clone();
	ss.isCDATA = false;
	ss.s = null;
	return ss;
    }

    void startStatement(Token tok,
			Lexer lex,
			ContentHandler handler,
			NamespaceTracker tracker)
	throws SyntaxException, IOException {

	int startingIndent = tok.getIndent();
	AttributesImpl attributes = new AttributesImpl();

	init(tok, lex);

	// Output start element
	String localName = getXMLLocalName();
	String qName = getXMLQualifiedName();
	String namespace = getNamespace(tracker);
	
	if (logCat.isDebugEnabled())
	    logCat.debug("handler.startElement( " +
			 namespace + ", " +
			 localName + ", " +
			 qName + ", " +
			 attributes + ")");
	try {
	
	    handler.startElement(namespace,
				 localName,
				 qName,
				 attributes);
	    
	    outputXMLChildren(handler, tracker);
	    
	    if (logCat.isDebugEnabled())
		logCat.debug("handler.endElement( " +
			     namespace + ", " +
			     localName + ", " +
			     qName + ")");
	    
	    handler.endElement(namespace,
			       localName,
			       qName);
	} catch  (SAXException se) {
	    logCat.warn("SAXException se");
	    throw new SyntaxException(se, tok);
	}
    }
}
