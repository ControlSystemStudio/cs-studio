package org.csstudio.sds.cosywidgets.ui.internal.figures;

import java.util.HashMap;

import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Color;

import org.csstudio.sds.cosywidgets.common.ICosySwitch;
import org.csstudio.sds.cosywidgets.common.SwitchPlugins;
import org.csstudio.sds.cosywidgets.ui.internal.switchtypes.UnknownSwitch;
import org.csstudio.sds.cosywidgets.ui.internal.utils.Trigonometry;

/**
 * A switch figure.
 * 
 * @author jbercic
 * 
 */
public final class RefreshableSwitchFigure extends Shape {
	/**
	 * Currently defined switch states.
	 */
	public static final int STATE_UNKNOWN = -1;
	public static final int STATE_AUS = 0;
	public static final int STATE_EIN = 1;
	public static final int STATE_GESTOERT = 8;
	public static final int STATE_SCHALTET = 6;
	
	/**
	 * Colors for the defined states.
	 */
	public static final HashMap<Integer,Color> state_colors;
	
	/**
	 * static initializer for the color array.
	 */
	static {
		state_colors=new HashMap<Integer,Color>();
		/*these colors were taken from the switch adl files*/
		state_colors.put(STATE_AUS,CustomMediaFactory.getInstance().getColor(new RGB(253,0,0)));
		state_colors.put(STATE_EIN,CustomMediaFactory.getInstance().getColor(new RGB(0,216,0)));
		state_colors.put(STATE_GESTOERT,CustomMediaFactory.getInstance().getColor(new RGB(251,243,74)));
		state_colors.put(STATE_SCHALTET,CustomMediaFactory.getInstance().getColor(new RGB(158,158,158)));
	}
	
	/**
	 * Current state of the switch.
	 */
	private int switch_state=STATE_AUS;
	
	/**
	 * Current switch type - drawing class.
	 */
	private ICosySwitch switch_painter=new UnknownSwitch();
	private int switch_type=0;
	{
		switch_painter.construct(this,10,10);
	}
	
	/**
	 * Switch orientation:
	 *   rotation (in degrees, use to change horizontal/vertical or any other angle)
	 *   scaling koeficient (needed because only by rotating, the switch could go out of bounds)
	 */
	private int rot_angle=0;
	private double k=1.0;
	
	/**
	 * Double versions of the current width and height.
	 * wdth is the shorter side, hght is the longer side
	 */
	private double wdth=1.0,hght=1.0;
	
	/**
	 * True if the switch was resized after last paint event.
	 */
	private boolean resized=true;
	
	/**
	 * Fills the background.
	 */
	protected void fillShape(Graphics gfx) {
		gfx.setBackgroundColor(getBackgroundColor());
		gfx.fillRectangle(getBounds());
	}
	
	/**
	 * Draws the outline of the image, i.e. the switch itself.
	 */
	protected void outlineShape(final Graphics gfx) {
		AntialiasingUtil.getInstance().enableAntialiasing(gfx);
		gfx.translate(getBounds().getLocation());
		
		if (resized==true) {
			/*some trigonometry to determine the new scaling factor*/
			wdth=(bounds.width<bounds.height)?(double)bounds.width:(double)bounds.height;
			hght=(bounds.width<bounds.height)?(double)bounds.height:(double)bounds.width;
			double angle=(double)rot_angle;
			
			if (rot_angle<=90 || (rot_angle>180 && rot_angle<=270)) {
				k=wdth/(wdth*Trigonometry.sin(-angle+90.0)+hght*Trigonometry.cos(-angle+90.0));
				k=Math.abs(k);
			}
			if ((rot_angle>90 && rot_angle<=180) || (rot_angle>270 && rot_angle<=360)) {
				k=wdth/(hght*Trigonometry.sin(angle)-wdth*Trigonometry.cos(angle));
				k=Math.abs(k);
			}
			switch_painter.resize((int)(k*(double)bounds.width),(int)(k*(double)bounds.height));
			resized=false;
		}
		
		if (rot_angle!=0) {
			gfx.translate(bounds.width/2,bounds.height/2);
			try {
				gfx.rotate((float)rot_angle);
			} catch (RuntimeException e) {
				/*this Graphics does not support rotation*/
			}
			gfx.translate(-(int)(k*(double)bounds.width*0.5),-(int)(k*(double)bounds.height*0.5));
		}
		
		if (switch_state==STATE_UNKNOWN) {
			gfx.setForegroundColor(getForegroundColor());
		} else {
			gfx.setForegroundColor(state_colors.get(switch_state));
		}
		gfx.setBackgroundColor(gfx.getForegroundColor());
		gfx.setLineWidth(getLineWidth());
		switch_painter.paintSwitch(gfx,switch_state);
		
		if (rot_angle!=0) {
			gfx.translate((int)(k*(double)bounds.width*0.5),(int)(k*(double)bounds.height*0.5));
			try {
				gfx.rotate(-(float)rot_angle);
			} catch (RuntimeException e) {
				/*this Graphics does not support rotation*/
			}
			gfx.translate(-bounds.width/2,-bounds.height/2);
		}
	}
	
	public void resize() {
		resized=true;
	}
	
	public void setType(final int newval) {
		try {
			switch_painter=(ICosySwitch)SwitchPlugins.classes_map.get(SwitchPlugins.ids[newval]).createExecutableExtension("Class");
			switch_painter.construct(this,bounds.width,bounds.height);
		} catch (Exception e) {
			switch_painter=new UnknownSwitch();
			switch_painter.construct(this,bounds.width,bounds.height);
		}
		switch_type=newval;
	}
	public int getType() {
		return switch_type;
	}
	
	public void setState(final int newval) {
		if (!state_colors.containsKey(newval)) {
			switch_state=STATE_UNKNOWN;
			return;
		}
		switch_state=newval;
	}
	public int getState() {
		return switch_state;
	}
	
	public void setRotation(final int newval) {
		rot_angle=newval;
		resized=true;
	}
	public int getRotation() {
		return rot_angle;
	}
}
