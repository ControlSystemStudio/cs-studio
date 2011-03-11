
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.archive.sdds.server.conversion.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.csstudio.archive.sdds.server.Activator;
import org.csstudio.archive.sdds.server.command.header.DataRequestHeader;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.internal.ServerPreferenceKey;
import org.csstudio.archive.sdds.server.util.DataException;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @since 08.03.2011
 */
public class MinMaxAverageHandler extends AlgorithmHandler {

    /** The logger for this class */
    private Logger logger;
    
    /** Max. allowed difference of the last allowed record (in seconds)*/ 
    @SuppressWarnings("unused")
	private long validRecordBeforeTime;
    
	/**
	 * Constructor.
	 * @param maxSamples
	 */
	public MinMaxAverageHandler(int maxSamples) {
		
		super(maxSamples);

        logger = CentralLogger.getInstance().getLogger(this);
        
        IPreferencesService pref = Platform.getPreferencesService();
        validRecordBeforeTime = pref.getLong(Activator.PLUGIN_ID,
                                             ServerPreferenceKey.P_VALID_RECORD_BEFORE, 3600, null);
        
        logger.debug("MinMaxAverageHandler created. Max. samples per request: " + maxSamples);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<EpicsRecordData> handle(DataRequestHeader header, EpicsRecordData[] data)
	throws DataException, AlgorithmHandlerException, MethodNotImplementedException {

        if (data == null) {
            return new ArrayList<EpicsRecordData>(0);
        } else if (data.length == 0){
            return new ArrayList<EpicsRecordData>(0);
        }

        // Get the number of requested samples
        int resultLength = header.getMaxNumOfSamples();
        
        // More then max. allowed number of samples?
        if(resultLength > this.getMaxSamplesPerRequest()) {
            resultLength = this.getMaxSamplesPerRequest();
        }
        
        long intervalStart = header.getFromSec();
        long intervalEnd = header.getToSec();

        long deltaTime = (intervalEnd - intervalStart) / (long) resultLength;
        if(deltaTime == 0) {
            
            // Requested region very short --> only 1 point per sec
            deltaTime = 1;
            header.setMaxNumOfSamples((int)(intervalEnd - intervalStart));
        }

        List<EpicsRecordData> resultData = new ArrayList<EpicsRecordData>(header.getMaxNumOfSamples());

		return resultData;
	}

}
