/**
 * 
 */
package org.csstudio.channel.opiwidgets;


/**
 * @author shroffk
 * 
 */
public class TunerModel extends AbstractChannelWidgetModel {
	
	public TunerModel() {
		super(true);
	}

	public final String ID = "org.csstudio.channel.opiwidgets.AbstractChannelWidget"; //$NON-NLS-1$
	@Override
	protected void configureProperties() {
	}

	@Override
	public String getTypeID() {
		return ID;
	}

}
