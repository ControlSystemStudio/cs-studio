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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptor;
import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * <code>PropertySheetEntry</code> is an implementation of
 * <code>IPropertySheetEntry</code> which uses <code>IPropertySource</code>
 * and <code>IPropertyDescriptor</code> to interact with domain model objects.
 * <p>
 * Every property sheet entry has a single descriptor (except the root entry
 * which has none). This descriptor determines what property of its objects it
 * will display/edit.
 * </p>
 * <p>
 * Entries do not listen for changes in their objects. Since there is no
 * restriction on properties being independent, a change in one property may
 * affect other properties. The value of a parent's property may also change. As
 * a result we are forced to refresh the entire entry tree when a property
 * changes value.
 * </p>
 *
 * @since 3.0 (was previously internal)
 *
 * @author Sven Wende
 */
public class PropertySheetEntry extends EventManager implements
        IPropertySheetEntry {

    /**
     * The values we are displaying/editing. These objects repesent the value of
     * one of the properties of the values of our parent entry. Except for the
     * root entry where they represent the input (selected) objects.
     */
    private Object[] _values = new Object[0];

    /**
     * The property sources for the values we are displaying/editing.
     */
    private Map _sources = new HashMap(0);

    /**
     * The dynamics descriptors of the properties we are displaying/editing.
     */
    private DynamicsDescriptor[] _dynamicsDescriptors = new DynamicsDescriptor[0];

    private List<Map<String, String>> _aliases = new ArrayList<Map<String,String>>();

    private Map<String, String> _alias;

    /**
     * The dynamics descriptor of the first property, if several properties are
     * selected and changed at the same time.
     */
    private DynamicsDescriptor _dynamicsDescriptor;

    /**
     * The alias descriptors of the underlying widget model.
     */

    /**
     * The value of this entry is defined as the the first object in its value
     * array or, if that object is an <code>IPropertySource</code>, the value
     * it returns when sent <code>getEditableValue</code>.
     */
    private Object _editValue;

    /**
     * The parent property entry.
     */
    private PropertySheetEntry _parent;

    /**
     * The property source provider.
     */
    private IPropertySourceProvider _propertySourceProvider;

    /**
     * The property descriptor.
     */
    private IPropertyDescriptor _descriptor;

    /**
     * The current cell editor.
     */
    private CellEditor _editor;

    /**
     * An error text.
     */
    private String _errorText;

    /**
     * Child property entries.
     */
    private PropertySheetEntry[] _childEntries = null;

    /**
     * Create the CellEditorListener for this entry. It listens for value
     * changes in the CellEditor, and cancel and finish requests.
     */
    private ICellEditorListener _cellEditorListener = new ICellEditorListener() {
        @Override
        public void editorValueChanged(final boolean oldValidState,
                final boolean newValidState) {
            if (!newValidState) {
                // currently not valid so show an error message
                setErrorText(_editor.getErrorMessage());
            } else {
                // currently valid
                setErrorText(null);
            }
        }

        @Override
        public void cancelEditor() {
            setErrorText(null);
        }

        @Override
        public void applyEditorValue() {
            PropertySheetEntry.this.applyEditorValue();
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addPropertySheetEntryListener(
            final IPropertySheetEntryListener listener) {
        addListenerObject(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void applyEditorValue() {
        if (_editor == null) {
            return;
        }

        // Check if editor has a valid value
        if (!_editor.isValueValid()) {
            setErrorText(_editor.getErrorMessage());
            return;
        }

        setErrorText(null);

        // See if the value changed and if so update
        Object newValue = _editor.getValue();

        boolean changed = false;
        if (_values.length > 1) {
            changed = true;
        } else if (_editValue == null) {
            if (newValue != null) {
                changed = true;
            }
        } else if (!_editValue.equals(newValue)) {
            changed = true;
        }

        // Set the editor value
        if (changed) {
            setValue(newValue);
        }
    }

    /**
     * Return the unsorted intersection of all the
     * <code>IPropertyDescriptor</code>s for the objects.
     *
     * @return List
     */
    @SuppressWarnings("unchecked")
    private List computeMergedPropertyDescriptors() {
        if (_values.length == 0) {
            return new ArrayList(0);
        }

        IPropertySource firstSource = getPropertySource(_values[0]);
        if (firstSource == null) {
            return new ArrayList(0);
        }

        if (_values.length == 1) {
            return Arrays.asList(firstSource.getPropertyDescriptors());
        }

        // get all descriptors from each object
        Map[] propertyDescriptorMaps = new Map[_values.length];
        for (int i = 0; i < _values.length; i++) {
            Object object = _values[i];
            IPropertySource source = getPropertySource(object);
            if (source == null) {
                // if one of the selected items is not a property source
                // then we show no properties
                return new ArrayList(0);
            }
            // get the property descriptors keyed by id
            propertyDescriptorMaps[i] = computePropertyDescriptorsFor(source);
        }

        // intersect
        Map intersection = propertyDescriptorMaps[0];
        for (int i = 1; i < propertyDescriptorMaps.length; i++) {
            // get the current ids
            Object[] ids = intersection.keySet().toArray();
            for (int j = 0; j < ids.length; j++) {
                Object object = propertyDescriptorMaps[i].get(ids[j]);
                if (object == null ||
                // see if the descriptors (which have the same id) are
                        // compatible
                        !((IPropertyDescriptor) intersection.get(ids[j]))
                                .isCompatibleWith((IPropertyDescriptor) object)) {
                    intersection.remove(ids[j]);
                }
            }
        }

        // sorting is handled in the PropertySheetViewer, return unsorted (in
        // the original order)
        ArrayList result = new ArrayList(intersection.size());
        IPropertyDescriptor[] firstDescs = firstSource.getPropertyDescriptors();
        for (int i = 0; i < firstDescs.length; i++) {
            IPropertyDescriptor desc = firstDescs[i];
            if (intersection.containsKey(desc.getId())) {
                result.add(desc);
            }
        }
        return result;
    }

    /**
     * Returns an map of property descritptors (keyed on id) for the given
     * property source.
     *
     * @param source
     *            a property source for which to obtain descriptors
     * @return a table of decriptors keyed on their id
     */
    @SuppressWarnings("unchecked")
    private Map computePropertyDescriptorsFor(final IPropertySource source) {
        IPropertyDescriptor[] descriptors = source.getPropertyDescriptors();
        Map result = new HashMap(descriptors.length * 2 + 1);
        for (int i = 0; i < descriptors.length; i++) {
            result.put(descriptors[i].getId(), descriptors[i]);
        }
        return result;
    }

    /**
     * Create our child entries.
     */
    private void createChildEntries() {
        // get the current descriptors
        List descriptors = computeMergedPropertyDescriptors();

        // rebuild child entries using old when possible
        PropertySheetEntry[] newEntries = new PropertySheetEntry[descriptors
                .size()];
        for (int i = 0; i < descriptors.size(); i++) {
            IPropertyDescriptor d = (IPropertyDescriptor) descriptors.get(i);
            // create new entry
            PropertySheetEntry entry = createChildEntry();
            entry.setDescriptor(d);
            entry.setParent(this);
            entry.setPropertySourceProvider(_propertySourceProvider);
            entry.refreshValues();
            entry.refreshDynamicsDescriptors();
            entry.refreshAliases();
            newEntries[i] = entry;
        }
        // only assign if successful
        _childEntries = newEntries;
    }

    /**
     * Update our dynamics descriptors.
     */
    private void refreshDynamicsDescriptors() {
        // get our parent's value objects
        Object[] currentSources = _parent.getValues();

        // loop through the objects getting our property value from each
        DynamicsDescriptor[] newDynamicsDescriptors = new DynamicsDescriptor[currentSources.length];
        for (int i = 0; i < currentSources.length; i++) {
            IPropertySource source = _parent
                    .getPropertySource(currentSources[i]);
            newDynamicsDescriptors[i] = source
                    .getDynamicsDescriptor(_descriptor.getId());
        }

        // set our new values
        setDynamicsDescriptors(newDynamicsDescriptors);
    }

        private void refreshAliases() {
            // get our parent's value objects
            Object[] currentSources = _parent.getValues();

            // loop through the objects getting our property value from each
            List<Map<String, String>> newAliases = new ArrayList<Map<String,String>>();

            for (int i = 0; i < currentSources.length; i++) {
                IPropertySource source = _parent
                        .getPropertySource(currentSources[i]);
                newAliases.add(source.getAliases());
            }

            // set our new values
            setAliases(newAliases);
        }


    /**
     * Factory method to create a new child <code>PropertySheetEntry</code>
     * instance.
     * <p>
     * Subclasses may overwrite to create new instances of their own class.
     * </p>
     *
     * @return a new <code>PropertySheetEntry</code> instance for the
     *         descriptor passed in
     * @since 3.1
     */
    protected PropertySheetEntry createChildEntry() {
        return new PropertySheetEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (_editor != null) {
            _editor.dispose();
            _editor = null;
        }
        // recursive call to dispose children
        PropertySheetEntry[] entriesToDispose = _childEntries;
        _childEntries = null;
        if (entriesToDispose != null) {
            for (int i = 0; i < entriesToDispose.length; i++) {
                // an error in a property source may cause refreshChildEntries
                // to fail. Since the Workbench handles such errors we
                // can be left in a state where a child entry is null.
                if (entriesToDispose[i] != null) {
                    entriesToDispose[i].dispose();
                }
            }
        }
    }

    /**
     * The child entries of this entry have changed (children added or removed).
     * Notify all listeners of the change.
     */
    private void fireChildEntriesChanged() {
        Object[] array = getListeners();
        for (int i = 0; i < array.length; i++) {
            IPropertySheetEntryListener listener = (IPropertySheetEntryListener) array[i];
            listener.childEntriesChanged(this);
        }
    }

    /**
     * The error message of this entry has changed. Notify all listeners of the
     * change.
     */
    private void fireErrorMessageChanged() {
        Object[] array = getListeners();
        for (int i = 0; i < array.length; i++) {
            IPropertySheetEntryListener listener = (IPropertySheetEntryListener) array[i];
            listener.errorMessageChanged(this);
        }
    }

    /**
     * The values of this entry have changed. Notify all listeners of the
     * change.
     */
    private void fireValueChanged() {
        Object[] array = getListeners();
        for (int i = 0; i < array.length; i++) {
            IPropertySheetEntryListener listener = (IPropertySheetEntryListener) array[i];
            listener.valueChanged(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getCategory() {
        return _descriptor.getCategory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IPropertySheetEntry[] getChildEntries() {
        if (_childEntries == null) {
            createChildEntries();
        }
        return _childEntries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDescription() {
        return _descriptor.getDescription();
    }

    /**
     * Returns the descriptor for this entry.
     *
     * @return the descriptor for this entry
     * @since 3.1 (was previously private)
     */
    protected final IPropertyDescriptor getDescriptor() {
        return _descriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDisplayName() {
        return _descriptor.getDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CellEditor getEditor(final Composite parent) {

        if (_editor == null) {
            _editor = _descriptor.createPropertyEditor(parent);
            if (_editor != null) {
                _editor.addListener(_cellEditorListener);
            }
        }
        if (_editor != null) {
            _editor.setValue(_editValue);
            setErrorText(_editor.getErrorMessage());
        }
        return _editor;
    }

    /**
     * Returns the edit value for the object at the given index.
     *
     * @param index
     *            the value object index
     * @return the edit value for the object at the given index
     */
    protected final Object getEditValue(final int index) {
        Object value = _values[index];
        IPropertySource source = getPropertySource(value);
        if (source != null) {
            value = source.getEditableValue();
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getErrorText() {
        return _errorText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getFilters()[] {
        return _descriptor.getFilterFlags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object getHelpContextIds() {
        return _descriptor.getHelpContextIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Image getImage() {
        ILabelProvider provider = _descriptor.getLabelProvider();
        if (provider == null) {
            return null;
        }
        return provider.getImage(_editValue);
    }

    /**
     * Returns the parent of this entry.
     *
     * @return the parent entry, or <code>null</code> if it has no parent
     * @since 3.1
     */
    protected final PropertySheetEntry getParent() {
        return _parent;
    }

    /**
     * Returns an property source for the given object.
     *
     * @param object
     *            an object for which to obtain a property source or
     *            <code>null</code> if a property source is not available
     * @return an property source for the given object
     * @since 3.1 (was previously private)
     */
    @SuppressWarnings("unchecked")
    protected final IPropertySource getPropertySource(final Object object) {
        if (_sources.containsKey(object)) {
            return (IPropertySource) _sources.get(object);
        }

        IPropertySource result = null;
        IPropertySourceProvider provider = _propertySourceProvider;

        if (provider == null && object != null) {
            provider = (IPropertySourceProvider) Platform.getAdapterManager()
                    .getAdapter(object, IPropertySourceProvider.class);
        }

        if (provider != null) {
            result = provider.getPropertySource(object);
        } else if (object instanceof IPropertySource) {
            result = (IPropertySource) object;
        } else if (object instanceof IAdaptable) {
            result = (IPropertySource) ((IAdaptable) object)
                    .getAdapter(IPropertySource.class);
        } else {
            if (object != null) {
                result = (IPropertySource) Platform.getAdapterManager()
                        .getAdapter(object, IPropertySource.class);
            }
        }
        _sources.put(object, result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValueAsString() {
        if (_editValue == null) {
            return "";//$NON-NLS-1$
        }
        ILabelProvider provider = _descriptor.getLabelProvider();
        if (provider == null) {
            return _editValue.toString();
        }
        String text = provider.getText(_editValue);
        if (text == null) {
            return "";//$NON-NLS-1$
        }
        return text;
    }

    /**
     * Returns the value objects of this entry.
     *
     * @return the value objects of this entry
     * @since 3.1 (was previously private)
     */
    @Override
    public final Object[] getValues() {
        return _values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasChildEntries() {
        if (_childEntries != null && _childEntries.length > 0) {
            return true;
        }
        // see if we could have entires if we were asked
        return computeMergedPropertyDescriptors().size() > 0;
    }

    /**
     * Update our child entries. This implementation tries to reuse child
     * entries if possible (if the id of the new descriptor matches the
     * descriptor id of the old entry).
     */
    @SuppressWarnings("unchecked")
    private void refreshChildEntries() {
        if (_childEntries == null) {
            // no children to refresh
            return;
        }

        // get the current descriptors
        List descriptors = computeMergedPropertyDescriptors();

        // cache old entries by their descriptor id
        Map entryCache = new HashMap(_childEntries.length * 2 + 1);
        for (int i = 0; i < _childEntries.length; i++) {
            PropertySheetEntry childEntry = _childEntries[i];
            if (childEntry != null) {
                entryCache.put(childEntry.getDescriptor().getId(), childEntry);
            }
        }

        // create a list of entries to dispose
        List entriesToDispose = new ArrayList(Arrays.asList(_childEntries));

        // clear the old entries
        this._childEntries = null;

        // rebuild child entries using old when possible
        PropertySheetEntry[] newEntries = new PropertySheetEntry[descriptors
                .size()];
        boolean entriesChanged = descriptors.size() != entryCache.size();
        for (int i = 0; i < descriptors.size(); i++) {
            IPropertyDescriptor d = (IPropertyDescriptor) descriptors.get(i);
            // see if we have an entry matching this descriptor
            PropertySheetEntry entry = (PropertySheetEntry) entryCache.get(d
                    .getId());
            if (entry != null) {
                // reuse old entry
                entry.setDescriptor(d);
                entriesToDispose.remove(entry);
            } else {
                // create new entry
                entry = createChildEntry();
                entry.setDescriptor(d);
                entry.setParent(this);
                entry.setPropertySourceProvider(_propertySourceProvider);
                entriesChanged = true;
            }
            entry.refreshValues();
            entry.refreshDynamicsDescriptors();
            entry.refreshAliases();
            newEntries[i] = entry;
        }

        // only assign if successful
        this._childEntries = newEntries;

        if (entriesChanged) {
            fireChildEntriesChanged();
        }

        // Dispose of entries which are no longer needed
        for (int i = 0; i < entriesToDispose.size(); i++) {
            ((IPropertySheetEntry) entriesToDispose.get(i)).dispose();
        }
    }

    /**
     * Refresh the entry tree from the root down.
     *
     * @since 3.1 (was previously private)
     */
    protected final void refreshFromRoot() {
        if (_parent == null) {
            refreshChildEntries();
        } else {
            _parent.refreshFromRoot();
        }
    }

    /**
     * Update our value objects. We ask our parent for the property values based
     * on our descriptor.
     */
    private void refreshValues() {
        // get our parent's value objects
        Object[] currentSources = _parent.getValues();

        // loop through the objects getting our property value from each
        Object[] newValues = new Object[currentSources.length];
        for (int i = 0; i < currentSources.length; i++) {
            IPropertySource source = _parent
                    .getPropertySource(currentSources[i]);
            newValues[i] = source.getPropertyValue(_descriptor.getId());
        }

        // set our new values
        setValues(newValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removePropertySheetEntryListener(
            final IPropertySheetEntryListener listener) {
        removeListenerObject(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPropertyValue() {
        if (_parent == null) {
            // root does not have a default value
            return;
        }

        // Use our parent's values to reset our values.
        boolean change = false;
        Object[] objects = _parent.getValues();
        for (int i = 0; i < objects.length; i++) {
            IPropertySource source = getPropertySource(objects[i]);
            if (source.isPropertySet(_descriptor.getId())) {
                // fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=21756
                if (source instanceof IPropertySource2) {
                    IPropertySource2 extendedSource = (IPropertySource2) source;
                    // continue with next if property is not resettable
                    if (!extendedSource.isPropertyResettable(_descriptor
                            .getId())) {
                        continue;
                    }
                }
                source.resetPropertyValue(_descriptor.getId());
                change = true;
            }
        }
        if (change) {
            refreshFromRoot();
        }
    }

    /**
     * Set the descriptor.
     *
     * @param newDescriptor
     *            the descriptor
     */
    private void setDescriptor(final IPropertyDescriptor newDescriptor) {
        // if our descriptor is changing, we have to get rid
        // of our current editor if there is one
        if (_descriptor != newDescriptor && _editor != null) {
            _editor.dispose();
            _editor = null;
        }
        _descriptor = newDescriptor;
    }

    /**
     * Set the error text. This should be set to null when the current value is
     * valid, otherwise it should be set to a error string.
     *
     * @param newErrorText
     *            the new error text
     *
     */
    protected final void setErrorText(final String newErrorText) {
        _errorText = newErrorText;
        // inform listeners
        fireErrorMessageChanged();
    }

    /**
     * Sets the parent of the entry to be propertySheetEntry.
     *
     * @param propertySheetEntry
     *            the parent entry
     */
    private void setParent(final PropertySheetEntry propertySheetEntry) {
        _parent = propertySheetEntry;
    }

    /**
     * Sets a property source provider for this entry. This provider is used to
     * obtain an <code>IPropertySource</code> for each of this entries
     * objects. If no provider is set then a default provider is used.
     *
     * @param provider
     *            IPropertySourceProvider
     */
    public final void setPropertySourceProvider(
            final IPropertySourceProvider provider) {
        _propertySourceProvider = provider;
    }

    /**
     * Set the value for this entry.
     * <p>
     * We set the given value as the value for all our value objects. We then
     * call our parent to update the property we represent with the given value.
     * We then trigger a model refresh.
     * <p>
     *
     * @param newValue
     *            the new value
     */
    private void setValue(final Object newValue) {
        // Set the value
        for (int i = 0; i < _values.length; i++) {
            _values[i] = newValue;
        }

        // Inform our parent
        _parent.valueChanged(this);

        // Refresh the model
        refreshFromRoot();
    }

    /**
     * The <code>PropertySheetEntry</code> implementation of this method
     * declared on<code>IPropertySheetEntry</code> will obtain an editable
     * value for the given objects and update the child entries.
     * <p>
     * Updating the child entries will typically call this method on the child
     * entries and thus the entire entry tree is updated
     * </p>
     *
     * @param objects
     *            the new values for this entry
     */
    @Override
    public final void setValues(final Object[] objects) {
        _values = objects;
        _sources = new HashMap(_values.length * 2 + 1);

        if (_values.length == 0) {
            _editValue = null;
        } else {
            // set the first value object as the entry's value
            Object newValue = _values[0];

            // see if we should convert the value to an editable value
            IPropertySource source = getPropertySource(newValue);
            if (source != null) {
                newValue = source.getEditableValue();
            }
            _editValue = newValue;
        }

        // update our child entries
        refreshChildEntries();

        // inform listeners that our value changed
        fireValueChanged();
    }

    /**
     * The value of the given child entry has changed. Therefore we must set
     * this change into our value objects.
     * <p>
     * We must inform our parent so that it can update its value objects
     * </p>
     * <p>
     * Subclasses may override to set the property value in some custom way.
     * </p>
     *
     * @param child
     *            the child entry that changed its value
     */
    protected void valueChanged(final PropertySheetEntry child) {
        for (int i = 0; i < _values.length; i++) {
            IPropertySource source = getPropertySource(_values[i]);
            source.setPropertyValue(child.getDescriptor().getId(), child
                    .getEditValue(i));

        }

        // inform our parent
        if (_parent != null) {
            _parent.valueChanged(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isDynamicallySampled() {
        if (_editValue == null) {
            return false;
        }

        IPropertySource propertySource = _parent.getPropertySource(_parent
                .getValues()[_parent.getValues().length - 1]);

        if (propertySource != null) {
            return propertySource
                    .getDynamicsDescriptor(getDescriptor().getId()) != null;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final DynamicAspectsWizard getDynamicsDescriptionConfigurationWizard() {
        return new DynamicAspectsWizard(_dynamicsDescriptor, _alias, _descriptor, getValues()[0]);
    }

    /**
     * {@inheritDoc}
     */
    public final void setAliases(
            final List<Map<String, String>> aliasesList) {
        _aliases = aliasesList;

        if (aliasesList.size() == 0) {
            _alias = null;
        } else {
            // set the first value object as the entry's value
            _alias = _aliases.get(0);
        }

        // update our child entries
        refreshChildEntries();

        // inform listeners that our value changed
        fireValueChanged();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final void setDynamicsDescriptors(
            final DynamicsDescriptor[] dynamicsDescriptors) {
        _dynamicsDescriptors = dynamicsDescriptors;

        if (dynamicsDescriptors.length == 0) {
            _dynamicsDescriptor = null;
        } else {
            // set the first value object as the entry's value
            DynamicsDescriptor newValue = _dynamicsDescriptors[0];

            _dynamicsDescriptor = newValue;
        }

        // update our child entries
        refreshChildEntries();

        // inform listeners that our value changed
        fireValueChanged();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final DynamicsDescriptor[] getDynamicsDescriptors() {
        return _dynamicsDescriptors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void applyDynamicsDescriptor(
            final DynamicsDescriptor newDynamicsDescriptor) {
        // See if the value changed and if so update
        boolean changed = false;

        // See if the value changed and if so update
        if (_dynamicsDescriptors.length > 1) {
            changed = true;
        } else if (_dynamicsDescriptor == null) {
            if (newDynamicsDescriptor != null) {
                changed = true;
            }
        } else if (!_dynamicsDescriptor.equals(newDynamicsDescriptor)) {
            changed = true;
        }

        // Set the editor value
        if (changed) {
            // Set the new descriptor
            for (int i = 0; i < _dynamicsDescriptors.length; i++) {
                _dynamicsDescriptors[i] = newDynamicsDescriptor;
            }

            // Inform our parent
            _parent.dynamicDescriptorChanged(this);

            // Refresh the model
            refreshFromRoot();
        }
    }

    /**
     * The dynamics descriptor of the specified child entry has changed.
     * Therefore we must set this change into our value objects. We must inform
     * our parent so that it can update its value objects
     *
     * @param child
     *            the child entry that changed its value
     */
    protected void dynamicDescriptorChanged(final PropertySheetEntry child) {
        for (int i = 0; i < _values.length; i++) {
            IPropertySource source = getPropertySource(_values[i]);

            source.setDynamicsDescriptor(child.getDescriptor().getId(), child
                    .getDynamicsDescriptor(i));
        }

        // inform our parent
        if (_parent != null) {
            _parent.dynamicDescriptorChanged(this);
        }
    }

    /**
     * Gets the dynamics descriptor with the specified index.
     *
     * @param index
     *            the index
     * @return a dynamics descriptor
     */
    protected final DynamicsDescriptor getDynamicsDescriptor(final int index) {
        DynamicsDescriptor descriptor = _dynamicsDescriptors[index];
        return descriptor;
    }

}
