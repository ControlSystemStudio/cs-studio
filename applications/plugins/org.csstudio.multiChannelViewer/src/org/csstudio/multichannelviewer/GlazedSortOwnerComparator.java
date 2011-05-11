package org.csstudio.multichannelviewer;

import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.swt.SWT;

public class GlazedSortOwnerComparator implements IDirectionalComparator<ICSSChannel> {

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
	public int compare(ICSSChannel channel1, ICSSChannel channel2) {
		int ret = channel1.getChannel().getOwner().compareTo(
				channel2.getChannel().getOwner());
		if (direction == SWT.DOWN)
			ret = -ret;

		return ret;
	}
}
