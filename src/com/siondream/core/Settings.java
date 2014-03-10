package com.siondream.core;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;

/**
 * @class Settings
 * @author David Saltares Márquez
 * @date 02/09/2012
 * 
 * @brief Holds configuration in a key value fashion
 * 
 * It accepts the following format:
 * 
 * @code
	<?xml version="1.0" encoding="UTF-8"?>
	<settings>
		<string key="appName" value="Sion Engine"
		<int key="virtualWidth" value="1280" />
		<float key="ppm" value="30.0" />
		<vector key="gravity" x="0.0" y="12.0" z="0.0" />
		<bool key="drawBodies" value="true" />
	</settings>
 * @endcode
 */
public class Settings {
	private final Logger logger = new Logger("Settings", Logger.ERROR);
	
	private String settingsFile;
	
	private HashMap<String, String> strings = new HashMap<String, String>();
	private HashMap<String, Float> floats = new HashMap<String, Float>();
	private HashMap<String, Integer> ints = new HashMap<String, Integer>();
	private HashMap<String, Boolean> booleans = new HashMap<String, Boolean>();
	private HashMap<String, Vector3> vectors = new HashMap<String, Vector3>();
	
	/**
	 * Loads the default settings file: data/settings.xml
	 */
	public Settings() {
		this("data/settings.xml");
	}
	
	/**
	 * @param settings configuration file to load
	 */
	public Settings(String settings) {
		this.settingsFile = settings;
		loadSettings();
	}
	
