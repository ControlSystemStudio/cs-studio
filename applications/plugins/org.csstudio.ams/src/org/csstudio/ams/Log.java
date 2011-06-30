
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams;

import org.slf4j.LoggerFactory;

/**
 * This is a capsulate class if something must be changed or added
 * or central logger has an error.
 */
public class Log {
	
    public static final byte DEBUG = 1;
	public static final byte INFO = 2;
	public static final byte WARN = 3;
	public static final byte ERROR = 4;
	public static final byte FATAL = 5;
	
	/**
	 * Creates a log entry. 
	 * 
	 * @param level		byte
	 * @param t			Throwable
	 */
	public static void log(byte level, Throwable t) {
		log(null, level, t);
	}
	
	/**
	 * Creates a log entry.
	 * 
	 * @param obj		Object
	 * @param level		byte
	 * @param t			Throwable
	 */
	public static void log(Object obj, byte level, Throwable t) {
		
	    try {
			
	        switch (level) {
		      
			  case DEBUG:
			      LoggerFactory.getLogger(obj.getClass()).debug("{}", t);
			      break;
		      case INFO:
		          LoggerFactory.getLogger(obj.getClass()).info("{}", t);
		          break;
		      case WARN:
		          LoggerFactory.getLogger(obj.getClass()).warn("{}", t);
		          break;
		      case ERROR:
		          LoggerFactory.getLogger(obj.getClass()).error("{}", t);
		          break;
		      case FATAL:
		          LoggerFactory.getLogger(obj.getClass()).error("{}", t);
		          break;
		      default:
		          LoggerFactory.getLogger(obj.getClass()).error("{}", t);
		          break;
			}
		} catch (Exception e) {
		    // Can be ignored
		}
	}
	
	/**
	 * Creates a log entry.
	 * 
	 * @param level		byte
	 * @param msg		String
	 */
	public static void log(byte level, String msg) {
		log(null, level, msg);
	}
	
	/**
	 * Creates a log entry.
	 * 
	 * @param obj		Object
	 * @param level		byte
	 * @param msg		String
	 */
	public static void log(Object obj, byte level, String msg) {
		
	    try {
		    
	        switch (level) {
	            
	            case DEBUG:
	                LoggerFactory.getLogger(obj.getClass()).debug(msg);
	                break;
	            case INFO:
	                LoggerFactory.getLogger(obj.getClass()).info(msg);
	                break;
	            case WARN:
	                LoggerFactory.getLogger(obj.getClass()).warn(msg);
	                break;
	            case ERROR:
	                LoggerFactory.getLogger(obj.getClass()).error(msg);
	                break;
	            case FATAL:
	                LoggerFactory.getLogger(obj.getClass()).error(msg);
	                break;
	            default:
	                LoggerFactory.getLogger(obj.getClass()).error(msg);
	                break;
	        }
        } catch (Exception e) {
            // Can be ignored
        }
	}
	
	/**
	 * Creates a log entry.
	 * 
	 * @param level		byte
	 * @param msg		String
	 * @param t			Throwable
	 */
	public static void log(byte level, String msg, Throwable t) {
		log(null, level, msg, t);
	}
	
	/**
	 * Creates a log entry.
	 * 
	 * @param obj		Object
	 * @param level		byte
	 * @param msg		String
	 * @param t			Throwable
	 */
	public static void log(Object obj, byte level, String msg, Throwable t) {
	    
	    try {
	        
	        switch (level) {
		        case DEBUG:
		            LoggerFactory.getLogger(obj.getClass()).debug(msg, t);
		            break;
		        case INFO:
		            LoggerFactory.getLogger(obj.getClass()).info(msg, t);
		            break;
		        case WARN:
		            LoggerFactory.getLogger(obj.getClass()).warn(msg, t);
		            break;
		        case ERROR:
		            LoggerFactory.getLogger(obj.getClass()).error(msg, t);
		            break;
		        case FATAL:
		            LoggerFactory.getLogger(obj.getClass()).error(msg, t);
		            break;
		        default:
		            LoggerFactory.getLogger(obj.getClass()).error(msg, t);
		            break;
			}
        } catch (Exception e) {
            // Can be ignored
        }
	}
}
