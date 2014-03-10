package com.siondream.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;

public class Env {
	// Game façade
	public static SionGame game;
	public static PlatformResolver platform;
	
	// Application config
	public static float virtualWidth;
	public static float virtualHeight;
	public static float aspectRatio;
	public static float metresToPixels;
	public static float pixelsToMetres;
	public static boolean catchBack;
	public static boolean catchMenu;
	public static Color backgroundColor;
	
	// Physics
	public static Vector2 gravity;
	public static boolean doSleep;
	public static int velocityIterations;
	public static int positionIterations;
	public static float physicsDeltaTime;
	
	// Particles
	public static int particlePoolInitialCapacity;
	public static int particlePoolMaxCapacity;
	
	// Debug
	public static int debugLevel;
	public static boolean debug;
	public static boolean drawBodies;
	public static boolean drawJoints;
	public static boolean drawABBs;
	public static boolean drawInactiveBodies;
	public static boolean drawVelocities;
	public static boolean drawContacts;
	public static boolean drawStage;
	public static boolean drawGrid;
	public static boolean drawFPS;
	public static boolean drawViewportBuilder;
	
	private static String TAG = "Globals";
	private static Settings settings;
	private static Logger logger = new Logger(TAG, Logger.INFO);
	
	public static void init(SionGame game) {
		logger.info("initialising");
		
		Env.game = game;
		
		settings = new Settings("data/config/globals.xml");
		
		virtualWidth = settings.getFloat("virtualWidth", 1280.0f);
		virtualHeight = settings.getFloat("virtualHeight", 720.0f);
		aspectRatio = virtualWidth / virtualHeight;
		metresToPixels = settings.getFloat("metresToPixels", 64);
		pixelsToMetres = 1.0f / metresToPixels;
		catchBack = settings.getBoolean("catchBack", true);
		catchMenu = settings.getBoolean("catchMenu", true);
		catchMenu = settings.getBoolean("catchMenu", true);
		Vector3 background = settings.getVector("backgroundColor", Vector3.Zero);
		backgroundColor = new Color(background.x, background.y, background.z, 1.0f);
		
		Vector3 gravity3 = settings.getVector("gravity", Vector3.Zero); 
		gravity = new Vector2(gravity3.x, gravity3.y);
		doSleep = settings.getBoolean("doSleep", false);
		velocityIterations = settings.getInt("velocityIterations", 6);
		positionIterations = settings.getInt("positionIterations", 10);
		physicsDeltaTime = settings.getFloat("physicsDeltaTime", 0.01f);
		
		particlePoolInitialCapacity = settings.getInt("particlePoolInitialCapacity", 5);
		particlePoolMaxCapacity = settings.getInt("particlePoolMaxCapacity", 20);
		
		debugLevel = settings.getInt("debugLevel", Logger.INFO);
		debug = debugLevel > Logger.ERROR;
		drawBodies = settings.getBoolean("drawBodies", false);
		drawJoints = settings.getBoolean("drawJoints", false);
		drawABBs = settings.getBoolean("drawABBs", false);
		drawInactiveBodies = settings.getBoolean("drawInactiveBodies", false);
		drawVelocities = settings.getBoolean("drawVelocities", false);
		drawContacts = settings.getBoolean("drawContacts", false);
		drawStage = settings.getBoolean("drawStage", false);
		drawGrid = settings.getBoolean("drawGrid", true);
		drawFPS = settings.getBoolean("drawFPS", true);
		drawViewportBuilder = settings.getBoolean("drawViewportBuilder", true);
	}
}
