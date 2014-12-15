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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.sax.SAXSource;

import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.JDOMException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.zanthan.xsltxt.examples.XSLConverter;

import com.zanthan.xsltxt.exception.SyntaxException;

import junit.framework.*;

public class TestDriver
    extends TestCase {

    private final static Category logCat =
	Logger.getLogger(TestDriver.class);

    SAXParserFactory parserFactory = null;
    BasicResolver xslResolver = new BasicResolver();
    BasicResolver xmlResolver = new BasicResolver();
    
    public TestDriver(String name) {
	super(name);
    }

    public void setUp() {
	parserFactory = SAXParserFactory.newInstance();
	parserFactory.setValidating(false);
    }
    
    public void test0() {
	if (logCat.isDebugEnabled())
	    logCat.debug("test0()");

	String skip =
	    System.getProperty("zanthan.test0.skip");
	if ((skip != null) && skip.equals("yes")) {
	    if (logCat.isDebugEnabled())
		logCat.debug("skipping test0()");
	    return;
	}
	try {
	    String inputFileName =
		System.getProperty("zanthan.test0.fileName.in");
	    String outputFileName =
		System.getProperty("zanthan.test0.fileName.out");

	    if (inputFileName.endsWith(".xsl") &&
		outputFileName.endsWith(".txt")) {
		convertXslToTxt(inputFileName, outputFileName);
	    } else if (inputFileName.endsWith(".txt") &&
		       outputFileName.endsWith(".xsl")) {
		convertTxtToXsl(inputFileName, outputFileName);
	    }
	} catch (IOException ioe) {
	    fail("Should not throw exception " + ioe);
	} catch (SyntaxException se) {
	    fail("Should not throw exception " + se);
	} catch (JDOMException je) {
	    fail("Should no throw exception " + je);
	}
    }

    public void test1() {
	if (logCat.isDebugEnabled())
	    logCat.debug("test1()");

	String skip =
	    System.getProperty("zanthan.test1.skip");
	if ((skip != null) && skip.equals("yes")) {
	    if (logCat.isDebugEnabled())
		logCat.debug("skipping test1()");
	    return;
	}
	try {
 	    String xslFileName =
		System.getProperty("zanthan.test1.xslFileName");
 	    String xmlFileName =
		System.getProperty("zanthan.test1.xmlFileName");
 	    String outputFileName =
		System.getProperty("zanthan.test1.outputFileName");
	    transform(xslFileName, xmlFileName, outputFileName);
	} catch (TransformerException te) {
	    fail("Should not throw exception " + te);
	} catch (FileNotFoundException fnfe) {
	    fail("Should not throw exception " + fnfe);
	} catch (SAXException se) {
	    fail("Should not throw exception " + se);
	} catch (ParserConfigurationException pce) {
	    fail("Should not throw exception " + pce);
	}
    }

    
    public void test2() {
	if (logCat.isDebugEnabled())
	    logCat.debug("test1()");

	String skip =
	    System.getProperty("zanthan.test2.skip");
	if ((skip != null) && skip.equals("yes")) {
	    if (logCat.isDebugEnabled())
		logCat.debug("skipping test2()");
	    return;
	}
	try {
 	    String txtFileName =
		System.getProperty("zanthan.test2.txtFileName");
 	    String xmlFileName =
		System.getProperty("zanthan.test2.xmlFileName");
 	    String outputFileName =
		System.getProperty("zanthan.test2.outputFileName");

	    String xslFileName = txtFileName + ".cnv.xsl";
	    
	    convertTxtToXsl(txtFileName, xslFileName);
	    
	    transform(xslFileName, xmlFileName, outputFileName);
	    
	} catch (TransformerException te) {
	    fail("Should not throw exception " + te);
	} catch (FileNotFoundException fnfe) {
	    fail("Should not throw exception " + fnfe);
	} catch (IOException ioe) {
	    fail("Should not throw exception " + ioe);
	} catch (SyntaxException se) {
	    fail("Should not throw exception " + se);
	} catch (SAXException se) {
	    fail("Should not throw exception " + se);
	} catch (ParserConfigurationException pce) {
	    fail("Should not throw exception " + pce);
	}
    }
    
    private void convertXslToTxt(String inputFileName, String outputFileName)
	throws IOException, SyntaxException, JDOMException {

	if (logCat.isDebugEnabled())
	    logCat.debug("converting xsl to txt");
	XSLConverter converter = new XSLConverter();

	PrintWriter p =
	    new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
	converter.convert(new File(inputFileName),
			  p);
    }
    
    private void convertTxtToXsl(String inputFileName, String outputFileName)
	throws IOException, SyntaxException {
	
	if (logCat.isDebugEnabled())
	    logCat.debug("converting txt to xsl");
	BufferedReader r =
	    new BufferedReader(new BufferedReader(new FileReader(inputFileName)));
	
	Lexer lex = new Lexer(r, inputFileName);
	
	Statement stat =
	    StatementFactory.getInstance().getStatement(lex);
	
	if (stat == null)
	    return;
	
	PrintWriter p =
	    new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
	
	Outputter out = new Outputter(p);
	
	out.output("<?xml version=\"1.0\"?>");
	out.newline();
	stat.outputXML(out);
	out.flush();
    }

    private void transform(String xslFileName, String xmlFileName, String outputFileName)
	throws TransformerException, FileNotFoundException,
	       SAXException, ParserConfigurationException {

	if (logCat.isDebugEnabled())
	    logCat.debug("transform(" + xslFileName + ", " +
			 xmlFileName + ", " +
			 outputFileName + ")");

	TransformerFactory factory =
	    TransformerFactory.newInstance();
	factory.setURIResolver(xslResolver);

 	SAXSource src = new SAXSource(new InputSource(new FileReader(xslFileName)));
 	src.setSystemId(xslFileName);

	if (logCat.isDebugEnabled())
	    logCat.debug("transform - starting to build transformer");
	long before = System.currentTimeMillis();
 	Transformer transformer =
	    factory.newTransformer(src);
	long after = System.currentTimeMillis();
	if (logCat.isDebugEnabled())
	    logCat.debug("transform - transformer built: elapsed " +
			 (after - before));
 	
	transformer.setURIResolver(xmlResolver);
	
	SAXParser parser = parserFactory.newSAXParser();
	if (logCat.isDebugEnabled())
	    logCat.debug("parser is " +
			 parser.getClass().getName());
	XMLReader rdr = parser.getXMLReader();
	rdr.setEntityResolver(xmlResolver);
 	src = new SAXSource(rdr, 
 			    new InputSource(new FileInputStream(xmlFileName)));
 	src.setSystemId(xmlFileName);
	//	src = new SAXSource(new InputSource(xmlFileName));
	transformer.transform(src,
			      new StreamResult(new FileOutputStream(outputFileName)));
    }
}
