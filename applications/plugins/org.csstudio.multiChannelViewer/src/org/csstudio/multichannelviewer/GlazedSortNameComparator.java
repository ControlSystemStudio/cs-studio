package org.csstudio.multichannelviewer;

import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.swt.SWT;


public class GlazedSortNameComparator implements IDirectionalComparator<ICSSChannel> {

	private int col;
	private int direction;	

	public GlazedSortNameComparator(int col, int direction) {
		this.col = col;
		this.direction = direction;
	}
	
	@Override
	public int compare(ICSSChannel channel1, ICSSChannel channel2) {
		int ret = channel1.getChannel().getName().compareTo(channel2.getChannel().getName());
		if (direction == SWT.DOWN)
			ret = -ret;
		
		return ret;
	}

	@Override
	public void setDirection(int dir) {
		this.direction = dir;
	}

}
