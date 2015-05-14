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
package org.csstudio.dal.ui.util;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A transportable unit for a control system item, which is used in the process
 * of converting items to string representations and back.
 *
 * @author Sven Wende
 *
 */
public final class ControlSystemItemPath implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 4959241842467380642L;

    /**
     * Placeholder, which is used in PORTABLE_FORMAT.
     */
    private static final String TYPE_PART = "TYPE"; //$NON-NLS-1$

    /**
     * Placeholder, which is used in PORTABLE_FORMAT.
     */
    private static final String OBJECT_PART = "OBJECT_PART"; //$NON-NLS-1$

    /**
     * Defines the format of the String representation. Should contain the
     * placeholders TYPE_PART and OBJECT_PART.
     */
    private static final String PORTABLE_FORMAT = "##" + TYPE_PART + "###" //$NON-NLS-1$ //$NON-NLS-2$
            + OBJECT_PART + "##"; //$NON-NLS-1$

    /**
     * The type identifier for the control system item, which should be
     * transferred to a String representation.
     */
    private String _typeId;

    /**
     * The part of the String representation, which contains the individual data
     * of the control system item, that should be transferred to a String
     * representation.
     */
    private String _itemData;

    /**
     * Privat default constructur. Used only for serialization.
     */
    private ControlSystemItemPath() {
        _itemData = ""; //$NON-NLS-1$
        _typeId = ""; //$NON-NLS-1$
    }

    /**
     * Constructs a path.
     *
     * @param typeId
     *            the type identification of the control system item
     * @param itemData
     *            a String representation of the control system item´s data
     *
     */
    public ControlSystemItemPath(final String typeId, final String itemData) {
        assert typeId != null;
        assert itemData != null;
        _typeId = typeId;
        _itemData = itemData;
    }

    /**
     * @return the type id
     */
    public String getTypeId() {
        return _typeId;
    }

    /**
     * @return the item data in String format
     */
    public String getItemData() {
        return _itemData;
    }

    /**
     * @return a portable String representation a the control system, which can
     *         be used externally
     */
    public String toPortableString() {
        String portable = PORTABLE_FORMAT.replace(TYPE_PART, _typeId).replace(
                OBJECT_PART, _itemData);
        return portable;
    }

    /**
     * Creates a path object from a String representation.
     *
     * @param portableString
     *            the portable String, which must use the same format as it is
     *            provided by {@link #toPortableString()}
     *
     * @return a path object or null, if the provided String does not satisfy
     *         the format needs
     */
    public static ControlSystemItemPath createFromPortableString(
            final String portableString) {
        ControlSystemItemPath path = null;

        String regex = PORTABLE_FORMAT.replace(TYPE_PART, "(.*)").replace( //$NON-NLS-1$
                OBJECT_PART, "(.*)"); //$NON-NLS-1$

        Pattern pattern = Pattern.compile(regex);
        // Get a Matcher based on the target string.
        Matcher matcher = pattern.matcher(portableString);

        // Find all the matches.
        if (matcher.find()) {
            String typeId = matcher.group(1);
            String objecPart = matcher.group(2);

            if (typeId != null && !typeId.equals("") && objecPart != null) { //$NON-NLS-1$
                path = new ControlSystemItemPath(typeId, objecPart);
            }
        }

        return path;
    }
}
