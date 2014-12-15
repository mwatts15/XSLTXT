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
import java.util.Map;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.Attribute;
import org.jdom.Element;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;

import com.zanthan.xsltxt.exception.SyntaxException;

public class XMLStatement
    extends Statement 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(XMLStatement.class);

    static StatementDescriptor desc = new StatementDescriptor();

    String name = null;
    String prefix = null;
    String namespace = null;
    List attributes = new ArrayList();
    
    boolean namespaceChanged = false;
	
    XMLStatement() {
	super(desc);
    }

    void setupValueArrays() {
	super.setupValueArrays();
	attributes = new ArrayList();
    }
    
    void init(Element e)
	throws SyntaxException {
	
	if (logCat.isDebugEnabled())
	    logCat.debug("XMLStatement(" + e + ")");

	setupValueArrays();
	
	initNamespaces(e);
	
	name = e.getName();
	prefix = e.getNamespacePrefix().intern();
	namespace = e.getNamespaceURI().intern();

	for (Iterator it = e.getAttributes().iterator(); it.hasNext();) {
	    addAttribute((Attribute)it.next());
	}

	
	try {
	    processXMLContent(e);
	} catch (SyntaxException ex) {
	    ex.setContainingStatement("XMLStatement");
	    throw ex;
	}
    }

    void init(Token tok, Lexer lex)
	throws IOException, SyntaxException {

	setupValueArrays();
	
 	int startingIndent = initElement(tok, lex);
	
	try {
	    processTOKContent(startingIndent, lex);
	} catch (SyntaxException ex) {
	    ex.setContainingStatement("XMLStatement");
	    throw ex;
	}
    }

    private int initElement(Token tok, Lexer lex) 
	throws IOException, SyntaxException {
	
 	int startingIndent = tok.getIndent();

	String qName = tok.getValue();
	int pos = -1;
	if ((pos = qName.indexOf(':')) == -1) {
	    prefix = "";
	    name = qName;
	} else {
	    prefix = qName.substring(0, pos);
	    name = qName.substring(pos + 1);
	}

	Iterator it =
	    ((ElementToken)tok).getAttributes().entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry ent = (Map.Entry)it.next();
	    if (((String)ent.getKey()).startsWith("xmlns")) 
		addNamespace((String)ent.getKey(),
			     (String)ent.getValue());
	    else
		addAttribute((String)ent.getKey(),
			     (String)ent.getValue());
	}

	return startingIndent;
    }
    
    void addChild(Statement stat) 
	throws SyntaxException {
	
	if (stat == null)
	    return;
	if (templateChildren.contains(stat.getClass())) {
	    children.add(stat);
	} else {
	    super.addChild(stat);
	}
    }

    public void outputTXT(Outputter out) 
	throws SyntaxException {

	out.output("<");
	if ((prefix != null) && !prefix.equals("")) {
	    out.output(prefix);
	    out.output(":");
	}
	out.output(name);
	out.indent();

	for (Iterator it = attributes.iterator(); it.hasNext();) {
	    out.output(" ");
	    AttributeInfo at = (AttributeInfo)it.next();
	    out.output(at.getQualifiedName());
	    out.output("=");
	    out.output(fqs(at.getValue()));
	    out.maybeBreak();
	}
	if (hasChildren()) {
	    out.output(">");

	    out.newline();
	    
	    outputTXTChildren(out);
	    
	    out.dedent();
	    out.newline();

	    // Skip closing tag	    
	} else {
	    out.output(">");
	    out.dedent();
	    out.newline();
	}
    }

    public void outputXML(Outputter out) 
	throws SyntaxException {
	out.output("<");
	out.output(getXMLQualifiedName());
	out.indent();

	if (namespace != null)
	    namespaceChanged =
		out.pushPrefixMapping(prefix, namespace);
	
	for (Iterator it = attributes.iterator(); it.hasNext();) {
	    out.output(" ");
	    AttributeInfo at = (AttributeInfo)it.next();
	    out.output(at.getQualifiedName());
	    out.output("=");
	    out.output(fqs(at.getValue()));
	    out.maybeBreak();
	}
	if (hasChildren()) {
	    out.output(">");

	    out.newline();
	    
	    outputXMLChildren(out);
	    
	    out.dedent();
	    out.newline();

	    out.output("</");
	    out.output(getXMLQualifiedName());
	    out.output(">");
	    out.newline();
	} else {
	    out.output(">");
	    out.dedent();
	    out.newline();
	}

	if (namespaceChanged)
	    out.popPrefixMapping(prefix);
    }

    void addAttribute(org.jdom.Attribute a) {
	if (logCat.isDebugEnabled())
	    logCat.debug("addAttribute(" +
			 a +
			 ") namespace " +
			 a.getNamespaceURI());
	attributes.add(new AttributeInfo(a));
    }

    void addAttribute(String qualifiedName, String value) {
	if (logCat.isDebugEnabled())
	    logCat.debug("addAttribute(" +
			 qualifiedName +
			 ", " +
			 value +
			 ")");
	attributes.add(new AttributeInfo(qualifiedName, value));
    }

    void addNamespace(String prefix,
		      String value) {
	Namespace n = null;
	if (prefix.equals("xmlns"))
	    n = new Namespace("", value);
	else
	    n = new Namespace(prefix.substring(6), value);
	namespaces.add(n);
    }
    
    String getXMLLocalName() {
	return name;
    }

    String getXMLQualifiedName() {
	if ((prefix != null) && !prefix.equals(""))
	    return prefix + ":" + getXMLLocalName();
	else
	    return getXMLLocalName();
    }

    String getNamespace(NamespaceTracker tracker) {
	if (logCat.isDebugEnabled())
	    logCat.debug("getNamespace(...) prefix " + prefix);
	return tracker.getNamespace(prefix);
    }
    
    void startPrefixMapping(ContentHandler handler,
			    NamespaceTracker tracker)
	throws SyntaxException {
	
	super.startPrefixMapping(handler, tracker);

	String namespace = getNamespace(tracker);
	
	namespaceChanged =
	    tracker.pushPrefixMapping(prefix, namespace);
	if (namespaceChanged) {
	    try {
		handler.startPrefixMapping(prefix, namespace);
	    } catch (SAXException se) {
		logCat.warn("SAXException se");
		throw new SyntaxException(se);
	    }
	}
    }

    void addXMLRequiredFields(AttributesImpl attributes,
			      NamespaceTracker tracker) {

	for (Iterator it = this.attributes.iterator(); it.hasNext();) {
	    AttributeInfo at = (AttributeInfo)it.next();
	    attributes.addAttribute(tracker.getNamespace(at.getPrefix()),
				    at.getLocalName(),
				    at.getQualifiedName(),
				    "CDATA",
				    at.getValue());
	}
    }
    
    void addXMLOptionalFields(AttributesImpl attributes,
			      NamespaceTracker tracker) {
    }

    void endPrefixMapping(List namespaces,
			  ContentHandler handler,
			  NamespaceTracker tracker)
	throws SyntaxException {
	
	if (namespaceChanged) {
	    tracker.popPrefixMapping(prefix);
	    try {
		handler.endPrefixMapping(prefix);
	    } catch (SAXException se) {
		logCat.warn("SAXException se");
		throw new SyntaxException(se);
	    }
	}
	
	super.endPrefixMapping(handler, tracker);
    }

    void startStatement(Token tok,
			Lexer lex,
			ContentHandler handler,
			NamespaceTracker tracker)
	throws SyntaxException, IOException {

	int startingIndent = initElement(tok, lex);

	AttributesImpl attributes = new AttributesImpl();


	try {

	    //List namespaces = (List)((ArrayList)this.namespaces).clone();
	    
	    // Output namespaces
	    startPrefixMapping(handler, tracker);
	    //boolean namespaceChanged = this.namespaceChanged;
	    
	    addXMLRequiredFields(attributes, tracker);
	    
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
		
	    handler.startElement(namespace,
				 localName,
				 qName,
				 attributes);
	    
	    // Process any children
	    while (lex.hasNext(startingIndent)) {
		// Create child
		tok = lex.next();
		
		//Statement stat = factory.getStatementNoClone(tok, lex);
		Statement stat = factory.getStatementNoInit(tok, lex);
		
		stat.startStatement(tok, lex, handler, tracker);
	    }
	    
	    // Output end element
	    handler.endElement(namespace,
			       localName,
			       qName);

	    //this.namespaceChanged = namespaceChanged;
	    // Output any close namespace things
	    endPrefixMapping(namespaces, handler, tracker);
	    
	} catch (SAXException se) {
	    logCat.warn("SAXException se");
	    throw new SyntaxException(se);
	}
    }
    
    class AttributeInfo {

	private String prefix = null;
	private String localName = null;
	private String qualifiedName = null;
	private String value = null;

	AttributeInfo(org.jdom.Attribute a) {
	    setQualifiedName(a.getQualifiedName());
	    value = a.getValue();
	}

	AttributeInfo(String qualifiedName, String value) {
	    setQualifiedName(qualifiedName);
	    this.value = value;
	}

	void setQualifiedName(String qualifiedName) {
	    this.qualifiedName = qualifiedName;
	    int pos = -1;
	    if ((pos = qualifiedName.indexOf(':')) != -1) {
		prefix = qualifiedName.substring(0, pos);
		localName = qualifiedName.substring(pos + 1);
	    } else {
		prefix = "";
		localName = qualifiedName;
	    }
	}

	String getPrefix() {
	    return prefix;
	}
	
	String getQualifiedName() {
	    return qualifiedName;
	}

	String getLocalName() {
	    return localName;
	}
	
	String getValue() {
	    return value;
	}
    }
}
