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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.JDOMException;

import com.zanthan.xsltxt.Lexer;
import com.zanthan.xsltxt.Statement;

import com.zanthan.xsltxt.examples.XSLConverter;

import com.zanthan.xsltxt.exception.SyntaxException;

import junit.framework.*;

public class TestXSLConverter
    extends TestCase {

    private final static Category logCat =
	Logger.getLogger(TestXSLConverter.class);


    private XSLConverter converter = null;

    public TestXSLConverter(String name) {
	super(name);
    }

    protected void setUp() {
	if (logCat.isDebugEnabled())
	    logCat.debug("setUp()");
	converter = new XSLConverter();
    }

    public void test0() {
	if (logCat.isDebugEnabled())
	    logCat.debug("test0()");

	try {
	    PrintWriter p =
		new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("zanthan.test.file.one.out"))));
	    converter.convert(new File(System.getProperty("zanthan.test.file.one.in")),
			      p);
	} catch (JDOMException e) {
	    fail("Should not throw exception " + e);
	} catch (IOException ioe) {
	    fail("Should not throw exception " + ioe);
	} catch (SyntaxException se) {
	    fail("Should not throw exception " + se);
	}
    }

    public void test1() {
	if (logCat.isDebugEnabled())
	    logCat.debug("test1()");

	try {
	    PrintWriter p =
		new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("zanthan.test.file.two.out"))));
	    converter.convert(new File(System.getProperty("zanthan.test.file.two.in")),
			      p);
	} catch (JDOMException e) {
	    fail("Should not throw exception " + e);
	} catch (IOException ioe) {
	    fail("Should not throw exception " + ioe);
	} catch (SyntaxException se) {
	    fail("Should not throw exception " + se);
	}
    }

    public void test2() {
	if (logCat.isDebugEnabled())
	    logCat.debug("test2()");

	try {
	    FileReader r =
		new FileReader(System.getProperty("zanthan.test.file.three.in"));
	    
	    Lexer lex = new Lexer(r, System.getProperty("zanthan.test.file.three.in"));
	    
	    while (lex.hasNext()) {
		System.out.println(lex.next());
	    }
	} catch (IOException ioe) {
	    fail("Should not throw exception " + ioe);
	} catch (SyntaxException se) {
	    fail("Should not throw exception " + se);
	}
    }
	    
    public void test3() {
	if (logCat.isDebugEnabled())
	    logCat.debug("test3()");

	try {
	    FileReader r =
		new FileReader(System.getProperty("zanthan.test.file.four.in"));
	    
	    Lexer lex = new Lexer(r, System.getProperty("zanthan.test.file.four.in"));

	    Statement stat = StatementFactory.getInstance().getStatement(lex);

	    if (stat == null)
		return;
	    
	    PrintWriter p =
		new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));

	    Outputter out = new Outputter(p);
	    stat.outputTXT(out);
	    out.flush();

	    stat.outputXML(out);
	    out.flush();
	    
	} catch (IOException ioe) {
	    fail("Should not throw exception " + ioe);
	} catch (SyntaxException se) {
	    fail("Should not throw exception " + se);
	}
    }
	    
}
