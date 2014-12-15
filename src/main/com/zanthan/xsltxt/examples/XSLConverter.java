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
import java.io.PrintWriter;

import java.util.List;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import com.zanthan.xsltxt.Outputter;
import com.zanthan.xsltxt.Statement;
import com.zanthan.xsltxt.StatementFactory;

import com.zanthan.xsltxt.exception.SyntaxException;

public class XSLConverter {

    private static final Category logCat =
        Logger.getLogger(XSLConverter.class);

    private SAXBuilder builder = null;
    private Outputter out = null;

    public XSLConverter() {
        builder = new SAXBuilder();
    }

    public void convert(File f, PrintWriter p)
        throws JDOMException, SyntaxException {

        out = new Outputter(p);
        try
        {
            convert(builder.build(f));
        }
        catch (IOException e)
        {
            System.out.println("IO error building from a file "+f.toString() +":");
            e.printStackTrace();
        }
        out.flush();
    }

    public void convertFile(File inFile, File outFile) {

        if (logCat.isDebugEnabled())
            logCat.debug("convertFile(" + inFile + ", " + outFile + ")");

        try {
            convert(inFile,
                    new PrintWriter(new BufferedWriter(new FileWriter(outFile))));
            return;
        } catch (JDOMException jde) {
            jde.printStackTrace();
        } catch (SyntaxException se) {
            se.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        System.out.println("Error: Skipping file " + inFile);
    }

    public void convertDir(File inDir, File outDir)
        throws JDOMException, SyntaxException, IOException {

        if (logCat.isDebugEnabled())
            logCat.debug("convertDir(" + inDir + ", " + outDir + ")");

        if (outDir.exists()) {
            if (!outDir.isDirectory()) {
                System.out.println("Output file " + outDir +
                        " must be a directory");
                return;
            }
        } else {
            if (!outDir.mkdirs()) {
                System.out.println("Could not create directory " + outDir);
                return;
            }
        }

        String[] fileNames = inDir.list();

        for (int i = 0; i < fileNames.length; ++i) {
            File inFile = new File(inDir, fileNames[i]);
            if (inFile.isFile()) {
                if (fileNames[i].endsWith(".xsl")) {
                    File outFile =
                        new File(outDir,
                                fileNames[i].substring(0,
                                    fileNames[i].length() - 4) +
                                ".txt");
                    convertFile(inFile, outFile);
                }
            } else if (inFile.isDirectory()) {
                convertDir(inFile, new File(outDir, fileNames[i]));
            } else {
                System.out.println("Skipping " + fileNames[i]);
            }
        }
    }

    private void convert(Document doc)
        throws JDOMException, SyntaxException {

        Statement stat =
            StatementFactory.getInstance().getStatement(doc.getRootElement());
        if (stat != null)
            stat.outputTXT(out);
        else
            logCat.warn("Could not convert " + doc.getRootElement());
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: XSLConverter inDir outDir");
            return;
        }

        String inFileName = args[0];
        String outFileName = args[1];

        File inFile = new File(inFileName);
        if (!inFile.exists()) {
            System.out.println("Input file " + inFileName + " does not exist");
            return;
        }

        XSLConverter conv = new XSLConverter();
        try {
            if (inFile.isFile()) {
                File outFile = new File(outFileName);
                conv.convertFile(inFile.getCanonicalFile(), outFile);
            } if (inFile.isDirectory()) {
                File outFile = new File(outFileName);
                conv.convertDir(inFile.getCanonicalFile(), outFile);
            }
        } catch (JDOMException jde) {
            jde.printStackTrace();
        } catch (SyntaxException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
