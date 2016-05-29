package de.tuberlin.aec.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class represents the Path configuration file.
 * It reads and parses the configuration and gives access to its contents.
 * 
 */
public class PathConfiguration {

	public static final String LINK_TYPE_QUORUM = "quorum";
	public static final String LINK_TYPE_SYNC = "sync";
	public static final String LINK_TYPE_ASYNC = "async";

	private String configFile;
	
	Map<String, Map<String, List<PathLink>>> config;

	/**
	 * constructs a new PathConfiguration using the given config File 
	 * on the file system.
	 * @param configFile
	 */
	public PathConfiguration(String configFile) {
		this.configFile = configFile;
		config = new HashMap<String, Map<String, List<PathLink>>>();
		parse();
	}
	
	/**
	 * parses the configuration file
	 */
	private void parse() {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc;
			if(configFile.startsWith("http://")) {
				
				URL url = new URL(configFile); 
			    InputStream in = url.openStream(); 
				doc = builder.parse(in);
			} else {

				File xmlFile = new File(this.configFile);
				doc = builder.parse(xmlFile);
			}

			doc.getDocumentElement().normalize();
			
			NodeList paths = doc.getDocumentElement().getChildNodes();

			for (int i = 0; i < paths.getLength(); i++) {
				Node path = paths.item(i);
				if (path.getNodeType() == Node.ELEMENT_NODE) {

					Element pathElement = (Element) path;
					
					parseStartNode(pathElement);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		} catch (SAXException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	private void parseStartNode(Element pathElement) {

		String start = pathElement.getAttribute("start");

		Map<String, List<PathLink>> startNodeConfig = new HashMap<String, List<PathLink>>();
		config.put(start, startNodeConfig);

		NodeList links = pathElement.getChildNodes();

		for (int k = 0; k < links.getLength(); k++) {
			Node link = links.item(k);
			if (link.getNodeType() == Node.ELEMENT_NODE) {
				Element linkElement = (Element) link;
				
				parseLink(linkElement, startNodeConfig);
				
			}
		}
	}
	private void parseLink(Element linkElement, Map<String, List<PathLink>> startNodeConfig) {

		String src = linkElement.getAttribute("src");
		String type = linkElement.getAttribute("type");
		
		List<PathLink> list = startNodeConfig.get(src);
		if(list == null) {
			list = new ArrayList<PathLink>();
			startNodeConfig.put(src, list);
		}
		
		if(type.equals(PathConfiguration.LINK_TYPE_SYNC)) {
			String target = linkElement.getAttribute("target");
			PathLink pathLink = new PathLink(PathConfiguration.LINK_TYPE_SYNC, src, target);
			list.add(pathLink);
			
		} else if(type.equals(PathConfiguration.LINK_TYPE_ASYNC)) {
			String target = linkElement.getAttribute("target");
			PathLink pathLink = new PathLink(PathConfiguration.LINK_TYPE_ASYNC, src, target);
			list.add(pathLink);
		

		} else if(type.equals(PathConfiguration.LINK_TYPE_QUORUM)) {
			// TODO
			
		} else {
			// TODO config file error
	        System.out.println( "Configuration Error - Invalid link type: " + type );
		}
	}

	public List<String> getNodeNeighbours(String startNode, String node) {

		List<String> neighbours = new ArrayList<String>();
		Map<String, List<PathLink>> linksOfStartNode = config.get(startNode);
		if(linksOfStartNode != null) {
			List<PathLink> links = linksOfStartNode.get(node);
			if(links != null) {
				for(PathLink link : links) {
					neighbours.add(link.getTarget());
				}
			}
		}
		return neighbours;
		
	}

	public List<PathLink> getSyncNodePathLinks(String startNode, String node) {
		return getNodePathLinksByType(startNode, node, LINK_TYPE_SYNC);
	}
	public List<PathLink> getAsyncNodePathLinks(String startNode, String node) {
		return getNodePathLinksByType(startNode, node, LINK_TYPE_ASYNC);
	}

	private List<PathLink> getNodePathLinksByType(String startNode, String node, String type) {

		List<PathLink> links = new ArrayList<PathLink>();
		
		Map<String, List<PathLink>> linksOfStartNode = config.get(startNode);
		if(linksOfStartNode != null) {
			List<PathLink> allLinks = linksOfStartNode.get(node);
			if(allLinks != null) {
				for(PathLink link : allLinks) {
					if(link.getType().equals(type)) {
						links.add(link);
					}
				}
			}
		}
		return links;
		
	}
}
