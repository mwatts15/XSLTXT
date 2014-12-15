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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

public class ElementToken
    extends Token {

    private static final Category logCat =
	Logger.getLogger(ElementToken.class);

    private HashMap attributes = new HashMap();
    
    ElementToken(int indent, int line, String value) {
	super(indent, line, value);
    }

    /**
     * Add an attribute to the set being maintained by this token.
     * @param name the name of the attribute
     * @param value its value
     */
    void addAttribute(String name, String value) {
	if (logCat.isDebugEnabled())
	    logCat.debug("addAttribute(" +
			 name +
			 ", " +
			 value +
			 ")");
	attributes.put(name, value);
    }

    Map getAttributes() {
	return attributes;
    }
    
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("E ");
	sb.append(line);
	sb.append('.');
	sb.append(indent);
	sb.append(" : ");
	sb.append(value);
	for (Iterator it = attributes.entrySet().iterator(); it.hasNext();) {
	    Map.Entry me = (Map.Entry)it.next();
	    sb.append(' ');
	    sb.append(me.getKey());
	    sb.append("=\"");
	    sb.append(me.getValue());
	    sb.append('"');
	}
	
	return sb.toString();
    }

}
