package org.yaoqiang.bpmn.editor.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class EditorConstants {

	/**
	 * YAOQIANG_CONF_FILE
	 */
	public static final String YAOQIANG_RECENT_FILES = ".filelist";

	/**
	 * YAOQIANG_ARTIFACTS_DIR
	 */
	public static final String YAOQIANG_ARTIFACTS_DIR = "artifacts";

	/**
	 * RESOURCES_URI
	 */
	public static final String RESOURCES_URI = "org/yaoqiang/bpmn/editor/resources/";

	/**
	 * LAST_OPEN_DIR
	 */
	public static String LAST_OPEN_DIR = System.getProperty("user.dir");

	/**
	 * LAST_FILLCOLOR
	 */
	public static List<Color> LAST_FILLCOLOR = new ArrayList<Color>();

	/**
	 * COLORS
	 */
	public static final Color[] COLORS = new Color[] { Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLUE, Color.RED, Color.CYAN, Color.MAGENTA,
			Color.GREEN, Color.ORANGE, Color.YELLOW, Color.PINK, Color.WHITE };

}
