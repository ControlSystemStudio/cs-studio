package org.csstudio.nams.application.department.decision;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.Producer;

/**
 * 
 * Automat zum syncronisieren der globalen und lokalen Konfiguration.
 */
public class SyncronisationsAutomat {
	
//	private final static String MSGPROP_COMMAND = "COMMAND"; 
//	private final static String MSGVALUE_TCMD_RELOAD= "AMS_RELOAD_CFG";
//	private final static String MSGVALUE_TCMD_RELOAD_CFG_START = MSGVALUE_TCMD_RELOAD + "_START";
//	
	/**
	 * Fordert distributor auf die globale Konfiguration in die lokale zu
	 * uebertragen. Die Zugangsdaten zu den Datenbanken kennt der distributor
	 * selber. Die operation blockiert bis zur erfolgreichen Rückmeldung oder
	 * einem interrupt auf dem ausführerendem Thread. Es ist erforderlich das
	 * vor der ausführung dieser Operation keine Zugriffe auf die lokale DB
	 * erfolgen.
	 */
	public static void syncronisationUeberDistributorAusfueren(
			Producer amsAusgangsProducer, Consumer amsCommandConsumer) {
		Map<String, String> map = new HashMap<String, String>();
		
//		map.put(MSGPROP_COMMAND, MSGVALUE_TCMD_RELOAD_CFG_START);
		
		amsAusgangsProducer.sendeMap(map);
		
//		while(amsCommandConsumer.receiveMessage()) {
//			
//		}
		
//		// MapMessage mapMsg = amsSenderSession.createMapMessage();
//		MapMessage mapMsg = amsPublisherDist2.createMapMessage();
//		mapMsg.setString(MSGPROP_COMMAND,
//				MSGVALUE_TCMD_RELOAD_CFG_START);
//		// amsPublisherDist.send(mapMsg);
//		amsPublisherDist2.send(producerId, mapMsg);
//		boolean bRet = FlagDAO.bUpdateFlag(conDb, FLG_RPL,
//				FLAGVALUE_SYNCH_FMR_RPL,
//				FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
//		if (bRet) {
//			iCmd = CMD_RPL_WAITFOR_DIST;
//		} else {
//			Log.log(this, Log.FATAL,
//					"update not successful, could not update db flag to "
//							+ FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
//			return FilterManagerStart.STAT_ERR_FLG_RPL; // force new
//			// initialization,
//			// no recover()
//			// needed
//		}
		
		
//		amsAusgangsProducer.s
	}

}
