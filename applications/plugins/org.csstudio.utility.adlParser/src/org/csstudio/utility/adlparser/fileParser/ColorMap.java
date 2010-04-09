package org.csstudio.utility.adlparser.fileParser;

import java.util.ArrayList;

import org.csstudio.utility.adlparser.internationalization.Messages;
import org.eclipse.swt.graphics.RGB;

public class ColorMap {
	private RGB[] colors = new RGB[0];
	private int numColors;
	
	public ColorMap(ADLWidget colorMap) throws WrongADLFormatException {
		// find out the number of colors
		
		for (FileLine fileLine : colorMap.getBody()){
			String bodyPart = fileLine.getLine();
			String[] row = bodyPart.trim().split("=");
			if (row.length < 2){
				throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin + bodyPart + Messages.Label_WrongADLFormatException_Parameter_End);
			}
			if (row[0].trim().equals("ncolors")){
				numColors = Integer.parseInt(row[1].replaceAll("\"", "").trim());
			}
			colors = new RGB[numColors];
		}
			

		ADLWidget colorsWidget = colorMap.getObjects().get(0);
		if (colorsWidget.getType().equals("colors")){
			int colorCounter = 0;
			for (FileLine fileLine  : colorsWidget.getBody()){
				String bodyPart = fileLine.getLine();
				String[] row = bodyPart.split(",");
				if (row.length == 1){
					String color = row[0].trim();
					colors[colorCounter] = getRGBColor(color);
					colorCounter++;
				}
			}
			
		}
	}

	/** 
	 * @return Object[] colors
	 */
	public RGB[] getColors() {
		return colors;
	}

	public int getNumColors() {
		return numColors;
	}
	public RGB getRGBColor(String inColor){
		String redStr = "#".concat(inColor.substring(0, 2));
		String greenStr = "#".concat(inColor.substring(2, 4));
		String blueStr = "#".concat(inColor.substring(4, 6));
		int red = Integer.decode(redStr);
		int green = Integer.decode(greenStr);
		int blue = Integer.decode(blueStr);
		System.out.println("Red, Green, Blue:"+ red +", " + green + ", " +blue);
		return new RGB(red, green, blue);
	}
}
