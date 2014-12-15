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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.zanthan.xsltxt.exception.SyntaxException;

public class Stylesheet
    extends StatementWithContent 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(Stylesheet.class);

    final static String IDENT = STYLESHEET;
    
    static StatementDescriptor desc = new StatementDescriptor();

    static {
	desc.setTXTName(IDENT);
	desc.setXMLName("stylesheet");

	desc.addRequiredField("version");
	
	desc.addOptionalField("id", ID);
	desc.addOptionalField("extension-element-prefixes", EXTENSION_PREFIXES);
	desc.addOptionalField("exclude-result-prefixes", EXCLUDE_PREFIXES);
    }
    
    private List imports = new ArrayList();
    
    Stylesheet() {
	super(desc);
    }

    void setupValueArrays() {
	super.setupValueArrays();
	imports = new ArrayList();
    }
    
    void processTOKContent(int startingIndent, Lexer lex)
	throws IOException, SyntaxException {

	// Use -1 as startingIndent as whole of file is considered to
	// be part of the stylesheet
	super.processTOKContent(-1, lex);
    }
    
    void addChild(Statement stat) 
	throws SyntaxException {
	
	if (stat == null)
	    return;
	if (topLevelChildren.contains(stat.getClass())) {
	    if (stat instanceof ImportStatement) 
		imports.add(stat);
	    else
		children.add(stat);
	} else {
	    super.addChild(stat);
	}
    }

    /**
     * Dedent before the namespaces so that we
     * don't have to indent the whole of the file.
     */
    void outputTXTNamespaces(Outputter out) {
	out.dedent();
	super.outputTXTNamespaces(out);
    }

    void outputTXTChildren(Outputter out)  
	throws SyntaxException {
	
	outputTXTChildren(imports, out);

	super.outputTXTChildren(out);
    }

    void outputXMLChildren(Outputter out) 
	throws SyntaxException {
	
	outputXMLChildren(imports, out);

	super.outputXMLChildren(out);
    }

    void outputXMLNamespaces(Outputter out) {
	boolean found = false;
	if (!namespaces.isEmpty()) {
	    for (Iterator it = namespaces.iterator(); it.hasNext() && !found;) {
		Namespace n = (Namespace)it.next();
		if (n.getURI().equals(xslNamespaceURI))
		    found = true;
	    }
	} 
	if (!found)
	    out.outputAttribute("xmlns:xsl",
				xslNamespaceURI);
	super.outputXMLNamespaces(out);
    }

    void outputXMLChildren(ContentHandler handler,
			   NamespaceTracker tracker) 
	throws SyntaxException {
	
	outputXMLChildren(imports, handler, tracker);

	super.outputXMLChildren(handler, tracker);
    }

    void startPrefixMapping(ContentHandler handler,
			    NamespaceTracker tracker) 
	throws SyntaxException {

	try {
	    handler.startPrefixMapping("xsl", xslNamespaceURI);
	} catch (SAXException se) {
	    logCat.warn("SAXException se");
	    throw new SyntaxException(se);
	}
	tracker.pushPrefixMapping("xsl", xslNamespaceURI);
	super.startPrefixMapping(handler, tracker);
    }
    
    void endPrefixMapping(ContentHandler handler,
			  NamespaceTracker tracker) 
	throws SyntaxException {
	
	super.endPrefixMapping(handler, tracker);
	tracker.popPrefixMapping("xsl");
	try {
	    handler.endPrefixMapping("xsl");
	} catch (SAXException se) {
	    logCat.warn("SAXException se");
	    throw new SyntaxException(se);
	}
    }
    
    boolean hasChildren() {
	return !imports.isEmpty() || super.hasChildren();
    }

    void startStatementChildren(int startingIndent,
				Lexer lex,
				ContentHandler handler,
				NamespaceTracker tracker)
	throws SyntaxException, IOException {

	super.startStatementChildren(-1, lex, handler, tracker);
    }

    //List startStatementNamespaces(int startingIndent, List namespaces, Lexer lex)
    void startStatementNamespaces(int startingIndent, Lexer lex) 	
	throws SyntaxException, IOException {

	super.startStatementNamespaces(-1, lex);

	if (logCat.isDebugEnabled())
	    logCat.debug("startStatementNamespaces(...) returning ");
	
	//return l;
	return;
    }

}
