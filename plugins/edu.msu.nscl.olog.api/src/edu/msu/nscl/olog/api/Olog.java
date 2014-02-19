package edu.msu.nscl.olog.api;

import static edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder.*;

public class Olog {
    private static volatile OlogClient client;

    private Olog() {

    }

    public static void setClient(OlogClient client) throws Exception {
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
	    Olog.client = serviceURL().withHTTPAuthentication(false).create();
	}
	return client;
    }

    /**
     * Returns a newly created {@link OlogClient} with the supplied credentials.
     * 
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static OlogClient getClient(String username, String password)
	    throws Exception {
	return serviceURL().withHTTPAuthentication(true).username(username)
		.password(password).create();
    }

}
