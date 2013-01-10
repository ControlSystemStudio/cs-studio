
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.websuite.utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import org.csstudio.websuite.dataModel.AlarmMessage;
import org.csstudio.websuite.dataModel.BasicMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**Comparator class for comapring BasicMessages in defined order PRIORITY,OUTDATED,ACK,EVENTTIME
 * @author ababic
 *
 */
public class BasicMessageComparator implements Comparator<BasicMessage>, Serializable{
    
    private static final Logger LOG = LoggerFactory.getLogger(BasicMessageComparator.class);
	
	/** Generated serial version id */
    private static final long serialVersionUID = 2309005084573802194L;

    @Override
	public int compare(BasicMessage msg1, BasicMessage msg2){
		try{
			if(msg1.getSeverityNumber()<msg2.getSeverityNumber()){
				return -1;
			}else if(msg1.getSeverityNumber()>msg2.getSeverityNumber()){
				return 1;
			}else{
				if(!((AlarmMessage)msg1).isOutdated()&&((AlarmMessage)msg2).isOutdated()){
					return -1;
				}if(((AlarmMessage)msg1).isOutdated()&&!((AlarmMessage)msg2).isOutdated()){
					return 1;
				}else{
					if(msg1.getHashMap().get("ACK")!=null && msg2.getHashMap().get("ACK")==null){
						return -1;
					}else if(msg1.getHashMap().get("ACK")==null && msg2.getHashMap().get("ACK")!=null){
						return 1;
					}else{
						try{
							DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
							if(sdf.parse(msg1.getHashMap().get("EVENTTIME")).getTime()<sdf.parse(msg2.getHashMap().get("EVENTTIME")).getTime()){
								return -1;
							}else if(sdf.parse(msg1.getHashMap().get("EVENTTIME")).getTime()>sdf.parse(msg2.getHashMap().get("EVENTTIME")).getTime()){
								return 1;
							}else{
								return 0;
							}
						}catch (ParseException e) {
							LOG.error("Message with faulty date", e);
							return 0;
						}
					}
				}
			}
		}catch (Exception e) {
			LOG.error("Comparing problem", e);
			return 0;
		}
		
	}
}
