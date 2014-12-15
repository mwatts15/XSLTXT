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

import org.jdom.Element;

import org.xml.sax.ContentHandler;

import com.zanthan.xsltxt.exception.SyntaxException;
import com.zanthan.xsltxt.exception.TokenNotAllowedException;

public class StatementWithParams
    extends Statement {

    private static final Category logCat =
	Logger.getLogger(StatementWithParams.class);

    List params = new ArrayList();
    
    StatementWithParams(StatementDescriptor desc) {
	super(desc);
    }

    void init(org.jdom.Element e)
	throws SyntaxException {

	super.init(e);

	processXMLContent(e);
    }
    
    void init(Token tok, Lexer lex) 
	throws IOException, SyntaxException {

	super.init(tok, lex);

	int startingIndent = tok.getIndent();

	try {
	    processTOKParams(startingIndent, lex);
	    
	    processTOKContent(startingIndent, lex);
	} catch (SyntaxException se) {
	    se.setLineNumber(lex.getLineNumber());
	    se.setIndent(lex.getIndent());
	    throw se;
	}
    }
    
    void processTOKParams(int startingIndent, Lexer lex)
	throws IOException, SyntaxException {
	
	if (!lex.hasNext(startingIndent))
	    return;

	Token tok = lex.next(startingIndent);

	if (!tok.getValue().equals("(")) {
	    lex.push(tok);
	    return;
	}

	while(lex.hasNext(startingIndent)) {
	    tok = lex.next(startingIndent);

	    Param p = new Param();
	    p.setupValueArrays();
	    p.init(tok, lex);
	    addChild(p);

	    tok = lex.next(startingIndent);

	    if (tok.getValue().equals(")"))
		break;

	    if (!tok.getValue().equals(",")) {
		logCat.warn("TokenNotAllowedException");
		throw new TokenNotAllowedException(lex.getSourceIdent(), tok, ",");
	    }
	}
    }
    
    /**
     * Only adds Param statements, passes others to the 
     */
    void addChild(Statement stat)
	throws SyntaxException {
	
	if (stat == null)
	    return;
	if (stat instanceof Param) {
	    ((Param)stat).setUseWith(true);
	    params.add(stat);
	} else {
	    super.addChild(stat);
	}
    }

    void outputTXTChildren(Outputter out)
	throws SyntaxException {
	
	outputParams(out);
	super.outputTXTChildren(out);
    }

    boolean hasChildren() {
	return !params.isEmpty() || super.hasChildren();
    }
    
    void outputParams(Outputter out) 
	throws SyntaxException {
	if (hasLargeParams())
	    outputLargeParams(out);
	else
	    outputSmallParams(out);
    }
    
    boolean hasLargeParams() {
	if (params.isEmpty())
	    return false;
	for (Iterator it = params.iterator(); it.hasNext();) {
	    if (((Param)it.next()).hasChildren())
		return true;
	}
	return false;
    }

    void outputLargeParams(Outputter out) 
	throws SyntaxException {
	out.newline();
	for (Iterator it = params.iterator(); it.hasNext();) {
	    ((Param)it.next()).outputTXT(out, true);
	}
    }

    void outputSmallParams(Outputter out) 
	throws SyntaxException {
	if (params.isEmpty())
	    return;
	out.output(" (");
	boolean first = true;
	for (Iterator it = params.iterator(); it.hasNext();) {
	    if (!first) {
		out.output(", ");
		out.maybeBreak();
	    }
	    ((Param)it.next()).outputTXT(out, false);
	    first = false;
	}
	out.output(")");
	out.newline();
    }

    void outputXMLChildren(Outputter out) 
	throws SyntaxException {

	outputXMLChildren(params, out);
	super.outputXMLChildren(out);
    }
    
    void outputXMLChildren(ContentHandler handler,
			   NamespaceTracker tracker)
	throws SyntaxException {

	outputXMLChildren(params, handler, tracker);
	super.outputXMLChildren(handler, tracker);
    }

    public Object clone()
	throws CloneNotSupportedException {
	
	StatementWithParams p = (StatementWithParams)super.clone();
	p.params = new ArrayList();
	return p;
    }

    void startStatementChildren(int startingIndent,
				Lexer lex,
				ContentHandler handler,
				NamespaceTracker tracker)
	throws SyntaxException, IOException {
	if (logCat.isDebugEnabled())
	    logCat.debug("startStatementChildren(" +
			 startingIndent +
			 ", " +
			 lex +
			 ", " +
			 handler +
			 ", " +
			 tracker +
			 ")");

	startParamChildren(startingIndent, lex, handler, tracker);

	super.startStatementChildren(startingIndent, lex, handler, tracker);
    }

    void startParamChildren(int startingIndent,
			    Lexer lex,
			    ContentHandler handler,
			    NamespaceTracker tracker)
	throws SyntaxException, IOException {
	if (logCat.isDebugEnabled())
	    logCat.debug("startParamChildren(" +
			 startingIndent +
			 ", " +
			 lex +
			 ", " +
			 handler +
			 ", " +
			 tracker +
			 ")");

	if (!lex.hasNext(startingIndent))
	    return;

	Token tok = lex.next();
	
	if (!tok.getValue().equals("(")) {
	    lex.push(tok);
	    return;
	}

	while(lex.hasNext(startingIndent)) {
	    tok = lex.next();

	    Param param = new Param();
	    param.setupValueArrays();
	    Statement stat = modifyChild(param);

	    stat.startStatement(tok, lex, handler, tracker);
	    
	    tok = lex.next(startingIndent);

	    if (tok.getValue().equals(")"))
		break;

	    if (!tok.getValue().equals(",")) {
		logCat.warn("TokenNotAllowedException");
		throw new TokenNotAllowedException(lex.getSourceIdent(), tok, ",");
	    }
	}
    }

    Statement modifyChild(Statement stat) {
	if (stat instanceof Param) 
	    ((Param)stat).setUseWith(true);
	return stat;
    }

}
