package com.siondream.core.physics;

import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.siondream.core.Env;

public class CategoryBitsManager {
	private Logger logger;
	private ObjectMap<String, Short> categoryBits;
	private ObjectMap<Short, String> categoryNames;
	private int nextCategory;
	
	public CategoryBitsManager() {
		logger = new Logger("CategoryBitsManager", Env.debugLevel);
		logger.info("initialising");
		
		categoryBits = new ObjectMap<String, Short>();
		categoryNames = new ObjectMap<Short, String>();
		nextCategory = 0;
	}
	
	public short getCategoryBits(String name) {
		if (name.length() == 0) {
			return 0;
		}
		
		Short category = categoryBits.get(name);
		
		if (category == null) {
			if (nextCategory >= 16) {
				logger.error("maximum number of collision categories reached");
				return 0;
			}
			else {
				short newCategory = (short)(1 << (nextCategory++));
				categoryBits.put(name, newCategory);
				categoryNames.put(newCategory, name);
				logger.info("registering category " + name + " => " + newCategory);
				return newCategory;
			}
		}
		
		return category;
	}
	
	public String getCategoryName(short category) {
		if (category == 0) {
			return "";
		}
		
		String name = categoryNames.get(category);
		
		if (name == null) {
			logger.error("category for bits " + category + " does not exist");
			return "";
		}
		
		return name;
	}
}
