/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.trends.databrowser.fileimport;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 28.04.2008
 */
public class SampleFileImportSettings extends CSVImportSettings {
    
    /**
     * List whit all Channels from a Import File. 
     */
    ArrayList<Channel> _channelList = new ArrayList<Channel>();
    
    /**
     * Default sample start time.
     */
    Calendar _startTime;
    
    /**
     * Default sample end time.
     */
    Calendar _endTime;
    
    /**
     * The factor to parse the Samples. (i+factor)
     */
    private int _factor = 1;
    
    /**
     * The number of first Sample to pars.
     */
    private int _startSample;
    
    /**
     * The number of last Sample to pars.
     */
    int _endSample;
    
    /**
     * Number of selected samples.
     */
    private int _selectedSize;
    
    /**
     * The default are the value of the first channel.
     * @return the default resolution in millisec.
     */
    final double getDefaultResolution() {
        double value = _channelList.get(0).getResolution()/1000d;
    	return value;
    }

    /**
     * The default are the value of the first channel.
     * @return the default resolution in microsec.
     */
    final long getDefaultResolutionMiccro() {
        return _channelList.get(0).getResolution();
    }
    
    /**
     * 
     * @return the factor.
     */
    public final int getFactor() {
        return _factor;
    }

    /**
     * Can not set smaller than 1. 
     * @param factor Set the factor.
     */
    public final void setFactor(final int factor) {
        _factor = factor;
        if(_factor<1){
            _factor=1;
        }
        
    }

    /**
     * 
     * @return the Sample-number to begin parsing.
     */
    public final int getStartSample() {
        return _startSample;
    }

    /**
     * 
     * @param startSample the Sample-number to begin parsing.
     */
    public final void setStartSample(final int startSample) {
        _startSample = startSample;
    }

	public int getSelectedSize() {
		return _selectedSize;
	}

	public void setSelectedSize(int size) {
		_selectedSize = size;
	}
}
