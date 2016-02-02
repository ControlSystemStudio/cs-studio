package org.csstudio.saverestore.git;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

import javax.security.auth.Subject;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.security.SecuritySupport;
import org.csstudio.security.preferences.SecurePreferences;
import org.csstudio.ui.fx.util.Credentials;
import org.csstudio.ui.fx.util.UsernameAndPasswordDialog;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.ui.PlatformUI;

/**
 *
 * <code>CredentialUtilities</code> provides utility methods to save and retrieve user credentials to access the git
 * repository.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class CredentialUtilities {

    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String SYSTEM_PROPERTY_USER_NAME = "user.name";

    private CredentialUtilities() {
    }

    /**
     * Loads the username and password from the preferences or shows the dialog where user can enter his credentials.
     *
     * @param previous version of credentials that did not work
     * @return credentials if confirmed or an empty object if cancelled
     */
    public static final Credentials getCredentials(Optional<Credentials> previous) {
        final Credentials[] provider = new Credentials[1];
        Subject subj = SecuritySupport.getSubject();
        final String currentUser = previous.isPresent() ? previous.get().getUsername()
            : subj == null ? null : SecuritySupport.getSubjectName(subj);
        org.eclipse.swt.widgets.Display.getDefault().syncExec(() -> {
            String username = null;
            char[] password = null;
            boolean remember = false;
            if (previous.isPresent()) {
                username = previous.get().getUsername();
                password = previous.get().getPassword();
                remember = previous.get().isRemember();
            } else {
                username = getUsername(Optional.empty());
                password = getPassword(Optional.empty(), username).orElse(null);
            }
            if (username == null || password == null || previous.isPresent()) {
                new UsernameAndPasswordDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), username,
                    remember, "Please, provide the username and password to access Save and Restore git repository")
                        .openAndWat().ifPresent(e -> {
                    provider[0] = e;
                    if (e.isRemember()) {
                        storeCredentials(Optional.ofNullable(currentUser), e.getUsername(), e.getPassword());
                    }
                });
            } else {
                provider[0] = new Credentials(username, password, false);
            }
        });
        return provider[0];
    }

    /**
     * Create a git credentials provider using the username and password from the credentials.
     *
     * @param cred the credentials to transform to credentials provider
     * @return provider if credentials were non null or null if the given credentials were null
     */
    public static final CredentialsProvider toCredentialsProvider(Credentials cred) {
        return cred == null ? null : new UsernamePasswordCredentialsProvider(cred.getUsername(), cred.getPassword());
    }

    /**
     * Returns the git username stored in the secured preferences for the given user. The user is usually the user that
     * is logged in to CS-Studio. If no user is provided, system user is used.
     *
     * @param forUser the user for whom the username is to be retrieved
     * @return the username if it exists, or null otherwise
     */
    public static String getUsername(Optional<String> forUser) {
        String user = forUser.orElse(System.getProperty(SYSTEM_PROPERTY_USER_NAME));
        try {
            return SecurePreferences.getSecurePreferences().node(Activator.ID).node(user).get(PREF_USERNAME, null);
        } catch (StorageException | IOException e) {
            SaveRestoreService.LOGGER.log(Level.WARNING, "Could not read the username from secured storage.", e);
            return null;
        }
    }

    /**
     * Returns the password that is bound to the username and is stored for the specified <code>forUser</code> If the
     * <code>forUser</code> is not specified the system user is used.
     *
     * @param forUser the user for whom the password is being retrieved
     * @param username the matching username to go with the password
     * @return the password as a character array or empty object if password could not be loaded
     */
    public static Optional<char[]> getPassword(Optional<String> forUser, String username) {
        if (username == null) {
            return Optional.empty();
        }
        String user = forUser.orElse(System.getProperty(SYSTEM_PROPERTY_USER_NAME));
        try {
            byte[] val = SecurePreferences.getSecurePreferences().node(Activator.ID).node(user).node(username)
                .getByteArray(PREF_PASSWORD, null);
            if (val == null) {
                return Optional.empty();
            }
            CharBuffer buffer = Charset.forName("UTF-8").newDecoder().decode(ByteBuffer.wrap(val));
            return Optional.of(Arrays.copyOfRange(buffer.array(), buffer.position(), buffer.limit()));
        } catch (StorageException | IOException e) {
            SaveRestoreService.LOGGER.log(Level.WARNING,
                "Could not read the password for '" + forUser + "' from secured storage.", e);
            return Optional.empty();
        }
    }

    /**
     * Stores the username and matching password for a particular user.
     *
     * @param forUser the user for whom the credentials are stored
     * @param username the username to store
     * @param password the password to store
     */
    public static void storeCredentials(Optional<String> forUser, String username, char[] password) {
        String user = forUser.orElse(System.getProperty(SYSTEM_PROPERTY_USER_NAME));
        try {
            ISecurePreferences prefs = SecurePreferences.getSecurePreferences().node(Activator.ID).node(user);
            if (username == null) {
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
            SaveRestoreService.LOGGER.log(Level.FINE, "Stored new username and password for '" + user + "'.");
        } catch (StorageException | IOException e) {
            SaveRestoreService.LOGGER.log(Level.WARNING, "Could not store the username and password.", e);
        }
    }
}
