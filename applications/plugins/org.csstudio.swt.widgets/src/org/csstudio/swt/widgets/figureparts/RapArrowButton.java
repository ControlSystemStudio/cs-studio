package org.csstudio.swt.widgets.figureparts;

import org.csstudio.swt.widgets.util.RapButtonModel;
import org.eclipse.draw2d.ArrowButton;
import org.eclipse.draw2d.ButtonModel;
import org.eclipse.draw2d.ToggleModel;
import org.eclipse.swt.SWT;

public class RapArrowButton extends ArrowButton{
	
	
	public RapArrowButton() {
		super();
	}
	public RapArrowButton(int direction) {
		super(direction);
	}

	@Override
	protected ButtonModel createDefaultModel() {
		if(SWT.getPlatform().startsWith("rap")){
			if (isStyle(STYLE_TOGGLE))
				return new ToggleModel();
			else
				return new RapButtonModel();
		}			
		return super.createDefaultModel();
	}
	
}
