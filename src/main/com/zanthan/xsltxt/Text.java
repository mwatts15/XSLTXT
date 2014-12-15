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

import org.jdom.Element;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.zanthan.xsltxt.exception.SyntaxException;
import com.zanthan.xsltxt.exception.TokenNotAllowedException;

public class Text
    extends StatementWithContent 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(Text.class);

    final static String IDENT = TEXT;
    
    static StatementDescriptor desc = new StatementDescriptor();

    static {
	desc.setTXTName(IDENT);
	desc.setXMLName("text");

	desc.addOptionalField("disable-output-escaping");
    }

    String s = null;
    
    Text() {
	super(desc);
    }

    void init(org.jdom.Element e)
	throws SyntaxException {

	super.init(e);
	
	s = doubleSlash(e.getText());
    }

    void init(Token tok, Lexer lex) 
	throws IOException, SyntaxException {

	if (tok.getValue().equals(TEXT_NO_ESCAPE))
	    setOptionalValue(TXT, "disable-output-escaping", "yes");

	super.init(tok, lex);
    }
    
    /**
     * Don't process the content as it is done in init
     */
    void processXMLContent(org.jdom.Element e)
	throws SyntaxException {
    }
    
    void addChild(Statement stat)
	throws SyntaxException {

	if (stat == null)
	    return;
	if (stat instanceof StringStatement)
	    if (s == null)
		s = ((StringStatement)stat).getString();
	    else
		s += ((StringStatement)stat).getString();
	else
	    super.addChild(stat);
    }

    public void outputTXT(Outputter out)
	throws SyntaxException {

	if (desc == null)
	    logCat.error("desc is null in " + getClass());

	out.output(getTXTName());

	out.output(" ");

	if (s != null)
	    out.output(fqs(s));
	
	out.newline();
    }

    String getTXTName()
	throws SyntaxException {
	
	String v = getOptionalValue(TXT, "disable-output-escaping");
	if ((v != null) &&
	    v.equals("yes"))
	    return TEXT_NO_ESCAPE;
	else
	    return TEXT;
    }

    void handleXMLChildren(Outputter out)
	throws SyntaxException {
	
	if (logCat.isDebugEnabled())
	    logCat.debug("handleXMLChildren(...) s is " +
			 s);
	
	if (s == null) {
	    out.output("/>");
	    out.dedent();
	    out.newline();
	} else {
	    out.output(">");

	    out.output(es(s));
	    
	    out.dedent();
	    out.output("</");
	    out.output(getXMLQualifiedName());
	    out.output(">");
	    out.newline();
	}
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
    
    public String toString() {
	return TEXT_NO_ESCAPE + " or " + TEXT;
    }

    public Object clone() 
	throws CloneNotSupportedException {
	
	Text t = (Text)super.clone();
	t.s = null;
	return t;
    }

    void actOnFirstToken(Token tok)
	throws SyntaxException {
	
	if (tok.getValue().equals(TEXT_NO_ESCAPE))
	    setOptionalValue(TXT, "disable-output-escaping", "yes");
    }

    void startStatementChildren(int startingIndent,
				Lexer lex,
				ContentHandler handler,
				NamespaceTracker tracker)
	throws SyntaxException, IOException {
	if (logCat.isDebugEnabled())
	    logCat.debug("startStatementWithChildren(" +
			 startingIndent +
			 ", " +
			 lex +
			 ", " +
			 handler +
			 ", " +
			 tracker +
			 ")");

	String s = null;
	
	// Process any children
	while (lex.hasNext(startingIndent)) {
	    // Create child
	    Token tok = lex.next();

	    if (tok instanceof CommentToken)
		continue;
	    
	    if (!(tok instanceof QuotedToken)) {
		logCat.warn("TokenNotAllowedException");
		throw new TokenNotAllowedException(lex.getSourceIdent(),
						   tok,
						   "QuotedToken");
	    }
	    if (s == null)
		s = tok.getValue();
	    else
		s += tok.getValue();
	}

	if (s != null) {
	    char[] cs = s.toCharArray();
	    try {
		handler.characters(cs, 0, cs.length);
	    } catch (SAXException se) {
		logCat.warn("SAXException se");
		throw new SyntaxException(se);
	    }
	}
    }


}
