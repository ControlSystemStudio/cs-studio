/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.model;


/**The abstract widget model for all PV related widgets. 
 * @author Xihui Chen
 *
 */
public abstract class AbstractPVWidgetModel extends AbstractWidgetModel implements IPVWidgetModel{


	private PVWidgetModelDelegate delegate;
	
	public AbstractPVWidgetModel() {
	}
	
	public PVWidgetModelDelegate getDelegate(){
		if(delegate == null)
			delegate = new PVWidgetModelDelegate(this);
		return delegate;
	}
	
	@Override
	protected void configureBaseProperties() {
		super.configureBaseProperties();		
		getDelegate().configureBaseProperties();
	}

	public boolean isBorderAlarmSensitve(){
		return getDelegate().isBorderAlarmSensitve();
	}
	
	public boolean isForeColorAlarmSensitve(){
		return getDelegate().isForeColorAlarmSensitve();
	}
	
	public boolean isBackColorAlarmSensitve(){
		return getDelegate().isBackColorAlarmSensitve();
	}
	
	public String getPVName(){
		return getDelegate().getPVName();
	}
}
