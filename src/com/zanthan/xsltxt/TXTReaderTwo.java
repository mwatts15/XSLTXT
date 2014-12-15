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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.transform.sax.TemplatesHandler;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.zanthan.xsltxt.exception.SyntaxException;

public class TXTReaderTwo
    implements XMLReader {

    private static final Category logCat =
	Logger.getLogger(TXTReader.class);

    private EntityResolver entityResolver = null;
    private DTDHandler dtdHandler = null;
    private ContentHandler contentHandler = null;
    private ErrorHandler errorHandler = null;

    private Lexer lex = null;
    
    public TXTReaderTwo() {
	entityResolver = new BasicResolverTwo();
	errorHandler = new MyErrorHandler();
    }
    
    public boolean getFeature(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
	throw new SAXNotRecognizedException(name);
    }

    public void setFeature(String name, boolean value)
	throws SAXNotRecognizedException, SAXNotSupportedException {
	throw new SAXNotRecognizedException(name);
    }

    public Object getProperty(String name)
	throws SAXNotRecognizedException, SAXNotSupportedException {
	throw new SAXNotRecognizedException(name);
    }

    public void setProperty(String name, Object value)
	throws SAXNotRecognizedException, SAXNotSupportedException {
	throw new SAXNotRecognizedException(name);
    }

    public void setEntityResolver(EntityResolver resolver) {
	entityResolver = resolver;
    }

    public EntityResolver getEntityResolver() {
	return entityResolver;
    }
    
    public void setDTDHandler(DTDHandler handler) {
	dtdHandler = handler;
    }

    public DTDHandler getDTDHandler() {
	return dtdHandler;
    }

    public void setContentHandler(ContentHandler handler) {
	if (logCat.isDebugEnabled())
	    logCat.debug("setContentHandler(" + handler + ")");
	contentHandler = handler;
    }

    public ContentHandler getContentHandler() {
	return contentHandler;
    }

    public void setErrorHandler(ErrorHandler handler) {
	if (logCat.isDebugEnabled())
	    logCat.debug("setErrorHandler(" +
			 handler +
			 ")");
	errorHandler = handler;
    }

    public ErrorHandler getErrorHandler() {
	return errorHandler;
    }

    public void parse(InputSource input)
	throws IOException, SAXException {

	if (logCat.isDebugEnabled())
	    logCat.debug("starting parse(" + input.getSystemId() + ")");

	Reader cs = input.getCharacterStream();
	if (cs == null)
	    cs = new InputStreamReader(input.getByteStream());
	Lexer lex = new Lexer(new BufferedReader(cs), input.getSystemId());

	try {
	    ContentHandler handler = getContentHandler();
	    
	    if (handler instanceof TemplatesHandler) {
		if (logCat.isDebugEnabled())
		    logCat.debug("SystemId is " +
				 ((TemplatesHandler)handler).getSystemId());
	    }
	    
	    handler.setDocumentLocator(new MyLocator(lex,
						     input.getPublicId(),
						     input.getSystemId()));
	    
	    handler.startDocument();

	    startStatement(lex);
	    
	    handler.endDocument();
	    
	} catch (SyntaxException se) {
	    throw new SAXException(se);
	}
	
	if (logCat.isDebugEnabled())
	    logCat.debug("ending parse(" + input.getSystemId() + ")");
    }

    public void parse(String systemId)
	throws IOException, SAXException {

	parse(new InputSource(systemId));
    }

    private void startStatement(Lexer lex)
	throws SyntaxException, IOException {

	if (!lex.hasNext())
	    return;
	
	Token tok = lex.next();
	Statement stat =
	    //StatementFactory.getInstance().getStatementNoClone(tok, lex);
	    StatementFactory.getInstance().getStatementNoInit(tok, lex);
	
	stat.startStatement(tok,
			    lex,
			    getContentHandler(),
			    new NamespaceTracker());
    }
    
    private class MyLocator
	implements Locator {

	private Lexer lex = null;
	private String publicId = null;
	private String systemId = null;
	
	private MyLocator(Lexer lex, String publicId, String systemId) {
	    this.lex = lex;
	    this.publicId = publicId;
	    this.systemId = systemId;
	}
	
	public int getColumnNumber() {
	    return lex.getIndent();
	}

	public int getLineNumber() {
	    return lex.getLineNumber();
	}

	public String getPublicId() {
	    return publicId;
	}

	public String getSystemId() {
	    return systemId;
	}
    }

    private class MyErrorHandler
	implements ErrorHandler {

	public void error(SAXParseException exception) {
	    logCat.warn("error", exception);
	}

	public void fatalError(SAXParseException exception) {
	    logCat.warn("fatalError", exception);
	}

	public void warning(SAXParseException exception) {
	    logCat.warn("warning", exception);
	}
    }
}
