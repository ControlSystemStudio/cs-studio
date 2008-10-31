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
package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;
import org.epics.css.dal.DynamicValueState;

/**
 * Color rule Alarm, translated from an ADL file.
 * 
 * @author jbercic, jhatje
 * 
 */
public final class Alarm implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.alarm";

	/**
	 * Standard constructor.
	 */
	public Alarm() {
	}

	/**
	 * Map the severity of a pv value to a color. For DAL severities
	 * (pv_name[severity]) the parameter 'arguments' is of type
	 * DynamicValueState.ID. Using the EPICS field (record.SEVR) the severity is
	 * a number.
	 * 
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			double d = 300.0;
			String s = "init";
			if (arguments[0] instanceof Double) {
				d = (Double) arguments[0];
			} else if (arguments[0] instanceof Long) {
				d = ((Long) arguments[0]).doubleValue();
			} else if (arguments[0] instanceof String) {
				s = (String) arguments[0];
			}

			if ((Math.abs(d - 0.0) < 0.00001)
					|| (s.equals(DynamicValueState.NORMAL.toString()))) {
				return new RGB(0, 216, 0);
			}
			if ((Math.abs(d - 1.0) < 0.00001)
					|| (s.equals(DynamicValueState.WARNING.toString()))) {
				return new RGB(251, 243, 74);
			}
			if ((Math.abs(d - 2.0) < 0.00001)
					|| (s.equals(DynamicValueState.ALARM.toString()))) {
				return new RGB(253, 0, 0);
			}
			if ((d >= 3.0 && d <= 255.0)
					|| (s.equals(DynamicValueState.ERROR.toString()))) {
				return new RGB(255, 255, 255);
			}
		}

		return new RGB(0, 0, 0);
	}
}
