/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmColorsList;
import org.csstudio.opibuilder.converter.model.EdmException;

/**
 * Class that writes OPI color definition file.
 * @author Matevz
*/
public class OpiColorDef {

	/**
	 * Receives the EdmColorsList object and outputs the color.def data in specified file name.
	 */
	public static void writeDefFile(EdmColorsList cList, String fileName) throws EdmException {
		
		File colorDefFile = new File(fileName);
		
		try {
			
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(colorDefFile));
			
			for (int colorInd = 0; colorInd < cList.getMenuColorCount(); colorInd++) {
				EdmColor color = cList.getMenuColor(colorInd);
				
				out.write(color.getName());
				out.write(" = " + OpiColor.colorComponentTo8Bits(color.getRed()));
				out.write(", " + OpiColor.colorComponentTo8Bits(color.getGreen()));
				out.write(", " + OpiColor.colorComponentTo8Bits(color.getBlue()));
				out.write("\r\n");
			}
		
			out.close();
			
		} catch (FileNotFoundException e) {
			throw new EdmException(EdmException.FILE_NOT_FOUND, colorDefFile.getName(), e);
		} catch (UnsupportedEncodingException e) {
			throw new EdmException(EdmException.OPI_WRITER_EXCEPTION, "Unsupported encoding.",e);
		} catch (IOException e) {
			throw new EdmException(EdmException.OPI_WRITER_EXCEPTION, "Error when writing color.def file.",e);
		}
	}
	
}
