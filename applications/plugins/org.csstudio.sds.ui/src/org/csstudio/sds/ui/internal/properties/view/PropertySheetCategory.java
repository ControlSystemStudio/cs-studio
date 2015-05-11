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
 package org.csstudio.sds.ui.internal.properties.view;

import java.util.ArrayList;
import java.util.List;

/**
 * A category in a PropertySheet used to group <code>IPropertySheetEntry</code>
 * entries so they are displayed together.
 *
 * @author Sven Wende
 */
final class PropertySheetCategory {
    /**
     * The category name.
     */
    private String _categoryName;

    /**
     * The category entries.
     */
    private List<IPropertySheetEntry> _entries = new ArrayList<IPropertySheetEntry>();

    /**
     * Flag, which indicates whether the category should be automatically
     * expanded.
     */
    private boolean _shouldAutoExpand = true;

    /**
     * Create a PropertySheet category with name.
     *
     * @param name
     *            the category name
     */
    public PropertySheetCategory(final String name) {
        _categoryName = name;
    }

    /**
     * Add an <code>IPropertySheetEntry</code> to the list of entries in this
     * category.
     *
     * @param entry
     *            the entry
     */
    public void addEntry(final IPropertySheetEntry entry) {
        _entries.add(entry);
    }

    /**
     * @return Returns the category name.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * Returns <code>true</code> if this category should be automatically
     * expanded. The default value is <code>true</code>.
     *
     * @return <code>true</code> if this category should be automatically
     *         expanded, <code>false</code> otherwise
     */
    public boolean getAutoExpand() {
        return _shouldAutoExpand;
    }

    /**
     * Sets if this category should be automatically expanded.
     *
     * @param autoExpand
     *            true, if the category should be automatically expanded
     */
    public void setAutoExpand(final boolean autoExpand) {
        _shouldAutoExpand = autoExpand;
    }

    /**
     * Returns the entries in this category.
     *
     * @return the entries in this category
     */
    public IPropertySheetEntry[] getChildEntries() {
        return _entries
                .toArray(new IPropertySheetEntry[_entries.size()]);
    }

    /**
     * Removes all of the entries in this category. Doing so allows us to reuse
     * this category entry.
     */
    public void removeAllEntries() {
        _entries = new ArrayList<IPropertySheetEntry>();
    }
}
