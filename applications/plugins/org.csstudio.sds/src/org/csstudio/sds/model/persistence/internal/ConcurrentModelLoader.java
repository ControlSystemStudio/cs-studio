package org.csstudio.sds.model.persistence.internal;

import java.io.InputStream;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.persistence.IDisplayModelLoadListener;

/**
 * Thread that loads display models concurrently.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class ConcurrentModelLoader extends Thread {

	/**
	 * The display model that is to be filled with the loaded data.
	 */
	private DisplayModel _displayModel;

	/**
	 * The source file resource.
	 */
	private InputStream _inputStream;

	/**
	 * Optional listener that will be notified of model loading events.
	 */
	private IDisplayModelLoadListener _loadListener;

	/**
	 * Standard constructor.
	 * 
	 * @param displayModel
	 *            The display model that is to be filled with the loaded data.
	 * @param inputStream
	 *            The source file resource.
	 * @param loaderCallback
	 *            Optional listener that will be notified of model loading
	 *            events (can be null).
	 */
	public ConcurrentModelLoader(final DisplayModel displayModel,
			final InputStream inputStream,
			final IDisplayModelLoadListener loadListener) {
		assert displayModel != null;
		assert inputStream != null;

		_displayModel = displayModel;
		_inputStream = inputStream;
		_loadListener = loadListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		_displayModel.setLoading(true);

		DisplayModelReader reader = new DisplayModelReader();
		reader.readModelFromXml(_inputStream,
				_displayModel, _loadListener);

		if (reader.isErrorOccurred()) {
			if (_loadListener != null) {
				_loadListener.onErrorsOccured(reader
						.getErrorMessages());
			}
		}

		if (_loadListener != null) {
			_loadListener.onDisplayModelLoaded();
		}
		
		_displayModel.setLoading(false);
	}
}
