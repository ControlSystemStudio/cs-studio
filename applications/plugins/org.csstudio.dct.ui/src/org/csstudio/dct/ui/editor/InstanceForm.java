package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.commands.ChangeParameterValueCommand;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.ConvenienceTableWrapper;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.csstudio.domain.common.LayoutUtil;

/**
 * Editing component for instances.
 *
 * @author Sven Wende
 *
 */
public final class InstanceForm extends AbstractPropertyContainerForm<IInstance> {
    private ParameterValuesClipboard parameterClipboard;
    private ConvenienceTableWrapper parameterValuesTable;
    private Button copyButton;
    private Button pasteWithValuesButton;

    /**
     * Constructor.
     *
     * @param editor
     *            the editor instance
     */
    public InstanceForm(DctEditor editor) {
        super(editor);
        parameterClipboard = new ParameterValuesClipboard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCreateControl(ExpandBar bar, CommandStack commandStack) {
        super.doCreateControl(bar, commandStack);

        // .. create table for field values
        Composite composite = new Composite(bar, SWT.NONE);
        composite.setLayout(LayoutUtil.createGridLayout(1, 5, 8, 8));

        // .. create table for parameter values
        parameterValuesTable = WidgetUtil.create3ColumnTable(composite, commandStack);
        parameterValuesTable.getViewer().getControl().setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(200));

        // .. add/remove buttons for properties
        Composite buttons = new Composite(composite, SWT.None);
        buttons.setLayout(new FillLayout());

        copyButton = new Button(buttons, SWT.FLAT);
        copyButton.setEnabled(true);
        copyButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/copy.gif"));
        copyButton.setToolTipText("Copy Parameter Values");
        copyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                parameterClipboard.setContent(getInput().getFinalParameterValues());
                pasteWithValuesButton.setEnabled(!parameterClipboard.isEmpty());
            }
        });

        pasteWithValuesButton = new Button(buttons, SWT.FLAT);
        pasteWithValuesButton.setEnabled(false);
        pasteWithValuesButton.setToolTipText("Paste Parameter Values");
        pasteWithValuesButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/paste.gif"));
        pasteWithValuesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                Map<String, String> parameters = parameterClipboard.getContent();

                CompoundCommand chain = new CompoundCommand("Paste Parameters");

                for (String key : parameters.keySet()) {
                    if (getInput().getPrototype().hasParameter(key)) {
                        chain.add(new ChangeParameterValueCommand(getInput(), key, parameters.get(key)));
                    }
                }

                if (!chain.isEmpty()) {
                    getCommandStack().execute(chain);
                }

            }
        });

        ExpandItem expandItem = new ExpandItem(bar, SWT.NONE, 1);
        expandItem.setText("Parameter Values");
        expandItem.setHeight(270);
        expandItem.setExpanded(true);
        expandItem.setControl(composite);
        expandItem.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/tab_parametervalues.png"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetInput(IInstance instance) {
        super.doSetInput(instance);

        // prepare input for parameter table
        List<ITableRow> rowsForParameters = new ArrayList<ITableRow>();
        for (Parameter parameter : instance.getPrototype().getParameters()) {
            rowsForParameters.add(new ParameterValueTableRowAdapter(instance, parameter));
        }

        parameterValuesTable.setInput(rowsForParameters);

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetFormLabel(IInstance input) {
        return "Instance";
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doAddCommonRows(List<ITableRow> rows, IInstance instance) {
        rows.add(new BeanPropertyTableRowAdapter("Type", instance, "prototype.name", true));
    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    protected String doGetAdditionalBreadcrumbLinks(IInstance instance) {
        String text = "jump to <a href=\"" + instance.getPrototype().getId() + "\">prototype</a> or <a href=\"" + instance.getParent().getId()
                + "\">parent instance</a>";
        return text;
    }

}
