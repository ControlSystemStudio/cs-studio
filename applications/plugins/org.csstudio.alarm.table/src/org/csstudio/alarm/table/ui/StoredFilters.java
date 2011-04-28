/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 * 
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.table.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.dbaccess.archivedb.Filter;
import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.IMessageViewer;
import org.csstudio.alarm.table.preferences.archive.ArchiveViewPreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Filters stored by user. Filters are stored in the preferences as a string
 * <p>
 * Delimiter for filter is '[###]': Filter1[###]Filter2[###]....
 * 
 * A filter starts with its name followed by the items with delimiter '[##]':
 * ....[###]FilterName[##]FilterItem1[##]FilterItem2[##].....[###]
 * 
 * A filter item consists of 'property', 'value' (original/not converted) and 'relation' separated
 * by delimiter '[#]': ...[##]Property[#]Value[#]Relation[##]...
 * <p>
 * 
 * @author jhatje
 * @since 21.06.2010
 */
public class StoredFilters {
    
    private final Set<IFilterListChangeListener> _listener = new HashSet<IFilterListChangeListener>();
    
    private List<Filter> _storedFilters = new ArrayList<Filter>();
    
    public List<Filter> getFilterList() {
        return _storedFilters;
    }

    @CheckForNull
    public Filter getCopyOfFilter(String newFilterName) {
        for (Filter filter : _storedFilters) {
            if (filter.getName().equals(newFilterName)) {
                return filter.copy();
            }
        }
        return null;
    }
    
    public void addCopyOfFilter(@Nonnull Filter filter) {
        _storedFilters.add(filter.copy());
        for (IFilterListChangeListener listener : _listener) {
            listener.FilterListChanged();
        }
    }
    
    public void removeFilterByName(@Nonnull String filterName) {
        for (Filter filter : _storedFilters) {
            if (filter.getName().equals(filterName)) {
                _storedFilters.remove(filter);
                break;
            }
        }
        for (IFilterListChangeListener listener : _listener) {
            listener.FilterListChanged();
        }
    }
    
    public void readFromPreferences() {
        IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
        String storedFiltersString = store.getString(ArchiveViewPreferenceConstants.STORED_FILTERS);
        String[] filterArray = storedFiltersString.split("\\[###\\]");
        for (String filterString : filterArray) {
            String[] filterContentArray = filterString.split("\\[##\\]");
            String filterName = filterContentArray[0];
            Filter filter = new Filter(filterName);
            for (int i = 1; i < filterContentArray.length; i++) {
                String filterItem = filterContentArray[i];
                String[] filterItemArray = filterItem.split("\\[#\\]");
                filter.addFilterItem(filterItemArray[0], filterItemArray[1], filterItemArray[2]);
            }
            _storedFilters.add(filter);
        }
    }
    
    public void writeToPreferences() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Filter filter : _storedFilters) {
            stringBuffer.append(filter.getName());
            // add delimiter after filter name
            stringBuffer.append("[##]");
            
            // add all filter items
            ArrayList<FilterItem> filterItems = filter.getFilterItems();
            for (FilterItem filterItem : filterItems) {
                stringBuffer.append(filterItem.getProperty());
                stringBuffer.append("[#]");
                stringBuffer.append(filterItem.getOriginalValue());
                stringBuffer.append("[#]");
                stringBuffer.append(filterItem.getRelation());
                stringBuffer.append("[##]");
            }
            stringBuffer.delete(stringBuffer.length() - 4, stringBuffer.length());
            stringBuffer.append("[###]");
        }
        setEclipsePreference(stringBuffer.toString());
    }
    
    private void setEclipsePreference(String preferenceString) {
        IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
        store.setValue(ArchiveViewPreferenceConstants.STORED_FILTERS, preferenceString);
        if (store.needsSaving()) {
            String qualifier = JmsLogsPlugin.getDefault().PLUGIN_ID;
            IPreferencesService prefsService = Platform.getPreferencesService();
            IEclipsePreferences root = prefsService.getRootNode();
            Preferences node = root.node(InstanceScope.SCOPE).node(qualifier);
            try {
                node.flush();
            } catch (BackingStoreException e) {
                CentralLogger.getInstance().warn(this,
                                                 "could not write preference string for filters!"
                                                         + e.getMessage());
            }
        }
    }

    public void addFilterListChangeListener(IFilterListChangeListener listener) {
        _listener.add(listener);
    }
    
    public void removeChangeListener(final IFilterListChangeListener listener) {
        _listener.remove(listener);
    }

}
