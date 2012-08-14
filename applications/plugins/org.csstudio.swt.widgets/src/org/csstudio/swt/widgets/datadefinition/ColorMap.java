/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.datadefinition;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

/**Color Map data type.
 * @author Xihui Chen
 *
 */
public class ColorMap {
	
	public enum PredefinedColorMap{
		None("None", new double[0], new RGB[0]),
		GrayScale("GrayScale", new double[]{0,1}, new RGB[]{new RGB(0,0,0), new RGB(255,255,255)}),
		JET("JET", new double[]{0, 0.111, 0.365, 0.619, 0.873, 1},
			new RGB[]{new RGB(0,0,143), new RGB(0,0,255), new RGB(0,255,255),
					  new RGB(255,255,0), new RGB(255,0,0), new RGB(128,0,0)}),
		ColorSpectrum("ColorSpectrum", new double[]{0, 0.126, 0.251, 0.375, 0.5, 0.625, 0.749, 0.874,1},
			new RGB[]{new RGB(0,0,0), new RGB(255,0,255), new RGB(0,0,255),
					  new RGB(0,255,255), new RGB(0,255,0), new RGB(255,255,0),
					  new RGB(255,128,0), new RGB(255,0,0), new RGB(255,255,255)}),
		Hot("Hot", new double[]{0, 0.365, 0.746, 1},
			new RGB[]{new RGB(11,0,0), new RGB(255,0,0), new RGB(255,255,0), new RGB(255,255,255)}),
		Cool("Cool", new double[]{0,1}, new RGB[]{new RGB(0,255,255), new RGB(255,0,255)}),
		Shaded("Shaded", new double[]{0, 0.5, 1},
			new RGB[]{new RGB(0,0,0), new RGB(255,0,0), new RGB(255,255,255)});
		String name;
		double[] values;
		RGB[] colors;
		private PredefinedColorMap(String name, double[] values, RGB[] colors){
			this.name = name;
			this.values = values;
			this.colors = colors;
		}
		
		public LinkedHashMap<Double, RGB> getMap() {
			LinkedHashMap<Double, RGB> map = new LinkedHashMap<Double, RGB>();
			for(int i=0; i<values.length; i++){
				map.put(values[i], colors[i]);
			}
			return map;
		}
		
		public static String[] getStringValues(){
			String[] result = new String[values().length];
			int i =0;
			for(PredefinedColorMap m : values())
				result[i++] = m.name;
			return result;
		}
		
		public static int toIndex(PredefinedColorMap p){
			return Arrays.asList(values()).indexOf(p);
		}
		
		public static PredefinedColorMap fromIndex(int index){
			return Arrays.asList(values()).get(index);
		}
		
		
		@Override
		public String toString() {
			return name;
		}
		
		
	}
	
	private LinkedHashMap<Double, RGB> colorMap;
	private PredefinedColorMap predefinedColorMap;
	private boolean autoScale;
	private boolean interpolate;	
	private RGB[] colorsLookupTable;
	private int[] pixelLookupTable;
	private PaletteData palette = new PaletteData(0xff, 0xff00, 0xff0000);
	private double colorMapMin;
	private double colorMapMax;
	
	public ColorMap() {
		colorMap = new LinkedHashMap<Double, RGB>();
		setAutoScale(true);
		setInterpolate(true);	
		predefinedColorMap = PredefinedColorMap.None;
	}
	
	public ColorMap(PredefinedColorMap predefinedColorMap, boolean autoScale, boolean interpolate){
		setAutoScale(autoScale);
		setInterpolate(interpolate);
		setPredefinedColorMap(predefinedColorMap);
	}
	
	
	/**
	 * @return the map which back up the ColorMap 
	 */
	public LinkedHashMap<Double, RGB> getMap() {
		return colorMap;
	}

	
	/**Set a new map.
	 * @param colorMap the new map.
	 */
	public void setColorMap(LinkedHashMap<Double, RGB> colorMap) {
		this.colorMap = colorMap;
		this.predefinedColorMap = PredefinedColorMap.None;
		colorsLookupTable = null;
	}

