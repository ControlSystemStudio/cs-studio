package org.csstudio.nams.service.configurationaccess.localstore.declaration;

public interface NewAMSConfigurationElementDTO {
	
	/**
	 * Liefert eine menschenlesbare, eindeutige Bezeichnung; z.B. den Name eines Filters.
	 */
	public String getUniqueHumanReadableName();
	
	/**
	 * Prueft, ob dieses Konfigurationselement in der Rubrik mit dem angegebenen
	 * Datenbank-Rubrik-Primaerschluessel (GroupRef) enthalten ist.
	 * 
	 * TODO Besser hier ein CategoryDTO verwenden.
	 * 
	 * @param categoryDBId 
	 * @return
	 */
	public boolean isInCategory(int categoryDBId);
}
