/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.ui.fx.util;

import java.util.Optional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * <code>ZoomableLineChart</code> is a decoration for the {@link LineChart}, which provides zooming capabilities. When
 * user drags the mouse cursor across the surface of the chart (from left to right) a green rectangle is drawn to mark
 * the zoomed in area. On mouse release action the axes ranges are adjusted to match the zoom rectangle. The zoom
 * rectangle should be at least 5 pixels high and 5 pixels wide. If the mouse is dragged from the right to left and the
 * drag range is more than 20 pixels, the zoom is reset to default (zoom out happens). On zoom out the axes are set to
 * auto range mode.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ZoomableLineChart extends StackPane {

    private static final int ZOOM_OUT_THRESHOLD = 20;
    private static final int MIN_ZOOM_THRESHOLD = 5;

    private final LineChart<Number, Number> chart;
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;
    private final Rectangle zoomRect;

    /**
     * Constructs a new chart without any labels. Labels can still be added later, by retrieving the chart (
     * {@link #getChart()}) and decorating it.
     */
    public ZoomableLineChart() {
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        chart = new LineChart<Number, Number>(xAxis, yAxis);
        zoomRect = new Rectangle();
        zoomRect.setManaged(false);
        zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
        zoomRect.setStroke(Color.DARKSEAGREEN.darker());
        getChildren().addAll(chart, zoomRect);
        setUpZooming();
    }

    /**
     * Constructs a new chart and decorates it with labels if they are provided.
     *
     * @param title the label for the chart title
     * @param xAxisLabel the horizontal axis label
     * @param yAxisLabel the vertical axis label
     */
    public ZoomableLineChart(Optional<String> title, Optional<String> xAxisLabel, Optional<String> yAxisLabel) {
        this();
        xAxisLabel.ifPresent(e -> xAxis.setLabel(e));
        yAxisLabel.ifPresent(e -> yAxis.setLabel(e));
        title.ifPresent(e -> chart.setTitle(e));
    }

    /**
     * Returns the underlying chart. Invoke this method to obtain the reference to the chart and adjust it to your
     * satisfaction. You may decorate the axes, change colours etc. Also use this method to add data to the chart. The
     * only thing that is not allowed is to add the chart to another parent. The chart is a direct descendant of this
     * stack pane.
     *
     * @return the underlying chart
     */
    public LineChart<Number, Number> getChart() {
        return chart;
    }

    private void setUpZooming() {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
        chart.setOnMousePressed(e -> {
            mouseAnchor.set(new Point2D(e.getX(), e.getY()));
            zoomRect.setWidth(0);
            zoomRect.setHeight(0);
        });
        chart.setOnMouseDragged(e -> {
            double x = e.getX();
            double y = e.getY();
            double xAnchor = mouseAnchor.get().getX();
            if (x < xAnchor) {
                if (x + ZOOM_OUT_THRESHOLD < xAnchor) {
                    defaultZoom();
                }
            } else {
                zoomRect.setX(Math.min(x, xAnchor));
                zoomRect.setY(Math.min(y, mouseAnchor.get().getY()));
                zoomRect.setWidth(Math.abs(x - xAnchor));
                zoomRect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
            }
        });
        chart.setOnMouseReleased(e -> {
            if (zoomRect.getWidth() < MIN_ZOOM_THRESHOLD || zoomRect.getHeight() < MIN_ZOOM_THRESHOLD) {
                zoomRect.setWidth(0);
                zoomRect.setHeight(0);
            } else {
                doZoom();
            }
        });
        defaultZoom();
    }

    private void defaultZoom() {
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }

    private void doZoom() {
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
        Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(),
            zoomRect.getY() + zoomRect.getHeight());
        Point2D yAxisInScene = yAxis.localToScene(0, 0);
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        double xOffset = zoomTopLeft.getX() - yAxisInScene.getX();
        double yOffset = zoomBottomRight.getY() - xAxisInScene.getY();
        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);
        yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
        yAxis.setUpperBound(yAxis.getLowerBound() - zoomRect.getHeight() / yAxisScale);
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }
}
