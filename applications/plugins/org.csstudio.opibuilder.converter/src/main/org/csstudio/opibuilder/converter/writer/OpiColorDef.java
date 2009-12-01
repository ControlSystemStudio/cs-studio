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
 * Object receives the EdmColorsList object and outputs the color.def data in specified file name.
 * @author Matevz
 *
 */
public class OpiColorDef {

	public static void writeDefFile(EdmColorsList cList, String fileName) throws EdmException {
		
		File colorDefFile = new File(fileName);
		
		try {
			
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(colorDefFile));
			
			for (String id : cList.getAttributeIdSet()) {
				EdmColor c = cList.getColor(Integer.parseInt(id));
				out.write(c.getName());
				out.write(" = " + OpiColor.colorComponentTo8Bits(c.getRed()));
				out.write(", " + OpiColor.colorComponentTo8Bits(c.getGreen()));
				out.write(", " + OpiColor.colorComponentTo8Bits(c.getBlue()));
				out.write("\r\n");
			}
		
			out.close();
			
		} catch (FileNotFoundException e) {
			throw new EdmException(EdmException.FILE_NOT_FOUND, colorDefFile.getName());
		} catch (UnsupportedEncodingException e) {
			throw new EdmException(EdmException.OPI_WRITER_EXCEPTION, "Unsupported encoding.");
		} catch (IOException e) {
			throw new EdmException(EdmException.OPI_WRITER_EXCEPTION, "Error when writing color.def file.");
		}
	}
	
}
