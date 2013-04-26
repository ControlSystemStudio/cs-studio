package org.csstudio.trends.sscan.model;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class XYGraphSettings {

	private String Title;
	private FontData titleFontData;
	
	private RGB titleColor;
	
	//private Color plotAreaBackColor;
	
	private boolean showTitle;
	private boolean showLegend;
	private boolean showPlotAreaBorder;
	private boolean transparent;
	
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}

	public FontData getTitleFontData() {
		return titleFontData;
	}
	public void setTitleFontData(FontData titleFontData) {
		this.titleFontData = titleFontData;
	}
	public RGB getTitleColor() {
		return titleColor;
	}
	public void setTitleColor(RGB titleColor) {
		this.titleColor = titleColor;
	}
	public boolean isShowTitle() {
		return showTitle;
	}
	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}
	public boolean isShowLegend() {
		return showLegend;
	}
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}
	public boolean isShowPlotAreaBorder() {
		return showPlotAreaBorder;
	}
	public void setShowPlotAreaBorder(boolean showPlotAreaBorder) {
		this.showPlotAreaBorder = showPlotAreaBorder;
	}
	public boolean isTransparent() {
		return transparent;
	}
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

}
