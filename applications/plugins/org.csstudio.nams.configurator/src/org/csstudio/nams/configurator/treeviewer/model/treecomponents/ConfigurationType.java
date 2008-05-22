package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

public enum ConfigurationType {

	ALARMBEATERBEITER("Alarmbearbeiter"), ALARMBEATERBEITERGRUPPE(
			"Alarmbearbeitergruppe"), ALARMTOPIC("Alarmtopics"), FILTERBEDINGUNG(
			"Filterbedingungen"), FILTER("Filter");

	private final String displayName;

	ConfigurationType(String displayName) {
		this.displayName = displayName;

	}

	public String getDisplayName() {
		return this.displayName;
	}
}
