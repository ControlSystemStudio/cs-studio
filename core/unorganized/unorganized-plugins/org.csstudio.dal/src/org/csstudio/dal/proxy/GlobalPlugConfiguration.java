/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal.proxy;

import java.util.Properties;


/**
 *
 *    Global plug configuration
 * @author ikriznar
 */
public class GlobalPlugConfiguration extends Properties
{
	private static final long serialVersionUID = 4279325169774545009L;

	private static GlobalPlugConfiguration global;

	/** Global plug timeout property name */
	public static final String GLOBAL_PLUG_TIMEOUT = "GlobalPlug.timeout";

	static final synchronized GlobalPlugConfiguration getGlobalPlugConfiguration()
	{
		if (global == null) {
			global = new GlobalPlugConfiguration();
		}

		return global;
	}

	/**
	 * Creates new global configuration with System properties as default.
	 */
	public GlobalPlugConfiguration()
	{
		super(System.getProperties());
	}

	/**
	 * Creates new global configuration with supplied properties as default
	 * @param defaults Default configuration properties
	 */
	public GlobalPlugConfiguration(Properties defaults)
	{
		super(defaults);
	}

	/**
	 * Returns default plug timeout
	 *
	 * @return Default plug timeout.
	 */
	public long getDefaultTimeout()
	{
		return Long.parseLong(getProperty(GLOBAL_PLUG_TIMEOUT, "60000"));
	}
}

/* __oOo__ */
