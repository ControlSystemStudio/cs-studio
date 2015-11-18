package org.csstudio.saverestore.git;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 *
 * <code>Activator</code> provides access to preferences of the saverestore.git plugin.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Activator extends AbstractUIPlugin {

    public static final String ID = "org.csstudio.saverestore.git";

    public static final String PREF_URL = "url";
    public static final String PREF_DESTINATION = "destination";
    public static final String PREF_AUTOMATIC_SYNC = "automaticSynhronisation";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_PASSWORD = "password";

    private static Activator INSTANCE;

    /**
     * The default instance.
     *
     * @return the instance
     */
    public static Activator getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        INSTANCE = this;
    }

    /**
     * @return the URL to git repository
     */
    public URI getGitURI() {
        String str = getPreferenceStore().getString(PREF_URL);
        if (str == null) {
            return null;
        } else {
            return URI.create(str);
        }
    }

    /**
     * @return the file to the location where a local git clone exists or will exist after first usage
     */
    public File getDestination() {
        URL workspace = Platform.getInstanceLocation().getURL();
        IPath location = new Path(new File(workspace.getFile()).toString());
        String s = getPreferenceStore().getString(PREF_DESTINATION);
        if (s == null || s.trim().isEmpty()) {
            s = location.append(".metadata").append(".plugins").append(ID).append("repository").toString();
        } else {
            s = location.append(s).toString();
        }
        return new File(s);
    }

    /**
     * Returns the git username stored in the secured preferences for the given user. The user is usually the
     * user that is logged in to CS-Studio. If no user is provided, system user is used.
     *
     * @param forUser the user for whom the username is to be retrieved
     * @return the username if it exists, or null otherwise
     */
    public String getUsername(Optional<String> forUser) {
        String user = forUser.orElse(System.getProperty("user.name"));
        try {
            return SecurePreferences.getSecurePreferences().node(ID).node(user).get(PREF_USERNAME, null);
        } catch (StorageException | IOException e) {
            SaveRestoreService.LOGGER.log(Level.WARNING, "Could not read the username from secured storage.", e);
            return null;
        }
    }

    /**
     * Returns the password that is bound to the username and is stored for the specified <code>forUser</code>
     * If the <code>forUser</code> is not specified the system user is used.
     *
     * @param forUser the user for whom the password is being retrieved
     * @param username the matching username to go with the password
     * @return the password as a character array
     */
    public char[] getPassword(Optional<String> forUser, String username) {
        if (username == null) {
            return null;
        }
        String user = forUser.orElse(System.getProperty("user.name"));
        try {
            byte[] val = SecurePreferences.getSecurePreferences().node(ID).node(user).node(username)
                    .getByteArray(PREF_PASSWORD, null);
            if (val == null) {
                return null;
            }
            CharBuffer buffer = Charset.forName("UTF-8").newDecoder().decode(ByteBuffer.wrap(val));
            return Arrays.copyOfRange(buffer.array(), buffer.position(), buffer.limit());
        } catch (StorageException | IOException e) {
            SaveRestoreService.LOGGER.log(Level.WARNING, "Could not read the password for '" + forUser
                    + "' from secured storage.", e);
            return null;
        }
    }

    /**
     * Stores the username and matching password for a particular user.
     *
     * @param forUser the user for whom the credentials are stored
     * @param username the username to store
     * @param password the password to store
     */
    public void storeCredentials(Optional<String> forUser, String username, char[] password) {
        String user = forUser.orElse(System.getProperty("user.name"));
        try {
            ISecurePreferences prefs = SecurePreferences.getSecurePreferences().node(ID).node(user);
            if (username == null) {
                prefs.remove(username);
                prefs = prefs.node(username);
                if (prefs != null) {
                    prefs.removeNode();
                }
                return;
            } else {
                prefs.put(PREF_USERNAME, username, false);
            }
            if (password == null) {
                prefs.node(username).remove(PREF_PASSWORD);
            } else {
                ByteBuffer buffer = Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(password));
                byte[] passwordData = Arrays.copyOfRange(buffer.array(), buffer.position(), buffer.limit());
                prefs.node(username).putByteArray(PREF_PASSWORD, passwordData, false);
            }
        } catch (StorageException | IOException e) {
            SaveRestoreService.LOGGER.log(Level.WARNING, "Could not store the username and password.", e);
        }
    }
}
