/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames.ui;

import org.eclipse.jface.fieldassist.IContentProposal;

public class PVContentProposalList {

	private IContentProposal[] proposals;
	private int count;
	
	public IContentProposal[] getProposals() {
		return proposals;
	}
	public void setProposals(IContentProposal[] proposals) {
		this.proposals = proposals;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
