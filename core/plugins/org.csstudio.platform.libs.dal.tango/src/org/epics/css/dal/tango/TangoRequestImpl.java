package org.epics.css.dal.tango;

import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.impl.RequestImpl;

import fr.esrf.TangoApi.CallBack;

/**
 * 
 * <code>TangoRequestImpl</code> is an implementation of the Request, which
 * also provides the Tango {@link CallBack} to be used by the Tango DAL api.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T> the type of data handled by this request
 */
public class TangoRequestImpl<T> extends RequestImpl<T> {

	protected TangoRequestCallback<T> callback;
	private PropertyProxyImpl<T> source;
	private T value;
	
	/**
	 * Constructs a new request implementation.
	 * 
	 * @param source the source of the request
	 * @param l the listener that will receive notifications from tango
	 */
	public TangoRequestImpl(PropertyProxyImpl<T> source, ResponseListener<T> l) {
		super(source,l);
		this.source = source;
	}
	
	/**
	 * Constructs a new request implementation.
	 * 
	 * @param source the source of the request
	 * @param l the listener that will receive notifications from tango
	 * @param value if this request is used in asynchronous write action, the value
	 * 			that was set should be given in constructor. This value is then 
	 * 			forwarded to the response listener when the response from tango
	 * 			arrives, because by default it does not provide the written value	
	 */
	public TangoRequestImpl(PropertyProxyImpl<T> source, ResponseListener<T> l, T value) {
		this(source,l);
		this.value = value;
	}

	/**
	 * Returns the TangoRequestCallback for this request. The returned
	 * callback is by default not associated with a monitor, but will
	 * sen all responses as last.
	 * 
	 * @return the callback
	 */
	TangoRequestCallback<T> getCallback() {
		if (callback == null) {
			callback = new TangoRequestCallback<T>(this,source,false,value);
		}
		return callback;
	}
	
}
