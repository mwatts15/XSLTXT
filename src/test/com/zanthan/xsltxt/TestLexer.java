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
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import com.zanthan.xsltxt.exception.SyntaxException;

import junit.framework.*;

public class TestLexer
    extends TestCase {

    private final static Category logCat =
	Logger.getLogger(TestDriver.class);

    public TestLexer(String name) {
	super(name);
    }

    public void test0() {
	if (logCat.isDebugEnabled())
	    logCat.debug("test0()");

	String txtFileName=
	    System.getProperty("zanthan.test.fileName.txt");

	try {
	    Lexer lexer =
		new Lexer(new BufferedReader(new FileReader(txtFileName)),
			  txtFileName);

	    long before = System.currentTimeMillis();
	    while (lexer.hasNext()) {
		Token t = lexer.next();
	    }
	    long after = System.currentTimeMillis();
	    
	    if (logCat.isDebugEnabled())
		logCat.debug("Before lexing " +
			     before +
			     " after lexing " +
			     after +
			     " elapsed " +
			     (after - before));
	    
	} catch (SyntaxException se) {
	    fail("Should not throw exception " + se);
	} catch (IOException ioe) {
	    fail("Should not throw exception " + ioe);
	}
    }
}
