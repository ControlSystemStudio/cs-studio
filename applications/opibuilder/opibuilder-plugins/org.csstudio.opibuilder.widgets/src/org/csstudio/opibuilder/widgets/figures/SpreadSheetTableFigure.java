/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.swt.widgets.natives.SpreadSheetTable;
import org.eclipse.swt.widgets.Composite;

/**A figure that holds a {@link SpreadSheetTable}.
 * @author Xihui Chen
 *
 */
public class SpreadSheetTableFigure extends AbstractSWTWidgetFigure<SpreadSheetTable> {

	public SpreadSheetTableFigure(AbstractBaseEditPart editpart) {
		super(editpart);
	}

	@Override
	protected SpreadSheetTable createSWTWidget(Composite parent, int style) {
		return new SpreadSheetTable(parent);
	}	
}
