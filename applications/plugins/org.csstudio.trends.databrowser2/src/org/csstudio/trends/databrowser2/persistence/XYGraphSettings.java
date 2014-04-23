package org.csstudio.trends.databrowser2.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.csstudio.swt.xygraph.figures.XYGraph;

/**
 * XML DTO for {@link XYGraph}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
@XmlRootElement
public class XYGraphSettings {

	final public static String TAG_NAME = "xyGraphSettings";

	private String title;
	private String titleFont;
	private ColorSettings titleColor;
	private ColorSettings plotAreaBackColor;
	private boolean showTitle;
	private boolean showLegend;
	private boolean showPlotAreaBorder;
	private boolean transparent;

	private List<AnnotationSettings> annotationSettingsList;
	private List<AxisSettings> axisSettingsList;
	private List<TraceSettings> traceSettingsList;

	public XYGraphSettings() {
		annotationSettingsList = new ArrayList<AnnotationSettings>();
		axisSettingsList = new ArrayList<AxisSettings>();
		traceSettingsList = new ArrayList<TraceSettings>();
	}

	public void addAnnotationSettings(AnnotationSettings settings) {
		annotationSettingsList.add(settings);
	}

	public void addAxisSettings(AxisSettings settings) {
		axisSettingsList.add(settings);
	}

	public void addTraceSettings(TraceSettings settings) {
		traceSettingsList.add(settings);
	}

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

	public ColorSettings getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(ColorSettings titleColor) {
		this.titleColor = titleColor;
	}

	public ColorSettings getPlotAreaBackColor() {
		return plotAreaBackColor;
	}

	public void setPlotAreaBackColor(ColorSettings plotAreaBackColor) {
		this.plotAreaBackColor = plotAreaBackColor;
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

	public List<AnnotationSettings> getAnnotationSettingsList() {
		return annotationSettingsList;
	}

	public void setAnnotationSettingsList(
			List<AnnotationSettings> annotationSettingsList) {
		this.annotationSettingsList = annotationSettingsList;
	}

	public List<AxisSettings> getAxisSettingsList() {
		return axisSettingsList;
	}

	public void setAxisSettingsList(List<AxisSettings> axisSettingsList) {
		this.axisSettingsList = axisSettingsList;
	}

	public List<TraceSettings> getTraceSettingsList() {
		return traceSettingsList;
	}

	public void setTraceSettingsList(List<TraceSettings> traceSettingsList) {
		this.traceSettingsList = traceSettingsList;
	}

}
