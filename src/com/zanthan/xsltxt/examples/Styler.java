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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.TooManyListenersException;

import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import javax.xml.transform.sax.SAXSource;

import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.apache.xalan.trace.PrintTraceListener;
import org.apache.xalan.trace.TraceListenerEx2;
import org.apache.xalan.trace.TraceListenerEx;
import org.apache.xalan.trace.TraceListener;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.EndSelectionEvent;
import org.apache.xalan.trace.TracerEvent;

import org.apache.xalan.transformer.TransformerImpl;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.xml.sax.helpers.XMLReaderFactory;

import com.zanthan.xsltxt.BasicResolver;

public abstract class Styler
    implements TraceListenerEx2 {

    private static final Category logCat =
	Logger.getLogger(Styler.class);

    protected Templates templates = null;
    protected BasicResolver resolver = null;

    /**
     * Transform the input file into the output file by applying the
     * transformation the styler represents.
     *
     * @param params parameters for the transform
     * @param inFileName the name of the input file
     * @param outFileName the name of the output file
     * @throws FileNotFoundException <<Description>>
     * @throws IOException <<Description>>
     * @throws ParserConfigurationException <<Description>>
     * @throws SAXException <<Description>>
     * @throws TransformerConfigurationException <<Description>>
     * @throws TransformerException <<Description>>
     */
    protected void style(Map params, String inFileName, String outFileName)
        throws TransformerConfigurationException,
               SAXException,
               FileNotFoundException,
               IOException,
               TransformerException,
               ParserConfigurationException {

	if (logCat.isDebugEnabled())
	    logCat.debug("style(" +
			 inFileName +
			 ", " +
			 outFileName +
			 ")");

 	Transformer transformer =
	    templates.newTransformer();

	transformer.setURIResolver(resolver);

	for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
	    Map.Entry ent = (Map.Entry)it.next();
	    transformer.setParameter((String)ent.getKey(), ent.getValue());
	}

	XMLReader xmlRdr = XMLReaderFactory.createXMLReader();
	xmlRdr.setEntityResolver(resolver);

	File inputFile = new File(inFileName);
	InputSource in = new InputSource(new FileInputStream(inputFile));
	SAXSource saxSrc = new SAXSource(xmlRdr, in);
	saxSrc.setSystemId(inputFile.getCanonicalPath()) ;

	StreamResult outResult =
	    new StreamResult(new FileOutputStream(outFileName));

	// ### Temp
	try {
	    PrintTraceListener ptl =
		new PrintTraceListener(new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out))));
	    ptl.m_traceTemplates = true;
	    ptl.m_traceElements = true;
	    ptl.m_traceGeneration = true;
	    ptl.m_traceSelection = true;
	    //((TransformerImpl)transformer).getTraceManager().addTraceListener(ptl);
	    ((TransformerImpl)transformer).getTraceManager().addTraceListener(this);
	    if (logCat.isDebugEnabled())
            logCat.debug("style(...) TransformerImpl.S_DEBUG .... S_DEBUG no longer exists (markw)");
	} catch (TooManyListenersException tmle) {
	    logCat.error("TooManyListenersException", tmle);
	}
	transformer.transform(saxSrc, outResult);
    }

    public void generated(GenerateEvent ev) {
//         if (logCat.isDebugEnabled())
//             logCat.debug("generated(" +
//                          ev +
//                          ")");
    }

    public void selected(SelectionEvent ev) {
//         if (logCat.isDebugEnabled())
//             logCat.debug("selected(" +
//                          ev +
//                          ")");
	logCat.debug("selected " + ev.m_styleNode.getNodeName() +
		     " at " + ev.m_styleNode.getLineNumber() +
		     ":" + ev.m_styleNode.getColumnNumber() +
		     " in " + ev.m_styleNode.getSystemId());
    }

    public void trace(TracerEvent ev) {
//         if (logCat.isDebugEnabled())
//             logCat.debug("trace(" +
//                          ev +
//                          ")");
	logCat.debug("trace " + ev.m_styleNode.getNodeName() +
		     " at " + ev.m_styleNode.getLineNumber() +
		     ":" + ev.m_styleNode.getColumnNumber() +
		     " in " + ev.m_styleNode.getSystemId());
    }

    public void selectEnd(EndSelectionEvent ev) {
//         if (logCat.isDebugEnabled())
//             logCat.debug("selectEnd(" +
//                          ev +
//                          ")");
	logCat.debug("selectEnd " + ev.m_styleNode.getNodeName() +
		     " at " + ev.m_styleNode.getLineNumber() +
		     ":" + ev.m_styleNode.getColumnNumber() +
		     " in " + ev.m_styleNode.getSystemId());
    }

    public void traceEnd(TracerEvent ev) {
//         if (logCat.isDebugEnabled())
//             logCat.debug("traceEnd(" +
//                          ev +
//                          ")");

	logCat.debug("traceEnd " + ev.m_styleNode.getNodeName() +
		     " at " + ev.m_styleNode.getLineNumber() +
		     ":" + ev.m_styleNode.getColumnNumber() +
		     " in " + ev.m_styleNode.getSystemId());
    }
}
