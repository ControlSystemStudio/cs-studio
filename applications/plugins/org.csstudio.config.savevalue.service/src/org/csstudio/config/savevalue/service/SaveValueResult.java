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
package org.csstudio.config.savevalue.service;

import java.io.Serializable;

/**
 * The result of a save value call. Objects of this class should only be used
 * to represent the results of successful save value calls. If a save value
 * call fails, it should throw a {@link SaveValueServiceException}.
 * 
 * @author Joerg Rathlev
 */
public final class SaveValueResult implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3013941933990845967L;
	
	/**
	 * The old value that was replaced by the save value call, or
	 * <code>null</code> if a new entry was added.
	 */
	private final String _replacedValue;

	/**
	 * Creates a new save value result.
	 * 
	 * @param replacedValue
	 *            the old value that was replaced, or <code>null</code> if a
	 *            new entry was added.
	 */
	public SaveValueResult(final String replacedValue) {
		_replacedValue = replacedValue;
	}

	/**
	 * Returns the value that was replaced by the save value call. If a new
	 * entry was added, returns <code>null</code>.
	 * 
	 * @return the value that was replaced, or <code>null</code> if a new
	 *         entry was added.
	 */
	public String getReplacedValue() {
		return _replacedValue;
	}
}
