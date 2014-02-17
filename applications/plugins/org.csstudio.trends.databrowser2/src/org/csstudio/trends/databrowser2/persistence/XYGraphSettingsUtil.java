package org.csstudio.trends.databrowser2.persistence;

import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.Annotation.CursorLineStyle;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.Trace.BaseLine;
import org.csstudio.swt.xygraph.figures.Trace.ErrorBarType;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.databrowser2.model.FontDataUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * Utility to create/restore {@link XYGraphSettings}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class XYGraphSettingsUtil {

	public static XYGraphSettings createSettings(final XYGraph xyGraph) {
		final XYGraphSettings settings = new XYGraphSettings();

		for (int i = 0; i < xyGraph.getPlotArea().getAnnotationList().size(); i++) {
			settings.addAnnotationSettings(new AnnotationSettings());
		}
		for (int i = 0; i < xyGraph.getAxisList().size(); i++) {
			settings.addAxisSettings(new AxisSettings());
		}
		for (int i = 0; i < xyGraph.getPlotArea().getTraceList().size(); i++) {
			settings.addTraceSettings(new TraceSettings());
		}

		saveXYGraphPropsToSettings(xyGraph, settings);
		return settings;
	}

	public static void saveXYGraphPropsToSettings(XYGraph xyGraph,
			XYGraphSettings settings) {
		settings.setTitle(xyGraph.getTitle());
		settings.setTitleFont(xyGraph.getTitleFont().getFontData()[0].toString());
		settings.setTitleColor(ColorSettings.fromSWT(xyGraph.getTitleColor()));
		settings.setPlotAreaBackColor(ColorSettings.fromSWT(xyGraph.getPlotArea().getBackgroundColor()));
		settings.setShowTitle(xyGraph.isShowTitle());
		settings.setShowLegend(xyGraph.isShowLegend());
		settings.setShowPlotAreaBorder(xyGraph.getPlotArea().isShowBorder());
		settings.setTransparent(xyGraph.isTransparent());
		
		int i = 0;
		for (Annotation anno : xyGraph.getPlotArea().getAnnotationList())
			saveAnnotationPropsToSettings(xyGraph, anno, settings
					.getAnnotationSettingsList().get(i++));
		i = 0;
		for (Axis axis : xyGraph.getAxisList()) {
			saveAxisPropsToSettings(axis,
					settings.getAxisSettingsList().get(i++));
		}
		i = 0;
		for (Trace trace : xyGraph.getPlotArea().getTraceList()) {
			saveTracePropsToSettings(xyGraph, trace, settings
					.getTraceSettingsList().get(i++));
		}
	}

	public static void restoreXYGraphPropsFromSettings(XYGraph xyGraph,
			XYGraphSettings settings) {
		if (settings == null)
			return;

		if (settings.getTitle() != null)
			xyGraph.setTitle(settings.getTitle());

		if (settings.getTitleFont() != null) {
			String fontInfo = settings.getTitleFont();
			if (fontInfo != null && !fontInfo.trim().isEmpty()) {
				FontData fontData = FontDataUtil.getFontData(fontInfo);
				xyGraph.setTitleFont(new Font(Display.getCurrent(), fontData));
			}
		}

		if (settings.getTitleColor() != null)
			xyGraph.setTitleColor(settings.getTitleColor().toSWT());

		if (settings.getPlotAreaBackColor() != null)
			xyGraph.getPlotArea().setBackgroundColor(
					settings.getPlotAreaBackColor().toSWT());

		xyGraph.setShowTitle(settings.isShowTitle());
		xyGraph.setShowLegend(settings.isShowLegend());
		xyGraph.getPlotArea().setShowBorder(settings.isShowPlotAreaBorder());
		xyGraph.setTransparent(settings.isTransparent());

		int i = 0;
		for (AxisSettings axisSettings : settings.getAxisSettingsList())
			restoreAxisPropsFromSettings(xyGraph.getAxisList().get(i++),
					axisSettings);
		i = 0;
		for (TraceSettings traceSettings : settings.getTraceSettingsList())
			restoreTracePropsFromSettings(xyGraph, xyGraph.getPlotArea()
					.getTraceList().get(i++), traceSettings);
		i = 0;
		for (AnnotationSettings annotationSettings : settings.getAnnotationSettingsList())
			restoreAnnotationPropsFromSettings(xyGraph, xyGraph.getPlotArea()
					.getAnnotationList().get(i++), annotationSettings);
	}

	private static void saveAnnotationPropsToSettings(XYGraph xyGraph,
			Annotation annotation, AnnotationSettings settings) {
		settings.setName(annotation.getName());
		if (annotation.isFree()) {
			settings.setFree(true);
			settings.setxAxis(xyGraph.getXAxisList().indexOf(annotation.getXAxis()));
			settings.setyAxis(xyGraph.getYAxisList().indexOf(annotation.getYAxis()));
		} else {
			settings.setFree(false);
			settings.setTrace(xyGraph.getPlotArea().getTraceList()
					.indexOf(annotation.getTrace()));
		}
		Color c = annotation.getAnnotationColor();
		settings.setAnnotationColor(c != null ? ColorSettings.fromSWT(c) : null);
		settings.setFont(annotation.getFont().getFontData()[0].toString());
		settings.setCursorLineStyle(annotation.getCursorLineStyle().name());
		settings.setShowName(annotation.isShowName());
		settings.setShowSampleInfo(annotation.isShowSampleInfo());
		settings.setShowPosition(annotation.isShowPosition());
		settings.setYValue(annotation.getYValue());
		settings.setXValue(annotation.getXValue());
	}

	private static void restoreAnnotationPropsFromSettings(XYGraph xyGraph,
			final Annotation annotation, final AnnotationSettings settings) {
		annotation.setName(settings.getName());
		if (settings.isFree()) {
			if (settings.getxAxis() >= 0 && settings.getyAxis() >= 0) {
				Axis xAxis = xyGraph.getXAxisList().get(settings.getxAxis());
				Axis yAxis = xyGraph.getYAxisList().get(settings.getyAxis());
				annotation.setFree(xAxis, yAxis);
			} else {
				return;
			}
		} else {
			Trace trace = xyGraph.getPlotArea().getTraceList().get(settings.getTrace());
			annotation.setTrace(trace);
		}
		if (settings.getAnnotationColor() != null)
			annotation.setAnnotationColor(settings.getAnnotationColor().toSWT());
		String fontInfo = settings.getFont();
		if (fontInfo != null && !fontInfo.trim().isEmpty()) {
			FontData fontData = FontDataUtil.getFontData(fontInfo);
			annotation.setFont(new Font(Display.getCurrent(), fontData));
		}
		annotation.setCursorLineStyle(CursorLineStyle.valueOf(settings.getCursorLineStyle()));
		annotation.setShowName(settings.isShowName());
		annotation.setShowSampleInfo(settings.isShowSampleInfo());
		annotation.setShowPosition(settings.isShowPosition());
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				annotation.setValues(settings.getXValue(),settings.getYValue());
			}
		});
	}

	private static void saveAxisPropsToSettings(Axis axis, AxisSettings settings) {
		settings.setTitle(axis.getTitle());
		settings.setTitleFont(axis.getTitleFont().getFontData()[0].toString());
		settings.setScaleFont(axis.getFont().getFontData()[0].toString());
		settings.setForegroundColor(ColorSettings.fromSWT(axis.getForegroundColor()));
		settings.setOnPrimarySide(axis.isOnPrimarySide());
		settings.setLogScale(axis.isLogScaleEnabled());
		settings.setAutoScale(axis.isAutoScale());
		settings.setAutoScaleThreshold(axis.getAutoScaleThreshold());
		settings.setRange(RangeSettings.fromSWT(axis.getRange()));
		settings.setDateEnabled(axis.isDateEnabled());
		settings.setAutoFormat(axis.isAutoFormat());
		settings.setFormatPattern(axis.getFormatPattern());
		settings.setShowMajorGrid(axis.isShowMajorGrid());
		settings.setDashGridLine(axis.isDashGridLine());
		settings.setMajorGridColor(ColorSettings.fromSWT(axis.getMajorGridColor()));
	}

	private static void restoreAxisPropsFromSettings(Axis axis,
			AxisSettings settings) {
		axis.setTitle(settings.getTitle());
		String fontInfo = settings.getTitleFont();
		if (fontInfo != null && !fontInfo.trim().isEmpty()) {
			FontData fontData = FontDataUtil.getFontData(fontInfo);
			axis.setTitleFont(new Font(Display.getCurrent(), fontData));
		}
		String scaleFont = settings.getScaleFont();
		if (scaleFont != null && !scaleFont.trim().isEmpty()) {
			FontData fontData = FontDataUtil.getFontData(fontInfo);
			axis.setFont(new Font(Display.getCurrent(), fontData));
		}
		if (settings.getForegroundColor() != null)
			axis.setForegroundColor(settings.getForegroundColor().toSWT());
		axis.setPrimarySide(settings.isOnPrimarySide());
		axis.setLogScale(settings.isLogScale());
		axis.setAutoScale(settings.isAutoScale());
		axis.setAutoScaleThreshold(settings.getAutoScaleThreshold());
		if (settings.getRange() != null)
			axis.setRange(settings.getRange().toSWT());
		axis.setDateEnabled(settings.isDateEnabled());
		axis.setAutoFormat(settings.isAutoFormat());
		axis.setFormatPattern(settings.getFormatPattern());
		axis.setShowMajorGrid(settings.isShowMajorGrid());
		axis.setDashGridLine(settings.isDashGridLine());
		if (settings.getMajorGridColor() != null)
			axis.setMajorGridColor(settings.getMajorGridColor().toSWT());
	}

	private static void saveTracePropsToSettings(XYGraph xyGraph, Trace trace,
			TraceSettings settings) {
		settings.setName(trace.getName());
		settings.setxAxis(xyGraph.getXAxisList().indexOf(trace.getXAxis()));
		settings.setyAxis(xyGraph.getYAxisList().indexOf(trace.getYAxis()));
		settings.setTraceColor(ColorSettings.fromSWT(trace.getTraceColor()));
		settings.setTraceType(trace.getTraceType().name());
		settings.setLineWidth(trace.getLineWidth());
		settings.setPointStyle(trace.getPointStyle().name());
		settings.setPointSize(trace.getPointSize());
		settings.setBaseLine(trace.getBaseLine().name());
		settings.setAreaAlpha(trace.getAreaAlpha());
		settings.setAntiAliasing(trace.isAntiAliasing());
		settings.setErrorBarEnabled(trace.isErrorBarEnabled());
		settings.setxErrorBarType(trace.getXErrorBarType().name());
		settings.setyErrorBarType(trace.getYErrorBarType().name());
		settings.setErrorBarColor(ColorSettings.fromSWT(trace.getErrorBarColor()));
		settings.setErrorBarCapWidth(trace.getErrorBarCapWidth());
		settings.setDrawYErrorInArea(trace.isDrawYErrorInArea());
	}

	private static void restoreTracePropsFromSettings(XYGraph xyGraph,
			Trace trace, TraceSettings settings) {
		trace.setName(settings.getName());
		if (settings.getxAxis() >= 0) {
			Axis xAxis = xyGraph.getXAxisList().get(settings.getxAxis());
			trace.setXAxis(xAxis);
		}
		if (settings.getyAxis() >= 0) {
			Axis yAxis = xyGraph.getYAxisList().get(settings.getyAxis());
			trace.setYAxis(yAxis);
		}
		if (settings.getTraceColor() != null)
			trace.setTraceColor(settings.getTraceColor().toSWT());
		trace.setTraceType(TraceType.valueOf(settings.getTraceType()));
		trace.setLineWidth(settings.getLineWidth());
		trace.setPointStyle(PointStyle.valueOf(settings.getPointStyle()));
		trace.setPointSize(settings.getPointSize());
		trace.setBaseLine(BaseLine.valueOf(settings.getBaseLine()));
		trace.setAreaAlpha(settings.getAreaAlpha());
		trace.setAntiAliasing(settings.isAntiAliasing());
		trace.setErrorBarEnabled(settings.isErrorBarEnabled());
		trace.setXErrorBarType(ErrorBarType.valueOf(settings.getxErrorBarType()));
		trace.setYErrorBarType(ErrorBarType.valueOf(settings.getyErrorBarType()));
		if (settings.getErrorBarColor() != null)
			trace.setErrorBarColor(settings.getErrorBarColor().toSWT());
		trace.setErrorBarCapWidth(settings.getErrorBarCapWidth());
		trace.setDrawYErrorInArea(settings.isDrawYErrorInArea());
	}

}
