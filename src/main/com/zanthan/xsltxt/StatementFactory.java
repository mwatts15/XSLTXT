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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.CDATA;

import com.zanthan.xsltxt.exception.SyntaxException;
import com.zanthan.xsltxt.exception.TokenNotAllowedException;

/**
 * An instance of StatementFactory is used to create instances of
 * Statement subclasses.
 */
public class StatementFactory {

    private static final Category logCat =
	Logger.getLogger(StatementFactory.class);

    private static HashMap statementsByElementName = new HashMap();
    private static HashMap statementsByTokenName = new HashMap();
    
    static {
	if (logCat.isDebugEnabled())
	    logCat.debug("initialization of statementsByElementName starting");

	statementsByElementName.put("apply-imports",
				    new ApplyImports());
	statementsByElementName.put("apply-templates",
				    new Apply());
	statementsByElementName.put("attribute",
				    new Attribute());
	statementsByElementName.put("attribute-set",
				    new AttributeSet());
	statementsByElementName.put("call-template",
				    new Call());
	statementsByElementName.put("choose",
				    new Choose());
	statementsByElementName.put("comment",
				    new Comment());
	statementsByElementName.put("copy",
				    new Copy());
	statementsByElementName.put("copy-of",
				    new CopyOf());
	statementsByElementName.put("decimal-format",
				    new DecimalFormat());
	statementsByElementName.put("element",
				    new Element());
	statementsByElementName.put("fallback",
				    new Fallback());
	statementsByElementName.put("for-each",
				    new ForEach());
	statementsByElementName.put("if",
				    new IfStatement());
	statementsByElementName.put("import",
				    new ImportStatement());
	statementsByElementName.put("include",
				    new Include());
	statementsByElementName.put("key",
				    new Key());
	statementsByElementName.put("message",
				    new Message());
	statementsByElementName.put("namespace-alias",
				    new NamespaceAlias());
	statementsByElementName.put("number",
				    new Number());
	statementsByElementName.put("otherwise",
				    new Otherwise());
	statementsByElementName.put("output",
				    new Output());
	statementsByElementName.put("param",
				    new Param());
	statementsByElementName.put("preserve-space",
				    new Preserve());
	statementsByElementName.put("processing-instruction",
				    new ProcessingInstruction());
	statementsByElementName.put("sort",
				    new Sort());
	statementsByElementName.put("strip-space",
				    new Strip());
	statementsByElementName.put("stylesheet",
				    new Stylesheet());
	statementsByElementName.put("template",
				    new Template());
	statementsByElementName.put("text",
				    new Text());
	// Transform is a synonym for Stylesheet
	statementsByElementName.put("transform",
				    new Stylesheet());
	statementsByElementName.put("value-of",
				    new ValueOf());
	statementsByElementName.put("variable",
				    new Variable());
	statementsByElementName.put("when",
				    new When());
	statementsByElementName.put("with-param",
				    new Param());
	if (logCat.isDebugEnabled())
	    logCat.debug("initialization of statementsByElementName completed");
    }
    
