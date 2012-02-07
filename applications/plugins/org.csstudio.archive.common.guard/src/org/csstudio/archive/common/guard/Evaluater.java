package org.csstudio.archive.common.guard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.domain.desy.time.TimeInstant;

public class Evaluater {

	public void analyse(List<SampleGapsForChannel> lostSamples) {
		for (SampleGapsForChannel sampleGapsForChannel : lostSamples) {
			System.out.println("Channel: " + sampleGapsForChannel.getChannelName());
			List<SampleGap> gapList = sampleGapsForChannel.getGapList();
			for (SampleGap sampleGap : gapList) {
				System.out.println("Gap : " + sampleGap.getStart() + " - " + sampleGap.getEnd());
			}
		}
	}

	public void aggregateGapsForRange(List<SampleGapsForChannel> lostSamples) {
		Map<TimeInstant, Integer> aggregatedGaps = new HashMap<TimeInstant, Integer>();
		for (SampleGapsForChannel sampleGapsForChannel : lostSamples) {
			List<SampleGap> gapList = sampleGapsForChannel.getGapList();
			for (SampleGap sampleGap : gapList) {
				Integer sum = aggregatedGaps.get(sampleGap.getStart());
				if (sum == null) {
					sum = 1;
				} else {
					sum++;
				}
				aggregatedGaps.put(sampleGap.getStart(), sum);
			}
		}
		Set<TimeInstant> keySet = aggregatedGaps.keySet();
		for (TimeInstant timeInstant : keySet) {
			Integer integer = aggregatedGaps.get(timeInstant);
			System.out.println("range : " + timeInstant.getSeconds() + " sum: " + integer);
		}
	}

}
