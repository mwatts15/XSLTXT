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

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

public class Outputter {

    private static final Category logCat =
	Logger.getLogger(Outputter.class);

    private static int LINE_LENGTH_LIMIT = 80;

    // Output printed to here.
    private PrintWriter p = null;

    // Used when creating the quoted representation for attribute
    // values.
    private StringBuffer attr = new StringBuffer(32);

    // The line currently being assembled is stored here.
    private StringBuffer line = new StringBuffer(128);

    // Used to assemble blocks of text for output
    private StringBuffer sb = new StringBuffer(128);

    // How many spaces to indent the current line.
    private int indentThisLine = 0;

    // How many spaces to indent the next line to be output
    private int indentNextLine = 0;

    // Keep track of changes in namespaces for XMLStatements
    private NamespaceTracker tracker = new NamespaceTracker();
    
    /**
     * Create a new Outputter that will use the provided
     * PrintWriter for output.
     * @param p output is printed using this
     */
    public Outputter(PrintWriter p) {
	this.p = p;
    }

    /**
     * Tells the outputter that if a newline needs to be written after
     * this is called then it should be indented 2 spaces more than
     * the current line.
     */
    void indent() {
	indentNextLine += 2;
 	if ((line.length() == 0) && (sb.length() == 0))
 	    indentThisLine += 2;
	if (logCat.isDebugEnabled())
	    logCat.debug("indent() indentThisLine " + indentThisLine +
			 " indentNextLine " + indentNextLine);
    }

    /**
     * Tells the outputter that if a newline needs to be written after
     * this is called then it should be indented 2 spaces less than
     * the current line.
     */
    void dedent() {
	indentNextLine -= 2;
	if ((line.length() == 0) && (sb.length() == 0))
	    indentThisLine -= 2;
	if (indentThisLine < 0) indentThisLine = 0;
	if (indentNextLine < 0) indentNextLine = 0;
	if (logCat.isDebugEnabled())
	    logCat.debug("dedent() indentThisLine " + indentThisLine +
			 " indentNextLine " + indentNextLine);
    }

    /**
     * Outputs a name and value formatted correctly for an XML
     * attribute. The value is surrounded by quotes and any &lt;,
     * &amp; and &quot; it contains are converted to &amp;lt;,
     * &amp;amp; and &amp;quot;.
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    void outputAttribute(String name, String value) {
	if ((value == null) || (name == null))
	    return;
	output(" ");
	output(name);
	output("=");
	output(quoteAttributeValue(value));
	maybeBreak();
    }
    
    /**
     * Output a labeled string with a break after it if needed.
     *
     * @param label the label
     * @param s the string
     */
    void outputPaddedWithBreak(String label, String s) {
	outputPadded(label, s);
	maybeBreak();
    }
    
    void outputWithBreak(String label, String s) {
	output(label, s);
	maybeBreak();
    }
    
    void outputPadded(String label, String s) {
	if (s == null)
	    return;
	outputPaddedBoth(label);
	output(s);
    }

    private void output(String label, String s) {
	if (s == null)
	    return;
	output(label);
	output(s);
    }

    void outputPaddedWithBreak(String s) {
	outputPadded(s);
	maybeBreak();
    }
    
    void outputWithBreak(String s) {
	output(s);
	maybeBreak();
    }
    
    void outputPadded(String s) {
	if (logCat.isDebugEnabled())
	    logCat.debug("outputPadded(" + s + ")");
	if (s == null)
	    return;
	sb.append(" ");
	sb.append(s);
    }

    void outputPaddedBoth(String s) {
	if (logCat.isDebugEnabled())
	    logCat.debug("outputPaddedBoth(" + s + ")");
	if (s == null)
	    return;
	outputPadded(s);
	sb.append(" ");
    }
	
    public void output(String s) {
	if (logCat.isDebugEnabled())
	    logCat.debug("output(" + s + ")");
	if (s == null)
	    return;
	sb.append(s);
    }

    void comment(String s) {
	if (logCat.isDebugEnabled())
	    logCat.debug("comment(" + s + ")");
	newline();
	p.print("#");
	if (!s.startsWith(" "))
	    p.print(" ");
	p.println(s);
    }

    /**
     * If the data added since the last maybeBreak call or newline
     * call would make the current line too long this outputs a
     * newline and indents appropriately. Prints the data currently
     * accumulated in line followed by a newline if adding the data in
     * sb to the data in line would make the line too long. After that
     * the data in sb is appended to line. When maybeBreak has
     * finished sb's contents have been appended to line and sb is now
     * empty.
     */
    void maybeBreak() {
	if (logCat.isDebugEnabled())
	    logCat.debug("maybeBreak()");
	if ((line.length() > 0) &&
	    ((line.length() + sb.length() + indentThisLine) > LINE_LENGTH_LIMIT)) {
	    printLine();
	}

	if (sb.length() > 0) {
	    line.append(sb.toString());
	    sb.setLength(0);
	}
    }

    /**
     * Outputs a newline.
     */
    public void newline() {
	if (logCat.isDebugEnabled())
	    logCat.debug("newline()");
	maybeBreak();
	if (line.length() > 0) {
	    printLine();
	}
    }

    /**
     * Write all remaining output to the print writer and flush it.
     */
    public void flush() {
	newline();
	p.flush();
    }

    boolean pushPrefixMapping(String prefix, String namespace) {
	if (logCat.isDebugEnabled())
	    logCat.debug("pushPrefixMapping(" +
			 prefix + ", " +
			 namespace + ")");

	boolean newMapping = tracker.pushPrefixMapping(prefix, namespace);
	if (newMapping)
	    outputNamespace(prefix, namespace);
	return newMapping;
    }	    

    void popPrefixMapping(String prefix) {
	tracker.popPrefixMapping(prefix);
    }

    private void outputNamespace(String prefix, String namespace) {
	if (((prefix == null) || prefix.equals("")) &&
	    ((namespace == null) || namespace.equals("")))
	    return;

	output(" xmlns");

	if ((prefix != null) && !prefix.equals("")) {
	    output(":");
	    output(prefix);
	}

	output("=\"");
	output(namespace);
	output("\"");
	    
	maybeBreak();
		
    }
    
    private void printLine() {
	if (logCat.isDebugEnabled())
	    logCat.debug("printLine() indentThisLine is " + indentThisLine +
			 " indentNextLine is " + indentNextLine + " printing \"" +
			 line.toString() + "\"");
	for (int i = 0; i < indentThisLine; ++i) {
	    p.print(' ');
	}
	indentThisLine = indentNextLine;
	p.println(line.toString().trim());
	line.setLength(0);
    }

    private String quoteAttributeValue(String value) {
	attr.setLength(0);
	attr.append('"');
	int start = 0;
	char[] cs = value.toCharArray();
	for (int i = 0; i < cs.length; ++i) {
	    if ((cs[i] == '"') ||
		(cs[i] == '<') ||
		(cs[i] == '&')) {

		if (i > start)
		    attr.append(cs, start, i - start);
		if (cs[i] == '"')
		    attr.append("&quot;");
		if (cs[i] == '<')
		    attr.append("&lt;");
		if (cs[i] == '&')
		    attr.append("&amp;");
		start = i + 1;
	    }
	}
	if (start == 0) {
	    attr.append(value);
	} else {
	    if (start != cs.length)
		attr.append(cs, start, cs.length - start);
	}
	attr.append('"');
	return attr.toString();
    }
}