    static {
	if (logCat.isDebugEnabled())
	    logCat.debug("initialization of statementsByTokenName starting");

	statementsByTokenName.put(ApplyImports.IDENT,
				  new ApplyImports());
	statementsByTokenName.put(Apply.IDENT,
				  new Apply());
	statementsByTokenName.put(Call.IDENT,
				  new Call());
	statementsByTokenName.put(Choose.IDENT,
				  new Choose());
	statementsByTokenName.put(Comment.IDENT,
				  new Comment());
	statementsByTokenName.put(Copy.IDENT,
				  new Copy());
	statementsByTokenName.put(CopyOf.IDENT,
				  new CopyOf());
	statementsByTokenName.put(DecimalFormat.IDENT,
				  new DecimalFormat());
	statementsByTokenName.put(Fallback.IDENT,
				  new Fallback());
	statementsByTokenName.put(ForEach.IDENT,
				  new ForEach());
	statementsByTokenName.put(IfStatement.IDENT,
				  new IfStatement());
	statementsByTokenName.put(ImportStatement.IDENT,
				  new ImportStatement());
	statementsByTokenName.put(Include.IDENT,
				  new Include());
	statementsByTokenName.put(Statement.MESSAGE,
				  new Message());
	statementsByTokenName.put(Statement.MESSAGE_FATAL,
				  new Message());
	statementsByTokenName.put(Namespace.IDENT,
				  new Namespace());
	statementsByTokenName.put(NamespaceAlias.IDENT,
				  new NamespaceAlias());
	statementsByTokenName.put(Number.IDENT,
				  new Number());
	statementsByTokenName.put(Otherwise.IDENT,
				  new Otherwise());
	statementsByTokenName.put(Output.IDENT,
				  new Output());
	statementsByTokenName.put(Preserve.IDENT,
				  new Preserve());
	statementsByTokenName.put(Sort.IDENT,
				  new Sort());
	statementsByTokenName.put(Strip.IDENT,
				  new Strip());
	statementsByTokenName.put(Stylesheet.IDENT,
				  new Stylesheet());
	statementsByTokenName.put(Statement.TEXT,
				  new Text());
	statementsByTokenName.put(Statement.TEXT_NO_ESCAPE,
				  new Text());
	statementsByTokenName.put(Statement.VALUE,
				  new ValueOf());
	statementsByTokenName.put(Statement.VALUE_NO_ESCAPE,
				  new ValueOf());
	statementsByTokenName.put(When.IDENT,
				  new When());
	statementsByTokenName.put(Param.IDENT,
				  new Param());
	statementsByTokenName.put(Statement.CDATA,
				  new StringStatement());
	statementsByTokenName.put(Attribute.IDENT,
				  new Attribute());
	statementsByTokenName.put(AttributeSet.IDENT,
				  new AttributeSet());
	statementsByTokenName.put(Element.IDENT,
				  new Element());
	statementsByTokenName.put(ProcessingInstruction.IDENT,
				  new ProcessingInstruction());
	statementsByTokenName.put(Template.IDENT,
				  new Template());
	statementsByTokenName.put(Variable.IDENT,
				  new Variable());
	statementsByTokenName.put(Key.IDENT,
				  new Key());

	if (logCat.isDebugEnabled())
	    logCat.debug("initialization of statementsByTokenName ending");
    }
    
    private static StatementFactory instance = null;

    /**
     * Gets the factory to use.
     * @return the StatementFactory instance to use
     */
    public static StatementFactory getInstance() {
	if (instance == null)
	    instance = new StatementFactory();
	
	if (logCat.isDebugEnabled())
	    logCat.debug("getInstance is returning " +
			 instance);
	return instance;
    }

    /**
     * Private as should only be accessed through getInstance
     */
    private StatementFactory() {
    }

    /**
     * Add descriptions for all of the statements this factory deals
     * with to the element passed in. This can be used to create
     * documentation.
     *
     * @param root the element to add the descriptions to
     */
    public void descriptionsToXML(org.jdom.Element root) {
	if (logCat.isDebugEnabled())
	    logCat.debug("descriptionsToXML(" +
			 root +
			 ")");

	Iterator it = statementsByTokenName.keySet().iterator();
	while (it.hasNext()) {
	    String name = (String)it.next();
	    ((Statement)statementsByTokenName.get(name)).descriptionToXML(name, root);
	}
    }
    
    /**
     * Returns the correct sort of Statement depending on the element
     * passed in. This traverses all of the children of the element
     * and constructs the correct statements for them also. So,
     * starting with the root element of a stylesheet this will return
     * a Statement that represents the stylesheet.
     *
     * @param e the element
     * @return an instance of a subclass of Statement
     * @throws SyntaxException if an error occurs
     */
    public Statement getStatement(org.jdom.Element e)
        throws SyntaxException {
	
	if (logCat.isDebugEnabled())
	    logCat.debug("getStatement(" + e + ")");
	org.jdom.Namespace namespace = e.getNamespace();
	Statement stat = null;
	if (namespace.getURI().equals(Statement.xslNamespaceURI)) {
	    stat = getXSLStatement(e);
	} else {
	    stat = getXMLStatement(e);
	}
	return stat;
    }

    /**
     * Returns an instance of StringStatement for the string provided. If
     * the string is entirely whitespace a null is returned.
     *
     * @param s the string
     * @return an instance of StringStatement
     */
    Statement getStatement(String s) {
	if (logCat.isDebugEnabled())
	    logCat.debug("getStatement(String s)");

	if (s.trim().equals(""))
	    return null;
	else 
	    return new StringStatement(s);
    }

