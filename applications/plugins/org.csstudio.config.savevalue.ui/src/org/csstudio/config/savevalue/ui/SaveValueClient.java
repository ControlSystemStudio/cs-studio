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

package org.csstudio.config.savevalue.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility class for calling the save value services. 
 * 
 * @author Joerg Rathlev
 */
final class SaveValueClient {
	
	/**
	 * Shut the f up, checkstyle.
	 */
	private SaveValueClient() {
	}
	
	/**
	 * Creates a {@code DecimalFormat} instance which can be used to format
	 * double values as required to write them in a CA file.
	 * 
	 * @return the formatted double value.
	 */
	private static DecimalFormat createCaFileDecimalFormat() {
		DecimalFormatSymbols dcf = new DecimalFormatSymbols(Locale.US);
		dcf.setDecimalSeparator('.');
		DecimalFormat result = new DecimalFormat("0.#", dcf); //$NON-NLS-1$
		return result;
	}
	
	/**
	 * Formats a double value using the syntax for CA files.
	 * 
	 * @param d
	 *            the double value.
	 * @param precision
	 *            the precision.
	 * @return a string representing the double value.
	 */
	public static String formatForCaFile(final double d, final int precision) {
		DecimalFormat format = createCaFileDecimalFormat();
		format.setMinimumFractionDigits(precision);
		format.setMaximumFractionDigits(precision);
		return format.format(d);
	}

}
