package org.csstudio.sds.cosywidgets.ui.internal.figures;

import java.util.IllegalFormatException;

import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.sds.util.CustomMediaFactory;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import org.csstudio.sds.cosywidgets.ui.internal.utils.ShadedDrawing;
import org.csstudio.sds.cosywidgets.ui.internal.utils.Trigonometry;
import org.csstudio.sds.cosywidgets.ui.internal.utils.TextPainter;

/**
 * The class that draws a meter on the screen.
 * 
 * @author jbercic
 *
 */
public final class RefreshableMeterFigure extends Shape {
	/**
	 * The meter is displayed as a circular sector.
	 * This property defines the central angle of the sector, in degrees.
	 * 
	 * inner_angle is the angle of the actual display - it must be smaller than angle.
	 */
	private int angle = 90;
	private int inner_angle = 80;
	
	/**
	 * This defines the color, with which the needle is drawn.
	 */
	private RGB needle_color = new RGB(0,255,0);
	
	/**
	 * The width of the display as a fraction of the total radius (height of the widget figure).
	 * The length of the major scale lines (fraction of the total radius).
	 *   Major scale lines are twice as long and twice as thick.
	 * The width of the scale value display area (as a fraction of the total radius).
	 * Both must be between 0.0 and 1.0.
	 */
	private double visible_radius = 0.5;
	private double scale_radius = 0.1;
	private double text_radius = 0.1;
	
	/**
	 * Steps for the minor and major scale lines.
	 * The maximum and minumum values for the scale.
	 * The current value.
	 * 
	 * The leftmost line is always a major scale line.
	 */
	private double minor_step=1.0,major_step=5.0;
	private double min_value=0.0,max_value=10.0;
	private double value=0.0;
	
	/**
	 * Color values for the five levels LOLO, LO, M, HI, HIHI
	 */
	private RGB m_color=new RGB(0,255,0);
	private RGB lolo_color=new RGB(255,0,0),lo_color=new RGB(255,81,81);
	private RGB hihi_color=new RGB(255,0,0),hi_color=new RGB(255,81,81);
	
	/**
	 * Upper values for the five levels.
	 */
	private double m_bound=6.0;
	private double lolo_bound=2.0,lo_bound=4.0;
	private double hihi_bound=10.0,hi_bound=8.0;
	
	/**
	 * border properties - the arced frame of the meter
	 * color, line width
	 */
	private RGB border_color = new RGB(0,0,0);
	private int border_width=1;
		
	/**
	 * scale line properties
	 */
	private RGB scale_color = new RGB(0,0,0);
	private int scale_width=1;
	
	/**
	 * The channel name the meter is connected to and the aliases defined for this meter.
	 * (we need the aliases so we can display the proper channel name)
	 */
	private String channel_name="none";
	
	/**
	 * Is the background transparent?
	 */
	private boolean transparent=true;
	
	/**
	 * Drawing data:
	 *   the current width and height of the widget
	 *   different radii:
	 *     of the whole meter (R), outer frame arc (out_r), inner frame arc (inn_r)
	 *     outer major scale lines (scale_maj_r), outer minor scale lines (scale_min_r)
	 *     scale value centers (text_r)
	 *   y coordinate of the current value text
	 *   double the height of the channel font, so that everything can be moved down
	 */
	private int img_height=10,img_width=10;
	private double R=1.0,out_r=1.0,inn_r=1.0,scale_maj_r=1.0,scale_min_r=1.0,text_r=1.0;
	private int val_y=10;
	private int top_delta=0;
	
