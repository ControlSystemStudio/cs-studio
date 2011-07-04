
package org.csstudio.nams.service.preferenceservice.declaration;

/**
 * Identifies an {@link Object} to hold an {@link String}-Id to be used in the
 * Eclipse PreferenceStore.
 * 
 * By default, the object to hold an Id is an element of a Enum-Class.
 */
public interface HoldsAPreferenceId {

	/**
	 * A human readable description.
	 */
	public String getDescription();

	/**
	 * The Id to used in the Preference-Store.
	 */
	public String getPreferenceStoreId();
}
