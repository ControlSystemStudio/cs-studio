package org.csstudio.nams.service.messaging;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MessagingActivator extends Plugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.messaging";
//	private ServiceTracker _ensureJustOneInstanceTracker;

	/**
	 * The constructor
	 */
	public MessagingActivator() {
	}

	/**
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		// TODO Use extension
//		final String classname = ConsumerFactoryService.class.getName();
//		_ensureJustOneInstanceTracker = new ServiceTracker(context, classname,
//				null) {
//			private boolean _addedBefore = false;
//
//			@Override
//			public Object addingService(ServiceReference reference) {
//				if (_addedBefore) {
//					throw new RuntimeException(
//							"Duplicated registration of service " + classname
//									+ " is prohibed.");
//				}
//				_addedBefore = true;
//				return super.addingService(reference);
//			}
//
//			@Override
//			public void removedService(ServiceReference reference,
//					Object service) {
//				throw new RuntimeException("Removeval of service " + classname
//						+ " is prohibed.");
//			}
//		};
//		_ensureJustOneInstanceTracker.open();
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
//		_ensureJustOneInstanceTracker.close();
	}
}
