package org.remotercp.util.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.thoughtworks.xstream.XStream;

public class SerializeUtil {

	public static byte[] convertObjectToByte(Object o) throws IOException {
		byte serializedObject[] = (byte[]) null;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(out);
		objOut.writeObject(o);
		objOut.flush();
		objOut.close();
		serializedObject = out.toByteArray();
		out.close();

		return serializedObject;
	}

	public static Object convertByteToObject(byte data[]) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream objIn = new ObjectInputStream(in);
		Object object = objIn.readObject();
		objIn.close();
		in.close();
		return object;
	}

	public static String convertObjectToXML(Object obj) {
		XStream stream = new XStream();
		String xml = stream.toXML(obj);
		return xml;
	}

	public static Object convertXMLToObject(String xml) {
		XStream stream = new XStream();
		Object obj = stream.fromXML(xml);
		return obj;
	}
}
