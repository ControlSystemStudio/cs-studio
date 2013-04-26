/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms.adapters;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListInt;

/**
 * @author msekoranja
 *
 */
public class BytesMessageToListByte {

    private final byte[] array;
    private final ListByte list;

    /**
     * @param pvField
     * @param disconnected
     */
    public BytesMessageToListByte(Message message, boolean disconnected) throws JMSException {

        int msgLength = 0;
        int BUF_SIZE = 50000; //some efficient maximum
        byte[] data = new byte[BUF_SIZE];
        while (true) {
            int len = ((BytesMessage) message).readBytes(data);
            if (len > 0) {
                msgLength += len;
            } else {
                break;
            }
        }

        this.array = new byte[msgLength];
        
        if (msgLength <= BUF_SIZE) {
            System.arraycopy(data, 0, array, 0, msgLength);
        } else {
            ((BytesMessage) message).reset();//reset cursor to beginning
            ((BytesMessage) message).readBytes(array);
        }
        
        this.list = new ListByte() {
				
				@Override
				public int size() {
					return array.length;
				}
				
				@Override
				public byte getByte(int index) {
					return array[index];
				}
			};
    }

    /* (non-Javadoc)
     * @see org.epics.pvmanager.data.Array#getSizes()
     */
    public ListInt getSizes() {
        return new ArrayInt(array.length);
    }

    /* (non-Javadoc)
     * @see org.epics.pvmanager.data.VByteArray#getData()
     */
    public ListByte getData() {
        return list;
    }
}
