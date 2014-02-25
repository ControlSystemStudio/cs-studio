/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.proposals;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.autocomplete.tooltips.TooltipData;
import org.csstudio.autocomplete.tooltips.TooltipDataHandler;
import org.eclipse.core.runtime.Assert;

/**
 * Defines a proposal as it will be displayed.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class Proposal implements Comparable<Proposal> {

	/**
	 * Value that completes the field content (originalValue) and will be
	 * displayed in the main pop-up.
	 */
	private final String value;
	/**
	 * Description that will be displayed in a secondary pop-up if not
	 * <code>null</code>.
	 */
	private String description;
	/**
	 * SWT StyleRange that will be applied to value.
	 */
	private List<ProposalStyle> styles;
	/**
	 * <code>true</code> if the proposal is not a final one (example: CWS- as
	 * part of a PV name CWS-C4CO-...) => display a magnifying glass icon.
	 */
	private final boolean isPartial;
	/**
	 * <code>true</code> if the proposal is a formula function => display a
	 * function icon.
	 */
	private boolean isFunction = false;
	/**
	 * <code>true</code> if the proposal start with the field content => append
	 * instead of replace. Used by top proposal manager.
	 */
	private boolean startWithContent = false;
	/**
	 * Used by top proposals manager to calculate a display priority if one
	 * proposal is provided more than once.
	 */
	private int occurrence;
	/**
	 * Insertion position of value in the field content (original content).
	 */
	private int insertionPos = 0;
	/**
	 * Value submitted for auto-completion.
	 */
	private String originalValue = "";
	/**
	 * Data that will be processed by {@link TooltipDataHandler} when the
	 * proposal is selected in the UI.
	 */
	private List<TooltipData> tooltips;

	public Proposal(String value, boolean isPartial) {
		Assert.isNotNull(value);
		Assert.isTrue(!value.isEmpty());
		Assert.isNotNull(isPartial);
		this.value = value;
		this.isPartial = isPartial;
		this.occurrence = 1;
		this.styles = new ArrayList<ProposalStyle>();
		this.tooltips = new ArrayList<TooltipData>();
	}

	public int getInsertionPos() {
		return insertionPos < 0 ? 0 : insertionPos;
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

	public void addStyle(ProposalStyle style) {
		this.styles.add(style);
	}

	public void addTooltipData(TooltipData td) {
		tooltips.add(td);
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

	public boolean isFunction() {
		return isFunction;
	}

	public void setFunction(boolean isFunction) {
		this.isFunction = isFunction;
	}

	public boolean getStartWithContent() {
		return startWithContent;
	}

	public void setStartWithContent(boolean startWithContent) {
		this.startWithContent = startWithContent;
	}

	public List<ProposalStyle> getStyles() {
		return styles;
	}

	public List<TooltipData> getTooltips() {
		return tooltips;
	}

	public void setInsertionPos(int insertionPos) {
		this.insertionPos = insertionPos;
	}

	public String getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
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

	@Override
	public String toString() {
		return "Proposal [value=" + value + ", description=" + description
				+ ", styles=" + styles + ", isPartial=" + isPartial
				+ ", isFunction=" + isFunction + ", startWithContent="
				+ startWithContent + ", occurrence=" + occurrence
				+ ", insertionPos=" + insertionPos + ", originalValue="
				+ originalValue + ", tooltips=" + tooltips + "]";
	}

}
