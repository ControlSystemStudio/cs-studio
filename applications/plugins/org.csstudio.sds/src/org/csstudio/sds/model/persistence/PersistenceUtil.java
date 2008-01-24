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
package org.csstudio.sds.model.persistence;

import java.io.InputStream;

import org.csstudio.platform.ExecutorAccess;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.persistence.internal.ConcurrentModelLoader;
import org.csstudio.sds.model.persistence.internal.DisplayModelReader;

/**
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public class PersistenceUtil {
	/**
	 * Load the given display model from the given input stream synchroneously.
	 * 
	 * @param displayModel
	 *            The diplay model to load.
	 * @param inputStream
	 *            The input stream to load the display model from.
	 */
	public static void syncFillModel(final DisplayModel displayModel,
			final InputStream inputStream) {
		DisplayModelReader reader = new DisplayModelReader();
		reader.readModelFromXml(inputStream, displayModel, null);
	}

	/**
	 * Load the given display model from the given input stream asynchroneously.
	 * 
	 * @param displayModel
	 *            The diplay model to load.
	 * @param inputStream
	 *            The input stream to load the display model from.
	 * @param loadListener
	 *            Optional listener that will be notified of model loading
	 *            events (can be null).
	 */
	public static void asyncFillModel(final DisplayModel displayModel,
			final InputStream inputStream,
			final IDisplayModelLoadListener loadListener) {
//		ConcurrentModelLoader loader = new ConcurrentModelLoader(displayModel,
//				inputStream, loadListener);
//		loader.start();
		
		Runnable r = new Runnable() {
			public void run() {
				displayModel.setLoading(true);

				DisplayModelReader reader = new DisplayModelReader();
				reader
						.readModelFromXml(inputStream, displayModel,
								loadListener);

				if (reader.isErrorOccurred()) {
					if (loadListener != null) {
						loadListener.onErrorsOccured(reader.getErrorMessages());
					}
				}

				if (loadListener != null) {
					loadListener.onDisplayModelLoaded();
				}

				displayModel.setLoading(false);
			}
		};

		ExecutorAccess.getInstance().getExecutorService().execute(r);
		
	}

	/**
	 * Create an input stream from the given display model.
	 * 
	 * @param model
	 *            The display model to create an input stream from.
	 * @return An input stream that was created from the given display model.
	 */
	public static InputStream createStream(final DisplayModel model) {
		return new DisplayModelInputStream(model);
	}

	/**
	 * Create a String from the given display model.
	 * 
	 * @param model
	 *            The display model to create an input stream from.
	 * @return A String that was created from the given display model.
	 */
	public static String createString(final DisplayModel model) {
		return new DisplayModelInputStream(model).getAsString();
	}
}
