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

package org.epics.css.dal.directory;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.spi.NamingManager;


/**
 * Convenience methods for handling DAL directory.
 *
 * @author ikriznar
 *
 */
public final class DirectoryUtilities
{
	private DirectoryUtilities()
	{
		super();
	}

	/**
	 * Returns DAL initial context.
	 * @return DAL initial context
	 * @throws NamingException if loading context fails
	 */
	public static DirContext getInitialContext() throws NamingException
	{
		Properties p = System.getProperties();

		try {
			URL url = ClassLoader.getSystemResource("jndi.properties");

			if (url != null) {
				FileInputStream fis = new FileInputStream(url.getFile());
				p.load(fis);
			}
		} catch (Exception e) {
			// TODO: exception should be logged somewhere
			return (DirContext)new InitialContextFactoryImpl().getInitialContext(System
			    .getProperties());
		}

		if (!p.containsKey("java.naming.factory.initial")) {
			p.setProperty("java.naming.factory.initial",
			    InitialContextFactoryImpl.class.getName());

			return (DirContext)NamingManager.getInitialContext(p);
		}

		return (DirContext)new InitialContextFactoryImpl().getInitialContext(System
		    .getProperties());
	}

	public static void main(String[] args) throws NamingException
	{
		System.out.println(getInitialContext());
	}
}

/* __oOo__ */
