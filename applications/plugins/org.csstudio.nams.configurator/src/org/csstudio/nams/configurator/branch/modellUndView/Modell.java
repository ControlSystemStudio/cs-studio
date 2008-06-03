package org.csstudio.nams.configurator.branch.modellUndView;

/**
 * Das Modell für die GUI-Editor. Dieses wird von der GUI erzeugt.
 */
public class Modell {
	
	private final Bean beanToWorkOn;

	public Modell(Bean beanToWorkOn) {
		this.beanToWorkOn = beanToWorkOn;
	}
	
	public GUIMemento ermittleAktuellesGUIMementoDerBean() {
		return new GUIMemento(/* Daten aus der Bean to Work on */);
	}

	public boolean istMementoGleichDemStandDerBean(GUIMemento aktuellesGUIMemento) {
		return false; // liefert das Dirty-flag für den Editor
	}
	
	public void tryToSave(GUIMemento aktuellesGUIMemento) throws SaveException {
		// Übertrage Änderungen auf die Bean.
	}
}
