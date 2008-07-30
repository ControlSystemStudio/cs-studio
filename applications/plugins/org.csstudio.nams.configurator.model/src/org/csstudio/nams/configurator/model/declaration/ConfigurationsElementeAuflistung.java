package org.csstudio.nams.configurator.model.declaration;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;

/**
 * Model für die Auflistungskomponenten der UI für die Elemente einer
 * Konfigurationsart, z.B.: Alarmbearbeiter, Filter.
 * 
 * XXX Das Modell-PlugIn könnte einen OSGi-Service hinterlegen, über den die
 * Views (UI PlugIn) die Modelle bezieht. Somit bliebe die
 * Modell-Exemplar-Erzeugung vollständig im Modell, die UI benutzte die Modelle
 * legiglich.
 * 
 * @see FilteredListVarianteA
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * @param <T>
 *            Der Konfigurationselementtyp.
 */
public class ConfigurationsElementeAuflistung<T extends NewAMSConfigurationElementDTO> {

	private SortedSet<T> elements;
	private SortedSet<T> visibleElements;
	private String currentNameFilter = null;
	private Integer currentCategoryFilter = null;

	/**
	 * Returns the "visible" elements for current filtering settings.
	 * 
	 * @return A by
	 *         {@link NewAMSConfigurationElementDTO#getUniqueHumanReadableName()}
	 *         sorted set to make sure that no element is duplicated; returned
	 *         set is unmodifiable.
	 * 
	 * @see ConfigurationsElementeAuflistung#setCategoryFilter(int)
	 * @see ConfigurationsElementeAuflistung#setNameFilter(String)
	 */
	public SortedSet<T> getVisibleElementsInAscendingOrder() {
		return Collections.unmodifiableSortedSet(this.visibleElements);
	}

	public void releaseCategoryFilter() {
		this.currentCategoryFilter = null;
		this.updateVisibleElements();
	}

	public void releaseNameFilter() {
		this.currentNameFilter = null;
		this.updateVisibleElements();
	}

	/**
	 * 
	 * @param categoryDatabseId
	 *            TODO Besser ein CategoryDTO benutzen
	 */
	public void setCategoryFilter(final int categoryDatabseId) {
		this.currentCategoryFilter = new Integer(categoryDatabseId);
		this.updateVisibleElements();
	}

	/**
	 * 
	 * @param elements
	 *            The Set of elements to show; this Set will be sorted in
	 *            ascending order to the name-property.
	 */
	public void setElements(final Set<T> elements) {
		final Comparator<T> comparator = new Comparator<T>() {
			public int compare(T element1, T element2) {
				return element1.getUniqueHumanReadableName().compareTo(
						element2.getUniqueHumanReadableName());
			}
		};

		this.elements = new TreeSet<T>(comparator);
		this.elements.addAll(elements);

		this.visibleElements = new TreeSet<T>(comparator);
		this.updateVisibleElements();
	}

	public void setNameFilter(final String visibleNameFilter) {
		this.currentNameFilter = visibleNameFilter;
		this.updateVisibleElements();
	}

	private boolean matchCurrentFilterSettings(final T element) {
		boolean result = true;

		if (this.currentNameFilter != null) {
			result = element.getUniqueHumanReadableName().toLowerCase()
					.contains(this.currentNameFilter.toLowerCase());
		}

		if (result && (this.currentCategoryFilter != null)) {
			final int categoryDBId = this.currentCategoryFilter.intValue();

			result = element.isInCategory(categoryDBId);
		}

		return result;
	}

	private void updateVisibleElements() {
		this.visibleElements.clear();
		for (final T element : this.elements) {
			if (this.matchCurrentFilterSettings(element)) {
				this.visibleElements.add(element);
			}
		}
	}

}
