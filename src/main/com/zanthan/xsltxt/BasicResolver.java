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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import javax.xml.transform.sax.SAXSource;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class BasicResolver
    implements URIResolver, EntityResolver {

    private final static Category logCat =
	Logger.getLogger(BasicResolver.class);

    private SAXParserFactory parserFactory = null;

    public BasicResolver() {
	parserFactory = SAXParserFactory.newInstance();
    }
    
    public InputSource resolveEntity(String publicId, String systemId)
	throws SAXException, IOException {

	if (logCat.isDebugEnabled())
	    logCat.debug("resolveEntity(" + publicId + ", " +
			 systemId + ")");

	if (systemId == null) {
	    logCat.warn("Possible Xerces problem. resolveEntity " +
			"has received a null systemId. public Id is \"" +
			publicId + "\". " +
			"Returning null to cause default resolution.");
	    return null;
	}
	
	if (systemId.startsWith("file://"))
	    systemId = systemId.substring(7);
 	try {
	    File f = new File(systemId);
	    if (!f.exists())
		logCat.error("Can not find file " +
			     systemId);
	    InputSource is = new InputSource(new FileReader(f));
	    is.setSystemId(f.getCanonicalPath());
	    if (logCat.isDebugEnabled())
		logCat.debug("resolveEntity setting systemId to " +
			     f.getCanonicalPath());
	    is.setPublicId(publicId);
	    return is;
	} catch (FileNotFoundException fnfe) {
	    logCat.warn("FileNotFoundException");
	    throw new SAXException(fnfe);
	}
    }

    public Source resolve(String href, String base)
	throws TransformerException {
	
	if (logCat.isDebugEnabled())
	    logCat.debug("resolve(" + href + ", " + base + ")");
	
	if (href.startsWith("file:"))
	    href = href.substring(5);
	if (base.startsWith("file:"))
	    base = base.substring(5);
	
	try {
	    File f = null;
	    File hrefFile = new File(href);
	    if (hrefFile.exists() && hrefFile.isFile()) {
		if (logCat.isDebugEnabled())
		    logCat.debug("href full qualified");
		f = hrefFile;
	    } else {
		File b = new File(base);
		logCat.debug("b is " + b.getCanonicalPath());
		if (href.equals("")) {
		    if (logCat.isDebugEnabled())
			logCat.debug("no href. using b");
		    f = b;
		} else {
		    String baseFile = new File(base).getParentFile().getCanonicalPath();
		    if (logCat.isDebugEnabled())
			logCat.debug("baseFile is " + baseFile);
		    f = new File(baseFile, href);
		}
	    }
	    String systemId = "file:" + f.getCanonicalPath();
	    if (logCat.isDebugEnabled())
		logCat.debug("systemid will be " + systemId +
			     " and the file " + (f.exists()?"exists":"does not exist"));

	    Source src = null;
	    if (systemId.endsWith(".txt")) {
		XMLReader rdr = getTXTReader();
		rdr.setEntityResolver(this);
		InputSource in = new InputSource(new FileReader(f));
		in.setSystemId(systemId);
		src = new SAXSource(rdr, in);
	    } else {
		src = new SAXSource(new InputSource(f.getCanonicalPath()));
	    }

	    src.setSystemId(systemId);
	    
	    return src;
	} catch (FileNotFoundException fe) {
	    logCat.warn("FileNotFoundException fe");
	    throw new TransformerException(fe);
	} catch (IOException ioe) {
	    logCat.warn("IOException ioe");
	    throw new TransformerException(ioe);
	}
    }

    protected XMLReader getTXTReader() {
	return new TXTReader();
    }
}
