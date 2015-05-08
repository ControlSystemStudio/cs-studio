/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
