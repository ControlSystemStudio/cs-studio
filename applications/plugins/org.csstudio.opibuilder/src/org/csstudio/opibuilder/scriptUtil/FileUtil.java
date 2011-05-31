/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**The Utility class to help file operating.
 * @author Xihui Chen
 *
 */
public class FileUtil {


	/**Load the root element of an XML file. The element is a JDOM Element.
	 * @param filePath path of the file. It can be an absolute path or a relative path to 
	 * the OPI that contains the specified widget. If it is an absolute path, it can be either<br>
	 * a workspace path such as <code>/BOY Examples/Scripts/myfile.xml</code><br> a local file
	 * system path such as <code>C:\myfile.xml</code><br> or an URL path such as 
	 * <code>http://mysite.com/myfile.xml</code>.
	 * @param widget a widget in the OPI, which is used to provide relative path reference. It 
	 * can be null if the path is an absolute path.
	 * @return the root element of the XML file.
	 * @throws Exception if the file does not exist or is not a correct XML file. 
	 */
	public static Element loadXMLFile(String filePath, AbstractBaseEditPart widget) throws Exception{	
		IPath path = buildAbsolutePath(filePath, widget);
		InputStream inputStream = ResourceUtil.pathToInputStream(path);
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(inputStream);
		Element root = doc.getRootElement();
		return root;
	}

	
	/**Return an {@link InputStream} of the file on the speicifed path.
	 * The client is responsible for closing the stream when finished.
	 * @param filePath path of the file. It can be an absolute path or a relative path to 
	 * the OPI that contains the specified widget. If it is an absolute path, it can be either<br>
	 * a workspace path such as <code>/BOY Examples/Scripts/myfile.xml</code><br> a local file
	 * system path such as <code>C:\myfile.xml</code><br> or an URL path such as 
	 * <code>http://mysite.com/myfile.xml</code>.
	 * @param widget a widget in the OPI, which is used to provide relative path reference. It 
	 * can be null if the path is an absolute path.
	 * @return java.io.InputStream of the file.
	 * @throws Exception if the file does not exist. 
	 */
	public static InputStream getInputStreamFromFile(String filePath, AbstractBaseEditPart widget) throws Exception{
		IPath path = buildAbsolutePath(filePath, widget);
		return ResourceUtil.pathToInputStream(path);
	}
	
	//TODO: unfinished.
	public static String readTextFile(String filePath, AbstractBaseEditPart widget) throws Exception{
		InputStream inputStream = getInputStreamFromFile(filePath, widget);
		BufferedReader reader
		   = new BufferedReader(new InputStreamReader(inputStream));
		return "";
	}
	
	//TODO: unfinished. should be able to deal with workspace file.
	public static void writeTextFile(String filePath, 
			AbstractBaseEditPart widget, String text, boolean append) throws IOException{
		IPath path = buildAbsolutePath(filePath, widget);
		FileWriter fileWriter = new FileWriter(path.toString());
		if(!append)
			fileWriter.flush();
		fileWriter.write(text);
		fileWriter.close();
	}
	
	
	protected static IPath buildAbsolutePath(String filePath,
			AbstractBaseEditPart widget) {
		IPath path = ResourceUtil.getPathFromString(filePath);
		if(!path.isAbsolute())
			path = ResourceUtil.buildAbsolutePath(widget.getWidgetModel(), path);
		return path;
	}
	
}
