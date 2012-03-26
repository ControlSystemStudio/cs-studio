/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.properties.StringTableProperty.TitlesProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The cellEditor for macros property descriptor.
 * @author Xihui Chen
 *
 */
public class StringTableCellEditor extends AbstractDialogCellEditor {
	
	private String[][] data;

	private TitlesProvider columnTitles;
	
	public StringTableCellEditor(Composite parent, String title, TitlesProvider columnTitles) {
		super(parent, title);
		this.columnTitles = columnTitles;
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
			
		StringTableEditDialog dialog = 
			new StringTableEditDialog(parentShell, arrayToList(data), dialogTitle, columnTitles.getTitles());
		if(dialog.open() == Window.OK){
			data = listToArray(dialog.getResult());			
		}
	}

	@Override
	protected boolean shouldFireChanges() {
		return data != null;
	}

	@Override
	protected Object doGetValue() {
		return data;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof String[][]))
			data = new String[0][0];
		else
			data = (String[][])value;			
	}
	
	private List<String[]> arrayToList(String[][] content){
		List<String[]> input = new ArrayList<String[]>();
		if (content.length <= 0) {
			return input;
		}
		int col = columnTitles.getTitles().length;
		for (int i = 0; i < content.length; i++) {
			String[] row = new String[col];
			for (int j = 0; j < col; j++) {
				if(j < content[i].length)
					row[j]=content[i][j];
				else 
					row[j]="";
			}
			input.add(row);
		}
		return input;
	}
	
	private String[][] listToArray(List<String[]> list){
		int col = 0;
		if(list.size() >0){
			col = list.get(0).length;
		}
		String[][] result = new String[list.size()][col];
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < col; j++) {
				result[i][j] = list.get(i)[j];
			}
		}
		return result;
	}
	
	

}
