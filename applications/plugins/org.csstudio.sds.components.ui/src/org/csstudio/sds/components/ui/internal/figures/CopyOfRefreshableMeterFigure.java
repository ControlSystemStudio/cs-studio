package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.sds.util.CustomMediaFactory;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Image;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;
import java.util.Map;

import org.csstudio.sds.components.ui.internal.utils.ShadedDrawing;
import org.csstudio.sds.components.ui.internal.utils.TextPainter;
import org.csstudio.sds.components.ui.internal.utils.Trigonometry;

/**
 * The class that draws a meter on the screen.
 * 
 * @author jbercic
 * 
 */
public final class CopyOfRefreshableMeterFigure extends RectangleFigure {
	/**
	 * The meter is displayed as a circular sector. This property defines the
	 * central angle of the sector, in degrees.
	 * 
	 * inner_angle is the angle of the actual display - it must be smaller than
	 * angle.
	 */
	private int angle = 90;

	private int inner_angle = 80;

	private int count = 0;

	/**
	 * This defines the color, with which the pointer is drawn;
	 */
	private RGB pointer_color = new RGB(0, 255, 0);

	/**
	 * The width of the display as a fraction of the total radius (height of the
	 * widget figure). The length of the major scale lines (fraction of the
	 * total radius). Major scale lines are twice as long and twice as thick.
	 * The width of the scale value display area (as a fraction of the total
	 * radius). Both must be between 0.0 and 1.0.
	 */
	private double visible_radius = 0.5;

	private double scale_radius = 0.1;

	private double text_radius = 0.1;

	/**
	 * Steps for the minor and major scale lines. The maximum and minumum values
	 * for the scale. The current value.
	 * 
	 * The leftmost line is always a major scale line.
	 */
	private double minor_step = 1.0, major_step = 5.0;

	private double min_value = 0.0, max_value = 10.0;

	private double value = 0.0;

	/**
	 * Color values for the five levels LOLO, LO, M, HI, HIHI
	 */
	private RGB m_color = new RGB(0, 255, 0);

	private RGB lolo_color = new RGB(255, 0, 0),
			lo_color = new RGB(255, 81, 81);

	private RGB hihi_color = new RGB(255, 0, 0),
			hi_color = new RGB(255, 81, 81);

	/**
	 * Upper values for the five levels.
	 */
	private double m_bound = 6.0;

	private double lolo_bound = 2.0, lo_bound = 4.0;

	private double hihi_bound = 10.0, hi_bound = 8.0;

	/**
	 * border properties - the arced frame of the meter color, line width
	 */
	private RGB border_color = new RGB(0, 0, 0);

	private int border_width = 1;

	/**
	 * scale line properties
	 */
	private RGB scale_color = new RGB(0, 0, 0);

	private int scale_width = 1;

	/**
	 * The channel name the meter is connected to and the aliases defined for
	 * this meter. (we need the aliases so we can display the proper channel
	 * name)
	 */
	private String channel_name = "none";

	private Map<String, String> aliases = null;

	/**
	 * Is the background transparent?
	 */
	private boolean transparent = true;

	/**
	 * Drawing data: an offscreen BufferedImage with the background the current
	 * width and height of the widget different radii: of the whole meter (R),
	 * outer frame arc (out_r), inner frame arc (inn_r) outer major scale lines
	 * (scale_maj_r), outer minor scale lines (scale_min_r) scale value centers
	 * (text_r)
	 */
	// private Image background = null;
	// private GC gcBackground = null;
	private int img_height = 10, img_width = 10;

	private double R = 1.0, out_r = 1.0, inn_r = 1.0, scale_maj_r = 1.0,
			scale_min_r = 1.0, text_r = 1.0;

	/***************************************************************************
	 * / {@inheritDoc}
	 * 
	 * @Override protected synchronized void fillShape(final Graphics gfx) { }
	 */

	private double cosine(double angl) {
		if (angl % 90 == 0) {
			return Trigonometry.cos(angl - 1.0);
		}
		return Trigonometry.cos(angl);
	}

