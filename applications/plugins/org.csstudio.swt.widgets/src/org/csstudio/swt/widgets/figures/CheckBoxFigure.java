/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.introspection.LabelWidgetIntrospector;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Image;
/**
 * Figure for a check box.
 *
 * @author Xihui Chen
 *
 */
public class CheckBoxFigure extends Label implements Introspectable, ITextFigure{

	protected long value = 0;

	protected int bit = -1;

	protected boolean boolValue = false;

	static final Image
	unChecked = createImage("icons/checkboxenabledoff.gif"), //$NON-NLS-1$
	checked = createImage("icons/checkboxenabledon.gif"); //$NON-NLS-1$

	private static Image createImage(String name) {
		InputStream stream = CheckBoxFigure.class.getResourceAsStream(name);
		Image image = new Image(null, stream);
		try {
			stream.close();
		} catch (IOException ioe) {
		}
		return image;
	}


//	private static Image checked = CustomMediaFactory.getInstance().getImageFromPlugin(
//			Activator.getDefault(), Activator.PLUGIN_ID, "icons/checkboxenabledon.gif");
//
//	private static Image unChecked = CustomMediaFactory.getInstance().getImageFromPlugin(
//			Activator.getDefault(), Activator.PLUGIN_ID, "icons/checkboxenabledoff.gif");

		/**
	 * Listeners that react on manual boolean value change events.
	 */
	private List<IManualValueChangeListener> boolControlListeners =
		new ArrayList<IManualValueChangeListener>();

	private boolean runMode;

	public CheckBoxFigure() {
		setIcon(unChecked);
		setLabelAlignment(PositionConstants.LEFT);
		addMouseListener(new MouseListener.Stub(){
			@Override
			public void mousePressed(MouseEvent me) {
				if(!runMode)
					return;
				if (me.button != 1)
					return;
				me.consume();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				if (me.button != 1)
					return;
				if(runMode){
					fireManualValueChange(!boolValue);
					requestFocus();
				}

			}
		});
	}

	/**add a boolean control listener which will be executed when pressed or released
	 * @param listener the listener to add
	 */
	public void addManualValueChangeListener(final IManualValueChangeListener listener){
		boolControlListeners.add(listener);
	}

	public void removeManualValueChangeListener(final IManualValueChangeListener listener){
		if(boolControlListeners.contains(listener))
			boolControlListeners.remove(listener);
	}


	/**
	 * Inform all boolean control listeners, that the manual value has changed.
	 *
	 * @param newManualValue
	 *            the new manual value
	 */
	protected void fireManualValueChange(final boolean newManualValue) {
		boolValue = newManualValue;
		updateValue();
		for (IManualValueChangeListener l : boolControlListeners) {
			l.manualValueChanged(value);
		}

	}

	/**
	 * @return the bit
	 */
	public int getBit() {
		return bit;
	}

	/**
	 * @return the boolValue
	 */
	public boolean getBoolValue() {
		return boolValue;
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	/**
	 * @return the runMode
	 */
	public boolean isRunMode() {
		return runMode;
	}

	/**
	 * @param bit the bit to set
	 */
	public void setBit(int bit) {
		if(this.bit == bit)
			return;
		this.bit = bit;
		updateBoolValue();
	}

	public void setBoolValue(boolean boolValue) {
		if(this.boolValue == boolValue)
			return;
		this.boolValue = boolValue;
		updateValue();
	}
	
	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		repaint();
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(boolean runMode) {
		if(this.runMode == runMode)
			return;
		this.runMode = runMode;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		setValue((long)value);
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(long value) {
		if(this.value == value)
			return;
		this.value = value;
		updateBoolValue();
		repaint();
	}

	/**
	 * update the boolValue from value and bit.
	 * All the boolValue based behavior changes should be implemented here by inheritance.
	 */
	protected void updateBoolValue() {
		//get boolValue
		if(bit <0 )
			boolValue = (this.value != 0);
		else if(bit >=0) {
			char[] binArray = Long.toBinaryString(this.value).toCharArray();
			if(bit >= binArray.length)
				boolValue = false;
			else {
				boolValue = (binArray[binArray.length - 1 - bit] == '1');
			}
		}
		updateImage();
	}

	private void updateImage() {
		if(boolValue)
			setIcon(checked);
		else
			setIcon(unChecked);
	}

	/**
	 * update the value from boolValue
	 */
	@SuppressWarnings("nls")
    private void updateValue(){
		//get boolValue
		if(bit < 0)
			setValue(boolValue ? 1 : 0);
		else if(bit >=0) {
			char[] binArray = Long.toBinaryString(value).toCharArray();
			if(bit >= 64 ) {
			    // Log with exception to obtain call stack
			    Activator.getLogger().log(Level.WARNING, "Bit " + bit + " exceeds 63.", new Exception());
            }
			else {
				char[] bin64Array = new char[64];
				Arrays.fill(bin64Array, '0');
				for(int i=0; i<binArray.length; i++){
					bin64Array[64-binArray.length + i] = binArray[i];
				}
				bin64Array[63-bit] = boolValue? '1' : '0';
				String binString = new String(bin64Array);

				if( binString.indexOf('1') <= -1){
					binArray = new char[]{'0'};
				}else {
					binArray = new char[64 - binString.indexOf('1')];
					for(int i=0; i<binArray.length; i++){
						binArray[i] = bin64Array[i+64-binArray.length];
					}
				}

				binString = new String(binArray);
				setValue(Long.parseLong(binString, 2));
			}
		}
		updateImage();
	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new LabelWidgetIntrospector().getBeanInfo(this.getClass());
	}



}
