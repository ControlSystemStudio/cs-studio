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

package org.epics.css.dal;


/**
 *
 *  DynamicValueListener interface for convenience. Extending class can
 *  override only necessary methods.
 *
 * @author Igor Kriznar (igor.kriznar@cosylab.com) Skeleton implementation of
 * @see org.epics.css.dal.DynamicValueListener
 */
public class DynamicValueAdapter<T, P extends SimpleProperty<T>>
	implements DynamicValueListener<T, P>
{
	/**
	 * Constructor for DynamicValueAdapter.
	 */
	public DynamicValueAdapter()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#valueUpdated(org.epics.css.dal.DynamicValueEvent)
	 */
	public void valueUpdated(DynamicValueEvent<T, P> event)
	{
		// ovrride this if necessary
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#valueChanged(org.epics.css.dal.DynamicValueEvent)
	 */
	public void valueChanged(DynamicValueEvent<T, P> event)
	{
		// ovrride this if necessary
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#timeoutStarts(org.epics.css.dal.DynamicValueEvent)
	 */
	public void timeoutStarts(DynamicValueEvent<T, P> event)
	{
		// ovrride this if necessary
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#timeoutStops(org.epics.css.dal.DynamicValueEvent)
	 */
	public void timeoutStops(DynamicValueEvent<T, P> event)
	{
		// ovrride this if necessary
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#timelagStarts(org.epics.css.dal.DynamicValueEvent)
	 */
	public void timelagStarts(DynamicValueEvent<T, P> event)
	{
		// ovrride this if necessary
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#timelagStops(org.epics.css.dal.DynamicValueEvent)
	 */
	public void timelagStops(DynamicValueEvent<T, P> event)
	{
		// ovrride this if necessary
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#errorResponse(org.epics.css.dal.DynamicValueEvent)
	 */
	public void errorResponse(DynamicValueEvent<T, P> event)
	{
		// ovrride this if necessary
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DynamicValueListener#conditionChange(org.epics.css.dal.DynamicValueEvent)
	 */
	public void conditionChange(DynamicValueEvent<T, P> event)
	{
		// ovrride this if necessary
	}
} /* __oOo__ */


/* __oOo__ */
