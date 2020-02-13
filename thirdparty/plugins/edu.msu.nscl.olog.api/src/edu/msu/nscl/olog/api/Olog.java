package edu.msu.nscl.olog.api;
import static edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder.*;

public class Olog {
	private static volatile OlogClient client;

	private Olog() {

	}

	public static void setClient(OlogClient client) {
		Olog.client = client;
	}

	/**
	 * Returns the default {@link OlogClient}.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static OlogClient getClient() throws Exception {
		if (client == null) {
			Olog.client =  serviceURL().withHTTPAuthentication(false)
					.create();
		}
		return client;
	}

}
