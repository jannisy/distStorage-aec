package de.tuberlin.aec.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * constructs a new PathConfiguration using the given config File 
	 * on the file system.
	 * @param configFile
	 */
	public PathConfiguration(String configFile) {
		this.configFile = configFile;
	}
	
	/**
	 * parses the configuration file
	 */
	public void parse() {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			File xmlFile = new File(this.configFile);

			Document doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
			NodeList paths = doc.getDocumentElement().getChildNodes();

			for (int i = 0; i < paths.getLength(); i++) {
				Node path = paths.item(i);
				if (path.getNodeType() == Node.ELEMENT_NODE) {

					Element pathElement = (Element) path;
					String start = pathElement.getAttribute("start");
					System.out.println("Found path for start=" + start);

					NodeList links = pathElement.getChildNodes();

					for (int k = 0; k < links.getLength(); k++) {
						Node link = links.item(k);
						if (link.getNodeType() == Node.ELEMENT_NODE) {
							Element linkElement = (Element) link;
							String src = linkElement.getAttribute("src");
							String type = linkElement.getAttribute("type");
							System.out.println("   Link src=" + src + ", type=" + type);
							if(type.equals(PathConfiguration.LINK_TYPE_SYNC)) {
								String target = pathElement.getAttribute("target");
								
							} else if(type.equals(PathConfiguration.LINK_TYPE_ASYNC)) {
								String target = pathElement.getAttribute("target");
							

							} else if(type.equals(PathConfiguration.LINK_TYPE_QUORUM)) {
								String target = pathElement.getAttribute("qsize");
								
							} else {
								// TODO config file error
						        System.out.println( "Error: invalid link type." );
							}
							
						}
					}
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
	
	public List<String> getNodeNeighbours(String startNode, String node) {
		List<String> neighbours = new ArrayList<String>();
		
		// TODO: this is very ugly!

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			File xmlFile = new File(this.configFile);

			Document doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();

//			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
			NodeList paths = doc.getDocumentElement().getChildNodes();

			for (int i = 0; i < paths.getLength(); i++) {
				Node path = paths.item(i);
				if (path.getNodeType() == Node.ELEMENT_NODE) {

					Element pathElement = (Element) path;
					String start = pathElement.getAttribute("start");
					if(!start.equals(startNode)) {
						continue;
					}
//					System.out.println("Found path for start=" + start);

					NodeList links = pathElement.getChildNodes();

					for (int k = 0; k < links.getLength(); k++) {
						Node link = links.item(k);
						if (link.getNodeType() == Node.ELEMENT_NODE) {
							Element linkElement = (Element) link;
							String src = linkElement.getAttribute("src");
							if(!src.equals(node)) {
								continue;
							}
							String type = linkElement.getAttribute("type");
//							System.out.println("   Link src=" + src + ", type=" + type);
							if(type.equals(PathConfiguration.LINK_TYPE_SYNC)) {
								String target = linkElement.getAttribute("target");
								neighbours.add(target);
								
							} else if(type.equals(PathConfiguration.LINK_TYPE_ASYNC)) {
								String target = linkElement.getAttribute("target");
								neighbours.add(target);
							

							} else if(type.equals(PathConfiguration.LINK_TYPE_QUORUM)) {
								// TODO target
								
							} else {
								// TODO config file error
//						        System.out.println( "Error: invalid link type." );
							}
							
						}
					}
					
					return neighbours;
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return neighbours;
		} catch (SAXException e) {
			e.printStackTrace();
			return neighbours;
		} catch (IOException e) {
			e.printStackTrace();
			return neighbours;
		}
		return neighbours;
		
	}
}
