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
package com.zanthan.xsltxt.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.sax.SAXSource;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.xml.sax.InputSource;

import com.zanthan.xsltxt.BasicResolver;

public class XSLStyler
    extends Styler {

    private static final Category logCat =
	Logger.getLogger(TXTStyler.class);

    private XSLStyler(String txtStylesheetFileName)
	throws FileNotFoundException,
	       IOException,
	       TransformerConfigurationException {
	
        if (logCat.isDebugEnabled())
            logCat.debug("XSLStyler(" +
                         txtStylesheetFileName +
                         ")");

	TransformerFactory factory =
	    TransformerFactory.newInstance();

	resolver = new BasicResolver();
	factory.setURIResolver(resolver);

	File inFile = new File(txtStylesheetFileName);
 	SAXSource src =
	    new SAXSource(new InputSource(new FileReader(txtStylesheetFileName)));
	src.setSystemId(inFile.getCanonicalPath());
	
 	templates =
	    factory.newTemplates(src);
    }
    
    public static void main(String[] args) {
	if (args.length != 3) {
	    System.out.println("Usage: XSLStyler stylesheet inFile outFile");
	    return;
	}

	try {
	    XSLStyler styler = new XSLStyler(args[0]);
	    styler.style(new HashMap(), args[1], args[2]);
	} catch (Exception e) {
	    logCat.error("Exception occurred", e);
	}
    }
}
