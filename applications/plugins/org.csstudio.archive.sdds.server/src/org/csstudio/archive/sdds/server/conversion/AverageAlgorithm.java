
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

import org.csstudio.archive.sdds.server.command.ServerCommandException;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.util.DataException;

/**
 * @author Markus Moeller
 *
 */
public class AverageAlgorithm implements ConversionAlgorithm
{
    /**
     * 
     * @throws ServerCommandException
     */
    public AverageAlgorithm()
    {
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.archive.jaapi.server.conversion.ConversionAlgorithm#getSamples(java.lang.String, long, long, long, java.util.Vector, org.csstudio.archive.jaapi.server.conversion.SampleCtrl)
     */
    @Override
    public void getSamples(String name, long dwStartTime, long dwInterval, long sampleCount,
                           EpicsRecordData[] data, SampleCtrl ctrl) throws DataException {
        long dwPointStartTime;
        long dwPointEndTime;
        long dwReadTime;
        long commonEndPoint;
        long now;
        long dwShiftTime;
        short onePortion = 0;
        
        commonEndPoint = dwStartTime + sampleCount * dwInterval;
        
        now = System.currentTimeMillis();
        
        dwPointStartTime = dwStartTime;
        dwShiftTime = dwStartTime;
        
        for(long lnCount = 0;lnCount < sampleCount;dwPointStartTime += dwInterval)
        {
            if(dwPointStartTime > now)
            {
                
            }
            
            dwPointEndTime = dwPointStartTime + dwInterval;
            
            //
            //
            //
            
            dwReadTime = dwPointStartTime;
            
            //
            //
            //
            
            do
            {
                
            }while((onePortion != 0) && (dwReadTime < dwPointEndTime));
        }
    }
    
    public void hold()
    {
//        long dwPointStartTime;
//        long dwPointEndTime;
//        long dwReadTime;
//        long commonEndPoint;
//        long now;
//        long dwShiftTime;
//        short onePortion = 0;
//        
//        commonEndPoint = dwStartTime + sampleCount * dwInterval;
//        
//        now = System.currentTimeMillis();
//        
//        dwPointStartTime = dwStartTime;
//        dwShiftTime = dwStartTime;
//        
//        for(long lnCount = 0;lnCount < sampleCount;dwPointStartTime += dwInterval)
//        {
//            if(dwPointStartTime > now)
//            {
//                
//            }
//            
//            dwPointEndTime = dwPointStartTime + dwInterval;
//            
//            //
//            //
//            //
//            
//            dwReadTime = dwPointStartTime;
//            
//            //
//            //
//            //
//            
//            do
//            {
//                
//            }while((onePortion != 0) && (dwReadTime < dwPointEndTime));
//        }
    }
}
