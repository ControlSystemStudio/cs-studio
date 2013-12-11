package gov.bnl.shiftClient;

import static gov.bnl.shiftClient.ShiftClientImpl.ShiftClientBuilder.*;

public class ShiftClientCreator {

	private static volatile ShiftClient client;

    private ShiftClientCreator() {

    }

    public static void setClient(ShiftClient client) throws Exception {
    	ShiftClientCreator.client = client;
    }

    /**
     * Returns the default {@link OlogClient}.
     * 
     * @return
     * @throws Exception
     */
    public static ShiftClient getClient(final String url) throws Exception {
	if (client == null) {
		ShiftClientCreator.client = serviceURL(url).withHTTPAuthentication(false).create();
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
    public static ShiftClient getClient(final String url, final String username, final String password) throws Exception {
    	return serviceURL(url).withHTTPAuthentication(true).username(username).password(password).create();
    }
}
