/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.components.ui.internal.figures;

import java.util.HashMap;

import org.csstudio.sds.components.common.CosySwitch;
import org.csstudio.sds.components.common.SwitchPlugins;
import org.csstudio.sds.components.ui.internal.utils.Trigonometry;
import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A switch figure.
 *
 * @author jbercic
 *
 */
public final class RefreshableSwitchFigure extends Shape implements IAdaptable {

    private static final Logger LOG = LoggerFactory.getLogger(RefreshableSwitchFigure.class);

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
    private double _scaling = 1.0;

    /**
     * True if the switch was resized after last paint event.
     */
    private boolean _resized=true;

    private boolean _transparent = true;

    private CrossedOutAdapter _crossedOutAdapter;

    private RhombusAdapter _rhombusAdapter;

    public RefreshableSwitchFigure() {
    }

    /**
     * Fills the background.
     * @param gfx The {@link Graphics} to use
     */
    @Override
    protected void fillShape(final Graphics gfx) {
        if (!_transparent) {
            gfx.setBackgroundColor(getBackgroundColor());
            Rectangle figureBounds = getBounds().getCopy();
            figureBounds.crop(this.getInsets());
            gfx.fillRectangle(figureBounds);
        }
        _crossedOutAdapter.paint(gfx);
        _rhombusAdapter.paint(gfx);


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

        gfx.translate(figureBounds.width/2,figureBounds.height/2);

        int scalingDegreeOffset = 90;
        if ((_rotAngle >= 90 && _rotAngle < 180) || (_rotAngle >= 270 && _rotAngle < 360)) {
            int tmpWidth = figureBounds.width;
            figureBounds.width = figureBounds.height;
            figureBounds.height = tmpWidth;
            scalingDegreeOffset = 0;
        }

        if (_resized) {
            /*some trigonometry to determine the new scaling factor*/
            double longSide  = (figureBounds.width<figureBounds.height)?(double)figureBounds.width:(double)figureBounds.height;
            double shortSide = (figureBounds.width<figureBounds.height)?(double)figureBounds.height:(double)figureBounds.width;

            _scaling=longSide / (longSide * Trigonometry.sin(-_rotAngle + scalingDegreeOffset) + shortSide * Trigonometry.cos(-_rotAngle + scalingDegreeOffset));
            _scaling=Math.abs(_scaling);
            _resized=false;
        }

        try {
            gfx.rotate(_rotAngle);
        } catch (RuntimeException e) {
            LOG.error("Error occurred during rotation");
        }
        gfx.translate(-(int)(_scaling*figureBounds.width*0.5),-(int)(_scaling*figureBounds.height*0.5));


        if (_switchState==CosySwitch.STATE_UNKNOWN) {
            gfx.setForegroundColor(getForegroundColor());
        } else {
            gfx.setForegroundColor(STATECOLORS.get(_switchState));
        }
        gfx.setBackgroundColor(gfx.getForegroundColor());
        gfx.setLineWidth(getLineWidth());
        _switchPainter.paintSwitch(gfx,_switchState, (int)(_scaling*figureBounds.width),(int)(_scaling*figureBounds.height));

        if (_rotAngle!=0) {
            gfx.translate((int)(_scaling*figureBounds.width*0.5),(int)(_scaling*figureBounds.height*0.5));
            try {
                gfx.rotate(-(float)_rotAngle);
            } catch (RuntimeException e) {
                LOG.error("Error occurred during rotation");
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
     * Sets, if this widget should have a transparent background.
     * @param transparent
     *                 The new value for the transparent property
     */
    public void setTransparent(final boolean transparent) {
        _transparent = transparent;
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
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        } else if(adapter == ICrossedFigure.class) {
            if(_crossedOutAdapter==null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if(adapter == IRhombusEquippedWidget.class) {
            if(_rhombusAdapter==null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }

        return null;
    }

}
