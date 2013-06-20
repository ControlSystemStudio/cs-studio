/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

public class Proposal implements Comparable<Proposal> {

	private final String value;
	private String description;
	private List<ProposalStyle> styles;
	private final boolean isPartial;
	private boolean startWithContent = false;
	private int occurrence;

	public Proposal(String value, boolean isPartial) {
		Assert.isNotNull(value);
		Assert.isTrue(!value.isEmpty());
		Assert.isNotNull(isPartial);
		this.value = value;
		this.isPartial = isPartial;
		this.occurrence = 1;
		this.styles = new ArrayList<ProposalStyle>();
	}

	public String getValue() {
		return value;
	}

	public boolean isPartial() {
		return isPartial;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void increment() {
		occurrence++;
	}

	public void decrement() {
		occurrence--;
	}

	public int getOccurrence() {
		return occurrence;
	}
	
	public boolean getStartWithContent() {
		return startWithContent;
	}

	public void setStartWithContent(boolean startWithContent) {
		this.startWithContent = startWithContent;
	}

	public void addStyle(ProposalStyle style) {
		this.styles.add(style);
	}

	public List<ProposalStyle> getStyles() {
		return styles;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Proposal other = (Proposal) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public int compareTo(Proposal arg0) {
		if (this.startWithContent && !arg0.getStartWithContent()) {
			return -1;
		} else if (!this.startWithContent && arg0.getStartWithContent()) {
			return 1;
		} else {
			if (this.occurrence > arg0.getOccurrence()) {
				return -1;
			} else if (this.occurrence < arg0.getOccurrence()) {
				return 1;
			} else {
				return this.value.compareTo(arg0.value);
			}
		}
	}

}
