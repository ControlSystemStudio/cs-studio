package org.csstudio.sds.components.ui.internal.figures;

import java.util.HashMap;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Color;

import org.csstudio.sds.components.common.CosySwitch;
import org.csstudio.sds.components.common.SwitchPlugins;
import org.csstudio.sds.components.ui.internal.utils.Trigonometry;

/**
 * A switch figure.
 * 
 * @author jbercic
 * 
 */
public final class RefreshableSwitchFigure extends Shape implements IAdaptable {
	
	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	/**
	 * Colors for the defined states.
	 */
	public static final HashMap<Integer,Color> STATECOLORS;
	
	/**
	 * static initializer for the color array.
	 */
	static {
		STATECOLORS=new HashMap<Integer,Color>();
		/*these colors were taken from the switch adl files*/
		STATECOLORS.put(CosySwitch.STATE_AUS,CustomMediaFactory.getInstance().getColor(new RGB(253,0,0)));
		STATECOLORS.put(CosySwitch.STATE_EIN,CustomMediaFactory.getInstance().getColor(new RGB(0,216,0)));
		STATECOLORS.put(CosySwitch.STATE_GESTOERT,CustomMediaFactory.getInstance().getColor(new RGB(251,243,74)));
		STATECOLORS.put(CosySwitch.STATE_SCHALTET,CustomMediaFactory.getInstance().getColor(new RGB(158,158,158)));
	}
	
	/**
	 * Current state of the switch.
	 */
	private int _switchState=CosySwitch.STATE_AUS;
	
	/**
	 * The current drawing class.
	 */
	private CosySwitch _switchPainter = new CosySwitch();
	/**
	 * The current switch type. 
	 */
	private int _switchType=0;
	
	/**
	 * The rotation (in degrees, use to change horizontal/vertical or any other angle).
	 */
	private int _rotAngle=0;
	/**
	 * The scaling coefficient (needed because only by rotating, the switch could go out of bounds).
	 */
	private double _coefficient=1.0;
	
	/**
	 * Double versions of the current width.
	 * wdth is the shorter side
	 */
	private double _wdth=1.0;
	/**
	 * Double versions of the current height.
	 * hght is the longer side
	 */
	private double _hght=1.0;
	/**
	 * True if the switch was resized after last paint event.
	 */
	private boolean _resized=true;
	
	/**
	 * Fills the background.
	 * @param gfx The {@link Graphics} to use
	 */
	protected void fillShape(final Graphics gfx) {
		gfx.setBackgroundColor(getBackgroundColor());
		Rectangle figureBounds = getBounds().getCopy();
		figureBounds.crop(this.getInsets());
		gfx.fillRectangle(figureBounds);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintFigure(final Graphics graphics) {
		this.fillShape(graphics);
		this.outlineShape(graphics);
	}
	
	/**
	 * Draws the outline of the image, i.e. the switch itself.
	 * @param gfx The {@link Graphics} to use
	 */
	protected void outlineShape(final Graphics gfx) {
		AntialiasingUtil.getInstance().enableAntialiasing(gfx);
		Rectangle figureBounds = getBounds().getCopy().crop(this.getInsets());
		gfx.translate(figureBounds.getLocation());
		
		if (_resized) {
			/*some trigonometry to determine the new scaling factor*/
			_wdth=(figureBounds.width<figureBounds.height)?(double)figureBounds.width:(double)figureBounds.height;
			_hght=(figureBounds.width<figureBounds.height)?(double)figureBounds.height:(double)figureBounds.width;
			double angle=(double)_rotAngle;
			
			if (_rotAngle<=90 || (_rotAngle>180 && _rotAngle<=270)) {
				_coefficient=_wdth/(_wdth*Trigonometry.sin(-angle+90.0)+_hght*Trigonometry.cos(-angle+90.0));
				_coefficient=Math.abs(_coefficient);
			}
			if ((_rotAngle>90 && _rotAngle<=180) || (_rotAngle>270 && _rotAngle<=360)) {
				_coefficient=_wdth/(_hght*Trigonometry.sin(angle)-_wdth*Trigonometry.cos(angle));
				_coefficient=Math.abs(_coefficient);
			}
			//switch_painter.resize((int)(k*(double)bounds.width),(int)(k*(double)bounds.height));
			_resized=false;
		}
		
		if (_rotAngle!=0) {
			gfx.translate(figureBounds.width/2,figureBounds.height/2);
			try {
				gfx.rotate((float)_rotAngle);
			} catch (RuntimeException e) {
				CentralLogger.getInstance().error(this, "Error occured during ratation");
			}
			gfx.translate(-(int)(_coefficient*(double)figureBounds.width*0.5),-(int)(_coefficient*(double)figureBounds.height*0.5));
		}
		
		if (_switchState==CosySwitch.STATE_UNKNOWN) {
			gfx.setForegroundColor(getForegroundColor());
		} else {
			gfx.setForegroundColor(STATECOLORS.get(_switchState));
		}
		gfx.setBackgroundColor(gfx.getForegroundColor());
		gfx.setLineWidth(getLineWidth());
		_switchPainter.paintSwitch(gfx,_switchState, (int)(_coefficient*(double)figureBounds.width),(int)(_coefficient*(double)figureBounds.height));
		
		if (_rotAngle!=0) {
			gfx.translate((int)(_coefficient*(double)figureBounds.width*0.5),(int)(_coefficient*(double)figureBounds.height*0.5));
			try {
				gfx.rotate(-(float)_rotAngle);
			} catch (RuntimeException e) {
				CentralLogger.getInstance().error(this, "Error occured during rotation");
			}
			gfx.translate(-figureBounds.width/2,-figureBounds.height/2);
		}
	}
	
	/**
	 * Resizes this switch.
	 */
	public void resize() {
		_resized=true;
	}
	
	/**
	 * Sets the type of the switch.
	 * @param newval The new type of the switch
	 */
	public void setType(final int newval) {
		try {
			_switchPainter=(CosySwitch)SwitchPlugins.classes_map.get(SwitchPlugins.ids[newval]).createExecutableExtension("Class");
		} catch (Exception e) {
			_switchPainter=new CosySwitch();
		}
		_switchType=newval;
	}
	
	/**
	 * Returns the current type of the switch.
	 * @return the current type
	 */
	public int getType() {
		return _switchType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLineWidth(final int w) {
		super.setLineWidth(w);
		_switchPainter.setLineWidth(w);
	}
	
	/**
	 * Sets the state of the switch.
	 * @param newval The new state of the switch
	 */
	public void setState(final int newval) {
		if (!STATECOLORS.containsKey(newval)) {
			_switchState=CosySwitch.STATE_UNKNOWN;
			return;
		}
		_switchState=newval;
	}
	
	/**
	 * Returns the current state of the switch.
	 * @return the current state
	 */
	public int getState() {
		return _switchState;
	}
	
	/**
	 * Sets the rotation angle of the switch.
	 * @param newval the rotation angle
	 */
	public void setRotation(final int newval) {
		_rotAngle=newval;
		_resized=true;
	}
	
	/**
	 * Returns the current rotation angle.
	 * @return the current rotation angle
	 */
	public int getRotation() {
		return _rotAngle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if (_borderAdapter == null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}
}
