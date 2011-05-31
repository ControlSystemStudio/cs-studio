package org.csstudio.multichannelviewer;

import gov.bnl.channelfinder.api.Channel;

import org.eclipse.swt.SWT;

public class GlazedSortOwnerComparator implements IDirectionalComparator<Channel> {

	private int col;
	private int direction;

	public GlazedSortOwnerComparator(int col, int direction) {
		this.col = col;
		this.direction = direction;
	}

	@Override
	public void setDirection(int dir) {
		this.direction = dir;
	}

	@Override
	public int compare(Channel channel1, Channel channel2) {
		int ret = channel1.getOwner().compareTo(
				channel2.getOwner());
		if (direction == SWT.DOWN)
			ret = -ret;

		return ret;
	}
}