	private double sine(double angl) {
		if (angl % 180 == 0) {
			return Trigonometry.sin(angl - 1.0);
		}
		return Trigonometry.sin(angl);
	}

	private void drawBackground(Graphics gfx) {
		double upp_angle = ((double) inner_angle / (max_value - min_value))
				* (lolo_bound - min_value);
		double lo_angle = 90.0 + (double) inner_angle / 2.0;

		// draw the circular sector backgrounds for the five levels
		// lolo area
		gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				lolo_color));
		gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
				(int) (out_r * 2.0), (int) (out_r * 2.0),
				(int) (lo_angle - upp_angle), (int) upp_angle);
		// lo area
		lo_angle -= upp_angle;
		upp_angle = ((double) inner_angle / (max_value - min_value))
				* (lo_bound - lolo_bound);
		gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				lo_color));
		gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
				(int) (out_r * 2.0), (int) (out_r * 2.0),
				(int) (lo_angle - upp_angle), (int) upp_angle);
		// m area
		lo_angle -= upp_angle;
		upp_angle = ((double) inner_angle / (max_value - min_value))
				* (m_bound - lo_bound);
		gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				m_color));
		gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
				(int) (out_r * 2.0), (int) (out_r * 2.0),
				(int) (lo_angle - upp_angle), (int) upp_angle);
		// hi area
		lo_angle -= upp_angle;
		upp_angle = ((double) inner_angle / (max_value - min_value))
				* (hi_bound - m_bound);
		gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				hi_color));
		gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
				(int) (out_r * 2.0), (int) (out_r * 2.0),
				(int) (lo_angle - upp_angle), (int) upp_angle);
		// hihi area
		lo_angle -= upp_angle;
		upp_angle = ((double) inner_angle / (max_value - min_value))
				* (hihi_bound - hi_bound);
		gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				hihi_color));
		gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
				(int) (out_r * 2.0), (int) (out_r * 2.0),
				(int) (lo_angle - upp_angle), (int) upp_angle);

		// background color
		gfx.setBackgroundColor(getBackgroundColor());
		gfx.fillArc((int) (img_width / 2 - inn_r), (int) (R - inn_r),
				(int) (inn_r * 2.0), (int) (inn_r * 2.0),
				(int) (90.0 - angle / 2), angle);
	}

	/**
	 * Draws the arched frame of the meter.
	 * 
	 * WARNING: if the starting and ending point of an arc are close together,
	 * the arc may not be drawn at all due to rounding errors (the angles given
	 * must be integer). For example if start=-89 and angle=359, the endings are
	 * only a pixel apart, if the radius is less than about 54 pixels. The
	 * ending is further to the left (drawing is counter-clockwise), meaning
	 * that the arc is only 1 pixel long, instead of a full circle.
	 * 
	 * @param gfx
	 *            The Graphics context.
	 */
	private void drawFrame(Graphics gfx) {
		gfx.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				border_color));
		gfx.setLineWidth(border_width);
		// outer arc
		gfx.drawArc((int) (img_width / 2 - out_r), (int) (R - out_r),
				(int) out_r * 2, (int) out_r * 2, (int) (90.0 - angle / 2),
				angle);
		// inner arc
		gfx.drawArc((int) (img_width / 2 - inn_r), (int) (R - inn_r),
				(int) inn_r * 2, (int) inn_r * 2, (int) (90.0 - angle / 2),
				angle);
		// leva in desna crta
		ShadedDrawing.drawLineAtAngle(gfx, inn_r, out_r, 90 + angle / 2,
				img_width / 2, (int) R);
		ShadedDrawing.drawLineAtAngle(gfx, inn_r, out_r, 90 - angle / 2,
				img_width / 2, (int) R);
	}

	private void drawScale(Graphics gfx) {
		double curr;
		double curr_angle;

		gfx.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				scale_color));
		gfx.setLineWidth(scale_width);
		// minor scale lines
		for (curr = min_value + minor_step; curr <= max_value - minor_step; curr += minor_step) {
			curr_angle = 90.0 + (double) inner_angle / 2
					- ((double) inner_angle / (max_value - min_value))
					* (curr - min_value);
			ShadedDrawing.drawLineAtAngle(gfx, out_r, scale_min_r, curr_angle,
					img_width / 2, (int) R);
		}
		int wdth = gfx.getLineWidth();
		if (wdth == 0) {
			wdth = 1;
		}
		gfx.setLineWidth(wdth * 2);
		// major scale lines
		String val;
		for (curr = min_value; curr <= max_value; curr += major_step) {
			curr_angle = 90.0 + (double) inner_angle / 2
					- ((double) inner_angle / (max_value - min_value))
					* (curr - min_value);
			gfx.setForegroundColor(CustomMediaFactory.getInstance().getColor(
					scale_color));
			ShadedDrawing.drawLineAtAngle(gfx, out_r, scale_maj_r, curr_angle,
					img_width / 2, (int) R);
			val = Double.toString(curr);
			gfx.setForegroundColor(getForegroundColor());
			TextPainter.drawRotatedText(gfx, val, 90.0 - curr_angle, img_width
					/ 2 + (int) (cosine(curr_angle) * text_r), (int) R
					- (int) (sine(curr_angle) * text_r), TextPainter.CENTER);
		}
	}

	public void drawPointer(Graphics gfx) {
		gfx.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				pointer_color));
		// TODO: Pointer Width property
		gfx.setLineWidth(4);

		double curr_angle = 90.0 + (double) inner_angle / 2
				- ((double) inner_angle / (max_value - min_value))
				* (value - min_value);
		ShadedDrawing.drawLineAtAngle(gfx, inn_r, out_r, curr_angle,
				img_width / 2, (int) R);

		String val = Double.toString(value);
		// val=Integer.toString(count);
		gfx.setForegroundColor(getForegroundColor());
		TextPainter.drawText(gfx, val, img_width / 2,
		// img_height-(int)(0.3*(inn_r-R+img_height)),
				img_height + 30, TextPainter.CENTER);

		gfx.setForegroundColor(getForegroundColor());
		TextPainter.drawText(gfx, channel_name, img_width / 2,
		// img_height-(int)(0.6*(inn_r-R+img_height)),
				img_height + 10, TextPainter.CENTER);
	}

	/**
	 * Draws the static part of the meter widget (the background) into a
	 * BufferedImage, so subsequent redraws can be faster (only the needle and
	 * text are dynamic).
	 */
	private void staticImage() {
		// calculateRadii();
		//		
		// background=new Image(Display.getDefault(),img_width,img_height);
		// gcBackground=new GC(background);
		// gcBackground.setAntialias(SWT.ON);
		// gcBackground.setTextAntialias(SWT.ON);
		//		
		// gcBackground.setBackground(getBackgroundColor());
		// drawBackground(gcBackground);
		// drawScale(gcBackground);
		// drawFrame(gcBackground);
	}

	/**
	 * Calculates all the needed radii and gets the current dimensions of the
	 * widget.
	 */
	private void calculateRadii() {
		Rectangle bound = getBounds();

		img_width = bound.width;
		img_height = bound.height - 40;

		R = ((double) img_height)
				/ (visible_radius + scale_radius + text_radius + (1
						- visible_radius - scale_radius - text_radius)
						* (1 - cosine(angle / 2)));
		if ((double) img_width / 2 < sine(angle / 2) * R) {
			R = ((double) img_width) / (2.0 * sine(angle / 2));
		}
		if (angle > 180) {
			R = ((double) img_height) / (1 - cosine(angle / 2));
			if ((double) img_width < 2.0 * R) {
				R = (double) img_width / 2.0;
			}
		}

		if (text_radius + scale_radius + visible_radius > 1.0) {
			if (text_radius <= scale_radius && text_radius <= visible_radius) {
				double k1 = scale_radius / text_radius;
				double k2 = visible_radius / text_radius;
				text_radius *= 1.0 / (1.0 + k1 + k2);
				scale_radius = k1 * text_radius;
				visible_radius = k2 * text_radius;
			} else {
				if (scale_radius <= text_radius
						&& scale_radius <= visible_radius) {
					double k1 = text_radius / scale_radius;
					double k2 = visible_radius / scale_radius;
					scale_radius *= 1.0 / (1.0 + k1 + k2);
					text_radius = k1 * scale_radius;
					visible_radius = k2 * scale_radius;
				} else {
					if (visible_radius <= text_radius
							&& visible_radius <= scale_radius) {
						double k1 = text_radius / visible_radius;
						double k2 = scale_radius / visible_radius;
						visible_radius *= 1.0 / (1.0 + k1 + k2);
						text_radius = k1 * visible_radius;
						scale_radius = k2 * visible_radius;
					}
				}
			}
		}

		out_r = (1 - text_radius - scale_radius) * R;
		inn_r = (1 - text_radius - scale_radius - visible_radius) * R;
		scale_min_r = (1 - text_radius - 0.5 * scale_radius) * R;
		scale_maj_r = (1 - text_radius) * R;
		text_r = (1 - 0.5 * text_radius) * R;
	}

	public void paintFigure(Graphics gfx) {
		super.paintFigure(gfx);
		calculateRadii();
		// gfx.setAntialias(SWT.ON);
		// gfx.setTextAntialias(SWT.ON);

		gfx.translate(getBounds().getLocation());

		// gfx.drawImage(background, 0, 0);
		// calculateRadii();
		if (!transparent) {
			gfx.fillRectangle(0, 0, getBounds().width, getBounds().height);
		}
		// drawBackground(gfx);
		drawScale(gfx);
		drawFrame(gfx);
		drawPointer(gfx);

		// shader.drawLine(gfx,base.x,base.y,base.x+bound.width,base.y+bound.width);
	}

	// public void invalidateBackground() {
	// if (background==null) { return; }
	// background.dispose();
	// gcBackground.dispose();
	// background=null;
	// gcBackground=null;
	// }

	public void setDynamicValue(DynamicsDescriptor dynaDesc) {
		if (dynaDesc == null) {
			channel_name = "none";
			return;
		}
		if (dynaDesc.getInputChannels().length < 1) {
			channel_name = "none";
			return;
		}
		channel_name = new String(dynaDesc.getInputChannels()[0].getChannel());
		if (aliases != null) {
			repaint();
		}
		channel_name = new String(dynaDesc.getInputChannels()[0].getChannel());
		repaint();
	}

	public void setAliases(final Map<String, String> newval) {
		aliases = newval;
		repaint();
	}

	public void setAngle(final int angl) {
		angle = angl;
	}

	public int getAngle() {
		return angle;
	}

	public void setInnerAngle(final int angl) {
		inner_angle = angl;
	}

	public int getInnerAngle() {
		return inner_angle;
	}

	public void setPointerColor(final RGB color) {
		pointer_color = color;
	}

	public RGB getPointerColor() {
		return pointer_color;
	}

	public void setVisibleRadius(final double newrad) {
		visible_radius = newrad;

	}

	public double getVisibleRadius() {
		return visible_radius;
	}

	public void setScaleRadius(final double newrad) {
		scale_radius = newrad;

	}

	public double getScaleRadius() {
		return scale_radius;
	}

	public void setMinorStep(final double minstep) {
		minor_step = minstep;

	}

	public double getMinorStep() {
		return minor_step;
	}

	public void setMajorStep(final double maxstep) {
		major_step = maxstep;

	}

	public double getMajorStep() {
		return major_step;
	}

	public void setMaxValue(final double max) {
		max_value = max;

	}

	public double getMaxValue() {
		return max_value;
	}

	public void setMinValue(final double min) {
		min_value = min;

	}

	public double getMinValue() {
		return min_value;
	}

	public void setValue(final double newval) {
		value = newval;
	}

	public double getValue() {
		return value;
	}

	public void setBorderColor(final RGB newval) {
		border_color = newval;

	}

	public RGB getBorderColor() {
		return border_color;
	}

	public void setScaleColor(final RGB newval) {
		scale_color = newval;

	}

	public RGB getScaleColor() {
		return scale_color;
	}

	public void setBorderWidth(final int newval) {
		border_width = newval;

	}

	public int getBorderWidth() {
		return border_width;
	}

	public void setScaleWidth(final int newval) {
		scale_width = newval;

	}

	public int getScaleWidth() {
		return scale_width;
	}

	public void setTextRadius(final double newval) {
		text_radius = newval;

	}

	public double getTextRadius() {
		return text_radius;
	}

	public void setTransparent(final boolean newval) {
		transparent = newval;
	}

	public boolean getTransparent() {
		return transparent;
	}

	public void setMColor(final RGB newval) {
		m_color = newval;

	}

	public RGB getMColor() {
		return m_color;
	}

	public void setLOLOColor(final RGB newval) {
		lolo_color = newval;

	}

	public RGB getLOLOColor() {
		return lolo_color;
	}

	public void setLOColor(final RGB newval) {
		lo_color = newval;

	}

	public RGB getLOColor() {
		return lo_color;
	}

	public void setHIColor(final RGB newval) {
		hi_color = newval;

	}

	public RGB getHIColor() {
		return hi_color;
	}

	public void setHIHIColor(final RGB newval) {
		hihi_color = newval;

	}

	public RGB getHIHIColor() {
		return hihi_color;
	}

	public void setMBound(final double newval) {
		m_bound = newval;

	}

	public double getMBound() {
		return m_bound;
	}

	public void setLOLOBound(final double newval) {
		lolo_bound = newval;

	}

	public double getLOLOBound() {
		return lolo_bound;
	}

	public void setLOBound(final double newval) {
		lo_bound = newval;

	}

	public double getLOBound() {
		return lo_bound;
	}

	public void setHIBound(final double newval) {
		hi_bound = newval;

	}

	public double getHIBound() {
		return hi_bound;
	}

	public void setHIHIBound(final double newval) {
		hihi_bound = newval;

	}

	public double getHIHIBound() {
		return hihi_bound;
	}

	class FrameFigure extends Panel {

		@Override
		protected void paintFigure(Graphics graphics) {
			drawBackground(graphics);
		}

		private void drawBackground(Graphics gfx) {
			double upp_angle = ((double) inner_angle / (max_value - min_value))
					* (lolo_bound - min_value);
			double lo_angle = 90.0 + (double) inner_angle / 2.0;

			// draw the circular sector backgrounds for the five levels
			// lolo area
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
					lolo_color));
			gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
					(int) (out_r * 2.0), (int) (out_r * 2.0),
					(int) (lo_angle - upp_angle), (int) upp_angle);
			// lo area
			lo_angle -= upp_angle;
			upp_angle = ((double) inner_angle / (max_value - min_value))
					* (lo_bound - lolo_bound);
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
					lo_color));
			gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
					(int) (out_r * 2.0), (int) (out_r * 2.0),
					(int) (lo_angle - upp_angle), (int) upp_angle);
			// m area
			lo_angle -= upp_angle;
			upp_angle = ((double) inner_angle / (max_value - min_value))
					* (m_bound - lo_bound);
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
					m_color));
			gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
					(int) (out_r * 2.0), (int) (out_r * 2.0),
					(int) (lo_angle - upp_angle), (int) upp_angle);
			// hi area
			lo_angle -= upp_angle;
			upp_angle = ((double) inner_angle / (max_value - min_value))
					* (hi_bound - m_bound);
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
					hi_color));
			gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
					(int) (out_r * 2.0), (int) (out_r * 2.0),
					(int) (lo_angle - upp_angle), (int) upp_angle);
			// hihi area
			lo_angle -= upp_angle;
			upp_angle = ((double) inner_angle / (max_value - min_value))
					* (hihi_bound - hi_bound);
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
					hihi_color));
			gfx.fillArc((int) (img_width / 2 - out_r), (int) (R - out_r),
					(int) (out_r * 2.0), (int) (out_r * 2.0),
					(int) (lo_angle - upp_angle), (int) upp_angle);

			// background color
			gfx.setBackgroundColor(getBackgroundColor());
			gfx.fillArc((int) (img_width / 2 - inn_r), (int) (R - inn_r),
					(int) (inn_r * 2.0), (int) (inn_r * 2.0),
					(int) (90.0 - angle / 2), angle);
		}
	}
}
