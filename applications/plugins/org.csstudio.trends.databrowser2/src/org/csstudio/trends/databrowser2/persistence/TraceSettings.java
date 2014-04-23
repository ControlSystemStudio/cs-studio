package org.csstudio.trends.databrowser2.persistence;

import org.csstudio.swt.xygraph.figures.Trace;

/**
 * XML DTO for {@link Trace}
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class TraceSettings {

	private String name;
	private int xAxis, yAxis;
	private ColorSettings traceColor;
	private String traceType;
	private int lineWidth;
	private String pointStyle;
	private int pointSize;
	private String baseLine;
	private int areaAlpha;
	private boolean antiAliasing;
	private boolean errorBarEnabled;
	private String xErrorBarType, yErrorBarType;
	private ColorSettings errorBarColor;
	private int errorBarCapWidth;
	private boolean drawYErrorInArea;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public ColorSettings getTraceColor() {
		return traceColor;
	}

	public void setTraceColor(ColorSettings traceColor) {
		this.traceColor = traceColor;
	}

	public String getTraceType() {
		return traceType;
	}

	public void setTraceType(String traceType) {
		this.traceType = traceType;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public String getPointStyle() {
		return pointStyle;
	}

	public void setPointStyle(String pointStyle) {
		this.pointStyle = pointStyle;
	}

	public int getPointSize() {
		return pointSize;
	}

	public void setPointSize(int pointSize) {
		this.pointSize = pointSize;
	}

	public String getBaseLine() {
		return baseLine;
	}

	public void setBaseLine(String baseLine) {
		this.baseLine = baseLine;
	}

	public int getAreaAlpha() {
		return areaAlpha;
	}

	public void setAreaAlpha(int areaAlpha) {
		this.areaAlpha = areaAlpha;
	}

	public boolean isAntiAliasing() {
		return antiAliasing;
	}

	public void setAntiAliasing(boolean antiAliasing) {
		this.antiAliasing = antiAliasing;
	}

	public boolean isErrorBarEnabled() {
		return errorBarEnabled;
	}

	public void setErrorBarEnabled(boolean errorBarEnabled) {
		this.errorBarEnabled = errorBarEnabled;
	}

	public String getxErrorBarType() {
		return xErrorBarType;
	}

	public void setxErrorBarType(String xErrorBarType) {
		this.xErrorBarType = xErrorBarType;
	}

	public String getyErrorBarType() {
		return yErrorBarType;
	}

	public void setyErrorBarType(String yErrorBarType) {
		this.yErrorBarType = yErrorBarType;
	}

	public ColorSettings getErrorBarColor() {
		return errorBarColor;
	}

	public void setErrorBarColor(ColorSettings errorBarColor) {
		this.errorBarColor = errorBarColor;
	}

	public int getErrorBarCapWidth() {
		return errorBarCapWidth;
	}

	public void setErrorBarCapWidth(int errorBarCapWidth) {
		this.errorBarCapWidth = errorBarCapWidth;
	}

	public boolean isDrawYErrorInArea() {
		return drawYErrorInArea;
	}

	public void setDrawYErrorInArea(boolean drawYErrorInArea) {
		this.drawYErrorInArea = drawYErrorInArea;
	}

}
