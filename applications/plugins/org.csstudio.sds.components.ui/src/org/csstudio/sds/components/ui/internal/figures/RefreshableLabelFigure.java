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

import java.text.NumberFormat;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**
 * A label figure.
 * 
 * @author Sven Wende, Alexander Will, Stefan Hofer
 * 
 */
public final class RefreshableLabelFigure extends Label implements
		IAdaptable {
	/**
	 * Default label font.
	 */
	public static final Font FONT = CustomMediaFactory.getInstance().getFont(
			"Arial", 8, SWT.NONE); //$NON-NLS-1$

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	/**
	 * The potenz for the precision.
	 */
	private int _decimalPlaces = 2;
	
	/**
	 * The text for the Label.
	 */
	private String _valueText;

	/**
	 * An Array, which contains the PositionConstants for Center, Top, Bottom, Left, Right.
	 */
	private final int[] _alignments = new int[] {PositionConstants.CENTER, PositionConstants.TOP, PositionConstants.BOTTOM, PositionConstants.LEFT, PositionConstants.RIGHT};
	
	/**
	 * Constructor.
	 */
	public RefreshableLabelFigure() {
		setFont(FONT);
	}

	/**
	 * This method is a tribute to unit tests, which need a way to test the
	 * performance of the figure implementation. Implementors should produce
	 * some random changes and refresh the figure, when this method is called.
	 * 
	 */
	public void randomNoiseRefresh() {
		setText("" + Math.random()); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(final String s) {
		_valueText = s;
		this.updateValueText();
		//super.setText(s);
	}
	
	/**
	 * Sets the count of decimal places for this Figure.
	 * 
	 * @param decimalPlaces
	 *            The precision
	 */
	public void setDecimalPlaces(final int decimalPlaces) {
		_decimalPlaces = decimalPlaces;
		this.updateValueText();
	}
	
	/**
	 * Gets the precision of this Figure.
	 * 
	 * @return The precision
	 */
	public int getDecimalPlaces() {
		return _decimalPlaces;
	}
	
	/**
	 * Updates the value labels text.
	 */
	private void updateValueText() {
		// update the value label text
		try {
			double d = Double.parseDouble(_valueText);
			NumberFormat format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(_decimalPlaces);
			super.setText(format.format(d)); //$NON-NLS-1$
		} catch (Exception e) {
			super.setText(_valueText);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTextAlignment(final int alignment) {
		if (alignment>=0 && alignment<_alignments.length) {
			if (_alignments[alignment]==PositionConstants.LEFT || _alignments[alignment]==PositionConstants.RIGHT) {
				super.setTextPlacement(PositionConstants.NORTH);
			} else {
				super.setTextPlacement(PositionConstants.EAST);
			}
			super.setTextAlignment(_alignments[alignment]);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if(_borderAdapter==null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}
}
