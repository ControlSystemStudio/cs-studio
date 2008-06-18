package org.csstudio.nams.common.material;


import org.csstudio.nams.common.wam.Fachwert;

@Fachwert
public final class Regelwerkskennung {
	@Deprecated
	public static Regelwerkskennung valueOf() {
		return new Regelwerkskennung();
	}

	private static int zaehler = 0;
	private final int meinZaehlerWert;
	private final String name;
	
	public static Regelwerkskennung valueOf(int filterId, String regelwerkName){
		return new Regelwerkskennung(filterId, regelwerkName);
	}
	
	private Regelwerkskennung(int filterId, String regelwerkName){
		this.name = regelwerkName;
		this.meinZaehlerWert = filterId;
//		this.meinZaehlerWert = zaehler++;
	}
	
	private Regelwerkskennung(String name) {
		this.name = name;
		meinZaehlerWert = zaehler++;
	}
	
	private Regelwerkskennung() {
		meinZaehlerWert = zaehler++;
		this.name = ""+meinZaehlerWert;
	}

	@Override
	public String toString() {
		return meinZaehlerWert + "-"+name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + meinZaehlerWert;
		return result;
	}

	public static Regelwerkskennung valueOf(String name) {
		return new Regelwerkskennung(name);
	}
	
	public int getRegelwerksId(){
		return meinZaehlerWert;
	}

//  Eine eigene equals()-Methode ist nicht erforderlich, da Regelwerkskennungen nur gleich sind, wenn sie identisch sind! 
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (!(obj instanceof Regelwerkskennung))
//			return false;
//		final Regelwerkskennung other = (Regelwerkskennung) obj;
//		if (meinZaehlerWert != other.meinZaehlerWert)
//			return false;
//		return true;
//		Dies wir nie passieren: ist der Zaehler identisch sind auch die Kennungen identisch!!
//		return false;
//	}
	
	
}
