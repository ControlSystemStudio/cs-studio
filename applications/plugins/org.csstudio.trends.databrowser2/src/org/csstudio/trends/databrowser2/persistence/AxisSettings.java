package org.csstudio.trends.databrowser2.persistence;

import org.csstudio.swt.xygraph.figures.Axis;

/**
 * XML DTO for {@link Axis}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class AxisSettings {

	private String title;
	private String titleFont;
	private String scaleFont;
	private ColorSettings foregroundColor;
	private boolean onPrimarySide;
	private boolean logScale;
	private boolean autoScale;
	private double autoScaleThreshold;
	private RangeSettings range;
	private boolean dateEnabled;
	private boolean autoFormat;
	private String formatPattern;
	private boolean showMajorGrid;
	private boolean dashGridLine;
	private ColorSettings majorGridColor;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleFont() {
		return titleFont;
	}

	public void setTitleFont(String titleFont) {
		this.titleFont = titleFont;
	}
	
	public String getScaleFont() {
		return scaleFont;
	}
	
	public void setScaleFont(String scaleFont) {
		this.scaleFont = scaleFont;
	}

	public ColorSettings getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(ColorSettings foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	public boolean isOnPrimarySide() {
		return onPrimarySide;
	}

	public void setOnPrimarySide(boolean onPrimarySide) {
		this.onPrimarySide = onPrimarySide;
	}

	public boolean isLogScale() {
		return logScale;
	}

	public void setLogScale(boolean logScale) {
		this.logScale = logScale;
	}

	public boolean isAutoScale() {
		return autoScale;
	}

	public void setAutoScale(boolean autoScale) {
		this.autoScale = autoScale;
	}

	public double getAutoScaleThreshold() {
		return autoScaleThreshold;
	}

	public void setAutoScaleThreshold(double autoScaleThreshold) {
		this.autoScaleThreshold = autoScaleThreshold;
	}

	public RangeSettings getRange() {
		return range;
	}

	public void setRange(RangeSettings range) {
		this.range = range;
	}

	public boolean isDateEnabled() {
		return dateEnabled;
	}

	public void setDateEnabled(boolean dateEnabled) {
		this.dateEnabled = dateEnabled;
	}

	public boolean isAutoFormat() {
		return autoFormat;
	}

	public void setAutoFormat(boolean autoFormat) {
		this.autoFormat = autoFormat;
	}

	public String getFormatPattern() {
		return formatPattern;
	}

	public void setFormatPattern(String formatPattern) {
		this.formatPattern = formatPattern;
	}

	public boolean isShowMajorGrid() {
		return showMajorGrid;
	}

	public void setShowMajorGrid(boolean showMajorGrid) {
		this.showMajorGrid = showMajorGrid;
	}

	public boolean isDashGridLine() {
		return dashGridLine;
	}

	public void setDashGridLine(boolean dashGridLine) {
		this.dashGridLine = dashGridLine;
	}

	public ColorSettings getMajorGridColor() {
		return majorGridColor;
	}

	public void setMajorGridColor(ColorSettings majorGridColor) {
		this.majorGridColor = majorGridColor;
	}

}
