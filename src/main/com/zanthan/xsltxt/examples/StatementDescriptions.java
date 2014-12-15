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
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;

import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import com.zanthan.xsltxt.StatementFactory;

public class StatementDescriptions {

    private static final Category logCat =
	Logger.getLogger(StatementDescriptions.class);

    private File outFile = null;

    public StatementDescriptions(String outFileName) {
	outFile = new File(outFileName);
    }

    public void output()
	throws IOException {

	Element root = new Element("statements");
	Document doc = new Document(root);

	StatementFactory.getInstance().descriptionsToXML(root);

	XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
	out.output(doc, new BufferedWriter(new FileWriter(outFile)));
    }

    public static void main(String[] args) {

	if (args.length != 1) {
	    System.out.println("Usage: StatementDescriptions outFile");
	    return;
	}

	StatementDescriptions sd =
	    new StatementDescriptions(args[0]);

	try {
	    sd.output();
	} catch (IOException ioe) {
	    logCat.error("IOException", ioe);
	}
    }
}
