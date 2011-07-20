
package org.csstudio.nams.common.decision;

import java.net.InetAddress;
import java.util.Date;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.wam.Fachwert;

@Fachwert
public final class Vorgangsmappenkennung {

	private static long zaehler = 0;

	/**
	 * FIXME Dieses Verhalten in eine Factory auslagern, FW wieder per valueOf
	 * mit entsprechenden Parametern!
	 */
	public static Vorgangsmappenkennung createNew(final InetAddress hostAdress,
			final Date time) {
		Vorgangsmappenkennung.zaehler += 1;
		return new Vorgangsmappenkennung(hostAdress.getHostAddress(), time
				.getTime(), Vorgangsmappenkennung.zaehler, null);
	}

	@Deprecated
	public static Vorgangsmappenkennung valueOf(final InetAddress hostAdress,
			final Date time) {
		Contract.require(hostAdress != null, "hostAdress!=null");
		Contract.require(time != null, "time!=null");
		return new Vorgangsmappenkennung(hostAdress.getHostAddress(), time
				.getTime(), null);
	}

	public static Vorgangsmappenkennung valueOf(
			final Vorgangsmappenkennung kennung, final String ergaenzung) {
		Contract.require(!kennung.hatErgaenzung(), "!kennung.hatErgaenzung()");
		// zaehler += 1;
		return new Vorgangsmappenkennung(kennung.hostAdress, kennung.timeInMS,
				kennung.counter, ergaenzung);
	}

	private final String hostAdress;

	private final long timeInMS;

	private final String ergaenzung;

	/**
	 * @deprecated TODO Klären, ob der Counter notwendig und überhaupt sinnhaft
	 *             ist.
	 */
	@Deprecated
	private final long counter;

	private Vorgangsmappenkennung(final String address, final long timeMS,
			final long cnt, final String e) {
		this.hostAdress = address;
		this.timeInMS = timeMS;
		this.counter = cnt;
		this.ergaenzung = e;
	}

	private Vorgangsmappenkennung(final String hostAdress, final long timeInMS,
			final String ergaenzung) {
		this.hostAdress = hostAdress;
		this.timeInMS = timeInMS;
		this.ergaenzung = ergaenzung;
		this.counter = 0;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Vorgangsmappenkennung)) {
			return false;
		}
		final Vorgangsmappenkennung other = (Vorgangsmappenkennung) obj;
		if (this.ergaenzung == null) {
			if (other.ergaenzung != null) {
				return false;
			}
		} else if (!this.ergaenzung.equals(other.ergaenzung)) {
			return false;
		}
		if (!this.hostAdress.equals(other.hostAdress)) {
			return false;
		}
		if (this.timeInMS != other.timeInMS) {
			return false;
		}
		if (this.counter != other.counter) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.ergaenzung == null) ? 0 : this.ergaenzung.hashCode());
		result = prime * result + this.hostAdress.hashCode();
		result = prime * result
				+ (int) (this.timeInMS ^ (this.timeInMS >>> 32));
		result = prime * result + (int) (this.counter ^ (this.counter >>> 32));
		return result;
	}

	public boolean hatErgaenzung() {
		return this.ergaenzung != null;
	}

	@Override
	public String toString() {
		// time@hostAdress
		// time@hostAdress/ergaenzung
		final StringBuilder builder = new StringBuilder();
		builder.append(this.timeInMS);
		builder.append(',');
		builder.append(this.counter);
		builder.append('@');
		builder.append(this.hostAdress);
		if (this.ergaenzung != null) {
			builder.append('/');
			builder.append(this.ergaenzung);
		}
		return builder.toString();
	}
}
