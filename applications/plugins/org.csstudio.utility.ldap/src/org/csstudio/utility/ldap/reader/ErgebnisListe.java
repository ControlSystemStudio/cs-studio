package org.csstudio.utility.ldap.reader;

import java.util.ArrayList;
import java.util.Observable;

public class ErgebnisListe extends Observable{

	private ArrayList<String> ergbnis = new ArrayList<String>();

	public ArrayList<String> getAnswer() {
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.addAll(ergbnis);
		ergbnis.clear();
		setChanged();
		return tmp;
	}

	public void setAnswer(ArrayList<String> ergbnis) {
		this.ergbnis.addAll(ergbnis);
		setChanged();
		notifyObservers();

	}

	public void notifyView() {
		setChanged();
		notifyObservers();
	}
}
