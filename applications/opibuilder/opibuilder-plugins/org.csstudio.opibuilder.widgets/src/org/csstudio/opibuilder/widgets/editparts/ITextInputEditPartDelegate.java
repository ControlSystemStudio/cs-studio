/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.opibuilder.widgets.editparts;

import org.eclipse.draw2d.IFigure;

/**The delegate interface that describes the common functions of Native Text and draw2d Text Input.
 * @author Xihui Chen
 *
 */
public interface ITextInputEditPartDelegate {

    public IFigure doCreateFigure();

    public void registerPropertyChangeHandlers();

    public void updatePropSheet();

    public void createEditPolicies();


}