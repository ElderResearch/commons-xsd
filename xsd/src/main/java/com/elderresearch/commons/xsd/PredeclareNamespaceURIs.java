/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package com.elderresearch.commons.xsd;

import org.eclipse.persistence.internal.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.internal.oxm.record.namespaces.PrefixMapperNamespaceResolver;
import org.eclipse.persistence.oxm.NamespaceResolver;


/**
 * Namespace prefix mapper implementation that allows convenient declaration of predeclared namespace prefix/URI pairs.
 * 
 * @author <a href="mailto:dimeo@datamininglab.com">John Dimeo</a>
 * @see NamespacePrefixMapper#getPreDeclaredNamespaceUris2()
 * @since Jul 5, 2016
 */
class PredeclareNamespaceURIs extends PrefixMapperNamespaceResolver {
	public PredeclareNamespaceURIs(NamespacePrefixMapper mapper, NamespaceResolver nestedResolver) {
		super(mapper, nestedResolver);
		// TODO Auto-generated constructor stub
	}

	private String[] pairs;
	
	/*PredeclareNamespaceURIs(String... pairs) {
		this.pairs = pairs;
	}
	
	
	
	
	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
		return suggestion;
	}
	
	
	@Override
	public String[] getPreDeclaredNamespaceUris2() {
		return pairs;
	}*/
}
