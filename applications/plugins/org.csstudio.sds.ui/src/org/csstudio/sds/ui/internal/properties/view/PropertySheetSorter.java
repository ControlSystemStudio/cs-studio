package org.csstudio.sds.ui.internal.properties.view;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Class used by
 * {@link org.csstudio.sds.ui.internal.properties.view.PropertySheetPage} to
 * sort properties.
 * <p>
 * The default implementation sorts alphabetically. Subclasses may overwrite to
 * implement custom sorting.
 * </p>
 * 
 * @since 3.1
 * 
 * @author Sven Wende
 */
public final class PropertySheetSorter {

	/**
	 * The collator used to sort strings.
	 */
	private Collator _collator;

	/**
	 * Creates a new sorter, which uses the default collator to sort strings.
	 */
	public PropertySheetSorter() {
		this(Collator.getInstance());
	}

	/**
	 * Creates a new sorter, which uses the given collator to sort strings.
	 * 
	 * @param collator
	 *            the collator to use to sort strings
	 */
	public PropertySheetSorter(final Collator collator) {
		this._collator = collator;
	}

	/**
	 * Returns a negative, zero, or positive number depending on whether the
	 * first element is less than, equal to, or greater than the second element.
	 * <p>
	 * The default implementation of this method uses the collator to compare
	 * the display names. Subclasses may override.
	 * </p>
	 * 
	 * @param entryA
	 *            the first element
	 * @param entryB
	 *            the second element
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	public int compare(final IPropertySheetEntry entryA,
			final IPropertySheetEntry entryB) {
		return getCollator().compare(entryA.getDisplayName(),
				entryB.getDisplayName());
	}

	/**
	 * Returns a negative, zero, or positive number depending on whether the
	 * first element is less than, equal to, or greater than the second element.
	 * <p>
	 * The default implementation of this method uses the collator to compare
	 * the strings. Subclasses may override.
	 * </p>
	 * 
	 * @param categoryA
	 *            the first element
	 * @param categoryB
	 *            the second element
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	public int compareCategories(final String categoryA, final String categoryB) {
		return getCollator().compare(categoryA, categoryB);
	}

	/**
	 * Returns the collator used to sort strings.
	 * 
	 * @return the collator used to sort strings
	 */
	protected Collator getCollator() {
		return _collator;
	}

	/**
	 * Sorts the given elements in-place, modifying the given array.
	 * <p>
	 * The default implementation of this method uses the java.util.Arrays#sort
	 * algorithm on the given array, calling <code>compare</code> to compare
	 * elements.
	 * </p>
	 * <p>
	 * Subclasses may reimplement this method to provide a more optimized
	 * implementation.
	 * </p>
	 * 
	 * @param entries
	 *            the elements to sort
	 */
	@SuppressWarnings("unchecked")
	public void sort(final IPropertySheetEntry[] entries) {
		Arrays.sort(entries, new Comparator() {
			public int compare(final Object a, final Object b) {
				return PropertySheetSorter.this.compare(
						(IPropertySheetEntry) a, (IPropertySheetEntry) b);
			}
		});
	}

	/**
	 * Sorts the given categories in-place, modifying the given array.
	 * 
	 * @param categories
	 *            the categories to sort
	 */
	
	@SuppressWarnings("unchecked")
	void sort(final PropertySheetCategory[] categories) {
		Arrays.sort(categories, new Comparator() {
			public int compare(final Object a, final Object b) {
				return PropertySheetSorter.this.compareCategories(
						((PropertySheetCategory) a).getCategoryName(),
						((PropertySheetCategory) b).getCategoryName());
			}
		});
	}
}
