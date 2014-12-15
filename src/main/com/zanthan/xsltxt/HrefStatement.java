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

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

public abstract class HrefStatement
    extends Statement {

    private static final Category logCat =
	Logger.getLogger(HrefStatement.class);

    int source = -1;
    
    HrefStatement(StatementDescriptor desc) {
	super(desc);
    }

    void setRequiredValue(int source, String attributeName,
			  int attributeIndex, String value) {
	this.source = source;
	super.setRequiredValue(source, attributeName, attributeIndex, value);
    }

    String getRequiredValue(int target, String attributeName,
			    int attributeIndex) {
	String s = super.getRequiredValue(target, attributeName, attributeIndex);
	if (target != source) {
	    switch (target) {
	    case XML:
		s = convertTXTtoXSL(s);
		break;
	    case TXT:
		s = convertXSLtoTXT(s);
		break;
	    }
	}
	return s;
    }

    String convertTXTtoXSL(String s) {
	if (logCat.isDebugEnabled())
	    logCat.debug("convertTXTtoXSL(" + s + ")");

	if (s.endsWith(".txt"))
	    s = s.substring(0, s.length() - 4) + ".xsl";
	return s;
    }

    String convertXSLtoTXT(String s) {
	if (logCat.isDebugEnabled())
	    logCat.debug("convertXSLtoTXT(" + s + ")");

	if (s.endsWith(".xsl"))
	    s = s.substring(0, s.length() - 4) + ".txt";
	return s;
    }

    public Object clone() 
	throws CloneNotSupportedException {
	
	HrefStatement h = (HrefStatement)super.clone();
	h.source = -1;
	return h;
    }
}
