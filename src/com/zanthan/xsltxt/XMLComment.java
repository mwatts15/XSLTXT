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
import java.util.StringTokenizer;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.Comment;

import org.xml.sax.ContentHandler;

import com.zanthan.xsltxt.exception.SyntaxException;

public class XMLComment
    extends Statement 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(XMLComment.class);

    static StatementDescriptor desc = new StatementDescriptor();

    List commentLines = new ArrayList();
    
    XMLComment() {
	super(desc);
    }
    
    XMLComment(Comment c) {

	super(desc);
	
	StringTokenizer tokenizer =
	    new StringTokenizer(c.getText(), "\r\n");
	while (tokenizer.hasMoreTokens())
	    commentLines.add(tokenizer.nextToken());
    }

    void init(Token c, Lexer lex)
	throws IOException, SyntaxException {
	
	commentLines.add(c.getValue());
	while (lex.hasNext()) {
	    Token tok = lex.next();
	    if (tok instanceof CommentToken) {
		commentLines.add(tok.getValue());
	    } else {
		lex.push(tok);
		return;
	    }
	}
	return;
    }
    
    public void outputTXT(Outputter out) {

	for (Iterator it = commentLines.iterator(); it.hasNext();) {
	    out.comment((String)it.next());
	}
    }

    public void outputXML(Outputter out) {
	if (commentLines.size() > 1) {
	    out.output("<!--");
	    out.newline();
	    out.indent();
	    
	    boolean first = true;
	    for (Iterator it = commentLines.iterator(); it.hasNext();) {
		if (!first)
		    out.newline();
		out.output((String)it.next());
		first = false;
	    }
	    
	    out.dedent();
	    out.newline();
	    out.output("-->");
	    out.newline();
	} else {
	    out.output("<!--");
	    out.output((String)commentLines.get(0));
	    out.output("-->");
	    out.newline();
	}
    }

    void outputXML(ContentHandler handler,
		   NamespaceTracker tracker)
	throws SyntaxException {
	
	return;
    }

    public Object clone() 
	throws CloneNotSupportedException {
	
	XMLComment x = (XMLComment)super.clone();
	x.commentLines = new ArrayList();
	return x;
    }

    void startStatement(Token tok,
			Lexer lex,
			ContentHandler handler,
			NamespaceTracker tracker)
	throws SyntaxException, IOException {

	while (lex.hasNext()) {
	    tok = lex.next();
	    if (!(tok instanceof CommentToken)) {
		lex.push(tok);
		return;
	    }
	}
	return;
    }
}
