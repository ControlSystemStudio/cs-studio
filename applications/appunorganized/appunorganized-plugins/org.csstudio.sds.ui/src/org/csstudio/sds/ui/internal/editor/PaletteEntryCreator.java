package org.csstudio.sds.ui.internal.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.csstudio.sds.internal.preferences.CategorizationType;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.ui.internal.feedback.GraphicalFeedbackContributionsService;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jface.resource.ImageDescriptor;

import com.cosylab.util.StringComparator;

public class PaletteEntryCreator {

    private final WidgetModelFactoryService _widgetService;
    private final KeyListenerAdapter _keyAdapter;

    public PaletteEntryCreator(final WidgetModelFactoryService widgetService,
            final KeyListenerAdapter keyAdapter) {
        _widgetService = widgetService;
        _keyAdapter = keyAdapter;
    }

    @SuppressWarnings("unchecked")
    public void createEntries(PaletteRoot root, CategorizationType categorization) {
        if (CategorizationType.NONE.equals(categorization)) {
            List<ToolEntry> toolentries = createToolEntries(root, _widgetService
                    .getUsedWidgetTypes());
            for (ToolEntry entry : toolentries) {
                PaletteGroup controlGroup = new PaletteGroup("controlGroup");
                controlGroup.add(entry);
                root.add(controlGroup);
            }
        } else {
            PaletteContainer widgetCategory = null;

            List<String> allCategories = new ArrayList<String>();
            allCategories.addAll(_widgetService.getAllCategories());

            Collections.sort(allCategories, new StringComparator());

            for (String category : allCategories) {
                List<ToolEntry> toolEntries = createToolEntries(root, _widgetService.getWidgetForCategory(category));

                widgetCategory = new PaletteDrawer(category);
                if (CategorizationType.STACK.equals(categorization)) {
                    widgetCategory = new PaletteStack(category, category, null);
                    widgetCategory.addAll(toolEntries);
                    PaletteGroup controlGroup = new PaletteGroup("controlGroup");
                    controlGroup.add(widgetCategory);
                    root.add(controlGroup);
                } else {
                    widgetCategory.addAll(toolEntries);
                    root.add(widgetCategory);
                }

            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<ToolEntry> createToolEntries(final PaletteRoot root, Set<String> usedWidgetTypes) {
        List<ToolEntry> result = new ArrayList<ToolEntry>();

        CombinedTemplateCreationEntry toolEntry;

        for (String typeId : usedWidgetTypes) {
            String contributingPluginId = _widgetService
                    .getContributingPluginId(typeId);

            String iconPath = _widgetService.getIcon(typeId);

            ImageDescriptor icon = CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(contributingPluginId,
                            iconPath);
            toolEntry = new CombinedTemplateCreationEntry(_widgetService.getName(typeId),
                    _widgetService.getDescription(typeId),
                    new WidgetCreationFactory(typeId, _keyAdapter), icon, icon);

            Class toolClass = GraphicalFeedbackContributionsService
                    .getInstance().getGraphicalFeedbackFactory(typeId)
                    .getCreationTool();

            if (toolClass != null) {
                toolEntry.setToolClass(toolClass);
            }
            result.add(toolEntry);
        }
        Collections.sort(result, new Comparator<ToolEntry>() {
            public int compare(final ToolEntry entry1, final ToolEntry entry2) {
                return entry1.getLabel().compareTo(entry2.getLabel());
            }
        });
        return result;
    }

}
