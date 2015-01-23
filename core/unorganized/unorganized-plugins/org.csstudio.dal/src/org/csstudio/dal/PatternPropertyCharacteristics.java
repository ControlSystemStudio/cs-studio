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
 *  Decleares Characteristics which may be contained  in <code>PatternProperty</code>
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>
 */
public interface PatternPropertyCharacteristics extends PropertyCharacteristics
{
	/**
	 * Name of the characteristic describing bits. Returned value type
	 * is <code>String[]</code>.
	 */
	public static final String C_BIT_DESCRIPTIONS = CharacteristicInfo.C_BIT_DESCRIPTIONS.getName();

	/**
	 * Name of the characteristic defining active bit significance.
	 * Returned value type is <code>BitCondition[]</code>.
	 */
	public static final String C_CONDITION_WHEN_SET = CharacteristicInfo.C_CONDITION_WHEN_SET.getName();

	/**
	 * Name of the characteristic defining inactive bit significance.
	 * Returned value type is <code>BitCondition[]</code>.
	 */
	public static final String C_CONDITION_WHEN_CLEARED = CharacteristicInfo.C_CONDITION_WHEN_CLEARED.getName();

	/**
	 * Name of the characteristic defining bits relevance. Returned
	 * value type is <code>BitSet</code>.
	 */
	public static final String C_BIT_MASK = CharacteristicInfo.C_BIT_MASK.getName();
} /* __oOo__ */


/* __oOo__ */
