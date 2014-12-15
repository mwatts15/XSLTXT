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

import java.util.ArrayList;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.xml.sax.ContentHandler;

import com.zanthan.xsltxt.exception.SyntaxException;

/**
 * Represents xsl:apply-templates
 */
public class Apply
    extends StatementWithParams
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(Apply.class);

    final static String IDENT = APPLY_TEMPLATES;

    static StatementDescriptor desc = new StatementDescriptor();

    static {
	desc.setTXTName(IDENT);
	desc.setXMLName("apply-templates");
	
	desc.addOptionalField("select", SELECT);
	desc.addOptionalField("mode", MODE);

	desc.setExample("apply .select \"statement\"\n" +
			"  sort .by \"txtName\"\n" +
			"\n" +
			"apply .select \"requiredFields/field\" .mode \"required\"");
    }
    
    private ArrayList sorts = new ArrayList();
    
    Apply() {
	super(desc);
    }

    void addChild(Statement stat) 
	throws SyntaxException {
	
	if (stat == null)
	    return;
	if (stat instanceof Sort)
	    sorts.add(stat);
	else
	    super.addChild(stat);
    }

    boolean hasChildren() {
	return !sorts.isEmpty() || super.hasChildren();
    }

    void outputTXTChildren(Outputter out)
	throws SyntaxException {
	
	outputParams(out);
	outputTXTChildren(sorts, out);
    }

    void outputXMLChildren(Outputter out) 
	throws SyntaxException {

	outputXMLChildren(params, out);
	outputXMLChildren(sorts, out);
    }

    void outputXMLChildren(ContentHandler handler,
			   NamespaceTracker tracker)
	throws SyntaxException {

	outputXMLChildren(params, handler, tracker);
	outputXMLChildren(sorts, handler, tracker);
    }

    public Object clone() 
	throws CloneNotSupportedException {
	
	Apply a = (Apply)super.clone();
	a.sorts = new ArrayList();
	return a;
    }
}
