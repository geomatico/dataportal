package org.dataportal.csw;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class CSWNamespaceContext implements NamespaceContext{

	@Override
	public String getNamespaceURI(String prefix) {
		String uri;
		if (prefix.equals("gmd"))
			uri = "http://www.isotc211.org/2005/gmd";
		else if (prefix.equals("gco"))
			uri = "http://www.isotc211.org/2005/gco";
		else if (prefix.equals("gmi"))
			uri = "http://www.isotc211.org/2005/gmi";
		else if (prefix.equals("gts"))
			uri = "http://www.isotc211.org/2005/gts";
		else if (prefix.equals("xsi"))
			uri = "http://www.w3.org/2001/XMLSchema-instance";
		else if (prefix.equals("geonet"))
			uri = "http://www.fao.org/geonetwork";
		else if (prefix.equals("srv"))
			uri = "http://www.isotc211.org/2005/srv";
		else if (prefix.equals("csw"))
			uri = "http://www.opengis.net/cat/csw/2.0.2";
		else
			uri = null;
		return uri;
	}

	@Override
	public String getPrefix(String namespaceURI) {
		String prefix;
		if (namespaceURI.equals("http://www.isotc211.org/2005/gmd"))
			prefix = "gmd";
		else if (namespaceURI.equals("http://www.isotc211.org/2005/gco"))
			prefix = "gco";
		else if (namespaceURI.equals("http://www.isotc211.org/2005/gmi"))
			prefix = "gmi";
		else if (namespaceURI.equals("http://www.isotc211.org/2005/gts"))
			prefix = "gts";
		else if (namespaceURI.equals("http://www.w3.org/2001/XMLSchema-instance"))
			prefix = "xsi";
		else if (namespaceURI.equals("http://www.fao.org/geonetwork"))
			prefix = "geonet";
		else if (namespaceURI.equals("http://www.isotc211.org/2005/srv"))
			prefix = "srv";
		else if (namespaceURI.equals("http://www.opengis.net/cat/csw/2.0.2"))
			prefix = "csw";
		else
			prefix = null;
		
		return prefix;
	}

	@Override
	public Iterator getPrefixes(String namespaceURI) {
		// dummy implement not used
		return null;
	}

}
