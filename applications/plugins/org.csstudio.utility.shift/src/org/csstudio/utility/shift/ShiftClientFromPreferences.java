/**
 * 
 */
package org.csstudio.utility.shift;

import gov.bnl.shiftClient.Shift;
import gov.bnl.shiftClient.ShiftClient;
import gov.bnl.shiftClient.ShiftClientImpl.ShiftClientBuilder;
import gov.bnl.shiftClient.ShiftFinderException;
import gov.bnl.shiftClient.Type;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;


@SuppressWarnings("deprecation")
public class ShiftClientFromPreferences implements ShiftClient {

	private static Logger log = Logger.getLogger(ShiftClientFromPreferences.class.getName());
	private volatile ShiftClient client;

	/**
	 * 
	 */
	public ShiftClientFromPreferences() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		final ShiftClientBuilder shiftClientBuilder;
		final String url = prefs.getString(Activator.PLUGIN_ID,PreferenceConstants.Shift_URL,
				"https://localhost:8181/Shift/resources", null);
		shiftClientBuilder = ShiftClientBuilder.serviceURL(url);
		if (prefs.getBoolean(Activator.PLUGIN_ID,PreferenceConstants.Use_authentication, false, null)) {
			shiftClientBuilder.withHTTPAuthentication(true)
				.username(prefs.getString(Activator.PLUGIN_ID,PreferenceConstants.Username, "username", null))
					.password(SecurePreferences.get(Activator.PLUGIN_ID, PreferenceConstants.Password, null));
		} else {
			shiftClientBuilder.withHTTPAuthentication(false);
		}
		log.info("Creating Olog client : " + url);
		try {
			// OlogClientManager.registerDefaultClient(ologClientBuilder.create());
			this.client = shiftClientBuilder.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Collection<Shift> listShifts() throws ShiftFinderException {
		return client.listShifts();
	}
	
	@Override
	public Shift getShiftByType(final String type) throws ShiftFinderException {
		return client.getShiftByType(type);
	}

	@Override
	public Shift getShift(final Integer shiftId, final String type) throws ShiftFinderException {
		return client.getShift(shiftId, type);
	}

	@Override
	public Shift start(final Shift shift) throws ShiftFinderException {	
		return client.start(shift);
	}

	@Override
	public Shift end(final Shift shift) throws ShiftFinderException {
		return client.end(shift);
	}

	@Override
	public Shift close(final Shift shift) throws ShiftFinderException {
		return client.close(shift);
	}

	@Override
	public Collection<Shift> findShiftsBySearch(final String pattern) throws ShiftFinderException {
		return client.findShiftsBySearch(pattern);
	}

	@Override
	public Collection<Shift> findShifts(Map<String, String> map) throws ShiftFinderException {
		return client.findShifts(map);
	}

	@Override
	public Collection<Shift> findShifts(final MultivaluedMap<String, String> map) throws ShiftFinderException {
		return client.findShifts(map);
	}

	@Override
	public Collection<Type> listTypes() throws ShiftFinderException {
		return client.listTypes();
	}

}
