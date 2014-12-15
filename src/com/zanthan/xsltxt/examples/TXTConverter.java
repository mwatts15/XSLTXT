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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import com.zanthan.xsltxt.Lexer;
import com.zanthan.xsltxt.Outputter;
import com.zanthan.xsltxt.Statement;
import com.zanthan.xsltxt.StatementFactory;

import com.zanthan.xsltxt.exception.SyntaxException;

/**
 * An example class showing how to convert files coded in xsltxt into
 * equivalent files in xsl.
 */
public class TXTConverter {

    /**
     * Category used for logging.
     */
    private static final Category logCat =
	Logger.getLogger(TXTConverter.class);

    /**
     * Used to write the xsl after the conversion.
     */
    private Outputter out = null;

    /**
     * Convert a file in xsltxt format into xsl format
     * and write out the result.
     * @param f the input file
     * @param p where the output will be written
     * @throws IOException
     * @throws SyntaxException
     */
    public void convert(File f, PrintWriter p)
        throws IOException, SyntaxException {

        // A buffered reader speeds up things considerably.
        BufferedReader r =
            new BufferedReader(new FileReader(f));

        // Create a Lexer to read the input file via the reader. The
        // name of the file is used in error messages so that you know
        // where the problem is.
        Lexer lex = new Lexer(r, f.getCanonicalPath());

        // Calling getStatement on a StatementFactory instance reads
        // the first statement from the lexer and all of the
        // statements it contains. So, it reads the whole file for a
        // well constructed file.
        Statement stat =
            StatementFactory.getInstance().getStatement(lex);

        // Leave if nothing was returned.
        if (stat == null)
            return;

        // Create an Outputter wrapping the PrintWriter
        Outputter out = new Outputter(p);

        // Ouput the xml header
        out.output("<?xml version=\"1.0\"?>");
        out.newline();
        // Output the statement returned from the factory. This
        // outputs its children so writing out the whole file.
        stat.outputXML(out);
        // Flush to make sure all is written.
        out.flush();
    }

    /**
     * Convert an input file in xsltxt format into an output file in
     * xsl format.
     * @param inFile the input file
     * @param outFile the output file
     */
    public void convertFile(File inFile, File outFile) {

	if (logCat.isDebugEnabled())
	    logCat.debug("convertFile(" + inFile + ", " + outFile + ")");

	try {
	    convert(inFile,
		    new PrintWriter(new BufferedWriter(new FileWriter(outFile))));
	    return;
	} catch (SyntaxException se) {
	    se.printStackTrace();
	} catch (IOException ie) {
	    ie.printStackTrace();
	}

	// As we're just an example simply display an error.
	System.out.println("Error: Skipping file " + inFile);
    }

    /**
     * Convert all of the xsltxt files in a directory into xsl files
     * and write them to another directory. The xsltxt files are those
     * with the extension .txt.
     * @param inDir the input dir
     * @param outDir the output dir
     * @throws SyntaxException
     * @throws IOException
     */
    public void convertDir(File inDir, File outDir)
	throws SyntaxException, IOException {

 	if (logCat.isDebugEnabled())
 	    logCat.debug("convertDir(" + inDir + ", " + outDir + ")");

	// Make sure that if the output directory exists it is a
	// directory.
	if (outDir.exists()) {
	    if (!outDir.isDirectory()) {
		System.out.println("Output file " + outDir +
				   " must be a directory");
		return;
	    }
	} else {
	    // If there isn't an output directory create one.
	    if (!outDir.mkdirs()) {
		System.out.println("Could not create directory " + outDir);
		return;
	    }
	}

	// A list of the files in the input directory
	String[] fileNames = inDir.list();

	// Process each directory entry
	for (int i = 0; i < fileNames.length; ++i) {
	    File inFile = new File(inDir, fileNames[i]);
	    // If this entry is an xsltxt file convert it
	    if (inFile.isFile()) {
		if (fileNames[i].endsWith(".txt")) {
		    File outFile =
			new File(outDir,
				 fileNames[i].substring(0,
							fileNames[i].length() - 4) +
				 ".xsl");
		    convertFile(inFile, outFile);
		}
	    } else if (inFile.isDirectory()) {
		// This entry is a directory so recurse and convert it
		convertDir(inFile, new File(outDir, fileNames[i]));
	    } else {
		// Don't know what this is but let's skip it
		System.out.println("Skipping " + fileNames[i]);
	    }
	}
    }

    public static void main(String[] args) {
	if (args.length != 2) {
	    System.out.println("Usage: TXTConverter inDir outDir");
	    return;
	}

	String inFileName = args[0];
	String outFileName = args[1];

	File inFile = new File(inFileName);
	if (!inFile.exists()) {
	    System.out.println("Input file " + inFileName + " does not exist");
	    return;
	}

	TXTConverter conv = new TXTConverter();
	try {
	    if (inFile.isFile()) {
		File outFile = new File(outFileName);
		conv.convertFile(inFile.getCanonicalFile(), outFile);
	    } if (inFile.isDirectory()) {
		File outFile = new File(outFileName);
		conv.convertDir(inFile.getCanonicalFile(), outFile);
	    }
	} catch (SyntaxException se) {
	    se.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
    }
}