	/**
	 * @param key setting key
	 * @return setting value in string form
	 */
	public String getString(String key) {
		return getString(key, "");
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value in string form
	 */
	public String getString(String key, String defaultValue) {
		String string = strings.get(key);
		
		if (string != null) {
			return string;
		}
		
		logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * @param key setting key 
	 * @return setting value as a float
	 */
	public float getFloat(String key) {
		return getFloat(key, 0.0f);
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value as a float
	 */
	public float getFloat(String key, float defaultValue) {
		Float f = floats.get(key);
		
		if (f != null) {
			return f;
		}
		
		logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * @param key setting key 
	 * @return setting value as an int
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value as an int
	 */
	public int getInt(String key, int defaultValue) {
		Integer i = ints.get(key);
		
		if (i != null) {
			return i;
		}
		
		logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * @param key setting key 
	 * @return setting value as a boolean
	 */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value as a boolean
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		Boolean b = booleans.get(key);
		
		if (b != null) {
			return b;
		}
		
		logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * @param key setting key 
	 * @return setting value as a Vector3
	 */
	public Vector3 getVector(String key) {
		return getVector(key, Vector3.Zero.cpy());
	}
	
	/**
	 * @param key setting key
	 * @param defaultValue default value in case the key doesn't exist 
	 * @return setting value as a Vector3
	 */
	public Vector3 getVector(String key, Vector3 defaultValue) {
		Vector3 v = vectors.get(key);
		
		if (v != null) {
			return new Vector3(v);
		}
		
		logger.error(key + " not found, returning default " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setString(String key, String value) {
		strings.put(key, value);
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setFloat(String key, float value) {
		floats.put(key, value);
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setInt(String key, int value) {
		ints.put(key, value);
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setBoolean(String key, boolean value) {
		booleans.put(key, value);
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setVector(String key, Vector3 value) {
		vectors.put(key, new Vector3(value));
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public String getFile() {
		return settingsFile;
	}
	
	/**
	 * Modifies or creates a new setting with the given key
	 * 
	 * @param key key to identify the setting
	 * @param value value of the setting
	 */
	public void setFile(String settingsFile) {
		this.settingsFile = settingsFile;
	}
	
	/**
	 * Reloads all the settings trying to use the external storage by default
	 */
	public void loadSettings() {
		loadSettings(true);
	}
	
	/**
	 * Reloads all the settings
	 * 
	 * @param tryExternal if true, it looks for an external settings file first (not in the app internal storage).
	 * Note that external files won't be taken into account in WebGL applications.
	 */
	public void loadSettings(boolean tryExternal) {
		logger.info("Settings: loading file " + settingsFile);
		
		try {
			FileHandle fileHandle = null;
			
			if (tryExternal &&
				Gdx.app.getType() != Application.ApplicationType.WebGL &&
				Gdx.files.external(settingsFile).exists()) {
				fileHandle = Gdx.files.external(settingsFile);
				logger.info("Settings: loading as external file");
			}
			else {
				fileHandle = Gdx.files.internal(settingsFile);
				logger.info("Settings: loading as internal file");
			}
			
			XmlReader reader = new XmlReader();
			Element root = reader.parse(fileHandle);

			// Load strings
			strings.clear();
			Array<Element> stringNodes = root.getChildrenByName("string");
			
			for (int i = 0; i < stringNodes.size; ++i) {
				Element stringNode = stringNodes.get(i);
				String key = stringNode.getAttribute("key");
				String value = stringNode.getAttribute("value");
				strings.put(key, value);
				logger.info("Settings: loaded string " + key + " = " + value);
			}
			
			// Load floats
			floats.clear();
			Array<Element> floatNodes = root.getChildrenByName("float");
			
			for (int i = 0; i < floatNodes.size; ++i) {
				Element floatNode = floatNodes.get(i);
				String key = floatNode.getAttribute("key");
				Float value = Float.parseFloat(floatNode.getAttribute("value"));
				floats.put(key, value);
				logger.info("Settings: loaded float " + key + " = " + value);
			}
			
			// Load ints
			ints.clear();
			Array<Element> intNodes = root.getChildrenByName("int");
			
			for (int i = 0; i < intNodes.size; ++i) {
				Element intNode = intNodes.get(i);
				String key = intNode.getAttribute("key");
				Integer value = Integer.parseInt(intNode.getAttribute("value"));
				ints.put(key, value);
				logger.info("Settings: loaded int " + key + " = " + value);
			}
			
			// Load booleans
			booleans.clear();
			Array<Element> boolNodes = root.getChildrenByName("bool");
			
			for (int i = 0; i < boolNodes.size; ++i) {
				Element boolNode = boolNodes.get(i);
				String key = boolNode.getAttribute("key");
				Boolean value = Boolean.parseBoolean(boolNode.getAttribute("value"));
				booleans.put(key, value);
				logger.info("Settings: loaded boolean " + key + " = " + value);
			}
			
			// Load vectors
			vectors.clear();
			Array<Element> vectorNodes = root.getChildrenByName("vector");
			
			for (int i = 0; i < vectorNodes.size; ++i) {
				Element vectorNode = vectorNodes.get(i);
				String key = vectorNode.getAttribute("key");
				Float x = Float.parseFloat(vectorNode.getAttribute("x"));
				Float y = Float.parseFloat(vectorNode.getAttribute("y"));
				Float z = Float.parseFloat(vectorNode.getAttribute("z"));
				vectors.put(key, new Vector3(x, y, z));
				logger.info("Settings: loaded vector " + key + " = (" + x + ", " + y + ", " + z + ")");
			}
			
			logger.info("Settings: successfully finished loading settings");
		}
		catch (Exception e) {
			logger.error("Settings: error loading file: " + settingsFile + " " + e.getMessage());
		}
	}
	
	/**
	 *	Saves settings as an external file. If called from a WebGL application, nothing will be done.
	 */
	public void saveSettings() {
		if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
			logger.info("Settings: saving file " + settingsFile);
			XmlWriter xml;
			
			try {
				StringWriter writer = new StringWriter();
				xml = new XmlWriter(writer);
				
				// Create root
				xml = xml.element("settings");
				
				// Create string nodes
				for (Entry<String, String> entry : strings.entrySet()) {
					xml = xml.element("string");
					xml.attribute("key", entry.getKey());
					xml.attribute("value", entry.getValue());
					xml = xml.pop();
				}
				
				// Create float nodes
				for (Entry<String, Float> entry : floats.entrySet()) {
					xml = xml.element("float");
					xml.attribute("key", entry.getKey());
					xml.attribute("value", entry.getValue());
					xml = xml.pop();
				}
				
				// Create int nodes
				for (Entry<String, Integer> entry : ints.entrySet()) {
					xml = xml.element("int");
					xml.attribute("key", entry.getKey());
					xml.attribute("value", entry.getValue());
					xml = xml.pop();
				}
				
				// Create boolean nodes
				for (Entry<String, Boolean> entry : booleans.entrySet()) {
					xml = xml.element("bool");
					xml.attribute("key", entry.getKey());
					xml.attribute("value", entry.getValue());
					xml = xml.pop();
				}
				
				// Create vector nodes
				for (Entry<String, Vector3> entry : vectors.entrySet()) {
					xml = xml.element("vector");
					xml.attribute("key", entry.getKey());
					Vector3 vector = entry.getValue();
					xml.attribute("x", vector.x);
					xml.attribute("y", vector.y);
					xml.attribute("z", vector.z);
					xml = xml.pop();
				}
				
				xml = xml.pop();
				Gdx.files.external(settingsFile).writeString(writer.toString(), true);
				xml.close();
				
				logger.info("Settings: successfully saved");
			}
			catch (Exception e) {
				logger.error("Settings: error saving file " + settingsFile);
			}
		}
		else {
			logger.error("Settings: saving feature not supported in HTML5");
		}
	}
}
