
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;

/**
 * Ein DTO der NAMS-Konfiguration
 * 
 * TODO Rename to NamsDTO.
 */
public interface NewAMSConfigurationElementDTO {

	/**
	 * Equals have to be implemented by each DTO!.
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
    public boolean equals(Object obj);

	/**
	 * Liefert eine menschenlesbare, eindeutige Bezeichnung; z.B. den Name eines
	 * Filters.
	 */
	public String getUniqueHumanReadableName();

	/**
	 * Equals have to be implemented by each DTO!.
	 * 
	 * @see Object#hashCode()
	 */
	@Override
    public int hashCode();

	/**
	 * Prueft, ob dieses Konfigurationselement in der Rubrik mit dem angegebenen
	 * Datenbank-Rubrik-Primaerschluessel (GroupRef) enthalten ist.
	 * 
	 * @deprecated mz 20080710: Hier ein {@link RubrikDTO} verwenden.
	 */
	@Deprecated
	public boolean isInCategory(int categoryDBId);
}
