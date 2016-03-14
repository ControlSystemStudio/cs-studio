package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.metamodel.PromptGroup;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.ConvenienceTableWrapper;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.CompareUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.csstudio.domain.common.LayoutUtil;

/**
 * Editing component for {@link IRecord}.
 *
 * @author Sven Wende
 *
 */
public final class RecordForm extends AbstractPropertyContainerForm<IRecord> {
    private ConvenienceTableWrapper recordFieldTable;
    private boolean hideDefaults = false;
    private PromptGroup promptGroup = PromptGroup.ALL;

    /**
     * Constructor.
     *
     * @param editor
     *            the editor instance
     */
    public RecordForm(DctEditor editor) {
        super(editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCreateControl(ExpandBar bar, CommandStack commandStack) {
        super.doCreateControl(bar, commandStack);

        // .. field table
        Composite composite = new Composite(bar, SWT.NONE);
        GridLayoutFactory.swtDefaults().margins(5, 5).spacing(8,8).applyTo(composite);


        // .. filter options
        Composite buttons = new Composite(composite, SWT.None);
        buttons.setLayout(LayoutUtil.createGridLayout(3, 0, 5, 5));

        // .. promptgroup filter combo
        Label l = new Label(buttons, SWT.NONE);
        l.setText("Group:");
        GridDataFactory.swtDefaults().applyTo(l);
        ComboViewer promptGroupCombo = new ComboViewer(new CCombo(buttons, SWT.READ_ONLY | SWT.BORDER));
        GridDataFactory.swtDefaults().applyTo(promptGroupCombo.getControl());
        promptGroupCombo.setContentProvider(new ArrayContentProvider());
        promptGroupCombo.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                PromptGroup group = (PromptGroup) element;
                return group.getDescription();
            }
        });
        promptGroupCombo.setInput(PromptGroup.values());
        promptGroupCombo.setSelection(new StructuredSelection(PromptGroup.ALL));

        promptGroupCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                promptGroup = (PromptGroup) ((IStructuredSelection) event.getSelection()).getFirstElement();
                refreshFilter();
            }
        });

        // .. filter button
        final Button hideDefaultsButton = new Button(buttons, SWT.CHECK);
        GridDataFactory.swtDefaults().applyTo(hideDefaultsButton);
        hideDefaultsButton.setText("Hide Defaults");
        hideDefaultsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                hideDefaults = hideDefaultsButton.getSelection();
                refreshFilter();
            }
        });

        hideDefaultsButton.setSelection(false);

        // .. table with record fields
        recordFieldTable = WidgetUtil.create3ColumnTable(composite, commandStack);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(recordFieldTable.getViewer().getControl());


        // .. the expand item
        ExpandItem expandItem = new ExpandItem(bar, SWT.NONE);
        expandItem.setText("Fields");
        expandItem.setHeight(1400);
        expandItem.setControl(composite);
        expandItem.setExpanded(true);
        expandItem.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/tab_fields.png"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetInput(IRecord record) {
        super.doSetInput(record);

        // prepare input for field table
        List<ITableRow> rowsForFields = new ArrayList<ITableRow>();

        for (String key : record.getFinalFields().keySet()) {
            rowsForFields.add(new RecordFieldTableRowAdapter(record, key));
        }

        recordFieldTable.setInput(rowsForFields);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetFormLabel(IRecord record) {
        StringBuffer sb = new StringBuffer();
        sb.append(record.getType());
        sb.append("-Record");

        if (!record.isAbstract()) {
            try {
                String resolvedName = ResolutionUtil.resolve(AliasResolutionUtil.getEpicsNameFromHierarchy(record), record);
                sb.append(" (");
                sb.append(resolvedName);
                sb.append(")");
            } catch (AliasResolutionException e) {
                // ignore
            }
        }

        return sb.toString();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doAddCommonRows(List<ITableRow> rows, IRecord record) {
        rows.add(new BeanPropertyTableRowAdapter("Type", record, "type", true));
        rows.add(new HierarchicalBeanPropertyTableRowAdapter("Epics Name", record, "epicsName", false));
        rows.add(new HierarchicalBeanPropertyTableRowAdapter("Disabled", record, "disabled", false));
    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    protected String doGetAdditionalBreadcrumbLinks(IRecord record) {
        if (record.isInherited()) {
            return "jump to <a href=\"" + record.getParentRecord().getId() + "\">parent record</a>";
        } else {
            return null;
        }
    }

    /**
     * Returns the currently selected property.
     *
     * @return the selected property or null
     */
    public String getSelectedField() {
        String result = null;

        IStructuredSelection sel = recordFieldTable != null ? (IStructuredSelection) recordFieldTable.getViewer().getSelection() : null;

        if (sel != null && sel.getFirstElement() != null) {
            RecordFieldTableRowAdapter adapter = (RecordFieldTableRowAdapter) sel.getFirstElement();
            result = adapter.getFieldKey();
        }

        return result;
    }

    protected void refreshFilter() {
        recordFieldTable.getViewer().setFilters(new ViewerFilter[] { new HideDefaultsFilter(hideDefaults), new PromptGroupFilter(promptGroup) });
    }

    private abstract class AbstractFilter extends ViewerFilter {
        @Override
        public final boolean select(Viewer viewer, Object parentElement, Object element) {
            RecordFieldTableRowAdapter row = (RecordFieldTableRowAdapter) element;
            return doSelect(row.getDelegate(), row.getFieldKey());
        }

        protected abstract boolean doSelect(IRecord record, String field);
    }

    private final class PromptGroupFilter extends AbstractFilter {
        private PromptGroup promptGroup;

        public PromptGroupFilter(PromptGroup promptGroup) {
            this.promptGroup = promptGroup;
        }

        @Override
        protected boolean doSelect(IRecord record, String field) {
            if (promptGroup != null && promptGroup != PromptGroup.ALL) {
                IRecordDefinition recordDefinition = record.getRecordDefinition();

                if (recordDefinition != null) {
                    return recordDefinition.getFieldDefinitions(field).getPromptGroup() == promptGroup;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    private final class HideDefaultsFilter extends AbstractFilter {
        private boolean active;

        public HideDefaultsFilter(boolean active) {
            this.active = active;
        }

        @Override
        protected boolean doSelect(IRecord record, String field) {
            if (active) {
                String finalValue = record.getFinalFields().get(field);
                String defaultValue = record.getDefaultFields().get(field);
                return !CompareUtil.equals(finalValue, defaultValue);
            } else {
                return true;
            }
        }
    }
}
