package de.c1wps.desy.ams.allgemeines;

import java.net.InetAddress;
import java.util.Date;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.wam.Fachwert;

@Fachwert
public final class Vorgangsmappenkennung {

	private final String hostAdress;
	private final long timeInMS;
	private final String ergaenzung;
	
	/**
	 * @deprecated TODO Klären, ob der Counter notwendig und überhaupt sinnhaft ist.
	 */
	@Deprecated
	private final long counter;
	private static long zaehler = 0;

	private Vorgangsmappenkennung(String hostAdress, long timeInMS,
			String ergaenzung) {
		this.hostAdress = hostAdress;
		this.timeInMS = timeInMS;
		this.ergaenzung = ergaenzung;
		this.counter = 0;
	}

	private Vorgangsmappenkennung(String hostAdress, long timeInMS,
			long counter, String ergaenzung) {
		this.hostAdress = hostAdress;
		this.timeInMS = timeInMS;
		this.counter = counter;
		this.ergaenzung = ergaenzung;
	}

	@Deprecated
	public static Vorgangsmappenkennung valueOf(InetAddress hostAdress,
			Date time) {
		Contract.require(hostAdress != null, "hostAdress!=null");
		Contract.require(time != null, "time!=null");
		return new Vorgangsmappenkennung(hostAdress.getHostAddress(), time
				.getTime(), null);
	}

	/**
	 * FIXME Dieses Verhalten in eine Factory auslagern, FW wieder per valueOf
	 * mit entsprechenden Parametern!
	 */
	public static Vorgangsmappenkennung createNew(InetAddress hostAdress,
			Date time) {
		zaehler += 1;
		return new Vorgangsmappenkennung(hostAdress.getHostAddress(), time
				.getTime(), zaehler, null);
	}

	@Override
	public String toString() {
		// time@hostAdress
		// time@hostAdress/ergaenzung
		StringBuilder builder = new StringBuilder();
		builder.append(this.timeInMS);
		builder.append(',');
		builder.append(counter);
		builder.append('@');
		builder.append(this.hostAdress);
		if (this.ergaenzung != null) {
			builder.append('/');
			builder.append(this.ergaenzung);
		}
		return builder.toString();
	}

	public static Vorgangsmappenkennung valueOf(Vorgangsmappenkennung kennung,
			String ergaenzung) {
		Contract.require(!kennung.hatErgaenzung(), "!kennung.hatErgaenzung()");
		// zaehler += 1;
		return new Vorgangsmappenkennung(kennung.hostAdress, kennung.timeInMS,
				kennung.counter, ergaenzung);
	}

	public boolean hatErgaenzung() {
		return this.ergaenzung != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ergaenzung == null) ? 0 : ergaenzung.hashCode());
		result = prime * result + hostAdress.hashCode();
		result = prime * result + (int) (timeInMS ^ (timeInMS >>> 32));
		result = prime * result + (int) (counter ^ (counter >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Vorgangsmappenkennung))
			return false;
		final Vorgangsmappenkennung other = (Vorgangsmappenkennung) obj;
		if (ergaenzung == null) {
			if (other.ergaenzung != null)
				return false;
		} else if (!ergaenzung.equals(other.ergaenzung))
			return false;
		if (!hostAdress.equals(other.hostAdress))
			return false;
		if (timeInMS != other.timeInMS)
			return false;
		if (counter != other.counter)
			return false;
		return true;
	}
}
