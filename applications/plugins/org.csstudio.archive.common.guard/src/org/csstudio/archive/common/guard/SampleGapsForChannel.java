package org.csstudio.archive.common.guard;

import java.util.ArrayList;
import java.util.List;

public class SampleGapsForChannel {

	String channelName;

	List<SampleGap> _gapList = new ArrayList<>();
	
	public SampleGapsForChannel(String chanName) {
		channelName = chanName;
	}
	
	public void addGap(SampleGap gap) {
		_gapList.add(gap);
	}

	public void addGapList(List<SampleGap> gapList) {
		_gapList = gapList;
	}

	public String getChannelName() {
		return channelName;
	}

	public List<SampleGap> getGapList() {
		return _gapList;
	}
}
