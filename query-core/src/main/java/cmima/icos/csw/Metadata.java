/**
 * 
 */
package cmima.icos.csw;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cmima.icos.utils.BBox;

/**
 * @author Micho Garcia
 * 
 */
public class Metadata implements IMetadata {

	private static Logger logger = Logger.getLogger(Metadata.class);

	private Document cswXml;
	private XPath xpath;

	/**
	 * @throws Exception
	 * 
	 */
	public Metadata(InputStream responseCSWXml) throws Exception {
		super();		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		cswXml = dBuilder.parse(responseCSWXml);
		cswXml.getDocumentElement().normalize();

		CSWNamespaceContext ctx = new CSWNamespaceContext();
		
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		xpath.setNamespaceContext(ctx);

		logger.debug("CREATED METADATA");
	}

	/**
	 * 
	 * Extract from metadata the xpath expresion and return the node value
	 * 
	 * @param xpathExpr
	 * @return node value (String)
	 */
	private String getMetadata(String xpathExpr) {
		String metadataAbstract = "";

		try {
			Node abstractNode = (Node) xpath.evaluate(xpathExpr, cswXml,
					XPathConstants.NODE);
			metadataAbstract = abstractNode.getNodeValue();

		} catch (XPathExpressionException eXpath) {
			logger.error("ERROR: " + eXpath.getMessage());
		} finally {
			xpath.reset();
		}
		return metadataAbstract;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmima.icos.csw.IMetadata#getExtent()
	 */
	@Override
	public BBox getExtent() {

		String[] coords = { "westBoundLongitude", "southBoundLatitude",
				"eastBoundLongitude", "northBoundLatitude" };
		String iniExpresion = "//EX_GeographicBoundingBox//";
		String endExpresion = "/Decimal/child::node()";
		String[] extent = new String[4];

		try {
			for (int nCoord = 0; nCoord < coords.length; nCoord++) {
				String expresion = iniExpresion + coords[nCoord] + endExpresion;
				Node tmpNode = (Node) xpath.evaluate(expresion, cswXml,
						XPathConstants.NODE);
				extent[nCoord] = tmpNode.getNodeValue();
			}

		} catch (XPathExpressionException eXpath) {
			logger.error("ERROR: " + eXpath.getMessage());
		} finally {
			xpath.reset();
		}

		BBox extentMetadata = new BBox(extent);

		return extentMetadata;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmima.icos.csw.IMetadata#getTemporalExtent()
	 */
	@Override
	public String[] getTemporalExtent() {

		String[] temporalExtent = new String[2];

		String xpathStartDate = "//EX_TemporalExtent//beginPosition/child::node()";
		String xpathEndDate = "//EX_TemporalExtent//endPosition/child::node()";

		try {
			Node startNode = (Node) xpath.evaluate(xpathStartDate, cswXml,
					XPathConstants.NODE);
			Node endNode = (Node) xpath.evaluate(xpathEndDate, cswXml,
					XPathConstants.NODE);

			temporalExtent[0] = startNode.getNodeValue();
			temporalExtent[1] = endNode.getNodeValue();

		} catch (XPathExpressionException eXpath) {
			logger.error("ERROR: " + eXpath.getMessage());
		} finally {
			xpath.reset();
		}

		logger.debug("TEMPORAL EXTENT: star_date " + temporalExtent[0]
				+ " ;end_date " + temporalExtent[1]);

		return temporalExtent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmima.icos.csw.IMetadata#getVariables()
	 */
	@Override
	public String[] getVariables() {

		ArrayList<String> variables = new ArrayList<String>();
		String xpathVariables = "//MD_Band//MemberName/aName/CharacterString/child::node()";

		try {
			NodeList variablesXML = (NodeList) xpath.evaluate(xpathVariables,
					cswXml, XPathConstants.NODESET);
			for (int nNode = 0; nNode < variablesXML.getLength(); nNode++) {
				variables.add(variablesXML.item(nNode).getNodeValue());
			}
		} catch (XPathExpressionException eXpath) {
			logger.error("ERROR: " + eXpath.getMessage());
		} finally {
			xpath.reset();
		}

		String[] metadataVariables = new String[variables.size()];
		variables.toArray(metadataVariables);
		logger.debug("VARIABLES: " + variables.size());

		return metadataVariables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmima.icos.csw.IMetadata#getTitle()
	 */
	@Override
	public String getTitle() {

		String xpathTitle = "//identificationInfo/MD_DataIdentification/citation/CI_Citation/title/CharacterString/child::node()";
		String metadataTitle = getMetadata(xpathTitle);
		logger.debug("TITLE; " + metadataTitle);

		return metadataTitle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmima.icos.csw.IMetadata#getSummary()
	 */
	@Override
	public String getAbstract() {

		String xpathAbstract = "//identificationInfo/MD_DataIdentification/abstract/CharacterString/child::node()";
		String metadataAbstract = getMetadata(xpathAbstract);
		logger.debug("ABSTRACT; " + metadataAbstract);

		return metadataAbstract;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmima.icos.csw.IMetadata#getSchema()
	 */
	@Override
	public String getSchema() {

		String xpathSchema = "//metadataStandardName/CharacterString/child::node()";
		String metadataSchema = getMetadata(xpathSchema);
		logger.debug("SCHEMA; " + metadataSchema);

		return metadataSchema;
	}

}
