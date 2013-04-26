package org.remotercp.common.tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class GenericServiceTracker<S> extends ServiceTracker {

	private S service;
	private List<IGenericServiceListener<S>> serviceListener;
	private final Class<S> clazz;

	public GenericServiceTracker(BundleContext context, Class<S> clazz) {

		super(context, clazz.getName(), null);
		this.clazz = clazz;
//		this.serviceListener = new ArrayList<IGenericServiceListener<S>>();
		this.serviceListener = Collections
				.synchronizedList(new ArrayList<IGenericServiceListener<S>>());
	}

	@Override
	public Object addingService(ServiceReference reference) {
		service = clazz.cast(super.addingService(reference));

		informListener();
		return service;
	}

	private void informListener() {
		for (IGenericServiceListener<S> listener : serviceListener) {
			listener.bindService(service);
		}
	}

	public void addServiceListener(IGenericServiceListener<S> serviceListener) {
		// inform immediately if service available
		if (this.service != null) {
			serviceListener.bindService(service);
		}
		this.serviceListener.add(serviceListener);
	}
}