	/**
	 * @param autoScale the autoScale to set
	 */
	public void setAutoScale(boolean autoScale) {
		this.autoScale = autoScale;
		colorsLookupTable = null;		
	}


	/**
	 * @return the autoScale
	 */
	public boolean isAutoScale() {
		return autoScale;
	}


	/**
	 * @param interpolate the interpolate to set
	 */
	public void setInterpolate(boolean interpolate) {
		this.interpolate = interpolate;
	}


	/**
	 * @return the interpolate
	 */
	public boolean isInterpolate() {
		return interpolate;
	}


	/**
	 * @param predefinedColorMap the predefinedColorMap to set
	 */
	public void setPredefinedColorMap(PredefinedColorMap predefinedColorMap) {
		this.predefinedColorMap = predefinedColorMap;
		if(predefinedColorMap != PredefinedColorMap.None)
			colorMap = predefinedColorMap.getMap();
		colorsLookupTable = null;
	}


	/**
	 * @return the predefinedColorMap
	 */
	public PredefinedColorMap getPredefinedColorMap() {
		return predefinedColorMap;
	}
	
	@Override
	public String toString() {
		if(predefinedColorMap != null && predefinedColorMap != PredefinedColorMap.None)
			return predefinedColorMap.toString();
		else 
			return "Customized";
	}
	
	
	/**Calculate the image data from source data based on the color map.
	 * @param dataArray the source data
	 * @param dataWidth number of columns of dataArray; This will be the width of image data.
	 * @param dataHeight number of rows of dataArray; This will be the height of image data.
	 * @param max the upper limit of the data in dataArray
	 * @param min the lower limit of the data in dataArray
	 * @param imageData the imageData to be filled. null if a new instance should be created.
	 * @param shrink true if area size of image data is smaller than dataWidth*dataHeight. If this is true, it will use
	 * the nearest neighbor iamge scaling algorithm as described at http://tech-algorithm.com/articles/nearest-neighbor-image-scaling/.
	 * @return the image data. null if dataWidth or dataHeight is less than 1.
	 */
	public ImageData drawImage(IPrimaryArrayWrapper dataArray, 
			int dataWidth, int dataHeight, double max, double min, ImageData imageData, boolean shrink){
		if(dataWidth <1 || dataHeight < 1 || dataWidth *dataHeight > dataArray.getSize()|| dataWidth * dataHeight < 0)
			return null;
		if(imageData == null)
			imageData = new ImageData(dataWidth,dataHeight, 24, palette);	
		if(colorsLookupTable == null)
			getColorsLookupTable();
		
		if(!autoScale){
			min = colorMapMin;
			max = colorMapMax;
		}
			if(shrink){				
				int height = imageData.height;
				int width = imageData.width;
				    // EDIT: added +1 to account for an early rounding problem
				    int x_ratio = (int)((dataWidth<<16)/width) +1;
				    int y_ratio = (int)((dataHeight<<16)/height) +1;
				    //int x_ratio = (int)((w1<<16)/w2) ;
				    //int y_ratio = (int)((h1<<16)/h2) ;
				    int x2, y2 ;
				    for (int i=0;i<height;i++) {
				        for (int j=0;j<width;j++) {
				            x2 = ((j*x_ratio)>>16) ;
				            y2 = ((i*y_ratio)>>16) ;
				            int index = (int) ((dataArray.get(y2 * dataWidth + x2) - min) / (max - min) * 255);
							if (index < 0)
								index = 0;
							else if (index > 255)
								index = 255;
							int pixel = pixelLookupTable[index];
				            imageData.setPixel(j,i,pixel); ;
				        }                
				    }  
				
					
			}else{
				for (int y = 0; y < dataHeight; y++) {
					for (int x = 0; x < dataWidth; x++) {
						// the index of the value in the color table array
						int index = (int) ((dataArray.get(y * dataWidth + x) - min) / (max - min) * 255);
						if (index < 0)
							index = 0;
						else if (index > 255)
							index = 255;
						int pixel = pixelLookupTable[index];
						imageData.setPixel(x, y, pixel);
					}
				}
			}
				
			
			return imageData;
	}
		
