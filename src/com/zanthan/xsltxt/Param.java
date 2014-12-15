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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;

import com.zanthan.xsltxt.exception.SyntaxException;
import com.zanthan.xsltxt.exception.TokenNotAllowedException;

public class Param
    extends StatementWithContent 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(Param.class);

    final static String IDENT = PARAM;
    
    static StatementDescriptor desc = new StatementDescriptor();

    static {
	desc.setTXTName(IDENT);
	desc.setXMLName("with-param");
	
	desc.addRequiredField("name");
	
	desc.addOptionalField("select", SELECT);

	desc.setAllowedChildren(templateChildren);

	desc.setExample("call \"columnTemplate\" (\"title\"%\"One\", \"value\":\"@cost\")\n\n" +
			"call \"columnTemplate\"\n" +
			"  param \"title\"\n" +
			"    \"One\"\n" +
			"  param \"value\" .select \"@cost\"");
    }

    boolean useWith = false;
    
    Param() {
	super(desc);
    }

    void setUseWith(boolean useWith) {
	if (logCat.isDebugEnabled())
	    logCat.debug("setUseWith(" +
			 useWith +
			 ")");
	this.useWith = useWith;
    }
    
    void init(Token tok, Lexer lex)
	throws IOException, SyntaxException {
	
	try {
	    if (tok.getValue().equals(IDENT))
		super.init(tok, lex);
	    else
		initSmall(tok, lex);
	} catch (SyntaxException e) {
	    e.setToken(tok);
	    e.setContainingStatement(IDENT);
	    throw e;
	}
    }

    private void initSmall(Token tok, Lexer lex)
	throws IOException, SyntaxException {

	if (logCat.isDebugEnabled())
	    logCat.debug("initSmall(" + tok + ", " + lex + ")");

	if (!(tok instanceof QuotedToken)) {
	    logCat.warn("TokenNotAllowedException");
	    throw new TokenNotAllowedException(lex.getSourceIdent(),
					       tok,
					       "Quoted String");
	}

	int startingIndent = tok.getIndent();

	String name = tok.getValue();
	setRequiredValue(TXT, "name", name);

	tok = lex.next(startingIndent);
	
	if (logCat.isDebugEnabled())
	    logCat.debug("initSmall(..) name " + name +
			 " tok " + tok);

	String separator = tok.getValue();
	if (!separator.equals(":") && !separator.equals("%")) {
	    lex.push(tok);
	    return;
	}

	tok = lex.next(startingIndent);
	if (!(tok instanceof QuotedToken)) {
	    logCat.warn("TokenNotAllowedException");
	    throw new TokenNotAllowedException(lex.getSourceIdent(),
					       tok,
					       "Quoted String");
	}

	if (separator.equals(":"))
	    setOptionalValue(TXT, "select", tok.getValue());
	else
	    addChild(factory.getStatement(tok, lex));
    }
    
    public void outputTXT(Outputter out) 
	throws SyntaxException {
	outputTXT(out, true);
    }

    void outputTXT(Outputter out, boolean large) 
	throws SyntaxException {

	if (logCat.isDebugEnabled())
	    logCat.debug("outputTXT(out, " + large);
	
	if (large)
	    super.outputTXT(out);
	else
	    outputTXTSmall(out);
    }

    void outputTXTSmall(Outputter out) 
	throws SyntaxException {

	if (logCat.isDebugEnabled())
	    logCat.debug("outputTXTSmall(out)");
	
	out.output(fqs(getRequiredValue(TXT, "name")));
	String v = getOptionalValue(TXT, "select");
	if (v != null) {
	    out.output(":");
	    out.output(fqs(v));
	}
    }

    String getXMLLocalName() {
	if (logCat.isDebugEnabled())
	    logCat.debug("getXMLLocalName() useWith is " +
			 useWith);
	if (useWith)
	    return "with-param";
	else
	    return "param";
    }
    
    public Object clone()
	throws CloneNotSupportedException {
	
	Param p = (Param)super.clone();
	p.useWith = false;
	return p;
    }

    void startStatement(Token tok,
			Lexer lex,
			ContentHandler handler,
			NamespaceTracker tracker)
	throws SyntaxException, IOException {

	if (logCat.isDebugEnabled())
	    logCat.debug("startStatement(" +
			 tok +
			 ", " +
			 lex +
			 ", " +
			 handler +
			 ", " +
			 tracker +
			 ")");

	if (tok.getValue().equals(IDENT)) {
	    super.startStatement(tok, lex, handler, tracker);
	    return;
	}
	
	if (!(tok instanceof QuotedToken)) {
	    logCat.warn("TokenNotAllowedException");
	    throw new TokenNotAllowedException(lex.getSourceIdent(),
					       tok,
					       "Quoted String");
	}

	int startingIndent = tok.getIndent();
	AttributesImpl attributes = new AttributesImpl();

	String name = tok.getValue();
	setRequiredValue(TXT, "name", name);

	tok = lex.next(startingIndent);
	
	if (logCat.isDebugEnabled())
	    logCat.debug("startStatement(..) name " + name +
			 " tok " + tok);

	// : means the following value is a select, % means the
	// following value is a text element.
	String separator = tok.getValue();
	if (!separator.equals(":") && !separator.equals("%")) {
	    lex.push(tok);
	} else {
	    tok = lex.next(startingIndent);
	    if (!(tok instanceof QuotedToken)) {
		logCat.warn("TokenNotAllowedException");
		throw new TokenNotAllowedException(lex.getSourceIdent(),
						   tok,
						   "Quoted String");
	    }
	    // If the 
	    if (separator.equals(":"))
		setOptionalValue(TXT, "select", tok.getValue());
	}

	addXMLRequiredFields(attributes, tracker);
	addXMLOptionalFields(attributes, tracker);
	
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

	    if (separator.equals("%")) {
		Statement stat = modifyChild(factory.getStatementNoInit(tok, lex));
		stat.startStatement(tok, lex, handler, tracker);
	    }
		
	    // Output end element
	    handler.endElement(namespace,
			       localName,
			       qName);
	} catch (SAXException se) {
	    logCat.warn("SAXException se");
	    throw new SyntaxException(se, tok);
	}
    }
}
