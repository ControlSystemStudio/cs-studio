/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.utility.treemodel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


import junit.framework.Assert;

import org.csstudio.utility.treemodel.builder.XmlFileContentModelBuilder;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 13.07.2010
 */
public final class TreeModelTestUtils {

    /**
     * Don't instantiate
     */
    private TreeModelTestUtils() {
        // EMPTY
    }

    public static URL findResource(final String path) {
        final Bundle bundle = Platform.getBundle(TreeModelActivator.PLUGIN_ID);
        return bundle.getResource(path);
    }


    public static <T extends Enum<T> & ITreeNodeConfiguration<T>>
        ContentModel<T> buildContentModel(final URL resource,
                                          final T root) throws CreateContentModelException, IOException {

        InputStream stream = null;
        try {
            stream = resource.openStream();
            final XmlFileContentModelBuilder<T> builder =
                new XmlFileContentModelBuilder<T>(root, stream);
            builder.build();
            return builder.getModel();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (final IOException e) {
                Assert.fail("Unexpected exception closing input stream: " + e.getMessage() + "\n" + e.getCause());
            }
        }
    }
}
