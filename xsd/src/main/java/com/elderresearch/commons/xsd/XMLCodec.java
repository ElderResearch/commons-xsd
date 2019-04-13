/*******************************************************************************
 * Copyright (c) 2016 Elder Research, Inc.
 * All rights reserved.
 *******************************************************************************/
package com.elderresearch.commons.xsd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML reader and writer using code generated by JAXB. This will read and write
 * your root element to and from XML files or streams.
 * 
 * @author <a href="mailto:dimeo@datamininglab.com">John Dimeo</a>
 * @param <T> the root element type
 * @since Jan 15, 2016
 */
public class XMLCodec<T> {
	// Declared in MarshallerImpl, but is protected
	protected static final String PREFIX_MAPPER = "com.sun.xml.bind.namespacePrefixMapper";
	
	private Class<T> c;
	private JAXBContext context;
	private Binder<Node> binder;
	private String[] predeclaredNamespaceURIs;
	
	/**
	 * Create a new codec.
	 * @param c the root element class
	 * @param predeclaredNamespaceURIs optional pairs of prefixes and namespace URIs to declare at the top of marshalled
	 * XML files. This potentially avoids unnecessary redundant and nested namespace declarations.
	 * @throws IOException if there was a problem instantiating the JAXB context
	 */
	public XMLCodec(Class<T> c, String... predeclaredNamespaceURIs) throws IOException {
		this.c = c;
		this.predeclaredNamespaceURIs = predeclaredNamespaceURIs;
		try {
			context = JAXBContext.newInstance(c);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Load and parse the contents of an XML file to a new root element instance.
	 * @param f the file to parse
	 * @return the parsed root element
	 * @throws IOException if there was a problem parsing the file
	 */
	public T load(File f) throws IOException {
		try (FileInputStream fis = new FileInputStream(f);
		     InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
			InputSource src = new InputSource(isr);
			src.setSystemId(f.getAbsolutePath());
			return load(src);
		}
	}
	
	/**
	 * Load and parse the contents of an XML input source to a new root element instance.
	 * Ensure that the source has a valid system ID if you use relative paths in
	 * your XML.
	 * @param src the source to parse
	 * @return the parsed root element
	 * @throws IOException if there was a problem parsing the source
	 */
	public T load(InputSource src) throws IOException {
		try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setValidating(true);
	        dbf.setXIncludeAware(true);
	        dbf.setNamespaceAware(true);
	        
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document document = db.parse(src);
			
			binder = context.createBinder();
			return binder.unmarshal(document, c).getValue();
		} catch (JAXBException | SAXException | ParserConfigurationException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Save the root element to a file.
	 * @param elem the root element to save
	 * @param f the destination file
	 * @throws IOException if there was a problem saving the file
	 */
	public void save(JAXBElement<T> elem, File f) throws IOException {
		saveToFile(elem, f);
	}
	
	/**
	 * Save the root element to a file. Note that the type <tt>T</tt> must
	 * have been generated with a {@link XmlRootElement} annotation to use
	 * this method. Otherwise, use the generated <tt>ObjectFactory.create...()</tt>
	 * method to wrap your element first and use {@link #save(JAXBElement, File)}
	 * instead.
	 * @param elem the root element to save
	 * @param f the destination file
	 * @throws IOException if there was a problem saving the file
	 */
	public void save(T elem, File f) throws IOException {
		saveToFile(elem, f);
	}
	
	/**
	 * Save the root element to an output stream.
	 * @param elem the root element to save
	 * @param os the destination output stream
	 * @throws IOException if there was a problem saving to the output stream
	 */
	public void save(JAXBElement<T> elem, OutputStream os) throws IOException {
		saveToStream(elem, os);
	}

	/**
	 * Save the root element to an output stream. Note that the type <tt>T</tt> must
	 * have been generated with a {@link XmlRootElement} annotation to use
	 * this method. Otherwise, use the generated <tt>ObjectFactory.create...()</tt>
	 * method to wrap your element first and use {@link #save(JAXBElement, OutputStream)}
	 * instead.
	 * @param elem the root element to save
	 * @param os the destination output stream
	 * @throws IOException if there was a problem saving to the output stream
	 */
	public void save(T elem, OutputStream os) throws IOException {
		saveToStream(elem, os);
	}
	
	private void saveToFile(Object elem, File f) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(f)) {
			saveToStream(elem, fos);
		}
	}
	
	private void saveToStream(Object elem, OutputStream os) throws IOException {
		try {
			binder.updateXML(elem);

	        TransformerFactory tf = TransformerFactory.newInstance();
	        
	        Transformer t = tf.newTransformer();
	        customizeTransformer(t);
	        
	        t.transform(new DOMSource(binder.getXMLNode(elem)), new StreamResult(os));
		} catch (JAXBException | TransformerException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Customize (set properties on) the marshaller used by this codec. By default,
	 * formatting ("pretty printing") is turned on and UTF encoding is used.
	 * @param m the marshaller
	 * @throws JAXBException if there was a problem customizing the marshaller
	 */
	protected void customizeTransformer(Transformer t) throws JAXBException {
		t.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		
		if (predeclaredNamespaceURIs.length > 0) {
			m.setProperty(PREFIX_MAPPER, new PredeclareNamespaceURIs(predeclaredNamespaceURIs));
		}
	}
	
	/**
	 * Gets the namespace of the JAXB-annotated package.
	 * @param p the package
	 * @return the namespace, or <tt>null</tt> if the package does not have an {@link XmlSchema} annotation
	 */
	public static String getNS(Package p) {
		XmlSchema schema = p.getAnnotation(XmlSchema.class);
		return schema == null? null : schema.namespace();
	}
}