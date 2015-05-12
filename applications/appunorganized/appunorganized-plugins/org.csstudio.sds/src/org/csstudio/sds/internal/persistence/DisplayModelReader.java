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
package org.csstudio.sds.internal.persistence;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.csstudio.sds.ErrorMessagesTracker;
import org.csstudio.sds.model.DisplayModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reader for the XML representation of display models.
 *
 * @author Alexander Will & Sven Wende
 * @version $Revision: 1.1 $
 *
 */
public final class DisplayModelReader extends ErrorMessagesTracker {
    private static final Logger LOG = LoggerFactory.getLogger(DisplayModelReader.class);

    /**
     * This class is not intended to be instantiated by clients.
     */
    public DisplayModelReader() {
    }

    /**
     * Parse the given <code>InputStream</code> into the given
     * <code>DisplayModel</code>.
     *
     * @param inputStream
     *            An <code>InputStream</code> that contains the XML
     *            representation of a <code>DisplayModel</code>.
     * @param displayModel
     *            The <code>DisplayModel</code> that the read model data will
     *            be appended to.
     * @param display
     *            SWT <code>Display</code> that will be used for asynchronous
     *            loading.
     */
    public void readModelFromXml(final InputStream inputStream,
            final DisplayModel displayModel,
            final IDisplayModelLoadListener loadListener) {
        resetErrorMessages();

        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

            SaxDisplayModelHandler saxDisplayModelHandler = new SaxDisplayModelHandler(
                    displayModel, loadListener);
            parser.parse(inputStream, saxDisplayModelHandler);

        } catch (Exception e) {
            trackErrorMessage("Exception: " + e.getMessage()); //$NON-NLS-1$
            LOG.debug(e.toString());
        } finally {
            // Important - close the stream
            closeStream(inputStream);
        }
    }

    private void closeStream(InputStream stream) {
        try {
            stream.close();
        } catch (IOException e) {
            LOG.debug(e.toString());
        }
    }
}
