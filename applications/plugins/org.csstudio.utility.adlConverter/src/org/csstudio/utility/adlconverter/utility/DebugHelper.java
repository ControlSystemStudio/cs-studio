package org.csstudio.utility.adlconverter.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DebugHelper {

	static Map<String, List<String>> all = new HashMap<String, List<String>>();
	static Map<String, List<String>> path = new HashMap<String, List<String>>();
	static Map<String, List<String>> withoutPath = new HashMap<String, List<String>>();
	private static File _createTempFile;
	private static FileWriter fw;
	static final private boolean DEBUG_ON = true;

	public static void add(Object clazz, String line) {
		if (DEBUG_ON) {
			String simpleName = clazz.getClass().getSimpleName();
			List<String> allList = all.get(simpleName);
			if (allList == null) {
				allList = new ArrayList<String>();
				all.put(simpleName, allList);
			}
			allList.add(line);

			if (line.contains(".adl")) {
				String simpleName2 = clazz.getClass().getSimpleName();
				List<String> pathList = path.get(simpleName2);
				if (pathList == null) {
					pathList = new ArrayList<String>();
					path.put(simpleName2, pathList);
				}
				pathList.add(line);
			} else {
				String simpleName2 = clazz.getClass().getSimpleName();
				List<String> pathList = withoutPath.get(simpleName2);
				if (pathList == null) {
					pathList = new ArrayList<String>();
					withoutPath.put(simpleName2, pathList);
				}
				pathList.add(line);

			}
		}
	}

	public static void clear() {
		if (DEBUG_ON) {
			all.clear();
			path.clear();
			try {
				_createTempFile = File.createTempFile("All", ".log");
				fw = new FileWriter(_createTempFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void showAll() {
		if (DEBUG_ON) {
			try {
				fw.append("##### Show All \r\n");
				for (String key : all.keySet()) {
					fw.append("### ");
					fw.append(key);
					fw.append("\r\n");
					for (String item : all.get(key)) {
						fw.append(item);
						fw.append("\r\n");
					}
					// LOG.error(key,
					// key+"\t"+Arrays.toString(all.get(key).toArray()));
					fw.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Path " + _createTempFile.getAbsolutePath());
		}
	}

	public static void showPath() {
		if (DEBUG_ON) {
			try {
				fw.append("##### Show Path \r\n");
				for (String key : path.keySet()) {
					fw.append("### ");
					fw.append(key);
					fw.append("\r\n");
					for (String item : path.get(key)) {
						fw.append(item);
						fw.append("\r\n");
					}
				}
				fw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// LOG.error(key,
			// key+"\t"+Arrays.toString(path
			// .get(key).toArray()));
		}
	}

	public static void showWithoutPath() {
		if (DEBUG_ON) {
			try {
				fw.append("##### Show without Path \r\n");
				for (String key : withoutPath.keySet()) {
					fw.append("### ");
					fw.append(key);
					fw.append("\r\n");
					for (String item : withoutPath.get(key)) {
						fw.append(item);
						fw.append("\r\n");
					}
				}
				fw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// LOG.error(key,
			// key+"\t"+Arrays.toString(path
			// .get(key).toArray()));
		}
	}

	public static void showAllCaller() {
		if (DEBUG_ON) {
			try {
				fw.append("##### show All Caller");
				fw.append("\r\n");
				for (String key : all.keySet()) {
					fw.append(key);
					fw.append("\r\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void showPathCaller() {
		if (DEBUG_ON) {
			try {
				fw.append("##### show Path Caller");
				fw.append("\r\n");
				for (String key : path.keySet()) {
					fw.append(key);
					fw.append("\r\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void closeLogFile() {
		if (DEBUG_ON) {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
