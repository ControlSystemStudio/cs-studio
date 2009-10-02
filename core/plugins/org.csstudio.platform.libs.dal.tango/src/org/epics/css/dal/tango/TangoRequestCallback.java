package org.epics.css.dal.tango;

import java.util.EnumSet;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.Response;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;

import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.AttrReadEvent;
import fr.esrf.TangoApi.AttrWrittenEvent;
import fr.esrf.TangoApi.CallBack;
import fr.esrf.TangoApi.CmdDoneEvent;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.events.EventData;

/**
 * 
 * <code>TangoRequestCallback</code> is a callback class that 
 * receives notifications from the tango control system and forwards
 * them to the request that initiated the asynchronous call.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T> the type of data handled by this request
 */
class TangoRequestCallback<T> extends CallBack {

	private static final long serialVersionUID = 8001560457662183225L;
	
	private RequestImpl<T> request;
	private PropertyProxyImpl<T> source;
	private boolean monitor;
	private T value;
	
	/**
	 * Constructs a new TangoRequestCallback.
	 * 
	 * @param request the request that will receive updates
	 * @param source the source proxy
	 * @param monitor true if this callback is associated with monitor or false 
	 * 			otherwise. If associated with a monitor the responses will
	 * 			not be final/last, otherwise they will be.
	 * @param the vale which was set if the callback listens to write response
	 * 			(@see {@link TangoRequestImpl#TangoRequestImpl(PropertyProxyImpl, org.epics.css.dal.ResponseListener, Object)}).
	 */
	TangoRequestCallback(RequestImpl<T> request, PropertyProxyImpl<T> source, boolean monitor, T value) {
		super();
		this.request = request;
		this.source = source;
		this.monitor = monitor;
		this.value = value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.esrf.TangoApi.CallBack#attr_read(fr.esrf.TangoApi.AttrReadEvent)
	 */
	@Override
	public void attr_read(AttrReadEvent evt) {
		processEvent(evt.argout, evt.err, evt.errors);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.esrf.TangoApi.CallBack#attr_written(fr.esrf.TangoApi.AttrWrittenEvent)
	 */
	@Override
	public void attr_written(AttrWrittenEvent evt) {
		processEvent(null, evt.err, evt.errors);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.esrf.TangoApi.CallBack#cmd_ended(fr.esrf.TangoApi.CmdDoneEvent)
	 */
	@Override
	public void cmd_ended(CmdDoneEvent evt) {
		//FIXME the commands are forwarded in the same way as write
		//the cmd event does not have DeviceAttribute but have DeviceData,
		//which has the same code interface but is not related hierarchically
		processEvent(null,evt.err,evt.errors);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.esrf.TangoApi.CallBack#push_event(fr.esrf.TangoApi.events.EventData)
	 */
	@Override
	public void push_event(EventData evt) {
		super.push_event(evt);
		processEvent(new DeviceAttribute[]{evt.attr_value},evt.err,evt.errors);
	}
	
	private void processEvent(DeviceAttribute[] attr, boolean error, DevError[] errors) {
    	if (error) {
    		DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),new Timestamp(System.currentTimeMillis(),0),"Error initializing proxy");
    		source.setCondition(condition);
    		Response<T> r = new ResponseImpl<T>(source,request,value,
    				source.getUniqueName(),false,new DataExchangeException(this,errors[0].desc),source.getCondition(),
    				new Timestamp(System.currentTimeMillis(),0),!monitor);
    		request.addResponse(r);
    	} else if (attr == null) {
    		//write event
    		DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),new Timestamp(System.currentTimeMillis(),0),"Error initializing proxy");
			source.setCondition(condition);
			Response<T> r = new ResponseImpl<T>(source,request,value,
					source.getUniqueName(),true,null,source.getCondition(),
					new Timestamp(System.currentTimeMillis(),0),!monitor);
			request.addResponse(r);
    	} else {
    		try {
        		for (DeviceAttribute a : attr) {
        			if (a.getName().equals(source.getPropertyName().getPropertyName())) {
        				T value = source.extractValue(a);
        				DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),new Timestamp(System.currentTimeMillis(),0),"Error initializing proxy");
        				source.setCondition(condition);
        				Response<T> r = new ResponseImpl<T>(source,request,value,
        						source.getUniqueName(),true,null,source.getCondition(),
        						new Timestamp(System.currentTimeMillis(),0),!monitor);
        				request.addResponse(r);
        				return;
        			}
        		}
    		} catch (DevFailed e) {
    			DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),new Timestamp(System.currentTimeMillis(),0),"Error initializing proxy");
    			source.setCondition(condition);
    			Response<T> r = new ResponseImpl<T>(source,request,value,
    					source.getUniqueName(),true,e,source.getCondition(),
    					new Timestamp(System.currentTimeMillis(),0),!monitor);
    			request.addResponse(r);
    		}
    	}
	}
}
