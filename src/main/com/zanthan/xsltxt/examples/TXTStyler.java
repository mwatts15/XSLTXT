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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.sax.SAXSource;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.xml.sax.InputSource;

import com.zanthan.xsltxt.BasicResolverTwo;
import com.zanthan.xsltxt.TXTReaderTwo;

/**
 * An example class showing how to use a TXTReader to produce xsl
 * transfomation templates from xsltxt source.
 */
public class TXTStyler
    extends Styler {

    private static final Category logCat =
	Logger.getLogger(TXTStyler.class);

    /**
     * Convert a xsltxt stylesheet into some templates to be used for
     * transformation.
     * @param txtStylesheetFileName the name of the xsltxt file to use
     * @throws FileNotFoundException
     * @throws IOException
     * @throws TransformerConfigurationException
     */
    private TXTStyler(String txtStylesheetFileName)
	throws FileNotFoundException,
	       IOException,
	       TransformerConfigurationException {
	
        if (logCat.isDebugEnabled())
            logCat.debug("TXTStyler(" +
                         txtStylesheetFileName +
                         ")");

	// We need a TransformerFactory
	TransformerFactory factory =
	    TransformerFactory.newInstance();

	// The factory needs a resolver. The job of the resolver is to
	// convert requests for imported and included xsltxt stylesheets
	// into SAXSource objects. We can't use the standard resolver as
	// we need to return a TXTReader powered source.
	resolver = new BasicResolverTwo();
	factory.setURIResolver(resolver);

	// Setup the input source that is going to be used. We need to
	// set the system id so that when the resolver is called to
	// resolve an import or include it has a location to use for
	// resolving relative info against.
	File inFile = new File(txtStylesheetFileName);
	InputSource in = new InputSource(new FileReader(inFile));
	in.setSystemId(inFile.getCanonicalPath());

	// Now create the templates from a SAXSource powered by a
	// TXTReader.
 	templates =
	    factory.newTemplates(new SAXSource(new TXTReaderTwo(), in));
    }
    
    public static void main(String[] args) {

	Map params = new HashMap();
	List arguments = new ArrayList(Arrays.asList(args));
	for (Iterator it = arguments.listIterator(); it.hasNext();) {
	    String nameAndValue = (String)it.next();
	    if (nameAndValue.startsWith("-p")) {
		it.remove();
		nameAndValue = nameAndValue.substring(2);
		int pos = nameAndValue.indexOf('=');
		if (pos == -1)
		    continue;
		params.put(nameAndValue.substring(0, pos),
			   nameAndValue.substring(pos + 1));
	    } else {
		break;
	    }
	}

	if (arguments.size() != 3) {
	    System.out.println("Usage: TXTStyler [-pname=value]* stylesheet inFile outFile");
	    return;
	}

	try {
	    TXTStyler styler = new TXTStyler((String)arguments.get(0));
	    styler.style(params,
			 (String)arguments.get(1),
			 (String)arguments.get(2));
	} catch (Exception e) {
	    logCat.error("Exception occurred", e);
	}
    }
}
