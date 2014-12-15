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

public class Attribute
    extends StatementWithContent 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(Attribute.class);

    final static String IDENT = ATTRIBUTE;

    static StatementDescriptor desc = new StatementDescriptor();

    static {
	desc.setTXTName(IDENT);
	desc.setXMLName("attribute");

	desc.addRequiredField("name");
	
	desc.addOptionalField("namespace", NAMESPACE);

	desc.setAllowedChildren(charTemplateChildren);

	desc.setExample("<a>\n" +
			"  attribute \"href\"\n" +
			"    call \"href.target\" (\"object\":\"$target[1]\")");
    }
    
    Attribute() {
	super(desc);
    }
}
