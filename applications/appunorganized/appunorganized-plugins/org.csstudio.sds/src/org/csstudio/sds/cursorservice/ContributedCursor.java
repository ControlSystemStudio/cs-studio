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

package org.csstudio.sds.cursorservice;


/**
 * A cursor contributed by another plug-in.
 *
 * @author Joerg Rathlev
 */
public final class ContributedCursor extends AbstractCursor {

    /**
     * The bundle in which this cursor is located.
     */
    private final String _bundle;

    /**
     * The name of the graphics file in the bundle.
     */
    private final String _image;

    /**
     * Creates a new contributed cursor.
     *
     * @param id
     *            the id of this cursor.
     * @param title
     *            the title of this cursor.
     * @param bundle
     *            the bundle in which this cursor is located.
     * @param image
     *            the name of the graphics file in the bundle.
     */
    ContributedCursor(final String id, final String title, final String bundle,
            final String image) {
        super(id, title);
        assert bundle != null;
        assert image != null;
        _bundle = bundle;
        _image = image;
    }

    /**
     * Returns the name of the bundle in which this cursor is located.
     *
     * @return the name of the bundle in which this cursor is located.
     */
    public String getBundle() {
        return _bundle;
    }

    /**
     * Returns the name of the graphics file in the bundle.
     *
     * @return the name of the graphics file in the bundle.
     */
    public String getImage() {
        return _image;
    }
}
