package org.csstudio.dct.ui.editor.tables;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class TableCellEditorComboTextButton {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Table table = new Table(shell, SWT.BORDER | SWT.MULTI);
		table.setLinesVisible(true);
		for (int i = 0; i < 3; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(100);
		}
		for (int i = 0; i < 12; i++) {
			new TableItem(table, SWT.NONE);
		}
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			TableEditor editor = new TableEditor(table);
			CCombo combo = new CCombo(table, SWT.READ_ONLY);
			combo.setText("CCombo");
			combo.add("item 1");
			combo.add("item 2");
			editor.grabHorizontal = true;
			editor.setEditor(combo, items[i], 0);
			editor = new TableEditor(table);
			Text text = new Text(table, SWT.NONE);
			text.setText("Text");
			editor.grabHorizontal = true;
			editor.setEditor(text, items[i], 1);
			editor = new TableEditor(table);
			Button button = new Button(table, SWT.CHECK);
			button.pack();
			editor.minimumWidth = button.getSize().x;
			editor.horizontalAlignment = SWT.LEFT;
			editor.setEditor(button, items[i], 2);
		}
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}