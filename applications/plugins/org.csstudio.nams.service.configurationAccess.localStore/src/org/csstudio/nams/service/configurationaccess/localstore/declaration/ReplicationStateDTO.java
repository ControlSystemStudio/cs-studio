
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TODO table name: AMS_FLAG columns: CFLAGNAME ("ReplicationState") (ID)
 * SFLAGVALUE ( 0 (repliziert/synchron), 1 (?), 2 (soll/darf repliziert werden)
 * oder 3 (starte replizieren) kp was diese bedeuten)
 * 
 * public final static short FLAGVALUE_SYNCH_IDLE = 0; public final static short
 * FLAGVALUE_SYNCH_FMR_RPL = 1; public final static short
 * FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED = 2; Filtermanger hat eine Syncronizations
 * Nachricht an den Distributor gesendet public final static short
 * FLAGVALUE_SYNCH_DIST_RPL = 3; public final static short
 * FLAGVALUE_SYNCH_DIST_NOTIFY_FMR = 4;
 */
@Entity
@Table(name = "AMS_FLAG")
public class ReplicationStateDTO {
	// TODO Design this DTO to fit table
	// TODO Register this DTO class in mapping file
	// TODO Register this DTO class in annotation configuration

	public static enum ReplicationState {
		/**
		 * (0) Es muss nicht Repliziert werden
		 */
		FLAGVALUE_SYNCH_IDLE((short) 0),

		/**
		 * (1) FilterManager beginnt zu Replizieren und schreibt history
		 * Eintrag.
		 * 
		 * <pre>
		 * private static void logHistoryRplStart(java.sql.Connection conDb, boolean bStart) {
		 * 	try {
		 * 		HistoryTObject history = new HistoryTObject();
		 * 
		 * 		history.setTimeNew(new Date(System.currentTimeMillis()));
		 * 		history.setType(&quot;Config Synch&quot;);
		 * 
		 * 		if (bStart)
		 * 			history
		 * 					.setDescription(&quot;Filtermanager stops normal work, wait for Distributor.&quot;);
		 * 		else
		 * 			history
		 * 					.setDescription(&quot;Filtermanager got config replication end, goes to normal work.&quot;);
		 * 
		 * 		HistoryDAO.insert(conDb, history);
		 * 		Log.log(Log.INFO, history.getDescription()); // history.getHistoryID() + &quot;. &quot;
		 * 	} catch (Exception ex) {
		 * 		Log.log(Log.FATAL, &quot;exception at history logging start=&quot; + bStart, ex);
		 * 	}
		 * }
		 * </pre>
		 */
		FLAGVALUE_SYNCH_FMR_RPL((short) 1),

		/**
		 * (2) FilterManager sendet eine Synchronizationsnachricht ueber das
		 * Nachrichten Topic an den Distributor und wartet auf Antwort.
		 */
		FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED((short) 2),

		/**
		 * (3) Distributor leitet Replication ein.
		 */
		FLAGVALUE_SYNCH_DIST_RPL((short) 3),

		/**
		 * (4) Distributor ist fertig mit Replizieren und sendet Command an das
		 * interne JMS Topic. Der Distributor setzt den Status wieder auf
		 * FLAGVALUE_SYNCH_IDLE (0).
		 */
		FLAGVALUE_SYNCH_DIST_NOTIFY_FMR((short) 4),

		/**
		 * (-1) Ungültiger Zustand. Daten Fehler.
		 */
		INVALID_STATE((short) -1);

		static ReplicationState valueOf(final short dbValue) {
			switch (dbValue) {
			case 0:
				return FLAGVALUE_SYNCH_IDLE;
			case 1:
				return FLAGVALUE_SYNCH_FMR_RPL;
			case 2:
				return FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED;
			case 3:
				return FLAGVALUE_SYNCH_DIST_RPL;
			case 4:
				return FLAGVALUE_SYNCH_DIST_NOTIFY_FMR;
			default:
				return INVALID_STATE;
			}
		}

		/**
		 * Wert wie er in der Datenbank steht
		 */
		private final short dbValue;

		ReplicationState(final short dbValue) {
			this.dbValue = dbValue;
		}

		short getDbValue() {
			return this.dbValue;
		}
	}

	public final static String DB_FLAG_NAME = "ReplicationState";

	@Id
	@Column(unique = true, name = "CFLAGNAME")
	private String flagName = ReplicationStateDTO.DB_FLAG_NAME;

	@Column(name = "SFLAGVALUE")
	private short value = ReplicationState.INVALID_STATE.getDbValue();

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ReplicationStateDTO)) {
			return false;
		}
		final ReplicationStateDTO other = (ReplicationStateDTO) obj;
		if (this.flagName == null) {
			if (other.flagName != null) {
				return false;
			}
		} else if (!this.flagName.equals(other.flagName)) {
			return false;
		}
		if (this.value != other.value) {
			return false;
		}
		return true;
	}

	public ReplicationState getReplicationState() {
		return ReplicationState.valueOf(this.getValue());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.flagName == null) ? 0 : this.flagName.hashCode());
		result = prime * result + this.value;
		return result;
	}

	public void setReplicationState(final ReplicationState replicationState) {
		this.setValue(replicationState.getDbValue());
	}

	@SuppressWarnings("unused")
	private String getFlagName() {
		return this.flagName;
	}

	@SuppressWarnings("unused")
	private short getValue() {
		return this.value;
	}

	@SuppressWarnings("unused")
	private void setFlagName(final String flagName) {
		this.flagName = flagName;
	}

	@SuppressWarnings("unused")
	private void setValue(final short value) {
		this.value = value;
	}

}
