
package org.csstudio.nams.common.material;

/**
 * Systemnachricht wie Aktualisieren im NAMS
 */
public interface SystemNachricht {

	// final static String MSGPROP_COMMAND = "COMMAND";
	// final static String MSGVALUE_TCMD_RELOAD = "AMS_RELOAD_CFG";
	// final static String MSGVALUE_TCMD_RELOAD_CFG_START = MSGVALUE_TCMD_RELOAD
	// + "_START";
	// final static String MSGVALUE_TCMD_RELOAD_CFG_END = MSGVALUE_TCMD_RELOAD +
	// "_END";

	// private final Map<String, String> map;

	// SystemNachricht(Map<String, String> map) {
	// this.map = new HashMap<String, String>(map);
	// Contract.require(this.map.containsKey(MSGPROP_COMMAND),
	// "map.containsKey(MSGPROP_COMMAND)");
	// }

	public boolean istSyncronisationsAufforderung();

	// {
	// return map.get(MSGPROP_COMMAND).equals(MSGVALUE_TCMD_RELOAD_CFG_START);
	// }

	public boolean istSyncronisationsBestaetigung();
	// {
	// return map.get(MSGPROP_COMMAND).equals(MSGVALUE_TCMD_RELOAD_CFG_END);
	// }
}
