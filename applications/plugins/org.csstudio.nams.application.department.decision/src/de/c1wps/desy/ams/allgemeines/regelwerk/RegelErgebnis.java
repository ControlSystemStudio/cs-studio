package de.c1wps.desy.ams.allgemeines.regelwerk;

public enum RegelErgebnis {
	ZUTREFFEND, NICHT_ZUTREFFEND, VIELLEICHT_ZUTREFFEND, NOCH_NICHT_GEPRUEFT;

	public boolean istEntschieden() {
		return ((this == ZUTREFFEND) || (this == NICHT_ZUTREFFEND));
	}
	
	
}
