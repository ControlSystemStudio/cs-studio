/*
 * Copyright (c) 2006 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package org.epics.css.dal.epics;

import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;


/**
 * Request implementation handling get requests.
 *
 * @author ikriznar
 */
public class GetRequest extends RequestImpl implements GetListener
{
	/**
	 * Proxy of this request.
	 */
	protected PropertyProxyImpl proxy;

	/**
	 * Construcor.
     * @param proxy proxy of this request.
     * @param l listener to notify.
     */
	public GetRequest(PropertyProxyImpl proxy, ResponseListener l)
	{
		super(proxy, l, 1);
		this.proxy = proxy;
	}

	/* (non-Javadoc)
	 * @see gov.aps.jca.event.GetListener#getCompleted(gov.aps.jca.event.GetEvent)
	 */
	public void getCompleted(GetEvent ev)
	{
		addResponse(new ResponseImpl(proxy, this, proxy.toJavaValue(ev.getDBR()),
		        "value", ev.getStatus().isSuccessful(), null,
		        proxy.getCondition(), null, true));
	}
}

/* __oOo__ */
