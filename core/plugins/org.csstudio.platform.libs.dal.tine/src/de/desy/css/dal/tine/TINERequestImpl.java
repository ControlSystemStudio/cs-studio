package de.desy.css.dal.tine;

import java.util.EnumSet;

import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.Identifiable;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;

import de.desy.tine.client.TCallback;
import de.desy.tine.client.TLink;
import de.desy.tine.dataUtils.TDataType;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class TINERequestImpl extends RequestImpl implements TCallback {
	
	private PropertyProxyImpl proxy;
	private TLink tLink;
	
	public TINERequestImpl(Identifiable source, ResponseListener l) {
		super(source, l);
	}
	
	public TINERequestImpl(PropertyProxyImpl source, ResponseListener l, TLink tLink) {
		super(source, l);
		this.proxy = source;
		this.tLink = tLink;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.desy.tine.client.TCallback#callback(int, int)
	 */
	public void callback(int linkIndex, int linkStatus) {
		int statusCode = tLink.getLinkStatus();  
		Exception e = null;
        if (statusCode > 0 || linkStatus > 0) {
        	proxy.setConnectionState(ConnectionState.CONNECTION_LOST);
        	e = new RemoteException(proxy,tLink.getLastError());
        } else {
        	proxy.setConnectionState(ConnectionState.CONNECTED);
        }
              
        TDataType dout = tLink.getOutputDataObject();
        
        Object data = proxy.extractData(dout);
        Timestamp timestamp = new Timestamp(tLink.getLastTimeStamp(),0);
        ResponseImpl response = new ResponseImpl(getSource(),this,data,"",true, e,null,timestamp,true);
        
        addResponse(response);
        if (e == null) {
        	proxy.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),timestamp.getMilliseconds(),"Value updated."));
        }
		tLink.close();
	}

	
	
}
