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
package org.csstudio.platform;

import java.util.Locale;

/**
 * 
 * @author awill
 * 
 */
public final class LocaleService {
	/**
	 * Property ID for the locale setting.
	 */
	public static final String PROP_LOCALE = "locale"; //$NON-NLS-1$

	/**
	 * This class can not be instantiated.
	 */
	private LocaleService() {
	}	
	
	/**
	 * Set the system's default locate according to the CSS settings.
	 * 
	 * @param locale
	 *            The system locale.
	 * 
	 */
	public static void setSystemLocale(final String locale) {
		if (locale != null) {
			String[] elements = locale.split("[_]"); //$NON-NLS-1$
			String language = ""; //$NON-NLS-1$
			String country = ""; //$NON-NLS-1$

			if (elements != null) {
				if (elements.length > 0) {
					language = elements[0];
				}

				if (elements.length > 1) {
					country = elements[1];
				}
			}

			if (language.length() > 0) {
				Locale.setDefault(new Locale(language, country, "")); //$NON-NLS-1$
			}
		}
	}
}
