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

public class ForEach
    extends StatementWithContent 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(ForEach.class);

    final static String IDENT = FOR_EACH;
    
    static StatementDescriptor desc = new StatementDescriptor();

    static {
	desc.setTXTName(IDENT);
	desc.setXMLName("for-each");

	desc.addRequiredField("select");

	desc.setAllowedChildren(templateChildren);
    }
    
    private ArrayList sorts = new ArrayList();
    
    ForEach() {
	super(desc);
    }

    void addChild(Statement stat) 
	throws SyntaxException {

	if (logCat.isDebugEnabled())
	    logCat.debug("addChild(" + stat + ")");
	
	if (stat == null)
	    return;
	if (stat instanceof Sort)
	    sorts.add(stat);
	else
	    super.addChild(stat);
    }
    
    void outputTXTChildren(Outputter out)
	throws SyntaxException {
	
	outputTXTChildren(sorts, out);
	super.outputTXTChildren(out);
    }

    boolean hasChildren() {
	return !sorts.isEmpty() || super.hasChildren();
    }

    void outputXMLChildren(Outputter out) 
	throws SyntaxException {

	outputXMLChildren(sorts, out);
	super.outputXMLChildren(out);
    }
    
    void outputXMLChildren(ContentHandler handler,
			   NamespaceTracker tracker)
	throws SyntaxException {

	outputXMLChildren(sorts, handler, tracker);
	super.outputXMLChildren(handler, tracker);
    }

    public Object clone() 
	throws CloneNotSupportedException {
	
	ForEach f = (ForEach)super.clone();
	f.sorts = new ArrayList();
	return f;
    }
}
