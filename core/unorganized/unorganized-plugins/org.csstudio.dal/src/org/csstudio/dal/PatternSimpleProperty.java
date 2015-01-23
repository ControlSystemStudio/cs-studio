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

package org.csstudio.dal;

import com.cosylab.util.BitCondition;

import java.util.BitSet;


/**
 * This interface defines a <b>java.util.BitSet</b> property, by extending pattern access
 * and abstract property. Statically defined characteristics are defined as well.
 *
 * @author <a href="mailto:matej.sekoranja@cosylab.com">Matej Sekoranja</a>
 */
public interface PatternSimpleProperty extends PatternAccess,
	SimpleProperty<BitSet>, PatternPropertyCharacteristics
{
	/**
	 * Returns an array of Strings. For each bit, this array contains a
	 * (maximum) one line description.
	 *
	 * @return String[] an array of descriptions
	 *
	 * @throws DataExchangeException id access to remote layer fails
	 */
	public String[] getBitDescriptions() throws DataExchangeException;

	/**
	 * Returns an array of <code>Condition</code> objects that carry
	 * information on significance of each bit when it is cleared.
	 *
	 * @return BitCondition[] an array of rendering hints for cleared bits
	 *
	 * @throws DataExchangeException id access to remote layer fails
	 */
	public BitCondition[] getConditionWhenCleared()
		throws DataExchangeException;

	/**
	 * Returns an array of <code>Condition</code> objects that carry
	 * information on significance of each bit when it is set.
	 *
	 * @return BitCondition[] an array of rendering hints for set bits
	 *
	 * @throws DataExchangeException id access to remote layer fails
	 */
	public BitCondition[] getConditionWhenSet() throws DataExchangeException;

	/**
	 * Returns a bit mask determining which bits in the
	 * <code>value</code> are significant. To be used by displayers not to
	 * show unused bits.
	 *
	 * @return BitSet a bit mask
	 *
	 * @throws DataExchangeException id access to remote layer fails
	 */
	public BitSet getBitMask() throws DataExchangeException;
}

/* __oOo__ */
