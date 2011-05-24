package org.csstudio.multichannelviewer;

import gov.bnl.channelfinder.api.Channel;

import org.eclipse.swt.SWT;


public class GlazedSortNameComparator implements IDirectionalComparator<Channel> {

	private int col;
	private int direction;	

	public GlazedSortNameComparator(int col, int direction) {
		this.col = col;
		this.direction = direction;
	}
	
	@Override
	public int compare(Channel channel1, Channel channel2) {
		int ret = channel1.getName().compareTo(channel2.getName());
		if (direction == SWT.DOWN)
			ret = -ret;
		
		return ret;
	}

	@Override
	public void setDirection(int dir) {
		this.direction = dir;
	}

}
