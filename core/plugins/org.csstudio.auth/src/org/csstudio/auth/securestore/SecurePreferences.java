/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.auth.securestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This class stores the preferences in an encrypted format on the disk.
 * Applications should not use this class directly but should instead use 
 * the {@link SecureStore} class to load and save preferences.
 * 
 * @author avodovnik
 */
class SecurePreferences implements Serializable {
	private static final long serialVersionUID = -5107673110782652011L;
 
	// holds the encrypted data in a hash map
	private HashMap<Object, SecurePreferencesItem> dataMap =
		new HashMap<Object, SecurePreferencesItem>();
	
	public SecurePreferences(String username) {
		this.setFilename(username.concat(".prefs"));
	}
	
	/**
	 * Retrieves the object stored under an identifier and decrypts it
	 * with the selected private key.
	 * 
	 * @param identifier The identifier under which the object is stored in the map.
	 * @param key The private key used to decrypt the object's byte[] representation.
	 * @param xform The xform of the key loaded from the preferences.
	 * @return Returns the object stored under the identifier.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException If key, data or preference settings are corrupt.
	 * @throws InvalidKeyException If key or data are corrupt.
	 * @throws IllegalBlockSizeException If key or data are corrupt.
	 * @throws BadPaddingException If key or data are corrupt.
	 * @throws InvalidAlgorithmParameterException 
	 */
	public Object getObject(Object identifier, Key key, String xform) 
			throws IOException, 
			ClassNotFoundException,
			NoSuchAlgorithmException,
			NoSuchPaddingException,
			InvalidKeyException,
			IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {
		
		// if the item is not present, return null
		if(!this.dataMap.containsKey(identifier)) return null;
		// get the item from the map (encrypted own structure)
		SecurePreferencesItem spi = this.dataMap.get(identifier);
		// get the byte value
		byte[] retVal = spi.getValue();
		// decrypt it
		Cipher cipher = Cipher.getInstance(xform);
	    cipher.init(Cipher.DECRYPT_MODE, key);
		retVal = cipher.doFinal(retVal);
		
		// get the object behind the byte array
		ByteArrayInputStream bais = new ByteArrayInputStream(retVal);
		ObjectInputStream ois = new ObjectInputStream(bais);
		
		// return the object
		return ois.readObject();
	}

	public void setObject(Object identifier, Object value, Key key, String xform) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher = Cipher.getInstance(xform);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		// serialize the object into a byte[]
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(value);
		// get the byte array
		byte[] object = baos.toByteArray();
		object = cipher.doFinal(object);
		
		// create new representation of the data
		SecurePreferencesItem spi = new SecurePreferencesItem(object, "");
		// store the data
		this.dataMap.put(identifier, spi);
		
		// try and save the store
		this.save();
	}
	
	private String filename = ""; 
	private void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void save() throws FileNotFoundException, IOException {	
		// create an object output stream
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
		// write this to the oos
		oos.writeObject(this);
	}
	
	public static SecurePreferences load(String username) throws FileNotFoundException, IOException, ClassNotFoundException {
		String fileName = username.concat(".prefs");
		File file = new File(fileName) ;
		if(!file.exists())
			return new SecurePreferences(username);
		
		// create an object input stream
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		SecurePreferences prefs = (SecurePreferences)ois.readObject();
		// set the username
		prefs.setFilename(fileName);
		// return the element
		return prefs;
	}
	
	/**
	 * Structure containing all the preference items that are securely stored in a file.
	 * @author avodovnik
	 *
	 */
	private class SecurePreferencesItem implements Serializable {
		private static final long serialVersionUID = -4879977206232453114L;
		
		// the encrypted value in bytes
		byte[] value;
		// the class name of the requestor
		String className;
		/**
		 * Creates a new instance of the preferences item.
		 * @param val The encrypted byte array representation of the object.
		 * @param className The Class name of the requestor of this information.
		 */
		public SecurePreferencesItem(byte[] val, String className) {
			this.value = val;
			this.className = className;
		}
		
		/**
		 * Returns the encrypted byte array representation of the value.
		 * @return
		 */
		public byte[] getValue() {
			return value;
		}
	}
}
