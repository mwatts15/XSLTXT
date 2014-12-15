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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.CDATA;
import org.jdom.Element;

public class StatementDescriptor {

    private String txtName = null;
    private String xmlName = null;
    private List requiredFields = new ArrayList();
    private List optionalFields = new ArrayList();
    private Set allowedChildren = new HashSet();
    private String example = null;
    
    void setTXTName(String txtName) {
	this.txtName = txtName;
    }

    String getTXTName() {
	return txtName;
    }

    void setXMLName(String xmlName) {
	this.xmlName = xmlName;
    }

    String getXMLName() {
	return xmlName;
    }

    void addRequiredField(String attributeName) {
	requiredFields.add(new FieldDescriptor(attributeName));
    }

    List getRequiredFields() {
	return requiredFields;
    }

    void addOptionalField(String attributeName, String txtName) {
	optionalFields.add(new FieldDescriptor(attributeName, txtName));
    }

    void addOptionalField(String attributeName) {
	optionalFields.add(new FieldDescriptor(attributeName));
    }

    List getOptionalFields() {
	return optionalFields;
    }

    Set getAllowedChildren() {
	return allowedChildren;
    }
    
    void setAllowedChildren(Set allowedChildren) {
	this.allowedChildren = allowedChildren;
    }

    void setExample(String example) {
	this.example = example;
    }

    String getExample() {
	return example;
    }
    
    /**
     * Adds a new element to the one passed in. If the
     * content passed in is null no new element is added.
     * @param root the element to add the new one to
     * @param newElementName the name of the new element to add
     * @param newElementContent the content for the new element.
     */
    private void addElement(Element root,
			   String newElementName,
			   String newElementContent) {

	if (newElementContent == null)
	    return;

	Element e = new Element(newElementName);
	e.addContent(newElementContent);
	root.addContent(e);
    }
    
    /**
     * Output an xml representation of this statement descriptor.
     * @param name the name of the token recognized for this statement
     * @param root the element to which the representation should
     * be added.
     */
    void toXML(String name, Element root) {

	Element e = new Element("statement");

	addElement(e, "txtName", name);
	
	addElement(e, "elementName", getXMLName());

	if (requiredFields.size() > 0) {
	    Element req = new Element("requiredFields");
	    addFields(req, requiredFields);
	    e.addContent(req);
	}

	if (optionalFields.size() > 0) {
	    Element opt = new Element("optionalFields");
	    addFields(opt, optionalFields);
	    e.addContent(opt);
	}

	String ex = getExample();
	if (ex != null) {
	    Element example = new Element("example");
	    example.addContent(new CDATA(ex));
	    e.addContent(example);
	}
	
	root.addContent(e);
    }

    private void addFields(Element root, List fields) {
	for (Iterator it = fields.iterator(); it.hasNext();) {
	    ((FieldDescriptor)it.next()).toXML(root);
	}
    }
    
    class FieldDescriptor {

	private String attributeName = null;
	private String txtName = null;

	private FieldDescriptor(String attributeName) {
	    this.attributeName = attributeName;
	}
	
	private FieldDescriptor(String attributeName, String txtName) {
	    this.attributeName = attributeName;
	    this.txtName = txtName;
	}

	String getAttributeName() {
	    return attributeName;
	}

	String getTXTName() {
	    return txtName;
	}

	void toXML(Element root) {

	    Element e = new Element("field");
	    
	    addElement(e, "txtName", getTXTName());

	    addElement(e, "attributeName", getAttributeName());

	    root.addContent(e);
	}
    }
}
