/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.internal.persistence;

import java.io.InputStream;

import org.csstudio.sds.model.DisplayModel;

/**
 * Thread that loads display models concurrently.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
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