	/**Calculate the image data from source data based on the color map.
	 * @param dataArray the source data
	 * @param dataWidth number of columns of dataArray; This will be the width of image data.
	 * @param dataHeight number of rows of dataArray; This will be the height of image data.
	 * @param max the upper limit of the data in dataArray
	 * @param min the lower limit of the data in dataArray
	 * @param imageData the imageData to be filled. null if a new instance should be created.
	 * @return the image data. null if dataWidth or dataHeight is less than 1.
	 */
	public ImageData drawImage(double[] dataArray, 
			int dataWidth, int dataHeight, double max, double min){
		return drawImage(new DoubleArrayWrapper(dataArray), dataWidth, dataHeight, max, min, null, false);
	}
	/**
	 * @param colorTupleArray
	 * @param keyArray
	 * @param value the value which has been scaled or not based on the autoScale flag.
	 * @param min
	 * @param max
	 * @return
	 */
	public RGB getValueRGB(ColorTuple[] colorTupleArray, double[] keyArray, double value){
		
		int insertPoint = Arrays.binarySearch(keyArray, value);
		if(insertPoint >= 0)
			return colorTupleArray[insertPoint].rgb;
		else{			
			insertPoint = -insertPoint -1;
			if(insertPoint == 0)
				return colorTupleArray[0].rgb;
			if(insertPoint == colorTupleArray.length)
				return colorTupleArray[colorTupleArray.length -1].rgb;
			return getInterpolateRGB(colorTupleArray[insertPoint-1], colorTupleArray[insertPoint], value);
		}	
	}	

	
	private RGB getInterpolateRGB(ColorTuple start, ColorTuple end, double value){
		if(interpolate){
			double f = (value - start.value)/(end.value - start.value);			
			int r =(int) ((end.rgb.red - start.rgb.red)*f + start.rgb.red);
			int g =(int) ((end.rgb.green - start.rgb.green)*f + start.rgb.green);
			int b =(int) ((end.rgb.blue - start.rgb.blue)*f + start.rgb.blue);
			return new RGB(r,g,b);
		}else
			return start.rgb;
	}

	/**Get a colors lookup table from 0 to 255. This only works for autoScale is true;
	 * @return the colorsLookupTable a array of 256 colors corresponding to the value from min to max
	 */
	public RGB[] getColorsLookupTable() {
		if(colorsLookupTable == null){
			//convert map to array to simplify the calculation
			ColorTuple[] colorTupleArray = new ColorTuple[colorMap.size()];
			
			int i=0;
			for(Double k : colorMap.keySet()){			
				colorTupleArray[i++]=new ColorTuple(k, colorMap.get(k));
			}
			
			//sort the array
			Arrays.sort(colorTupleArray);
			colorMapMin = colorTupleArray[0].value;
			colorMapMax = colorTupleArray[colorTupleArray.length-1].value;			
			if(autoScale)
				for(ColorTuple t : colorTupleArray){
					t.value = (t.value - colorMapMin)/(colorMapMax-colorMapMin);
				}
			
			double[] keyArray = new double[colorTupleArray.length];
			for(int j = 0; j<colorTupleArray.length; j++)
				keyArray[j] = colorTupleArray[j].value;
			
			colorsLookupTable = new RGB[256];
			pixelLookupTable = new int[256];
			for(int k=0; k<256; k++){				
				colorsLookupTable[k] = getValueRGB(colorTupleArray, keyArray, autoScale? k/255.0 : colorMapMin + k*(colorMapMax-colorMapMin)/255.0);
				pixelLookupTable[k] = palette.getPixel(colorsLookupTable[k]);
			}			
		}
			
		return colorsLookupTable;
	}
	
	public PaletteData getPalette() {
		return palette;
	}
	
	
}
