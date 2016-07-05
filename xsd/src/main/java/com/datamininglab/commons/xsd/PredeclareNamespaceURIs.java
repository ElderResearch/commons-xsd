/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package com.datamininglab.commons.xsd;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Namespace prefix mapper implementation that allows convenient declaration of predeclared namespace prefix/URI pairs.
 * 
 * @author <a href="mailto:dimeo@datamininglab.com">John Dimeo</a>
 * @see NamespacePrefixMapper#getPreDeclaredNamespaceUris2()
 * @since Jul 5, 2016
 */
class PredeclareNamespaceURIs extends NamespacePrefixMapper {
	private String[] pairs;
	
	PredeclareNamespaceURIs(String... pairs) {
		this.pairs = pairs;
	}
	
	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
		return suggestion;
	}
	
	@Override
	public String[] getPreDeclaredNamespaceUris2() {
		return pairs;
	}
}
