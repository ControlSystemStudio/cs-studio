package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.AbstractTabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.ISectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;

/**
 * Implementation of {@link ITabDescriptorProvider} that connects the Eclipse
 * property view to the elements selected in the current {@link DisplayEditor}.
 *
 * @author Sven Wende
 *
 */
public class TabDescriptorProvider implements ITabDescriptorProvider {
    private final ISectionFactory sectionFactory;

    public TabDescriptorProvider() {
        this.sectionFactory = new SectionFactory();
    }

    /**
     *{@inheritDoc}
     */
    public ITabDescriptor[] getTabDescriptors(final IWorkbenchPart part, final ISelection selection) {
        // .. determine all properties that are compatible for all selected
        // widgets
        List<WidgetProperty> compatibleProperties = getCompatibleProperties(selection);
        Map<WidgetPropertyCategory, Boolean> dynProperties = new HashMap<WidgetPropertyCategory, Boolean>();

        // Uncomment the following line to sort properties alphabetically!
        // Collections.sort(compatibleProperties, new PropertyComparator());

        // .. create the form sections for the chosen properties
        Map<WidgetPropertyCategory, List<ISectionDescriptor>> sectionMapping = new HashMap<WidgetPropertyCategory, List<ISectionDescriptor>>();

        for (WidgetProperty property : compatibleProperties) {
            WidgetPropertyCategory category = property.getCategory();
            if(property.getDynamicsDescriptor()!=null) {
                dynProperties.put(category, true);
            }
            // .. store sections by category
            if (!sectionMapping.containsKey(category)) {
                sectionMapping.put(category, new ArrayList<ISectionDescriptor>());
            }
            List<ISectionDescriptor> sections = sectionMapping.get(category);

            // .. create the section descriptor and add it
            MySectionDescriptor descriptor = new MySectionDescriptor(property.getPropertyType().name(), category.name(), property.getId(),
                    sectionFactory.createSection(property.getId(), property.getPropertyType()));

            assert descriptor != null;

            sections.add(descriptor);
        }

        // .. create tab descriptors for all tabs with active sections (order of
        // the tabs is determined by the order in WidgetPropertCategory enum)
        ITabDescriptor[] tabs = new ITabDescriptor[sectionMapping.keySet().size()];

        int i = 0;
        for (WidgetPropertyCategory category : WidgetPropertyCategory.values()) {
            if (sectionMapping.containsKey(category)) {
                Boolean haveDynProp = dynProperties.get(category);
                if((haveDynProp!=null)&&haveDynProp) {
                    tabs[i] = new MyTabDescriptor(category, sectionMapping.get(category), true);
                }else {
                    tabs[i] = new MyTabDescriptor(category, sectionMapping.get(category), false);
                }
                i++;
            }
        }

        return tabs;
    }

    /**
     * Determines properties that exists in all selected widgets.
     *
     * @param selection
     *            the current selection
     *
     * @return properties that exists in all selected widgets
     */
    private List<WidgetProperty> getCompatibleProperties(final ISelection selection) {
        List<WidgetProperty> properties = new ArrayList<WidgetProperty>();

        List<AbstractWidgetModel> widgets = getSelectedWidgets(selection);

        if (widgets.size() > 0) {
            List<String> propertyIds = new ArrayList<String>(widgets.get(0)
                    .getVisiblePropertyIds());

            if (widgets.size() > 1) {
                for (int i = 1; i < widgets.size(); i++) {
                    propertyIds.retainAll(widgets.get(i)
                            .getVisiblePropertyIds());
                }
            }

            for (String id : propertyIds) {
                properties.add(widgets.get(0).getPropertyInternal(id));
            }

        }

        return properties;
    }

    /**
     * Extracts widgets models from the current selection in a
     * {@link DisplayEditor}.
     *
     * @param selection
     *            the current selection
     *
     * @return a list of widget models
     */
    @SuppressWarnings("unchecked")
    private List<AbstractWidgetModel> getSelectedWidgets(final ISelection selection) {
        List<AbstractWidgetModel> result = new ArrayList<AbstractWidgetModel>();
        if (selection instanceof IStructuredSelection) {

            Iterator it = ((IStructuredSelection) selection).iterator();

            while (it.hasNext()) {
                Object o = it.next();

                if (o instanceof EditPart) {
                    EditPart ep = (EditPart) o;

                    if (ep.getModel() instanceof AbstractWidgetModel) {
                        AbstractWidgetModel widget = (AbstractWidgetModel) ep
                                .getModel();

                        if (widget != null) {
                            result.add(widget);
                        }

                    }
                }
            }
        }

        return result;
    }

    /**
     * Section descriptor implementation for the tabbed properties view as used
     * by SDS.
     *
     * @author Sven Wende
     *
     */
    private static class MySectionDescriptor extends AbstractSectionDescriptor {
        private final String sectionId;
        private final String targetTabId;
        private final ISection sectionClass;
        private final String propertyId;

        private MySectionDescriptor(final String sectionId, final String targetTabId,
                final String propertyId, final ISection section) {
            this.sectionId = sectionId;
            this.targetTabId = targetTabId;
            this.propertyId = propertyId;
            this.sectionClass = section;
        }

        public String getId() {
            return sectionId;
        }

        public ISection getSectionClass() {
            return sectionClass;
        }

        public String getTargetTab() {
            return targetTabId;
        }

        public IFilter getFilter() {
            return new PropertyFilter(propertyId);
        }

    }

    /**
     * Tab descriptor implementation for the tabbed properties view as used by
     * SDS.
     *
     * @author Sven Wende
     *
     */
    private static class MyTabDescriptor extends AbstractTabDescriptor {
        private final WidgetPropertyCategory widgetCategory;
        private final boolean _haveDynProperty;

        private MyTabDescriptor(final WidgetPropertyCategory widgetCategory,
                final List<ISectionDescriptor> sections, final boolean haveDynProperty) {
            this.widgetCategory = widgetCategory;
            _haveDynProperty = haveDynProperty;
            setSectionDescriptors(sections);
        }

        public String getCategory() {
            return "test";
        }

        public String getId() {
            return widgetCategory.name();
        }

        public String getLabel() {
            if(_haveDynProperty) {
                return widgetCategory.getDescription()+" +";
            }
            return widgetCategory.getDescription();
        }

    }
}
