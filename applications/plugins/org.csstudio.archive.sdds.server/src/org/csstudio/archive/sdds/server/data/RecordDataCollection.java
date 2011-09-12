
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.archive.sdds.server.data;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.sdds.server.conversion.SampleParameter;

/**
 * TODO (mmoeller) :
 *
 * @author mmoeller
 * @version
 * @since 26.05.2010
 */
public class RecordDataCollection {

    /** Data of PV's */
    private List<EpicsRecordData> data;

    /** The parameters of the data samples */
    private SampleParameter sampleParameter;

    /** Standard constructor */
    public RecordDataCollection() {
        data = new ArrayList<EpicsRecordData>(0);
        sampleParameter = new SampleParameter();
    }

    /**
     *
     * @return
     */
    public List<EpicsRecordData> getData() {
        return data;
    }

    /**
     *
     * @param data
     */
    public void setData(final List<EpicsRecordData> data) {
        this.data = data;
    }

    /**
     *
     * @return
     */
    public SampleParameter getSampleParameter() {
        return sampleParameter;
    }

    /**
     *
     * @param sampleParameter
     */
    public void setSampleParameter(final SampleParameter sampleParameter) {
        this.sampleParameter = sampleParameter;
    }

    /**
     * Returns the number of data samples.
     *
     * @return
     */
    public int getNumberOfData() {

        int result = 0;

        if (data != null) {
            result = data.size();
        }

        return result;
    }

    /**
     * Return true if data is present.
     *
     * @return
     */
    public boolean containsData() {
        return getNumberOfData() > 0;
    }
}
