package org.remotercp.contacts.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/*
 * As IRosterItems are not serializable it would be pretty a hack to serialize them. 
 * I rather decided to do the simplest thing that could possibly work and  created a singleton (@see DragAndDropSupport) and it works pretty well so far.
 */
public class TreeObjectTransfer extends ByteArrayTransfer {

	private static final String TYPE_NAME = "tree-object-transfer-format";

	private static final int TYPEID = registerType(TYPE_NAME);

	private static TreeObjectTransfer instance;

	private TreeObjectTransfer() {
		// singleton
	}

	public static TreeObjectTransfer getInstance() {
		return instance == null ? instance = new TreeObjectTransfer()
				: instance;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		byte[] bytes = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(byteOut);
			out.writeObject(object);
			out.close();

			bytes = byteOut.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (bytes != null)
			super.javaToNative(bytes, transferData);
	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		Object obj = null;
		byte[] bytes = (byte[]) super.nativeToJava(transferData);

		try {
			ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
					bytes));

			obj = in.readObject();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
