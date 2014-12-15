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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import junit.framework.*;

public class TestTXTReader
    extends TestCase {

    private final static Category logCat =
	Logger.getLogger(TestTXTReader.class);

    public TestTXTReader(String name) {
        super(name);
    }

    public void test0() {
	if (logCat.isDebugEnabled())
	    logCat.debug("test0()");

	String txtFileName = System.getProperty("zanthan.test.fileName.txt");
	String inputFileName = System.getProperty("zanthan.test.fileName.in");
	String outputFileName = System.getProperty("zanthan.test.fileName.out");

	try {
	    BasicResolver resolver = new BasicResolver();
	    TXTReader txtRdr = new TXTReader();

	    TransformerFactory factory =
		TransformerFactory.newInstance();
	    factory.setURIResolver(resolver);

	    InputSource in = new InputSource(new FileReader(txtFileName));
	    in.setSystemId(new File(txtFileName).getCanonicalPath());

	    if (logCat.isDebugEnabled())
		logCat.debug("test0() - starting to build transformer");

	    long before = System.currentTimeMillis();
	    Transformer transformer =
		factory.newTransformer(new SAXSource(txtRdr, in));
	    long after = System.currentTimeMillis();

	    if (logCat.isDebugEnabled())
		logCat.debug("test0() - transformer built: elapsed " +
			     (after - before));

	    transformer.setURIResolver(resolver);

	    StreamResult outResult =
		new StreamResult(new FileOutputStream(outputFileName));

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();



	    parserFactory.setValidating(false);
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader xmlRdr = parser.getXMLReader();
	    xmlRdr.setEntityResolver(resolver);
	    in = new InputSource(new FileInputStream(inputFileName));
	    //in.setSystemId(inputFileName);
	    SAXSource saxSrc = new SAXSource(xmlRdr, in);
	    saxSrc.setSystemId(new File(inputFileName).getCanonicalPath()) ;

	    transformer.transform(saxSrc, outResult);

	} catch (ParserConfigurationException pce) {
	    fail("Should not throw exception " + pce);
	} catch (TransformerException te) {
	    fail("Should not throw exception " + te);
	} catch (FileNotFoundException fnfe) {
	    fail("Should not throw exception " + fnfe);
	} catch (SAXException se) {
	    fail("Should not throw exception " + se);
	} catch (IOException ioe) {
	    fail("Should not throw exception " + ioe);
	}
    }
}