    /**
     * Returns an XMLComment statement.
     *
     * @param c the jdom Comment
     * @return an XMLComment
     */
    Statement getStatement(org.jdom.Comment c) {
	if (logCat.isDebugEnabled())
	    logCat.debug("getStatement(" + c + ")");
	return new XMLComment(c);
    }

    /**
     * Returns a StringStatement for the CDATA passed in.
     *
     * @param c the CDATA
     * @return a StringStatement
     */
    Statement getStatement(CDATA c) {
	if (logCat.isDebugEnabled())
	    logCat.debug("getStatement(" + c + ")");
	return new StringStatement(c);
    }

    /**
     * Return the correct sort of statement for the element passed
     * in. The returned statement has been initialized which means
     * that it is fully constructed, including its children.
     *
     * @param e the element
     * @return a Statement, may be null 
     * @throws SyntaxException if an error occurs
     */
    private Statement getXSLStatement(org.jdom.Element e)
        throws SyntaxException {
	
	if (logCat.isDebugEnabled())
	    logCat.debug("getXSLStatement(" + e + ")");

	Statement stat =
	    getStatementFromMap(e.getName(), statementsByElementName);
	if (stat != null) {
	    stat.setupValueArrays();
	    stat.init(e);
	}

	return stat;
    }

    /**
     * Return an XMLStatement Statement to represent the element
     * passed in.
     *
     * @param e the element
     * @return an XMLStatement
     * @throws SyntaxException if an error occurs
     */
    private Statement getXMLStatement(org.jdom.Element e)
        throws SyntaxException {

	Statement stat = new XMLStatement();
	stat.setupValueArrays();
	stat.init(e);
	return stat;
    }

    public Statement getStatement(Lexer lex)
	throws IOException, SyntaxException {
        if (logCat.isDebugEnabled())
            logCat.debug("getStatement(" +
                      lex +
                      ")");
	
	if (!lex.hasNext())
	    return null;
	
	Token tok = lex.next();

	return getStatement(tok, lex);
    }
    
    Statement getStatement(Token tok, Lexer lex)
	throws IOException, SyntaxException {

	Statement stat = getStatementNoInit(tok, lex);
		
	stat.init(tok, lex);

	return stat;
    }

    /**
     * Return an appropriate instance of Statement or throw a
     * SyntaxException. Will never return null.
     *
     * @param tok the current token
     * @param lex the lexer
     * @return an instance of a Statement subclass
     * @throws IOException <<Description>>
     * @throws SyntaxException <<Description>>
     */
    Statement getStatementNoInit(Token tok, Lexer lex)
	throws IOException, SyntaxException {
        if (logCat.isDebugEnabled())
            logCat.debug("getStatementNoInit(" +
			 tok +
			 ", " +
			 lex +
			 ")");

	Statement stat = null;
	
	if (tok instanceof ElementToken) 
	    stat = new XMLStatement();
	else if (tok instanceof QuotedToken)
	    stat = new StringStatement();
	else if (tok instanceof CommentToken)
	    stat = new XMLComment();
	else if (tok instanceof SimpleToken)
	    stat = getXSLStatement(tok, lex);

	stat.setupValueArrays();
	
	return stat;
    }
    
    private Statement getXSLStatement(Token tok, Lexer lex)
	throws IOException, SyntaxException {
	
	if (logCat.isDebugEnabled())
	    logCat.debug("getXSLStatement(" +
			 tok +
			 ", " +
			 lex +
			 ")");
	
	Statement stat =
	    getStatementFromMap(tok.getValue(),
				statementsByTokenName);
	if (stat == null) {
	    logCat.warn("TokenNotAllowedException");
	    throw new TokenNotAllowedException(lex.getSourceIdent(),
					       tok,
					       "Any IDENT");
	}
	return stat;
    }

    private Statement getStatementFromMap(String name, Map map)
	throws SyntaxException {

	Object o = map.get(name);
	if (o != null) {
	    try {
		return (Statement)((Statement)o).clone();
	    } catch (CloneNotSupportedException cnse) {
		logCat.warn("CloneNotSupportedException cnse");
		throw new SyntaxException(cnse);
	    }
	} else {
	    return null;
	}
    }
}
