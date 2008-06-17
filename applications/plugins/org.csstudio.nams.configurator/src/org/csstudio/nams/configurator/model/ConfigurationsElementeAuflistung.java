package org.csstudio.nams.configurator.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;

public class ConfigurationsElementeAuflistung<T> {

	private SortedSet<AlarmbearbeiterDTO> elements;
	private SortedSet<AlarmbearbeiterDTO> visibleElements;
	private String currentNameFilter = null;
	private Integer currentCategoryFilter = null;

	/**
	 * 
	 * @param elements
	 *            The Set of elements to show; this Set will be sorted in
	 *            ascending order to the name-property.
	 */
	public void setElements(Set<AlarmbearbeiterDTO> elements) {
		Comparator<AlarmbearbeiterDTO> comparator = new Comparator<AlarmbearbeiterDTO>() {
			public int compare(AlarmbearbeiterDTO bearbeiter1,
					AlarmbearbeiterDTO bearbeiter2) {
				return bearbeiter1.getUserName().compareTo(
						bearbeiter2.getUserName());
			}
		};

		this.elements = new TreeSet<AlarmbearbeiterDTO>(comparator);
		this.elements.addAll(elements);

		this.visibleElements = new TreeSet<AlarmbearbeiterDTO>(comparator);
		this.updateVisibleElements();
	}

	public SortedSet<AlarmbearbeiterDTO> getVisibleElementsInAscendingOrder() {
		return Collections.unmodifiableSortedSet(this.visibleElements);
	}

	public void setNameFilter(String visibleNameFilter) {
		this.currentNameFilter = visibleNameFilter;
		this.updateVisibleElements();
	}

	private void updateVisibleElements() {
		this.visibleElements.clear();
		for (AlarmbearbeiterDTO alarmbearbeiter : this.elements) {
			if (matchCurrentFilterSettings(alarmbearbeiter)) {
				this.visibleElements.add(alarmbearbeiter);
			}
		}
	}

	private boolean matchCurrentFilterSettings(
			AlarmbearbeiterDTO alarmbearbeiter) {
		boolean result = true;
		
		if (this.currentNameFilter != null) {
			result = alarmbearbeiter.getUserName().toLowerCase().contains(this.currentNameFilter.toLowerCase());
		}
		
		if( result && this.currentCategoryFilter != null ) {
			int categoryDBId = this.currentCategoryFilter.intValue();
			
			result = alarmbearbeiter.isInCategory(categoryDBId);
		}
		
		return result;
	}

	/**
	 * 
	 * @param categoryDatabseId
	 *            TODO Besser ein CategoryDTO benutzen
	 */
	public void setCategoryFilter(int categoryDatabseId) {
		this.currentCategoryFilter = new Integer(categoryDatabseId);
		this.updateVisibleElements();
	}

	public void releaseNameFilter() {
		this.currentNameFilter = null;
		this.updateVisibleElements();
	}

	public void releaseCategoryFilter() {
		this.currentCategoryFilter = null;
		this.updateVisibleElements();
	}

}
