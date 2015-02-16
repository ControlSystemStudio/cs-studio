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


/**
 * This specialization of the <code>SimpleProperty</code> declares a set of
 * dynamic and static characteristics that belong to properties that
 * parametrize numeric dynamic values.
 *
 * <p>
 * Numeric properties are properties,  the
 * dynamic value of which is a numeric type, bound by a range (minimum  and
 * maximum), and has units. The characteristics which are static are accessed
 * by statically declared getter methods of this interface. The
 * characteristics which are dynamic are accessed through the  dynamic methods
 * of the <code>CharacteristicContext</code> interface. If the property and
 * the underlying layer are capable of providing  a characteristic with a
 * description given by this interface, they  must provide it under a name
 * which is one of the constants of this interface.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 * @version $id$
 */
public interface NumericSimpleProperty<T,Ts> extends SimpleProperty<T>,
	NumericPropertyCharacteristics
{
	/**
	 * Accessor method for units characteristic. If the property
	 * represents a physical value, this accessor returns the  units of the
	 * physical value. In other cases (or in case  of dimensionless
	 * quantities), this method must return  an empty string.
	 *
	 * @return String the units characteristic
	 *
	 * @exception DataExchangeException if the characteristic query  operation
	 *            fails
	 */
	public String getUnits() throws DataExchangeException;

	/**
	 * Accessor method for format characteristic. Return value is a
	 * C-style format string. Format specification is not bound to the actual
	 * type, it should however be compatible with the underlying data type.
	 * If implementation does not have this information, exception is thrown.
	 * This ensures, that caller can handle such cases.
	 *
	 * @return C-style format string.
	 *
	 * @throws DataExchangeException
	 */
	public String getFormat() throws DataExchangeException;

	/**
	 * Returns the minimum value that this property may take.
	 *
	 * @return the minimum value
	 *
	 * @exception DataExchangeException when the query fails
	 */
	public Ts getMinimum() throws DataExchangeException;

	/**
	 * Returns the maximum value that this property may take.
	 *
	 * @return the maximum value
	 *
	 * @exception DataExchangeException when the query fails
	 */
	public Ts getMaximum() throws DataExchangeException;
}

/* __oOo__ */
