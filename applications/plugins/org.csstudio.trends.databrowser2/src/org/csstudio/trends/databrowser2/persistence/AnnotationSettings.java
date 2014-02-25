package org.csstudio.trends.databrowser2.persistence;

import org.csstudio.swt.xygraph.figures.Annotation;

/**
 * XML DTO for {@link Annotation}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class AnnotationSettings {

	private String name;
	private boolean free;
	private int xAxis, yAxis;
	private int trace;
	private ColorSettings annotationColor;
	private String font;
	private String cursorLineStyle;
	private boolean showName;
	private boolean showSampleInfo;
	private boolean showPosition;
	private double yValue;
	private double xValue;
	
	public double getXValue() {
		return xValue;
	}
	
	public void setXValue(double xValue) {
		this.xValue = xValue;
	}
	
	public double getYValue() {
		return yValue;
	}
	
	public void setYValue(double yValue) {
		this.yValue = yValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public int getxAxis() {
		return xAxis;
	}

	public void setxAxis(int xAxis) {
		this.xAxis = xAxis;
	}

	public int getyAxis() {
		return yAxis;
	}

	public void setyAxis(int yAxis) {
		this.yAxis = yAxis;
	}

	public int getTrace() {
		return trace;
	}

	public void setTrace(int trace) {
		this.trace = trace;
	}

	public ColorSettings getAnnotationColor() {
		return annotationColor;
	}

	public void setAnnotationColor(ColorSettings annotationColor) {
		this.annotationColor = annotationColor;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getCursorLineStyle() {
		return cursorLineStyle;
	}

	public void setCursorLineStyle(String cursorLineStyle) {
		this.cursorLineStyle = cursorLineStyle;
	}

	public boolean isShowName() {
		return showName;
	}

	public void setShowName(boolean showName) {
		this.showName = showName;
	}

	public boolean isShowSampleInfo() {
		return showSampleInfo;
	}

	public void setShowSampleInfo(boolean showSampleInfo) {
		this.showSampleInfo = showSampleInfo;
	}

	public boolean isShowPosition() {
		return showPosition;
	}

	public void setShowPosition(boolean showPosition) {
		this.showPosition = showPosition;
	}

}
