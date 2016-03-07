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
 package org.csstudio.sds.ui.internal.preferences;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.internal.preferences.WidgetSelectionStringConverter;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * A preference page to set the options for the display editors.
 *
 * @author Kai Meyer
 */
public final class WidgetOptionPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * The {@link org.eclipse.jface.viewers.TreeViewer} to show the available widgets.
     */
    private CheckboxTreeViewer _viewer;
    private ColorFieldEditor _cfeColor;
    private IntegerFieldEditor _ifeLine;

    /**
     * Constructor.
     */
    public WidgetOptionPreferencePage() {
        super();
        this.setMessage("Choose the widgets you want to use in the SDS");
    }

    /**
     * Constructor.
     * @param title The title for this page
     */
    public WidgetOptionPreferencePage(final String title) {
        super(title);
        this.setMessage("Choose the widgets you want to use in the SDS");
    }

    /**
     * Constructor.
     * @param title The title for this page
     * @param image The image for this page
     */
    public WidgetOptionPreferencePage(final String title, final ImageDescriptor image) {
        super(title, image);
        this.setMessage("Choose the widgets you want to use in the SDS");
    }

    /**
     *
     * (@inheritDoc)
     */
    @Override
    public void init(final IWorkbench workbench) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(final Composite parent) {
        final Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout(1, false));

        Label label = new Label(mainComposite, SWT.WRAP);
        label.setText("Registered widgets:");
        label.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false));

        _viewer = new CheckboxTreeViewer(mainComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK);
        final GridData gridData = new GridData(SWT.FILL,SWT.FILL,false,true);
        gridData.widthHint = 300;
        _viewer.getTree().setLayoutData(gridData);
        WidgetContentProvider contentProvider = new WidgetContentProvider();
        _viewer.setContentProvider(contentProvider);
        _viewer.setLabelProvider(new WidgetLabelProvider());
        _viewer.getTree().addSelectionListener(new TreeCheckSelectionListener());

        _viewer.setInput(WidgetModelFactoryService.getInstance());

        label = new Label(mainComposite, SWT.WRAP);
        label.setText("Changing these settings requires a re-opening of all active SDS-editors.");
        label.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false));

        _viewer.expandAll();

        final String widgets = this.getPreferenceStore().getString(PreferenceConstants.PROP_DESELECTED_WIDGETS);
        selectItems(contentProvider, widgets);
        _viewer.collapseAll();

        Label label2 = new Label(mainComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        label2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

        createCrossWidgetPart(mainComposite);
        return mainComposite;
    }

    /**
     * @param mainComposite
     */
    private void createCrossWidgetPart(final Composite mainComposite) {
        Composite color = new Composite(mainComposite, SWT.NONE);
        color.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        color.setLayout(new GridLayout(1, false));

        _cfeColor = new ColorFieldEditor("Widget Cross Color","Widget Cross Color", color);
        _cfeColor.setPreferenceName(PreferenceConstants.PROP_CROSSED_WIDGET_COLOR);
        _cfeColor.setPreferenceStore(getPreferenceStore());
        _cfeColor.load();
        _cfeColor.getColorSelector().addListener(new IPropertyChangeListener(){
            public void propertyChange(final PropertyChangeEvent event) {
                _cfeColor.getColorSelector().setColorValue((RGB) event.getNewValue());
            }
        });
        _ifeLine = new IntegerFieldEditor("Widget Cross Line width", "Widget Cross Line width", color);
        _ifeLine.setPreferenceName(PreferenceConstants.PROP_CROSSED_WIDGET_LINE_WIDTH);
        _ifeLine.setPreferenceStore(getPreferenceStore());
        _ifeLine.setValidRange(0, 9);
        _ifeLine.load();
    }

    /**
     * Sets the initial selection of the tree.
     * @param contentProvider The ContentProvider of the Tree
     * @param widgetsExcluded The string, which contains all not selected widget-IDs
     */
    private void selectItems(final WidgetContentProvider contentProvider, final String widgetsExcluded) {
        final List<String> widgetIds = WidgetSelectionStringConverter.createStringListFromString(widgetsExcluded);
        for (PluginTreeElement plugin : contentProvider.getElements()) {
            boolean allChecked = true;
            boolean oneChecked = false;
            for (WidgetTreeElement widget : plugin.getElements()) {
                if ( !widgetIds.contains(widget.getId()) ) {
                    oneChecked = true;
                    _viewer.setChecked(widget, true);
                } else {
                    allChecked = false;
                    _viewer.setChecked(widget, false);
                }
            }
            _viewer.setGrayed(plugin, !allChecked);
            _viewer.setChecked(plugin, oneChecked);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performOk() {
        final String storedString = WidgetSelectionStringConverter.createStringFromStringList(getNotCheckedWidgets());
        this.getPreferenceStore().putValue(PreferenceConstants.PROP_DESELECTED_WIDGETS, storedString);
        _cfeColor.store();
        _ifeLine.store();
        return super.performOk();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return SdsUiPlugin.getCorePreferenceStore();
    }

    /**
     * Returns all not selected widgets.
     * @return A list of the widget-IDs
     */
    private List<String> getNotCheckedWidgets() {
        final List<String> result = new LinkedList<String>();
        _viewer.expandAll();
        Object[] elements = _viewer.getExpandedElements();
        for (Object current : elements) {
            if (current instanceof PluginTreeElement) {
                PluginTreeElement plugin = (PluginTreeElement) current;
                for (WidgetTreeElement widget : plugin.getElements()) {
                    if (!_viewer.getChecked(widget)) {
                        result.add(widget.getId());
                    }
                }
            }
        }
        return result;
    }

    /**
     * The {@link ITreeContentProvider} for the tree.
     * @author Kai Meyer
     *
     */
    private final class WidgetContentProvider implements ITreeContentProvider {

        private PluginTreeElement[] _elements;

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getChildren(final Object parentElement) {
            if (parentElement instanceof PluginTreeElement) {
                return ((PluginTreeElement) parentElement).getElements();
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getParent(final Object element) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasChildren(final Object element) {
            if (element instanceof PluginTreeElement) {
                return ((PluginTreeElement) element).hasWidgets();
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getElements(final Object inputElement) {
            return _elements;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
            if (newInput instanceof WidgetModelFactoryService) {
                WidgetModelFactoryService service = (WidgetModelFactoryService)newInput;
                List<PluginTreeElement> elements = new ArrayList<PluginTreeElement>();

                for (String pluginId : service.getAllContributingPluginIds()) {
                    PluginTreeElement treeElement = new PluginTreeElement(pluginId);
                    final Set<String> widgetIdsOfPlugin = service.getWidgetIdsOfPlugin(pluginId);
                    for (String widgetId : widgetIdsOfPlugin) {
                        String name = service.getName(widgetId);
                        String iconPath = service.getIcon(widgetId);
                        treeElement.addWidget(new WidgetTreeElement(widgetId, name, iconPath, pluginId));
                    }
                    elements.add(treeElement);
                }
                _elements = elements.toArray(new PluginTreeElement[elements.size()]);
            }
        }

        public PluginTreeElement[] getElements() {
            return _elements;
        }
    }

    /**
     * The {@link LabelProvider} for the tree.
     * @author Kai Meyer
     *
     */
    private final class WidgetLabelProvider extends LabelProvider {

        /**
         * {@inheritDoc}
         */
        @Override
        public Image getImage(final Object element) {
            if (element instanceof WidgetTreeElement) {
                WidgetTreeElement widgetTreeElement = (WidgetTreeElement) element;
                String iconPath = widgetTreeElement.getIconPath();
                if (iconPath!=null) {
                    final String pluginId = widgetTreeElement.getPluginId();
                    return CustomMediaFactory.getInstance().getImageFromPlugin(pluginId, iconPath);
                }
            }
            return super.getImage(element);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public String getText(final Object element) {
            if (element instanceof WidgetTreeElement) {
                return ((WidgetTreeElement) element).getName();
            }
            if (element instanceof PluginTreeElement) {
                String typeId = ((PluginTreeElement)element).getId();
                try {
                    Dictionary headers = Platform.getBundle(typeId).getHeaders();
                    StringBuffer resultBuffer = new StringBuffer(headers.get("Bundle-Name").toString());
                    resultBuffer.append(" (");
                    resultBuffer.append(typeId);
                    resultBuffer.append(")");
                    return resultBuffer.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    // do nothing
                }
                return typeId;
            }
            return super.getText(element);
        }
    }

    /**
     *
     * @author Kai Meyer
     * @author $Author: jhatje $
     * @version $Revision: 1.8 $
     * @since 14.04.2010
     */
    private class PluginTreeElement {

        private final List<WidgetTreeElement> _widgets;
        private final String _id;

        public PluginTreeElement(final String id) {
            _id = id;
            _widgets = new ArrayList<WidgetTreeElement>();
        }

        public void addWidget(final WidgetTreeElement element) {
            _widgets.add(element);
        }

        public WidgetTreeElement[] getElements() {
            return _widgets.toArray(new WidgetTreeElement[_widgets.size()]);
        }

        public boolean hasWidgets() {
            return !_widgets.isEmpty();
        }

        public String getId() {
            return _id;
        }
    }

    /**
     *
     *
     * @author Kai Meyer
     * @author $Author: jhatje $
     * @version $Revision: 1.8 $
     * @since 14.04.2010
     */
    private class WidgetTreeElement {

        private final String _name;
        private final String _id;
        private final String _iconPath;
        private final String _pluginId;

        public WidgetTreeElement(final String id, final String name, final String iconPath, final String pluginId) {
            _id = id;
            _name = name;
            _iconPath = iconPath;
            _pluginId = pluginId;
        }

        public String getName() {
            return _name;
        }

        public String getIconPath() {
            return _iconPath;
        }

        public String getId() {
            return _id;
        }

        public String getPluginId() {
            return _pluginId;
        }

    }

}
