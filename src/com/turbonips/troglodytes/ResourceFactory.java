package com.turbonips.troglodytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.tiled.TiledMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.turbonips.troglodytes.Resource.ResourceType;

public class ResourceFactory {
	private final Logger logger = Logger.getLogger(getClass());
	private static final ResourceFactory instance = new ResourceFactory();
	private HashMap<String, Element> resourceElements = new HashMap<String, Element>();

	private ResourceFactory() {
		try {
			loadResources(new FileInputStream(new File("resources.xml")), false);
		} catch (FileNotFoundException e) {
			logger.fatal(e);
		} catch (SlickException e) {
			logger.fatal(e);
		}
	}

	public static ResourceFactory getInstance() {
		return instance;
	}
	
	

	public Resource create(String id) throws SlickException {
		Element resourceElement = resourceElements.get(id.toLowerCase());
		if (resourceElement == null) throw new RuntimeException("Cannot find ID " + id + " in XML document");
		String type = resourceElement.getAttribute("type").toLowerCase();
		String path = "resources/" + resourceElement.getAttribute("path");
		try {
			path = new File(path).getCanonicalPath();
		} catch (IOException e) {
			logger.error(e);
		}
		
		if (type.equals("image")) {
			return new Resource(id, ResourceType.IMAGE, path, new Image(path));
		} else if (type.equals("tiledmap")) {
			return new Resource(id, ResourceType.TILED_MAP, path, new TiledMap(path));
		} else if (type.equals("spritesheet")) {
			int width = Integer.valueOf(resourceElement.getAttribute("width"));
			int height = Integer.valueOf(resourceElement.getAttribute("height"));
			return new Resource(id, ResourceType.SPRITE_SHEET, path, new SpriteSheet(path, width, height));
		} else if (type.equals("creatureanimation")) {
			int width = Integer.valueOf(resourceElement.getAttribute("width"));
			int height = Integer.valueOf(resourceElement.getAttribute("height"));
			return new Resource(id, ResourceType.CREATURE_ANIMATION, path, new CreatureAnimation(path, width, height));
		} else if (type.equals("music")) {
			return new Resource(id, ResourceType.MUSIC, path, new Music(path));
		} else if (type.equals("sound")) {
			return new Resource(id, ResourceType.SOUND, path, new Sound(path));
		} else if (type.equals("font")) {
			UnicodeFont unicodeFont = new UnicodeFont(path, 70, false, false);
			unicodeFont.getEffects().add(new ColorEffect(java.awt.Color.white));
			
			return new Resource(id, ResourceType.FONT, path, unicodeFont);

//			String TGApath = "resources/" + String.valueOf(resourceElement.getAttribute("TGApath"));
//			return new Resource(id, ResourceType.FONT, path, new AngelCodeFont(, false));
		}
		
		return null;
	}

	private void loadResources(InputStream is, boolean deferred)
			throws SlickException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new SlickException("Could not load resources", e);
		}
		Document doc = null;
		try {
			doc = docBuilder.parse(is);
		} catch (SAXException e) {
			throw new SlickException("Could not load resources", e);
		} catch (IOException e) {
			throw new SlickException("Could not load resources", e);
		}

		// normalize text representation
		doc.getDocumentElement().normalize();

		NodeList listResources = doc.getElementsByTagName("resource");

		int totalResources = listResources.getLength();

		if (deferred) {
			LoadingList.setDeferredLoading(true);
		}

		for (int resourceIdx = 0; resourceIdx < totalResources; resourceIdx++) {

			Node resourceNode = listResources.item(resourceIdx);

			if (resourceNode.getNodeType() == Node.ELEMENT_NODE) {
				Element resourceElement = (Element) resourceNode;
				String id = resourceElement.getAttribute("id");
				resourceElements.put(id.toLowerCase(), resourceElement);
			}
		}

	}

	public ArrayList<String> getResourceIds(String resId) {
		ArrayList<String> resourceIds = new ArrayList<String>();
		
		for (Element resourceElement : resourceElements.values()) {
			String type = resourceElement.getAttribute("type").toLowerCase();
			
			if (type.equals(resId)) {
				String id = resourceElement.getAttribute("id").toLowerCase();
				resourceIds.add(id);
			}
		}
		
		return resourceIds;
	}

}
