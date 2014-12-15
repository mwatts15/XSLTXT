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

public class Output
    extends Statement 
    implements Cloneable {

    private static final Category logCat =
	Logger.getLogger(Output.class);

    final static String IDENT = OUTPUT;
    
    static StatementDescriptor desc = new StatementDescriptor();

    static {
	desc.setTXTName(IDENT);
	desc.setXMLName("output");
	
	desc.addOptionalField("method", METHOD);
	desc.addOptionalField("version", VERSION);
	desc.addOptionalField("encoding", ENCODING);
	desc.addOptionalField("omit-xml-declaration", OMIT_XML_DECLARATION);
	desc.addOptionalField("standalone", STANDALONE);
	desc.addOptionalField("doctype-public", DOCTYPE_PUBLIC);
	desc.addOptionalField("doctype-system", DOCTYPE_SYSTEM);
	desc.addOptionalField("cdata-section-elements", CDATA_SECTION_ELEMENTS);
	desc.addOptionalField("indent", INDENT);
	desc.addOptionalField("media-type", MEDIA_TYPE);
    }

    Output() {
	super(desc);
    }
}
