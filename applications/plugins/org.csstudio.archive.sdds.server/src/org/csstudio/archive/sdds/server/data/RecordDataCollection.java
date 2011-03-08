
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

import org.csstudio.archive.sdds.server.conversion.SampleCtrl;

/**
 * TODO (mmoeller) :
 * 
 * @author mmoeller
 * @version
 * @since 26.05.2010
 */
public class RecordDataCollection {
    
    /** Data of PV's */
    private EpicsRecordData[] data;
    
    /** The parameters of the data samples */
    private SampleCtrl sampleCtrl;
    
    /** Standard constructor */
    public RecordDataCollection() {
        data = new EpicsRecordData[0];
        sampleCtrl = new SampleCtrl();
    }

    public EpicsRecordData[] getData() {
        return data;
    }

    public void setData(EpicsRecordData[] data) {
        this.data = data;
    }

    public SampleCtrl getSampleCtrl() {
        return sampleCtrl;
    }

    public void setSampleCtrl(SampleCtrl sampleCtrl) {
        this.sampleCtrl = sampleCtrl;
    }
    
    /**
     * Returns the number of data samples.
     * 
     * @return
     */
    public int getNumberOfData() {
        
        int result = 0;
        
        if (data != null) {
            result = data.length;
        }
        
        return result;
    }
    
    /**
     * Return true if data is present.
     * 
     * @return
     */
    public boolean containsData() {
        return (getNumberOfData() > 0);
    }
}
