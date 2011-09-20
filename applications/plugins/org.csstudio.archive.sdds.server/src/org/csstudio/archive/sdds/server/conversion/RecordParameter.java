
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

package org.csstudio.archive.sdds.server.conversion;

/**
 * @author Markus Moeller
 *
 */
public class RecordParameter {
    /*
     *  typedef struct recParam
     *  {
     *      u_long from_sec;
     *      u_long from_usec;
     *      u_long to_sec;
     *      u_long to_usec;
     *      u_long maxNum;
     *      u_long convers;
     *      double conversPar;
     *  }
     */

    /** */
    private long fromSeconds;

    /** */
    private long fromUSeconds;

    /** */
    private long toSeconds;

    /** */
    private long toUSeconds;

    /** */
    private long maxNumber;

    /** */
    private long conversion;

    /** */
    private double conversPar;

    /**
     *
     * @return
     */
    public long getFromSeconds() {
        return fromSeconds;
    }

    /**
     *
     * @param fromSeconds
     */
    public void setFromSeconds(final long fromSeconds) {
        this.fromSeconds = fromSeconds;
    }

    /**
     *
     * @return
     */
    public long getFromUSeconds() {
        return fromUSeconds;
    }

    /**
     *
     * @param fromUSeconds
     */
    public void setFromUSeconds(final long fromUSeconds) {
        this.fromUSeconds = fromUSeconds;
    }

    /**
     *
     * @return
     */
    public long getToSeconds() {
        return toSeconds;
    }

    /**
     *
     * @param toSeconds
     */
    public void setToSeconds(final long toSeconds) {
        this.toSeconds = toSeconds;
    }

    /**
     *
     * @return
     */
    public long getToUSeconds() {
        return toUSeconds;
    }

    /**
     *
     * @param toUSeconds
     */
    public void setToUSeconds(final long toUSeconds) {
        this.toUSeconds = toUSeconds;
    }

    /**
     *
     * @return
     */
    public long getMaxNumber() {
        return maxNumber;
    }

    /**
     *
     * @param maxNumber
     */
    public void setMaxNumber(final long maxNumber) {
        this.maxNumber = maxNumber;
    }

    /**
     *
     * @return
     */
    public long getConversion() {
        return conversion;
    }

    /**
     *
     * @param conversion
     */
    public void setConversion(final long conversion) {
        this.conversion = conversion;
    }

    /**
     *
     * @return
     */
    public double getConversPar() {
        return conversPar;
    }

    /**
     *
     * @param conversPar
     */
    public void setConversPar(final double conversPar) {
        this.conversPar = conversPar;
    }
}
