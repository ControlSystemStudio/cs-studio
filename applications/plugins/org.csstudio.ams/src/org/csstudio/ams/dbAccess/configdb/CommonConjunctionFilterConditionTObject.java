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
 package org.csstudio.ams.dbAccess.configdb;

import java.io.Serializable;

import org.csstudio.ams.dbAccess.TObject;
import org.csstudio.ams.filter.IFilterCondition;

/**
 * This class represents a conjuncted FilterCondition.
 * @author C1 WPS / KM, MZ
 *
 */
public class CommonConjunctionFilterConditionTObject extends TObject implements
		Serializable {
	
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 3183596299015035085L;

	/**
	 * Filter this condition is assigned to.
	 */
	private int _ownFilterConditionReference;
	
	/**
	 * First FilterCondition this condition is using.
	 */
	private int _firstFilterConditionReference;
	
	/**
	 * Second FilterCondition this condition is using.
	 */
	private int _secondFilterConditionReference;
	
	public CommonConjunctionFilterConditionTObject() {
		this(-1,-1,-1);
	}
	
	/**
	 * Constructor.
	 * @param ownFilterConditionReference The id of this {@link IFilterCondition}
	 * @param firstFilterConditionReference The id of the first used {@link IFilterCondition}
	 * @param secondFilterConditionReference The id of the second used {@link IFilterCondition}
	 */
	public CommonConjunctionFilterConditionTObject(final int ownFilterConditionReference, final int firstFilterConditionReference, final int secondFilterConditionReference) {
		_ownFilterConditionReference = ownFilterConditionReference;
		_firstFilterConditionReference = firstFilterConditionReference;
		_secondFilterConditionReference = secondFilterConditionReference;
	}
	
	/**
	 * Sets the id of this {@link IFilterCondition}.
	 * @param ownFilterConditionReference The id
	 */
	public void setOwnFilterConditionReference(int ownFilterConditionReference) {
		_ownFilterConditionReference = ownFilterConditionReference;
	}

	/**
	 * Sets the id of the first used {@link IFilterCondition}.
	 * @param firstFilterConditionReference The id
	 */
	public void setFirstFilterConditionReference(int firstFilterConditionReference) {
		_firstFilterConditionReference = firstFilterConditionReference;
	}

	/**
	 * Sets the id of the second used {@link IFilterCondition}.
	 * @param secondFilterConditionReference The id
	 */
	public void setSecondFilterConditionReference(int secondFilterConditionReference) {
		_secondFilterConditionReference = secondFilterConditionReference;
	}

	/**
	 * Returns the id of this {@link IFilterCondition}.
	 * @return The id
	 */
	public int getOwnFilterConditionReference() {
		return _ownFilterConditionReference;
	}

	/**
	 * Returns the id of the first used {@link IFilterCondition}.
	 * @return The id
	 */
	public int getFirstFilterConditionReference() {
		return _firstFilterConditionReference;
	}

	/**
	 * Returns the id of the second used {@link IFilterCondition}.
	 * @return The id
	 */
	public int getSecondFilterConditionReference() {
		return _secondFilterConditionReference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _firstFilterConditionReference;
		result = prime * result + _ownFilterConditionReference;
		result = prime * result + _secondFilterConditionReference;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CommonConjunctionFilterConditionTObject))
			return false;
		final CommonConjunctionFilterConditionTObject other = (CommonConjunctionFilterConditionTObject) obj;
		if (_firstFilterConditionReference != other._firstFilterConditionReference)
			return false;
		if (_ownFilterConditionReference != other._ownFilterConditionReference)
			return false;
		if (_secondFilterConditionReference != other._secondFilterConditionReference)
			return false;
		return true;
	}

}
