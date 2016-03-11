/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.ITextFigure;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**
 * A action button figure.
 *
 * @author Sven Wende
 *
 */
public final class RefreshableActionButtonFigure extends Button implements
        IAdaptable, ITextFigure {

    /**
     * Default label font.
     */
    public static final Font FONT = CustomMediaFactory.getInstance().getFont(
            "Arial", 8, SWT.NONE); //$NON-NLS-1$

    /**
     * The Label for the Button.
     */
    private final Label _label;

    /**
     * An Array, which contains the PositionConstants for Center, Top, Bottom, Left, Right.
     */
    private final int[] _alignments = new int[] {PositionConstants.CENTER, PositionConstants.TOP, PositionConstants.BOTTOM, PositionConstants.LEFT, PositionConstants.RIGHT};

    private CrossedOutAdapter _crossedOutAdapter;

    private RhombusAdapter _rhombusAdapter;

    /**
     * Constructor.
     */
    public RefreshableActionButtonFigure() {
        _label = new Label("");
        setContents(_label);
        setFont(FONT);
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
       _crossedOutAdapter.paint(graphics);
       _rhombusAdapter.paint(graphics);
    }

    /**
     * Sets the text for the Button.
     * @param s
     *             The text for the button
     */
    @Override
    public void setTextValue(final String s) {
        _label.setText(s);
    }

    public void setPressed(final boolean pressed){
        getModel().setPressed(pressed);
    }

    /**
     * Sets the alignment of the buttons text.
     * The parameter is a {@link PositionConstants} (LEFT, RIGHT, TOP, CENTER, BOTTOM)
     * @param alignment
     *             The alignment for the text
     */
    public void setTextAlignment(final int alignment) {
        if ((alignment>=0) && (alignment<_alignments.length)) {
            if ((_alignments[alignment]==PositionConstants.LEFT) || (_alignments[alignment]==PositionConstants.RIGHT)) {
                _label.setTextPlacement(PositionConstants.NORTH);
            } else {
                _label.setTextPlacement(PositionConstants.EAST);
            }
            _label.setTextAlignment(_alignments[alignment]);
        }
    }

    /**
     * Set the style of the Button.
     * @param style false = Push, true=Toggle.
     */
    public void setStyle(final boolean style){
        if(style){
            setStyle(Button.STYLE_TOGGLE);
        }else{
            setStyle(Button.STYLE_BUTTON);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(final Class adapter) {
        if(adapter == ICrossedFigure.class) {
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
