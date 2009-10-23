package org.csstudio.opibuilder.widgets.figures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.Position;

import org.csstudio.opibuilder.widgets.Activator;
import org.csstudio.opibuilder.widgets.figures.AbstractBoolControlFigure.IBoolControlListener;
import org.csstudio.opibuilder.widgets.model.AbstractBoolWidgetModel;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Image;
/**
 * Base figure for a widget based on {@link AbstractBoolWidgetModel}.
 * 
 * @author Xihui Chen
 *
 */
public class CheckBoxFigure extends Label {
	
	protected long value = 0;
	
	protected int bit = -1;	
	
	protected boolean boolValue = false;
	
	private static Image checked = CustomMediaFactory.getInstance().getImageFromPlugin(
			Activator.getDefault(), Activator.PLUGIN_ID, "icons/checkboxenabledon.gif");
	
	private static Image unChecked = CustomMediaFactory.getInstance().getImageFromPlugin(
			Activator.getDefault(), Activator.PLUGIN_ID, "icons/checkboxenabledoff.gif");
	
		/**
	 * Listeners that react on manual boolean value change events.
	 */
	private List<IBoolControlListener> boolControlListeners = 
		new ArrayList<IBoolControlListener>();
	
	public CheckBoxFigure() {
		setIcon(unChecked);
		setLabelAlignment(PositionConstants.LEFT);
		addMouseListener(new MouseListener.Stub(){
			@Override
			public void mousePressed(MouseEvent me) {
				fireManualValueChange(!boolValue);
				requestFocus();
			}
		});
	}

	
	/**add a boolean control listener which will be executed when pressed or released
	 * @param listener the listener to add
	 */
	public void addBoolControlListener(final IBoolControlListener listener){
		boolControlListeners.add(listener);
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
		for (IBoolControlListener l : boolControlListeners) {					
			l.valueChanged(value);
		}			
		
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	/**
	 * @return the boolValue
	 */
	public boolean getBoolValue() {
		return boolValue;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = (long) value;
		updateBoolValue();			
	}

	

	/**
	 * @param bit the bit to set
	 */
	public void setBit(int bit) {
		this.bit = bit;
		updateBoolValue();
	}
	
	/**
	 * update the boolValue from value and bit. 
	 * All the boolValue based behavior changes should be implemented here by inheritance.
	 */
	protected void updateBoolValue() {
		//get boolValue
		if(bit == -1)
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
	private void updateValue(){
		//get boolValue
		if(bit == -1)
			setValue(boolValue ? 1 : 0);
		else if(bit >=0) {
			char[] binArray = Long.toBinaryString(value).toCharArray();
			if(bit >= 64 || bit <-1)
				try {
					throw new Exception("bit is out of range: [-1,63]");
				} catch (Exception e) {
					CentralLogger.getInstance().error(this, e);
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
	
	

}
