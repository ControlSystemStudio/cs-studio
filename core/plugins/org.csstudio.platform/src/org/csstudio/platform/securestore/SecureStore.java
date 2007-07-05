package org.csstudio.platform.securestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.csstudio.platform.logging.CentralLogger;

/**
 * Service that provides an encrypted store for login and other information.
 * 
 * @author Anze Vodovnik, Joerg Rathlev
 */
public final class SecureStore {
	
	/**
	 * Holds the single instance.
	 */
	private static SecureStore instance = null;
	
	/**
	 * Alias under which the encryption key is stored in the keystore.
	 */
	private static final String KEYSTORE_ALIAS = "css-securestore";
	
	/**
	 * The keystore provider.
	 */
	private static final String KEYSTORE_PROVIDER = "JCEKS";
	
	/**
	 * The transformation that is used for the encryption.
	 */
	private static final String CIPHER_TRANSFORMATION = "DES/ECB/PKCS5Padding";

	private SecretKey key;
	private SecurePreferences securePreferences;

	/**
	 * Singleton accessor for the secure store manager.
	 */
	public static synchronized SecureStore getInstance() {
		if( instance == null) 
			instance = new SecureStore();
		
		return instance;
	}
	
	// private constructor
	private SecureStore() {}
	
	/**
	 * Attempts to unlock this store and load its data. This method must be
	 * called before the store can be used by clients. It is expected that
	 * this method is called by the plug-in that is responsible for the
	 * primary user login.
	 * 
	 * @param username the username.
	 * @param password the user's password. This password is used to protect
	 *        the secret key which is used to encrypt the information stored
	 *        in this secure store.
	 * @return <code>true</code> if this store was successfully unlocked,
	 *        <code>false</code> otherwise.
	 * @throws IllegalArgumentException if <code>username</code> is
	 *        <code>null</code> or the empty string.
	 */
	public boolean unlock(String username, String password) {
		if(username == null || username.equals(""))
			throw new IllegalArgumentException("username was null");
		
		char[] passwd = password.toCharArray();
		try {
			KeyStore ks = loadKeyStore(username, passwd);
			if(!ks.containsAlias(KEYSTORE_ALIAS)) {
				generateKey();
				ks.setEntry(KEYSTORE_ALIAS,
						new KeyStore.SecretKeyEntry(key), 
						new KeyStore.PasswordProtection(passwd));
				saveKeyStore(username, passwd, ks);
			} else {
				try {
					key = (SecretKey) ks.getKey(KEYSTORE_ALIAS, passwd);
				} catch (Exception e) {
					generateKey();
					ks.setEntry(KEYSTORE_ALIAS,
							new KeyStore.SecretKeyEntry(key), 
							new KeyStore.PasswordProtection(passwd));
					saveKeyStore(username, passwd, ks);
				}
			}
			securePreferences = SecurePreferences.load(username);
			return true;
		} catch (Exception e) {
			CentralLogger.getInstance().warn(this,
					"Secure store could not be unlocked.", e);
			return false;
		}
	}
	
	/**
	 * Sets the key used by this secure store to a newly generated key.
	 * @throws NoSuchAlgorithmException if the algorithm is unavailable.
	 */
	private void generateKey() throws NoSuchAlgorithmException {
		KeyGenerator kg = KeyGenerator.getInstance("DES");
		kg.init(56); // fixed key size for DES!
		key = kg.generateKey();
	}
	
	/**
	 * Returns the keystore for the specified user. If a keystore file exists,
	 * the keystore is loaded from that file, otherwise a new keystore is
	 * returned.
	 * 
	 * @param username The username associated with the key store.
	 * @param password The password associated with the key store.
	 * @return the key store for the user.
	 * @throws KeyStoreException  If there was an error with the key store.
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException Thrown if the selected provider is not installed.
	 */
	private KeyStore loadKeyStore(String username, char[] password) 
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException {
		
		KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER);
		
		File file = new File(username.concat(".ks"));
		InputStream is = null;
		boolean loaded = false;
		if (file.exists()) {
			try {
				is = new FileInputStream(file);
				ks.load(is, password);
				loaded = true;
//			} catch (IOException e) {
			} catch (Exception e) {
				CentralLogger.getInstance().error(this,
						"Error loading keystore.", e);
				try {
					// if the keystore was not loaded from the file, create
					// an empty keystore
					ks.load(null);
				} catch (IOException e1) {
					CentralLogger.getInstance().error(this,
							"Error loading keystore.", e);
				}
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						CentralLogger.getInstance().error(this,
								"Error closing input stream.", e);
					}
				}
			}
		}
		if (!loaded) {
			try {
				ks.load(null);
			} catch (IOException e) {
				CentralLogger.getInstance().error(this,
						"Error loading keystore.", e);
			}
		}
		return ks;
	}

	/**
	 * Saves a keystore to a file.
	 * @param username the username. This is used to determine the file name.
	 * @param password the password used to protect the keystore's integrity.
	 * @param ks the keystore.
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 */
	private void saveKeyStore(String username, char[] password, KeyStore ks)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException {
		OutputStream os = null;
		try {
			File file = new File(username.concat(".ks"));
			os = new FileOutputStream(file);
			ks.store(os, password);
		} catch (IOException e) {
			CentralLogger.getInstance().error(this, "Error saving keystore", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					CentralLogger.getInstance().error(this,
							"Error closing output stream", e);
				}
			}
		}
	}
	
	/**
	 * Sets the object to the secure preferences store.
	 * 
	 * @param identifier The identifier under which the object is stored.
	 * @param value The value of the object.
	 */
	public void setObject(String identifier, Serializable value) {
		try {
			if (securePreferences != null) {
				securePreferences.setObject(identifier, value, key,
						CIPHER_TRANSFORMATION);
			}			
		} catch (Exception e) {
			CentralLogger.getInstance().error(this,
					"Error storing object in secure store.", e);
		}
	}
	
	/**
	 * Gets the object from the preferences store.
	 * 
	 * @param identifier The identifier under which the object is stored.
	 * @return Returns the value of the object stored in the preferences.
	 */
	public Object getObject(String identifier) {
		try {
			return securePreferences != null
				? securePreferences.getObject(identifier, key,
					CIPHER_TRANSFORMATION)
				: null;
		} catch (Exception e) {
			CentralLogger.getInstance().error(this,
					"Error loading object from secure store.", e);
			return null;
		}
	}
}
