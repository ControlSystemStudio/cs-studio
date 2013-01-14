package org.csstudio.sds.ui.thumbnail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class DisplayCacheValue implements Serializable {

	private static final long serialVersionUID = 5067117487677452786L;

	private byte[] hash;
	private byte[] imageBytes;
	private Set<SerializableTuple<ControlSystemEnum, String>> pvTuples;

	public DisplayCacheValue() {

	}

	public DisplayCacheValue(File file, ImageData imageData,
			Set<IProcessVariableAddress> pvAddresses) {
		hash = hashFile(file);

		// serialize ImageData
		if(imageData == null) {
			imageBytes = null;
		}
		else {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { imageData };
			imageLoader.save(byteArrayOutputStream, SWT.IMAGE_PNG);
			imageBytes = byteArrayOutputStream.toByteArray();
		}
		this.pvTuples = new HashSet<DisplayCacheValue.SerializableTuple<ControlSystemEnum, String>>(
				pvAddresses.size());
		for (IProcessVariableAddress processVariableAddress : pvAddresses) {
			SerializableTuple<ControlSystemEnum, String> pvTuple = new SerializableTuple<ControlSystemEnum, String>(
					processVariableAddress.getControlSystem(),
					processVariableAddress.getRawName());
			this.pvTuples.add(pvTuple);
		}
	}

	public ImageData getImage() {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

		return new ImageLoader().load(inputStream)[0];
	}

	public byte[] getHash() {
		return hash;
	}

	public Set<IProcessVariableAddress> getProcessVariableAddresses() {
		Set<IProcessVariableAddress> result = new HashSet<IProcessVariableAddress>(
				pvTuples.size());
		for (SerializableTuple<ControlSystemEnum, String> pvTuple : pvTuples) {
			IProcessVariableAddress processVariableAddress = ProcessVariableAdressFactory
					.getInstance().createProcessVariableAdress(pvTuple.getS2(),
							pvTuple.getS1());
			result.add(processVariableAddress);
		}
		return result;
	}

	private byte[] hashFile(File file) {
		assert file != null : "Precondition failed: file != null";
		assert file.exists() : "Precondition failed: file.exists()";

		FileInputStream fis = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
			fis = new FileInputStream(file);
			byte[] dataBytes = new byte[1024];

			int nread = 0;

			while ((nread = fis.read(dataBytes)) != -1) {
				messageDigest.update(dataBytes, 0, nread);
			}
			return messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}
	
	public static class Tuple<T1, T2> {
		private T1 p1;
		private T2 p2;

		public Tuple(T1 p1, T2 p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
		
		public T1 getP1() {
			return p1;
		}
		
		public T2 getP2() {
			return p2;
		}
	}

	public static class SerializableTuple<T1 extends Serializable, T2 extends Serializable>
			implements Serializable {

		private static final long serialVersionUID = -3037433875843076362L;
		private T1 s1;
		private T2 s2;

		public SerializableTuple(T1 s1, T2 s2) {
			this.s1 = s1;
			this.s2 = s2;
		}

		public T1 getS1() {
			return s1;
		}

		public T2 getS2() {
			return s2;
		}
	}

}