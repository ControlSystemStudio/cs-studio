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

package org.csstudio.dal.epics;

import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;


/**
 * Request implementation handling get requests.
 *
 * @author ikriznar
 */
public class GetRequest<T> extends RequestImpl<T> implements GetListener
{
    /**
     * Proxy of this request.
     */
    protected PropertyProxyImpl<T> proxy;

    /**
     * Construcor.
     * @param proxy proxy of this request.
     * @param l listener to notify.
     */
    public GetRequest(final PropertyProxyImpl<T> proxy, final ResponseListener<T> l)
    {
        super(proxy, l, 1);
        this.proxy = proxy;
    }

    /* (non-Javadoc)
     * @see gov.aps.jca.event.GetListener#getCompleted(gov.aps.jca.event.GetEvent)
     */
    @Override
    public void getCompleted(final GetEvent ev)
    {
        if (ev.getDBR()==null || ev.getStatus() == null || !ev.getStatus().isSuccessful()) {
            addResponse(new ResponseImpl<T>(proxy, this, null,
                    "value", false, null, proxy.getCondition(), null, true));
        } else {
            addResponse(new ResponseImpl<T>(proxy, this, proxy.toJavaValue(ev.getDBR()),
                "value", true, null, proxy.getCondition(), null, true));
        }
    }
}

/* __oOo__ */
