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

public class MoveBendpointCommand extends BendpointCommand {

	private WireBendpoint oldBendpoint;

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		WireBendpoint bp = new WireBendpoint();
		bp.setRelativeDimensions(getFirstRelativeDimension(),
				getSecondRelativeDimension());
		setOldBendpoint(getWire().getBendpoints().get(getIndex()));
		getWire().setBendpoint(getIndex(), bp);
		super.execute();
	}

	protected WireBendpoint getOldBendpoint() {
		return oldBendpoint;
	}

	public void setOldBendpoint(WireBendpoint bp) {
		oldBendpoint = bp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		super.undo();
		getWire().setBendpoint(getIndex(), getOldBendpoint());
	}

}
