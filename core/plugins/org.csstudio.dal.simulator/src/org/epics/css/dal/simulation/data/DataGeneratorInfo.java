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
package org.epics.css.dal.simulation.data;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Process variable name patterns for simulated channels.
 * 
 * @author swende
 * 
 */
public enum DataGeneratorInfo {

	/**
	 * Countdown generator pattern. The pattern reads the following variables in
	 * the process variable name
	 * <code> local://property COUNTDOWN:{from}:{to}:{period}:{update} </code>,
	 * for example <code>local://abc COUNTDOWN:100:0:10000:200</code> will
	 * count down from 100 to 0 in 10 seconds and an update event will be fired
	 * each 200 ms.
	 * 
	 */
	COUNTDOWN("^.* COUNTDOWN:([0-9]+):([0-9]+):([0-9]+):([0-9]+)$",
			new CountdownGeneratorFactory()),

	/**
	 * Random number generator pattern. The pattern reads the following
	 * variables in the process variable name
	 * <code> local://property RND:{from}:{to}:{period} </code>, for example
	 * <code>local://abc RND:1:100:10</code> which creates random numbers
	 * between 1 and 100 every 10 milliseconds.
	 * 
	 */
	RANDOM_NUMBER("^.* RND:([0-9]+):([0-9]+):([0-9]+)$",
			new RandomDoubleGeneratorFactory()),

	MEMORIZED("^.*", new MemorizedGeneratorFactory());
	
//	/**
//	 * Class method generator pattern. The pattern reads the following variables
//	 * in the process variable name
//	 * <code> local://property CLM:{classname}:{methodname}:{period} </code>,
//	 * for example <code>local://abc CLM:java.lang.String:toString:10</code>
//	 * which creates ...
//	 * 
//	 */
//	CLASS_METHOD("^.* CLM:(.+):(.+):([0-9]+)$",
//			new ClassMethodGenerator()),
//
//	SYSTEM_INFO("^.*SINFO:([a-zA-Z0-9]+)(:([0-9]+))?$",
//			new SystemInfoGenerator());

	private Pattern pattern;
	private ValueProviderFactory factory;

	private DataGeneratorInfo(String pattern,
			ValueProviderFactory factory) {
		this.pattern = Pattern.compile(pattern);
		this.factory = factory;
	}

	/**
	 * Returns the pattern used to match the name of the property to this
	 * data generator info.
	 * 
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * Returns the data generator factory associated with this info.
	 * 
	 * @return the data generator factory
	 */
	public ValueProviderFactory getDataGeneratorFactory() {
		return factory;
	}
	
	/**
	 * Returns the options as read from the property name.
	 * 
	 * @param name the property name
	 * @return the options
	 */
	public String[] getOptions(String name) {
		Matcher m = getPattern().matcher(name);

		if (m.find()) {
			final String[] options = new String[m.groupCount()];

			for (int i = 0; i < m.groupCount(); i++) {
				options[i] = m.group(i+1);
			}
			return options;
		}
		return null;
	}
	
	/**
	 * Reads the refresh rate from the property name.
	 * 
	 * @param name the property name
	 * @return the refresh rate in millis
	 */
	public long getRefreshRate(String name) {
		Matcher m = getPattern().matcher(name);

		if (m.find()) {
			final String[] options = new String[m.groupCount()];

			for (int i = 0; i < m.groupCount(); i++) {
				options[i] = m.group(i+1);
			}
			if (this==COUNTDOWN) {
				return Long.parseLong(options[3]);
			} else if (this == RANDOM_NUMBER) {
				return Long.parseLong(options[2]);
			} else {
				return 1000;
			}
		}
		return 1000;
	}
	
	/**
	 * Returns the appropriate data generator info based on the name of the proeprty.
	 *  
	 * @param name the name of the property
	 * @return the generator info
	 */
	public static DataGeneratorInfo getInfo(String name) {
		if (COUNTDOWN.pattern.matcher(name).find()) return COUNTDOWN;
		else if (RANDOM_NUMBER.pattern.matcher(name).find()) return RANDOM_NUMBER;
		return MEMORIZED;
	}
}
