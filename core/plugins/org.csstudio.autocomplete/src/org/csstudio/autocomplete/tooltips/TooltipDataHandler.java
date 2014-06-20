/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.tooltips;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.csstudio.autocomplete.proposals.ProposalStyle;


/**
 * Handles a list of {@link TooltipData} to provide a {@link TooltipContent}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class TooltipDataHandler {

	private List<TooltipData> tooltipDataList;

	public TooltipDataHandler() {
		this.tooltipDataList = Collections
				.synchronizedList(new ArrayList<TooltipData>());
	}

	public void addData(TooltipData data) {
		tooltipDataList.add(data);
	}

	public void clearData() {
		tooltipDataList.clear();
	}

	public TooltipContent generateTooltipContent(String fieldContent) {
		if (tooltipDataList.isEmpty() || fieldContent == null
				|| fieldContent.trim().isEmpty())
			return null; // no content

		// build content
		int offset = 0, maxLineLenght = 0, numberOfLines = 0;
		StringBuilder sb = new StringBuilder();
		List<ProposalStyle> styleList = new ArrayList<ProposalStyle>();
		synchronized (tooltipDataList) {
			for (TooltipData data : tooltipDataList) {
				int startLenght = sb.length();
				sb.append(data.value);
				sb.append("\n");
				if (data.styles != null) {
					for (ProposalStyle style : data.styles) {
						ProposalStyle ps = new ProposalStyle(style);
						ps.from += offset;
						ps.to += offset;
						styleList.add(ps);
					}
				}
				offset += sb.length() - startLenght;
				maxLineLenght = Math.max(maxLineLenght, sb.length() - startLenght);
				numberOfLines++;
			}
		}

		if (sb.length() == 0) {
			return null; // no content
		}

		// delete last \n
		sb.deleteCharAt(sb.length() - 1);

		TooltipContent tc = new TooltipContent();
		tc.value = sb.toString();
		tc.styles = styleList.toArray(new ProposalStyle[styleList.size()]);
		tc.numberOfLines = numberOfLines;
		tc.maxLineLenght = maxLineLenght;
		return tc;
	}

}
