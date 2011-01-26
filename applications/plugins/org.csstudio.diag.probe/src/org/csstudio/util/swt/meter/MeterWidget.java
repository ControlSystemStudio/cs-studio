/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.util.swt.meter;

import java.text.NumberFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/** Simple meter widget.
 *  @author Kay Kasemir
 */
public class MeterWidget extends Canvas implements DisposeListener,
		PaintListener
{
    /** Line width for scale outline (rest uses width 1) */
	final private static int LINE_WIDTH = 5;

	/** Number of labels (and ticks) */
	final private static int LABEL_COUNT = 5;

    final private static int start_angle = 140;
    final private static int end_angle = 40;
    final private static double scale_width = 0.35;

    final private Color background_color;
    final private Color face_color;
    final private Color needle_color;
    final private Color ok_color;
    final private Color warning_color;
    final private Color alarm_color;

    /** Minimum value. */
    private double min = -10.0;

    /** Lower alarm limit. */
    private double low_alarm = -5.0;

    /** Lower warning limit. */
    private double low_warning = -4.0;

    /** Upper warning limit. */
    private double high_warning = 4.0;

    /** Upper alarm limit. */
    private double high_alarm = 5.0;

    /** Maximum value. */
    private double max = +10.0;

    /** Display precision. */
    private int precision = 4;

    /** Current value. */
    private double value = 1.0;

    /** Most recent scale image or <code>null</code>. */
    private Image scale_image;

    /** ClientRect for which the image was created. */
    private Rectangle old_client_rect = new Rectangle(0, 0, 0, 0);

    /** X-coord of needle pivot point */
    private int pivot_x;

    /** Y-coord of needle pivot point */
    private int pivot_y;

    /** X-Radius of scale */
    private int x_radius;

    /** Y-Radius of scale */
    private int y_radius;

	/** Constructor */
	public MeterWidget(final Composite parent, final int style)
	{
	    // To reduce flicker, don't clear the background.
	    // On Linux, however, that seems to corrupt the overall
	    // widget layout, so we don't use that option.
	    // super(parent, style | SWT.NO_BACKGROUND);
        super(parent, style);
		background_color = new Color(null, 255, 255, 255);
		face_color = new Color(null, 20, 10, 10);
		needle_color = new Color(null, 20, 0, 200);
        ok_color = new Color(null, 0, 200, 0);
        warning_color = new Color(null, 200, 200, 0);
        alarm_color = new Color(null, 250, 0, 0);
		addDisposeListener(this);
		addPaintListener(this);
	}

    /** Configure the meter.
     *  @param min Minimum value.
     *  @param low_alarm Lower alarm limit.
     *  @param low_warning Lower warning limit.
     *  @param high_warning Upper warning limit.
     *  @param high_alarm Upper alarm limit.
     *  @param max Maximum value.
     *  @param precision Display precision
     */
    public void configure(final double min,
                          final double low_alarm,
                          final double low_warning,
                          final double high_warning,
                          final double high_alarm,
                          final double max,
                          final int precision)
    {
        if (min > max)
        {   // swap
            this.min = min;
            this.max = max;
        }
        else if (min == max)
        {   // Some fake default range
            this.min = min;
            this.max = min + 10.0;
        }
        else
        {   // Set as given
            this.min = min;
            this.max = max;
        }

        // Check for limits that are outside the value range
        // or NaN (since EPICS R3.14.11)
        if (low_alarm > this.min  &&  low_alarm < this.max)
        	this.low_alarm = low_alarm;
        else
        	this.low_alarm = this.min;

        if (low_warning > this.min  &&  low_warning < this.max)
        	this.low_warning = low_warning;
    	else
            this.low_warning = this.low_alarm;


        if (high_alarm > this.min  &&  high_alarm < this.max)
            this.high_alarm = high_alarm;
        else
            this.high_alarm = this.max;

        if (high_warning > this.min  &&  high_warning < this.max)
        	this.high_warning = high_warning;
        else
            this.high_warning = this.high_alarm;

        this.precision = precision;
        resetScale();
        redraw();
    }

    /** Set current value. */
    public void setValue(final double value)
    {
        this.value = value;
        if (!isDisposed()) {
            redraw();
        }
    }

    /** Reset the scale.
     *  <p>
     *  Clears the scale image, so it will be re-computed on redraw.
     */
    private void resetScale()
    {
        if (scale_image != null)
        {
            scale_image.dispose();
            scale_image = null;
        }
    }

    /** @see org.eclipse.swt.events.DisposeListener */
    @Override
    public void widgetDisposed(final DisposeEvent e)
    {
        resetScale();
        alarm_color.dispose();
        warning_color.dispose();
        ok_color.dispose();
        needle_color.dispose();
        face_color.dispose();
        background_color.dispose();
    }

	/** @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean) */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed)
	{
		int width, height;
		height = 100;
		width = 100;
		if (wHint != SWT.DEFAULT) {
            width = wHint;
        }
		if (hHint != SWT.DEFAULT) {
            height = hHint;
        }
		return new Point(width, height);
	}

    /** @return Angle in degrees for given value on scale. */
    private double getAngle(final double value)
    {
        if (value <= min) {
            return start_angle;
        }
        if (value >= max) {
            return end_angle;
        }
        return end_angle + (start_angle - end_angle) * (max-value) / (max-min);
    }

	/** @see org.eclipse.swt.events.PaintListener */
    @Override
    public void paintControl(final PaintEvent e)
    {
        final GC gc = e.gc;

        // Get the rectangle that exactly fills the 'inner' area
        // such that drawRectangle() will match.
        final Rectangle client_rect = getClientArea();

        // Background and border
        gc.setForeground(face_color);
        gc.setBackground(background_color);
        gc.setLineWidth(LINE_WIDTH);
        gc.setLineCap(SWT.CAP_ROUND);
        gc.setLineJoin(SWT.JOIN_ROUND);

        // To reduce flicker, the scale is drawn as a prepared image into
        // the widget whose background has not been cleared.
        createScaleImage(gc, client_rect);
        if (getEnabled())
        {
            gc.drawImage(scale_image, 0, 0);

            // Needle is added, so the needle could 'flicker'.
            // Alternative would be to copy the scale image,
            // add needle to copy, then draw the scale-with-needle image.
            // The added image copy sounds like overhead best avoided until
            // needle flicker turns into an actual problem.
            final double needle_angle = getAngle(value);
            final int needle_x_radius = (int)((1 - 0.5*scale_width)*x_radius);
            final int needle_y_radius = (int)((1 - 0.5*scale_width)*y_radius);
            gc.setForeground(needle_color);
            gc.drawLine(pivot_x, pivot_y,
                (int)(pivot_x + needle_x_radius*Math.cos(Math.toRadians(needle_angle))),
                (int)(pivot_y - needle_y_radius*Math.sin(Math.toRadians(needle_angle))));
        }
        else
        {   // Not enabled
            final Image grayed =
                new Image(gc.getDevice(), scale_image, SWT.IMAGE_DISABLE);
            gc.drawImage(grayed, 0, 0);
            grayed.dispose();

            final String message = Messages.MeterWidget_NoNumericInfo;
            final Point size = gc.textExtent(message);
            gc.drawString(message,
             (client_rect.width-size.x)/2, (client_rect.height-size.y)/2, true);
        }
    }

    /** Create image of the scale (labels etc.) _if_needed_ */
    private void createScaleImage(final GC gc, final Rectangle client_rect)
    {
        // Is there already a matching image?
        if ((scale_image != null)  &&  old_client_rect.equals(client_rect)) {
            return;
        }

        // Remember the client rect for the next call:
        old_client_rect = client_rect;

        // The area that one can use with drawRectangle()
        // is actually one pixel smaller...
        final Rectangle real_client_rect =
            new Rectangle(client_rect.x, client_rect.y,
                          client_rect.width-1, client_rect.height-1);

        // Create image buffer, prepare GC for it.
        // In case there's old one, delete it.
        if (scale_image != null) {
            scale_image.dispose();
        }
        scale_image = new Image(gc.getDevice(), client_rect);
        final GC scale_gc = new GC(scale_image);
        scale_gc.setForeground(face_color);
        scale_gc.setBackground(background_color);
        scale_gc.setLineWidth(LINE_WIDTH);
        scale_gc.setLineCap(SWT.CAP_ROUND);
        scale_gc.setLineJoin(SWT.JOIN_ROUND);

        // Background, border
        scale_gc.fillRectangle(real_client_rect);
        scale_gc.drawRectangle(real_client_rect);

        // Auto-scale everything. Probably sucks.
        pivot_x = real_client_rect.x + real_client_rect.width / 2;
        pivot_y = real_client_rect.y + real_client_rect.height;
        final NumberFormat fmt = NumberFormat.getNumberInstance();
        fmt.setMaximumFractionDigits(precision);

        final Point min_size = scale_gc.textExtent(fmt.format(min));
        final Point max_size = scale_gc.textExtent(fmt.format(max));
        final int text_width_idea = Math.max(min_size.x, max_size.x);
        final int text_height_idea = min_size.y;

        // Labels should somehow fit around the outside of the scale
        final int tick_x_radius = real_client_rect.width/2 - text_width_idea;
        final int tick_y_radius = real_client_rect.height - 2*text_height_idea;
        y_radius = tick_y_radius - text_height_idea;
        x_radius = tick_x_radius * y_radius/tick_y_radius;
        // Inner radius of scale.
        final int x_radius2 = (int)((1-scale_width)*x_radius);
        final int y_radius2 = (int)((1-scale_width)*y_radius);
        // Lower end of ticks.
        final int tick_x_radius2 = (int)(0.6*x_radius2);
        final int tick_y_radius2 = (int)(0.6*y_radius2);

        // Path for outline of scale
        final Path scale_path = createSectionPath(scale_gc.getDevice(),
                        pivot_x, pivot_y,
                        x_radius, y_radius,
                        x_radius2, y_radius2,
                        start_angle, end_angle);
        // Fill scale with 'ok' color
        scale_gc.setBackground(ok_color);
        scale_gc.fillPath(scale_path);
        // Border around the scale drawn later...

        // Colored alarm sections
        final int high_alarm_start = (int) getAngle(high_alarm);
        final Path high_alarm_path = createSectionPath(scale_gc.getDevice(),
                        pivot_x, pivot_y,
                        x_radius, y_radius,
                        x_radius2, y_radius2,
                        high_alarm_start, end_angle);
        scale_gc.setBackground(alarm_color);
        scale_gc.fillPath(high_alarm_path);
        high_alarm_path.dispose();

        final int low_alarm_end = (int) getAngle(low_alarm);
        final Path low_alarm_path = createSectionPath(scale_gc.getDevice(),
                        pivot_x, pivot_y,
                        x_radius, y_radius,
                        x_radius2, y_radius2,
                        start_angle, low_alarm_end);
        scale_gc.fillPath(low_alarm_path);
        low_alarm_path.dispose();

        // Warning sections
        final int high_warning_start = (int) getAngle(high_warning);
        final Path high_warning_path = createSectionPath(scale_gc.getDevice(),
                        pivot_x, pivot_y,
                        x_radius, y_radius,
                        x_radius2, y_radius2,
                        high_warning_start, high_alarm_start);
        scale_gc.setBackground(warning_color);
        scale_gc.fillPath(high_warning_path);
        high_warning_path.dispose();

        final int low_warning_end = (int) getAngle(low_warning);
        final Path low_warning_path = createSectionPath(scale_gc.getDevice(),
                        pivot_x, pivot_y,
                        x_radius, y_radius,
                        x_radius2, y_radius2,
                        low_alarm_end, low_warning_end);
        scale_gc.fillPath(low_warning_path);
        low_warning_path.dispose();

        // Scale outline
        scale_gc.drawPath(scale_path);
        scale_path.dispose();

        // Labels and tick marks
        scale_gc.setLineWidth(1);
        for (int i=0; i<LABEL_COUNT; ++i)
        {
            final double label_value = min+(max-min)*i/(LABEL_COUNT-1);
            final double angle = getAngle(label_value);
            final double cos_angle = Math.cos(Math.toRadians(angle));
            final double sin_angle = Math.sin(Math.toRadians(angle));
            scale_gc.drawLine(
                (int)(pivot_x + tick_x_radius2*cos_angle),
                (int)(pivot_y - tick_y_radius2*sin_angle),
                (int)(pivot_x + tick_x_radius*cos_angle),
                (int)(pivot_y - tick_y_radius*sin_angle));

            final String label_text = fmt.format(label_value);
            final Point size = scale_gc.textExtent(label_text);
            scale_gc.drawString(label_text,
                          (int)(pivot_x + tick_x_radius*cos_angle)-size.x/2,
                          (int)(pivot_y - tick_y_radius*sin_angle)-size.y,
                          true);
        }

        scale_gc.dispose();
    }

    /** Create path for a section of the colored scale.
     *  @param device Device
     *  @param x0 Center of arcs
     *  @param y0 Center of arcs
     *  @param x_radius Outer arc radius
     *  @param y_radius Outer arc radius
     *  @param x_radius2 Inner arc radius
     *  @param y_radius2 Inner arc radius
     *  @param start_angle degrees
     *  @param end_angle degrees
     *  @return
     */
    private Path createSectionPath(final Device device,
                    final int x0, final int y0,
                    final int x_radius, final int y_radius,
                    final int x_radius2, final int y_radius2,
                    final int start_angle, final int end_angle)
    {
        final Path path = new Path(device);
        // Right edge
        path.moveTo((float)(x0 + x_radius2*Math.cos(Math.toRadians(end_angle))),
                    (float)(y0 - y_radius2*Math.sin(Math.toRadians(end_angle))));
        // Upper edge
        path.addArc(x0 - x_radius, y0 - y_radius,
                    2*x_radius, 2*y_radius,
                    end_angle, start_angle-end_angle);
        // Left edge
        path.lineTo((float)(x0 + x_radius2*Math.cos(Math.toRadians(start_angle))),
                    (float)(y0 - y_radius2*Math.sin(Math.toRadians(start_angle))));
        addClockwiseArc(path, x0, y0, x_radius2, y_radius2,
                        start_angle, end_angle);
        path.close();
        return path;
    }

    /** Add a clockwise arc from start to end angle to path.
     *  @param path
     *  @param x0 Center of arc
     *  @param y0 Center of arc
     *  @param x_radius X radius of arc
     *  @param y_radius Y radius of arc
     *  @param start_angle start degrees (0=east, 90=north)
     *  @param end_angle end degrees
     */
    private void addClockwiseArc(final Path path,
                    final int x0, final int y0,
                    final int x_radius,
                    final int y_radius,
                    final float start_angle,
                    final float end_angle)
    {
        // TODO Would like to draw arc back, i.e. go clockwise,
        // but SWT didn't do that on all platforms, so we draw the arc ourselves.
        // Linux: OK
        // OS X : Rendering errors
//        if (false)
//            path.addArc(x0 - x_radius, y0 - y_radius,
//                     2*x_radius, 2*y_radius,
//                     start_angle, end_angle-start_angle);
//        else
//        {
            final double d_rad = Math.toRadians(5);
            final double start_rad = Math.toRadians(start_angle);
            final double end_rad = Math.toRadians(end_angle);
            double rad=start_rad;
            while (rad >= end_rad)
            {
                path.lineTo((float)(x0 + x_radius*Math.cos(rad)),
                             (float)(y0 - y_radius*Math.sin(rad)));
                rad -= d_rad;
            }
            path.lineTo((float)(x0 + x_radius*Math.cos(end_rad)),
                            (float)(y0 - y_radius*Math.sin(end_rad)));
//        }
    }
}
