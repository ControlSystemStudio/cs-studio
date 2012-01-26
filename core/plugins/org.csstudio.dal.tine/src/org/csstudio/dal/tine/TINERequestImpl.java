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

package org.csstudio.dal.tine;

import java.util.EnumSet;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.context.Identifiable;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;

import de.desy.tine.client.TCallback;
import de.desy.tine.client.TLink;
import de.desy.tine.dataUtils.TDataType;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class TINERequestImpl<T> extends RequestImpl<T> implements TCallback {
	
	private PropertyProxyImpl<T> proxy;
	private TLink tLink;
	
	public TINERequestImpl(Identifiable source, ResponseListener<T> l) {
		super(source, l);
	}
	
	public TINERequestImpl(PropertyProxyImpl<T> source, ResponseListener<T> l, TLink tLink) {
		super(source, l);
		this.proxy = source;
		this.tLink = tLink;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.desy.tine.client.TCallback#callback(int, int)
	 */
	public void callback(int linkIndex, int linkStatus) {
		int statusCode = this.tLink.getLinkStatus();  
		Exception e = null;
        if (statusCode > 0 || linkStatus > 0) {
        	e = new RemoteException(this.proxy,this.tLink.getLastError());
        	this.proxy.setConnectionState(ConnectionState.CONNECTION_LOST,e);
        } else {
        	this.proxy.setConnectionState(ConnectionState.CONNECTED);
        }
              
        TDataType dout = this.tLink.getOutputDataObject();
        
        T data = this.proxy.extractData(dout);
        Timestamp timestamp = new Timestamp(this.tLink.getLastTimeStamp(),0);
        ResponseImpl<T> response = new ResponseImpl<T>(getSource(),this,data,"",true, e,null,timestamp,true);
        
        addResponse(response);
        if (e == null) {
        	this.proxy.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),null,"Value updated."));
        }
		this.tLink.close();
	}	
}
