/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.csstudio.diag.diles.commands;

import org.csstudio.diag.diles.model.WireBendpoint;

public class CreateBendpointCommand extends BendpointCommand {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		WireBendpoint wbp = new WireBendpoint();
		wbp.setRelativeDimensions(getFirstRelativeDimension(),
				getSecondRelativeDimension());
		getWire().insertBendpoint(getIndex(), wbp);
		super.execute();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		super.undo();
		getWire().removeBendpoint(getIndex());
	}

}
