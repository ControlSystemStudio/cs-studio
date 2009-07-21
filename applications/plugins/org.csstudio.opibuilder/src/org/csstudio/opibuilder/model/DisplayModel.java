package org.csstudio.opibuilder.model;

public class DisplayModel extends AbstractContainerModel {
	
	/**
	 * The type ID of this model.
	 */
	public static final String ID = "org.csstudio.opibuilder.displaymodel"; //$NON-NLS-1$
	
	
	public DisplayModel() {
		super();
		setLocation(0, 0);
		setSize(800, 600);
	}

	@Override
	protected void configureProperties() {
		
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	


}
