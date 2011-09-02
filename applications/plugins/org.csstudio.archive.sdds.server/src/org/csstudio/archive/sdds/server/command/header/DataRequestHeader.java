
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */

package org.csstudio.archive.sdds.server.command.header;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class DataRequestHeader implements IRequestHeader {

    private static final Logger LOG = LoggerFactory.getLogger(DataRequestHeader.class);

    /**  */
    private int fromSec;

    /**  */
    private int fromUSec;

    /**  */
    private int toSec;

    /**  */
    private int toUSec;

    /**  */
    private int maxNumOfSamples;

    /**  */
    private int conversionTag;

    /** Deadband etc., not used by all conversions */
    private double conversionParameter;

    /**  */
    private int pvNameSize;

    /**  */
    private String[] pvName;

    /**
     *
     * @param data
     */
    public DataRequestHeader(@Nonnull final byte[] data) {
        setHeaderFromByteArray(data);
    }

    /**
     *
     */
    @Override
    public String toString() {

        final StringBuffer result = new StringBuffer();

        result.append("DataRequestHeader{");
        result.append("fromSec=" + fromSec + ",fromUSec=" + fromUSec + ",toSec=" + toSec + ",toUSec=" + toUSec);
        result.append(",maxNumOfSamples=" + maxNumOfSamples + ",conversionTag=" + conversionTag);
        result.append(",conversionParameter=" + conversionParameter + ",pvNameSize=" + pvNameSize);
        result.append(",pvName{");

        for(int i = 0;i < pvNameSize;i++) {
            result.append(pvName[i]);
            if(i < pvNameSize - 1) {
                result.append(",");
            }
        }

        result.append("}}");

        return result.toString();
    }

    /**
     *
     * @param d
     * @return
     * @throws IOException
     */
    private String readNextString(final DataInputStream d) throws IOException {
        StringBuffer tmp = null;
        byte c = -1;

        while((c = d.readByte()) == 0) {
            ;
        }

        tmp = new StringBuffer();
        if(c != 0) {
            tmp.append((char)c);
        }

        while((c = d.readByte()) != 0) {
            tmp.append((char)c);
        }

        return tmp.toString();
    }

    /* (non-Javadoc)
     * @see org.csstudio.archive.jaapi.server.command.header.IRequestHeader#getHeaderAsByteArray()
     */
    @Override
    public byte[] getHeaderAsByteArray() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.csstudio.archive.jaapi.server.command.header.IRequestHeader#setHeaderFromByteArray()
     */
    @Override
    public void setHeaderFromByteArray(final byte[] data) {

        final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

        try {

            fromSec = dis.readInt();
            fromUSec = dis.readInt();
            toSec = dis.readInt();
            toUSec = dis.readInt();
            maxNumOfSamples = dis.readInt();
            conversionTag = dis.readInt();
            conversionParameter = dis.readDouble();
            pvNameSize = dis.readInt();
            pvName = new String[pvNameSize];

            for(int i = 0;i < pvNameSize;i++) {
                pvName[i] = readNextString(dis);
            }

        } catch(final IOException ioe) {
            LOG.error("[*** IOException ***]: UUUURGS: " + ioe.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public boolean hasValidNumberofSamples() {
        return this.maxNumOfSamples > 0;
    }

    /**
     *
     * @return
     */
    public boolean isTimeDiffValid() {
        return toSec - fromSec > 0;
    }

    /**
     *
     * @return
     */
    public int getFromSec() {
        return fromSec;
    }

    /**
     *
     * @param fromSec
     */
    public void setFromSec(final int fromSec) {
        this.fromSec = fromSec;
    }

    /**
     *
     * @return
     */
    public int getFromUSec() {
        return fromUSec;
    }

    /**
     *
     * @param fromUSec
     */
    public void setFromUSec(final int fromUSec) {
        this.fromUSec = fromUSec;
    }

    /**
     *
     * @return
     */
    public int getToSec() {
        return toSec;
    }

    /**
     *
     * @param toSec
     */
    public void setToSec(final int toSec) {
        this.toSec = toSec;
    }

    /**
     *
     * @return
     */
    public int getToUSec() {
        return toUSec;
    }

    /**
     *
     * @param toUSec
     */
    public void setToUSec(final int toUSec) {
        this.toUSec = toUSec;
    }

    /**
     *
     * @return
     */
    public int getMaxNumOfSamples() {
        return maxNumOfSamples;
    }

    /**
     *
     * @param maxNumOfSamples
     */
    public void setMaxNumOfSamples(final int maxNumOfSamples) {
        this.maxNumOfSamples = maxNumOfSamples;
    }

    /**
     *
     * @return
     */
    public int getConversionTag() {
        return conversionTag;
    }

    /**
     *
     * @param conversionTag
     */
    public void setConversionTag(final int conversionTag) {
        this.conversionTag = conversionTag;
    }

    /**
     *
     * @return
     */
    public double getConversionParameter() {
        return conversionParameter;
    }

    /**
     *
     * @param conversionParameter
     */
    public void setConversionParameter(final double conversionParameter) {
        this.conversionParameter = conversionParameter;
    }

    /**
     *
     * @return
     */
    public int getPvNameSize() {
        return pvNameSize;
    }

    /**
     *
     * @param pvNameSize
     */
    public void setPvNameSize(final int pvNameSize) {
        this.pvNameSize = pvNameSize;
    }

    /**
     *
     * @return
     */
    public String[] getPvName() {
        return pvName;
    }

    /**
     *
     * @param pvName
     */
    public void setPvName(final String[] pvName) {
        this.pvName = pvName;
    }
}
