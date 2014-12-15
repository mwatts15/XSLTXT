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
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

public class NamespaceTracker {

    private static final Category logCat =
	Logger.getLogger(NamespaceTracker.class);

    private HashMap namespaceMappings = new HashMap();

    boolean pushPrefixMapping(String prefix, String namespace) {
	if (logCat.isDebugEnabled())
	    logCat.debug("pushPrefixMapping(" +
			 prefix + ", " +
			 namespace + ")");

	if (namespace == null)
	    namespace = "";
	
	boolean newMapping = false;
	List mappings = (List)namespaceMappings.get(prefix);
	if (mappings == null) {
	    newMapping = true;
	    mappings = new ArrayList();
	    namespaceMappings.put(prefix, mappings);
	} else if (mappings.isEmpty()) {
	    newMapping = true;
	    mappings.add(namespace);
	} else {
	    String currentNamespace =
		(String)mappings.get(mappings.size() - 1);
	    if (currentNamespace == null) {
		if (namespace != null) {
		    newMapping = true;
		}
	    } else {
		if ((namespace == null) ||
		    !currentNamespace.equals(namespace)) {
		    newMapping = true;
		}
	    }
	}
	mappings.add(namespace);
	return newMapping;
    }

    void popPrefixMapping(String prefix) {
	if (logCat.isDebugEnabled())
	    logCat.debug("popPrefixMapping(" +
			 prefix + ")");
	List mappings = (List)namespaceMappings.get(prefix);
	mappings.remove(mappings.size() - 1);
    }

    String getNamespace(String prefix) {

	String namespace = null;
	List mappings = (List)namespaceMappings.get(prefix);
	if ((mappings == null) || mappings.isEmpty())
	    namespace = "";
	else
	    namespace = (String)mappings.get(mappings.size() - 1);
	
	if (logCat.isDebugEnabled())
	    logCat.debug("getNamespace(" + prefix + ") => " + namespace);

	return namespace;
    }
}
