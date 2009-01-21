package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.ConvenienceTableWrapper;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

/**
 * Editing component for instances.
 * 
 * @author Sven Wende
 * 
 */
public final class InstanceForm extends AbstractPropertyContainerForm<IInstance> {

	private ConvenienceTableWrapper parameterValuesTable;

	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            the editor instance
	 */
	public InstanceForm(DctEditor editor) {
		super(editor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doCreateControl(ExpandBar bar, CommandStack commandStack) {
		super.doCreateControl(bar, commandStack);

		// create field table
		Composite composite = new Composite(bar, SWT.NONE);
		composite.setLayout(LayoutUtil.createGridLayout(1, 5, 8, 8));

		parameterValuesTable = WidgetUtil.createKeyColumErrorTable(composite, commandStack);
		parameterValuesTable.getViewer().getControl().setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(200));

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
	protected String doGetFormLabel() {
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
	protected String doGetLinkText(IInstance instance) {
		String text = "jump to defining <a href=\"" + instance.getPrototype().getId() + "\">prototype</a> or <a href=\""
				+ instance.getParent().getId() + "\">parent instance</a>";
		return text;
	}

}