	/**
	 * Font properties for the values (scale and current) and the channel name.
	 */
	private Font values_font=CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE);
	private Font channel_font=CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE);
	
	/**
	 * Format string for the current value and the values at major tick marks.
	 */
	private String value_format="%.3f";
	private String scale_format="%.1f";
	
	/**
	 * Subfigures for the background and the needle.
	 */
	private _MeterBackground _background=null;
	private _MeterNeedle _needle=null;
	
	/**
	 * Does anything have to be recalculated?
	 */
	private boolean do_calc=false;
	
	public RefreshableMeterFigure() {
		setLayoutManager(new XYLayout());
		_background=new _MeterBackground();
		add(_background);
		setConstraint(_background,new Rectangle(0,0,100,100));
		_needle=new _MeterNeedle();
		add(_needle);
		setConstraint(_needle,new Rectangle(0,0,100,100));
		
		addFigureListener(new FigureListener() {
			public void figureMoved(final IFigure figure) {
				setConstraint(_background,new Rectangle(0,0,bounds.width,bounds.height));
				setConstraint(_needle,new Rectangle(0,0,bounds.width,bounds.height));
				_background.invalidate();
				_needle.invalidate();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean useLocalCoordinates() {
		return true;
	}
	
	/**
	 * Fills the meter. Nothing to do here.
	 */
	protected void fillShape(final Graphics gfx) {}
	
	/**
	 * Draws the outline of the meter. Nothing to do here.
	 */
	protected void outlineShape(final Graphics gfx) {}
	
	/**
	 * Wrappers around the Trigonometry.* functions.
	 * @param angl the angle, in degrees
	 * @return the result of the trigonometric function
	 */
	private double cosine(double angl) {
		if (angl%90==0) { return Trigonometry.cos(angl-1.0); }
		return Trigonometry.cos(angl);
	}
	private double sine(double angl) {
		if (angl%180==0) { return Trigonometry.sin(angl-1.0); }
		return Trigonometry.sin(angl);
	}

	/**
	 * Calculates all the needed radii and gets the current dimensions of the widget.
	 */
	private void calculateRadii(final int offset) {
		Rectangle bound=getBounds();
		
		setConstraint(_background,new Rectangle(0,0,bounds.width,bounds.height));
		setConstraint(_needle,new Rectangle(0,0,bounds.width,bounds.height));
		
		img_width=bound.width;
		img_height=bound.height-2*channel_font.getFontData()[0].getHeight()-offset;
		
		R=((double)img_height)/(visible_radius+scale_radius+text_radius+ (1-visible_radius-scale_radius-text_radius)*(1-cosine(angle/2)) );
		if ((double)img_width/2<sine(angle/2)*R) {
			R=((double)img_width)/(2.0*sine(angle/2));
		}
		if (angle>180) {
			R=((double)img_height)/(1-cosine(angle/2));
			if ((double)img_width<2.0*R) {
				R=(double)img_width/2.0;
			}
		}
		
		if (text_radius+scale_radius+visible_radius>1.0) {
			if (text_radius<=scale_radius && text_radius<=visible_radius) {
				double k1=scale_radius/text_radius;
				double k2=visible_radius/text_radius;
				text_radius*=1.0/(1.0+k1+k2);
				scale_radius=k1*text_radius;
				visible_radius=k2*text_radius;
			} else {
				if (scale_radius<=text_radius && scale_radius<=visible_radius) {
					double k1=text_radius/scale_radius;
					double k2=visible_radius/scale_radius;
					scale_radius*=1.0/(1.0+k1+k2);
					text_radius=k1*scale_radius;
					visible_radius=k2*scale_radius;
				} else {
					if (visible_radius<=text_radius && visible_radius<=scale_radius) {
						double k1=text_radius/visible_radius;
						double k2=scale_radius/visible_radius;
						visible_radius*=1.0/(1.0+k1+k2);
						text_radius=k1*visible_radius;
						scale_radius=k2*visible_radius;
					}
				}
			}
		}
		
		top_delta=channel_font.getFontData()[0].getHeight()*2;
		out_r=(1-text_radius-scale_radius)*R;
		inn_r=(1-text_radius-scale_radius-visible_radius)*R;
		scale_min_r=(1-text_radius-0.5*scale_radius)*R;
		scale_maj_r=(1-text_radius)*R;
		text_r=(1-0.5*text_radius)*R;
		if (offset!=0) {
			val_y=top_delta+values_font.getFontData()[0].getHeight();
			return;
		}
		if (angle>180) {
			val_y=(int)R+top_delta;
		} else {
			if ((int)(inn_r-inn_r*sine(90.0-angle/2))<values_font.getFontData()[0].getHeight()*2) {
				calculateRadii(values_font.getFontData()[0].getHeight()*2);
			} else {
				val_y=top_delta+(int)(R-inn_r+(inn_r-inn_r*sine(90.0-angle/2))/2);
			}
		}
	}
	
	/**
	 * Invalidates the background subfigure.
	 */
	public void invalidateBackground() {
		do_calc=true;
		_background.invalidate();
	}
	
	/**
	 * Invalidates the needle subfigure.
	 */
	public void invalidateNeedle() {
		_needle.invalidate();
	}
	
	public void setDynamicValue(DynamicsDescriptor dynaDesc) {
		if (dynaDesc==null) {
			channel_name="none";
			return;
		}
		if (dynaDesc.getInputChannels().length<1) {
			channel_name="none";
			return;
		}
		channel_name=new String(dynaDesc.getInputChannels()[0].getChannel());
		invalidateBackground();
	}
	
	public void setAngle(final int angl) {
		angle=angl;
		invalidateBackground();
	}
	public int getAngle() {
		return angle;
	}
	
	public void setInnerAngle(final int angl) {
		inner_angle=angl;
		invalidateBackground();
		invalidateNeedle();
	}
	public int getInnerAngle() {
		return inner_angle;
	}
	
	public void setNeedleColor(final RGB color) {
		needle_color=color;
		invalidateNeedle();
	}
	public RGB getNeedleColor() {
		return needle_color;
	}
	
	public void setVisibleRadius(final double newrad) {
		visible_radius=newrad;
		invalidateBackground();
		invalidateNeedle();
	}
	public double getVisibleRadius() {
		return visible_radius;
	}
	
	public void setScaleRadius(final double newrad) {
		scale_radius=newrad;
		invalidateBackground();
	}
	public double getScaleRadius() {
		return scale_radius;
	}
	
	public void setMinorStep(final double minstep) {
		minor_step=minstep;
		invalidateBackground();
	}
	public double getMinorStep() {
		return minor_step;
	}
	
	public void setMajorStep(final double maxstep) {
		major_step=maxstep;
		invalidateBackground();
	}
	public double getMajorStep() {
		return major_step;
	}
	
	public void setMaxValue(final double max) {
		max_value=max;
		invalidateBackground();
		invalidateNeedle();
	}
	public double getMaxValue() {
		return max_value;
	}
	
	public void setMinValue(final double min) {
		min_value=min;
		invalidateBackground();
		invalidateNeedle();
	}
	public double getMinValue() {
		return min_value;
	}
	
	public void setValue(final double newval) {
		value=newval;
		invalidateNeedle();
	}
	public double getValue() {
		return value;
	}
	
	public void setBorderColor(final RGB newval) {
		border_color=newval;
		invalidateBackground();
	}
	public RGB getBorderColor() {
		return border_color;
	}
	
	public void setScaleColor(final RGB newval) {
		scale_color=newval;
		invalidateBackground();
	}
	public RGB getScaleColor() {
		return scale_color;
	}
	
	public void setBorderWidth(final int newval) {
		border_width=newval;
		invalidateBackground();
	}
	public int getBorderWidth() {
		return border_width;
	}
	
	public void setScaleWidth(final int newval) {
		scale_width=newval;
		invalidateBackground();
	}
	public int getScaleWidth() {
		return scale_width;
	}
	
	public void setTextRadius(final double newval) {
		text_radius=newval;
		invalidateBackground();
	}
	public double getTextRadius() {
		return text_radius;
	}
	
	public void setTransparent(final boolean newval) {
		transparent=newval;
	}
	public boolean getTransparent() {
		return transparent;
	}
	
	public void setMColor(final RGB newval) {
		m_color=newval;
		invalidateBackground();
	}
	public RGB getMColor() {
		return m_color;
	}
	
	public void setLOLOColor(final RGB newval) {
		lolo_color=newval;
		invalidateBackground();
	}
	public RGB getLOLOColor() {
		return lolo_color;
	}
	
	public void setLOColor(final RGB newval) {
		lo_color=newval;
		invalidateBackground();
	}
	public RGB getLOColor() {
		return lo_color;
	}
	
	public void setHIColor(final RGB newval) {
		hi_color=newval;
		invalidateBackground();
	}
	public RGB getHIColor() {
		return hi_color;
	}
	
	public void setHIHIColor(final RGB newval) {
		hihi_color=newval;
		invalidateBackground();
	}
	public RGB getHIHIColor() {
		return hihi_color;
	}
	
	public void setMBound(final double newval) {
		m_bound=newval;
		invalidateBackground();
	}
	public double getMBound() {
		return m_bound;
	}
	
	public void setLOLOBound(final double newval) {
		lolo_bound=newval;
		invalidateBackground();
	}
	public double getLOLOBound() {
		return lolo_bound;
	}
	
	public void setLOBound(final double newval) {
		lo_bound=newval;
		invalidateBackground();
	}
	public double getLOBound() {
		return lo_bound;
	}
	
	public void setHIBound(final double newval) {
		hi_bound=newval;
		invalidateBackground();
	}
	public double getHIBound() {
		return hi_bound;
	}
	
	public void setHIHIBound(final double newval) {
		hihi_bound=newval;
		invalidateBackground();
	}
	public double getHIHIBound() {
		return hihi_bound;
	}
	
	public void setValuesFont(final FontData newval) {
		values_font=CustomMediaFactory.getInstance().getFont(
				newval.getName(), newval.getHeight(),
				newval.getStyle());
		invalidateBackground();
		invalidateNeedle();
	}
	public Font getValuesFont() {
		return values_font;
	}
	
	public void setChannelFont(final FontData newval) {
		channel_font=CustomMediaFactory.getInstance().getFont(
				newval.getName(), newval.getHeight(),
				newval.getStyle());
		invalidateBackground();
		invalidateNeedle();
	}
	public Font getChannelFont() {
		return channel_font;
	}
	
	public void setFormat(final String newval) {
		value_format=new String(newval);
		invalidateNeedle();
	}
	public String getFormat() {
		return value_format;
	}
	
	public void setScaleFormat(final String newval) {
		scale_format=newval;
	}
	public String getScaleFormat() {
		return scale_format;
	}
	
	/**
	 * Subfigure that draws the arched frame and background of the meter.
	 * 
	 * @author jbercic
	 *
	 */
	class _MeterBackground extends Shape {
		/**
		 * Fills the background with colors for the five levels.
		 */
		protected void fillShape(final Graphics gfx) {
			double upp_angle=((double)inner_angle/(max_value-min_value))*(lolo_bound-min_value);
			double lo_angle=90.0+(double)inner_angle/2.0;
			
			if (transparent==false) {
				gfx.setBackgroundColor(getBackgroundColor());
				gfx.fillRectangle(getBounds());
			}
			
			AntialiasingUtil.getInstance().enableAntialiasing(gfx);
			//lolo area
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(lolo_color));
			gfx.fillArc((int)(img_width/2-out_r),(int)(R-out_r)+top_delta,
					(int)(out_r*2.0),(int)(out_r*2.0),
					(int)Math.round(lo_angle-upp_angle),(int)Math.round(upp_angle));
			//lo area
			lo_angle-=upp_angle;
			upp_angle=((double)inner_angle/(max_value-min_value))*(lo_bound-lolo_bound);
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(lo_color));
			gfx.fillArc((int)(img_width/2-out_r),(int)(R-out_r)+top_delta,
					(int)(out_r*2.0),(int)(out_r*2.0),
					(int)Math.round(lo_angle-upp_angle),(int)Math.round(upp_angle));
			//m area
			lo_angle-=upp_angle;
			upp_angle=((double)inner_angle/(max_value-min_value))*(m_bound-lo_bound);
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(m_color));
			gfx.fillArc((int)(img_width/2-out_r),(int)(R-out_r)+top_delta,
					(int)(out_r*2.0),(int)(out_r*2.0),
					(int)Math.round(lo_angle-upp_angle),(int)Math.round(upp_angle));
			//hi area
			lo_angle-=upp_angle;
			upp_angle=((double)inner_angle/(max_value-min_value))*(hi_bound-m_bound);
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(hi_color));
			gfx.fillArc((int)(img_width/2-out_r),(int)(R-out_r)+top_delta,
					(int)(out_r*2.0),(int)(out_r*2.0),
					(int)Math.round(lo_angle-upp_angle),(int)Math.round(upp_angle));
			//hihi area
			lo_angle-=upp_angle;
			upp_angle=((double)inner_angle/(max_value-min_value))*(hihi_bound-hi_bound);
			gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(hihi_color));
			gfx.fillArc((int)(img_width/2-out_r),(int)(R-out_r)+top_delta,
					(int)(out_r*2.0),(int)(out_r*2.0),
					(int)Math.round(lo_angle-upp_angle),(int)Math.round(upp_angle));
			
			//background color
			gfx.setBackgroundColor(getBackgroundColor());
			gfx.fillArc((int)(img_width/2-inn_r),(int)(R-inn_r)+top_delta,
					(int)(inn_r*2.0),(int)(inn_r*2.0),
					(int)Math.round(90.0-angle/2),angle);
		}
		
		/**
		 * The main drawing routine.
		 */
		public void paintFigure(final Graphics gfx) {
			if (do_calc==true) {
				do_calc=false;
				calculateRadii(0);
			}
			fillShape(gfx);
			outlineShape(gfx);
		}
		
		/**
		 * Draws the outline of the background: the frame, the scale and the channel name.
		 */
		protected void outlineShape(final Graphics gfx) {
			AntialiasingUtil.getInstance().enableAntialiasing(gfx);
			/**
			 * WORKAROUND: rotation does something weird to the Graphics object,
			 * so that subsequent font setting does not seem to work.
			 * Pushing and popping the state seems to fix this.
			 */
			gfx.pushState();
			drawScale(gfx);
			gfx.popState();
			drawChannelName(gfx);
			drawFrame(gfx);
		}
		
		/**
		 * Draws the channel name below the meter.</br>
		 * The channel used is the one connected to the value property.
		 * 
		 * @param gfx
		 * 		The Graphics context.
		 */
		private void drawChannelName(final Graphics gfx) {
			gfx.setForegroundColor(getForegroundColor());
			gfx.setFont(channel_font);
			TextPainter.drawText(gfx,channel_name,
					img_width/2,
					channel_font.getFontData()[0].getHeight(),
					TextPainter.CENTER);
		}
		
		/**
		 * Draws the scale and values at major scale lines.
		 * 
		 * @param gfx
		 * 		The Graphics context.
		 */
		private void drawScale(final Graphics gfx) {
			double curr;
			double curr_angle;
			
			gfx.setForegroundColor(CustomMediaFactory.getInstance().getColor(scale_color));
			gfx.setLineWidth(scale_width);
			//minor scale lines
			for (curr=min_value+minor_step;curr<=max_value-minor_step;curr+=minor_step) {
				curr_angle=90.0+(double)inner_angle/2-((double)inner_angle/(max_value-min_value))*(curr-min_value);
				ShadedDrawing.drawLineAtAngle(gfx,out_r,scale_min_r,curr_angle,img_width/2,(int)R+top_delta);
			}
			int wdth=gfx.getLineWidth();
			if (wdth==0) { wdth=1; }
			gfx.setLineWidth(wdth*2);
			//major scale lines
			String val;
			gfx.setFont(values_font);
			for (curr=min_value;curr<=max_value;curr+=major_step) {
				//the tick mark
				curr_angle=90.0+(double)inner_angle/2-((double)inner_angle/(max_value-min_value))*(curr-min_value);
				gfx.setForegroundColor(CustomMediaFactory.getInstance().getColor(scale_color));
				ShadedDrawing.drawLineAtAngle(gfx,out_r,scale_maj_r,curr_angle,img_width/2,(int)R+top_delta);
				//the value of the tick mark
				try {
					val=String.format(scale_format,curr);
				} catch (IllegalFormatException e) {
					val=Double.toString(curr);
				}
				gfx.setForegroundColor(getForegroundColor());
				TextPainter.drawRotatedText(gfx,val,90.0-curr_angle,
						img_width/2+(int)(cosine(curr_angle)*text_r),
						(int)R-(int)(sine(curr_angle)*text_r)+top_delta,
						TextPainter.CENTER);
			}
		}
		
		/**
		 * Draws the arched frame of the meter.
		 * <p>
		 * <b>WARNING:</b> if the starting and ending point of an arc are close together,
		 * the arc may not be drawn at all due to rounding errors (the angles given must be integer).
		 *   For example if start=-89 and angle=359, the endings are only a pixel apart, if the radius
		 *   is less than about 54 pixels. The ending is further to the left (drawing is counter-clockwise),
		 *   meaning that the arc is only 1 pixel long, instead of a full circle.</p>
		 * 
		 * @param gfx The Graphics context.
		 */
		private void drawFrame(final Graphics gfx) {
			gfx.setForegroundColor(CustomMediaFactory.getInstance().getColor(border_color));
			gfx.setLineWidth(border_width);
			//outer arc
			gfx.drawArc((int)(img_width/2-out_r),(int)(R-out_r)+top_delta,
					(int)out_r*2,(int)out_r*2,
					(int)Math.round(90.0-angle/2),angle);
			//inner arc
			gfx.drawArc((int)(img_width/2-inn_r),(int)(R-inn_r)+top_delta,
					(int)inn_r*2,(int)inn_r*2,
					(int)Math.round(90.0-angle/2),angle);
			//left and right lines
			gfx.drawArc(img_width/2-(int)R,top_delta,(int)(2.0*R),(int)(2.0*R),
					(int)Math.round(90.0-angle/2),angle);
			ShadedDrawing.drawLineAtAngle(gfx,inn_r,/*out_r*/R,90+angle/2,img_width/2,(int)R+top_delta);
			ShadedDrawing.drawLineAtAngle(gfx,inn_r,R,90-angle/2,img_width/2,(int)R+top_delta);
		}
	}
	
	/**
	 * Subfigure that draws the needle and current value.
	 * 
	 * @author jbercic
	 *
	 */
	class _MeterNeedle extends Shape {
		/**
		 * The main drawing routine.
		 */
		public void paintFigure(final Graphics gfx) {
			outlineShape(gfx);
		}
		
		/**
		 * Fills the subfigure. Nothing to do here.
		 */
		protected void fillShape(final Graphics gfx) {}
		
		/**
		 * Draws the outline. Here this means the needle and current value.
		 */
		protected void outlineShape(final Graphics gfx) {
			AntialiasingUtil.getInstance().enableAntialiasing(gfx);
			gfx.setForegroundColor(CustomMediaFactory.getInstance().getColor(needle_color));
			gfx.setLineWidth(4);
			gfx.setFont(channel_font);
			double curr_angle=90.0+(double)inner_angle/2-((double)inner_angle/(max_value-min_value))*(value-min_value);
			ShadedDrawing.drawLineAtAngle(gfx,inn_r,out_r,curr_angle,img_width/2,(int)R+top_delta);
			
			String val;
			try {
				val=String.format(value_format,value);
			} catch (IllegalFormatException e) {
				val=Double.toString(value);
			}
			gfx.setFont(channel_font);
			gfx.setForegroundColor(getForegroundColor());
			TextPainter.drawText(gfx,val,
					img_width/2,
					val_y,
					TextPainter.CENTER);
		}
	}
}
