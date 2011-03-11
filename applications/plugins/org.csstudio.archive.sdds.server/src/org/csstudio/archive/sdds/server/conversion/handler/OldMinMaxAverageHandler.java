
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

package org.csstudio.archive.sdds.server.conversion.handler;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.csstudio.archive.sdds.server.Activator;
import org.csstudio.archive.sdds.server.command.header.DataRequestHeader;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.internal.ServerPreferenceKey;
import org.csstudio.archive.sdds.server.sdds.SDDSType;
import org.csstudio.archive.sdds.server.util.ArchiveSeverity;
import org.csstudio.archive.sdds.server.util.DataException;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 23.06.2010
 */
public class OldMinMaxAverageHandler extends AlgorithmHandler {
    
    /** The logger for this class */
    private Logger logger;
    
    /** Max. allowed difference of the last allowed record (in seconds)*/ 
    private long validRecordBeforeTime;
    
    public OldMinMaxAverageHandler(int maxSamples) {
        super(maxSamples);
        logger = CentralLogger.getInstance().getLogger(this);
        
        IPreferencesService pref = Platform.getPreferencesService();
        validRecordBeforeTime = pref.getLong(Activator.PLUGIN_ID,
                                             ServerPreferenceKey.P_VALID_RECORD_BEFORE, 3600, null);
        
        logger.debug("MinMaxAverageHandler created. Max. samples per request: " + maxSamples);
    }

    /* (non-Javadoc)
     * @see org.csstudio.archive.jaapi.server.conversion.handler.AlgorithmHandler#handle(org.csstudio.archive.jaapi.server.command.header.DataRequestHeader, org.csstudio.archive.jaapi.server.data.EpicsRecordData[])
     */
    @Override
    public Iterable<EpicsRecordData> handle(DataRequestHeader header, EpicsRecordData[] data)
    throws DataException, AlgorithmHandlerException, MethodNotImplementedException {

        List<EpicsRecordData> tempData = new ArrayList<EpicsRecordData>();
        EpicsRecordData newData = null;
        EpicsRecordData curData = null;
        float average = Float.NaN;
        float tempMin = 0.0f;
        float tempMax = 0.0f;
        float curValue;
        float sum;
        float count;
        long deltaTime;
        int index;
        int dataLength;

        logger.debug("MinMaxAverageHandler is processing data.");
        
        if (data == null) {
            return new ArrayList<EpicsRecordData>(0);
        } else if (data.length == 0){
            return new ArrayList<EpicsRecordData>(0);
        }

        long intervalStart = header.getFromSec();
        long intervalEnd = header.getToSec();
        
        if(header.getMaxNumOfSamples() == 0) {
            throw new AlgorithmHandlerException(0, "Requested number of samples is 0.");
        }
        
        deltaTime = (intervalEnd - intervalStart) / header.getMaxNumOfSamples();
        if(deltaTime == 0) {
            
            // Requested region very short --> only 1 point per sec
            deltaTime = 1;
            header.setMaxNumOfSamples((int)(intervalEnd - intervalStart));
        }
        
        dataLength = data.length - 1;
        index = 0;
        if(dataLength > -1) {
            
            // Get the timestamp of the last received data
            intervalEnd = data[dataLength].getTime() + deltaTime;

            if(data[index].getTime() < intervalStart) {
                if((intervalStart - data[index].getTime()) > validRecordBeforeTime) {
                    index = (index > data.length) ? 0 : index++;
                }
            }
        
            long nextTime = 0;
            
            for(long curTime = intervalStart;curTime < intervalEnd;curTime += deltaTime) {            
                
                nextTime = curTime + deltaTime;
                
                if(dataLength >= -1) {
                    
                  count = 0.0f;
                  sum = 0.0f;
    
                    do {
                        if(index < data.length) {
                            curData = data[index];
                        } else {
                            curData = null;
                            break;
                        }
                        
                        if(curData != null) {
                            
                            if((curData.getSeverity() < ArchiveSeverity.INVALID.getSeverityValue())
                                    || (curData.getSeverity() == ArchiveSeverity.REPEAT.getSeverityValue())
                                    || (curData.getSeverity() == ArchiveSeverity.EST_REPEAT.getSeverityValue())) {
    
                                if((curData.getTime() >= curTime) && (curData.getTime() < nextTime)) {
                                
                                    curValue = (Float)data[index].getValue();
                                    sum += curValue;
                                    
                                    if(count < 1.0) {
                                        tempMin = curValue;
                                        tempMax = curValue;
                                    } else {
                                        tempMin = (curValue < tempMin) ? curValue : tempMin;
                                        tempMax = (curValue > tempMax) ? curValue : tempMax;
                                    }
                                    
                                    if(index < data.length) {
                                        index++;
                                        count += 1.0f;
                                    } else {
                                        break;
                                    }
                                } else {
                                    if(curData.getTime() < nextTime) {
                                        if(index < data.length) {
                                            index++;
                                            count += 1.0f;
                                        } else {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                if(index < data.length) {
                                    index++;
                                    //count += 1.0f;
                                } else {
                                    break;
                                }
                            }
                        }
                    } while((curData.getTime() <= nextTime));
                    
                    if(count > 0.0f) {
                        average = sum / count;
                    } else {
                        average = (average != Float.NaN) ? average : 0.0f;
                    }
                } else {
                    average = this.ERROR_VALUE;
                    tempMin = this.ERROR_VALUE;
                    tempMax = this.ERROR_VALUE;
                }
                
                newData = new EpicsRecordData(curTime, 0L, 0L, new Double(String.valueOf(tempMin)), SDDSType.SDDS_DOUBLE);
                tempData.add(newData);
                logger.debug(newData.toString());
                newData = null;
                  
                newData = new EpicsRecordData(curTime, 0L, 0L, new Double(String.valueOf(tempMax)), SDDSType.SDDS_DOUBLE);
                tempData.add(newData);
                logger.debug(newData.toString());
                newData = null;
                
                newData = new EpicsRecordData(curTime, 0L, 0L, new Double(String.valueOf(average)), SDDSType.SDDS_DOUBLE);
                tempData.add(newData);
                logger.debug(newData.toString());
                newData = null;
            }
        }
           
        return tempData;
    }
}
