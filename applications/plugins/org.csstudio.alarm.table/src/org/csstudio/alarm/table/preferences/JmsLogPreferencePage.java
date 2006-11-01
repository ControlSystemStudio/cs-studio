package org.csstudio.alarm.table.preferences;

import java.util.StringTokenizer;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class JmsLogPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public JmsLogPreferencePage() {
		super(GRID);
		setPreferenceStore(JmsLogsPlugin.getDefault().getPreferenceStore());
		setDescription("Settings for alarm severity keys");
		// TODO Auto-generated constructor stub
	}
//
//	public JmsLogPreferencePage(String title) {
//		super(title);
//		// TODO Auto-generated constructor stub
//	}
//
//	public JmsLogPreferencePage(String title, ImageDescriptor image) {
//		super(title, image);
//
//		// TODO Auto-generated constructor stub
//	}

	@Override
	public void createFieldEditors() {
//	protected Control createContents(Composite parent) {
		//noDefaultAndApplyButton();
		makeKeyWord();
		adjustGridLayout();
		// TODO Auto-generated method stub
//		return null;
	}

	private void makeKeyWord() {
		Group g1 = new Group(getFieldEditorParent(),SWT.NONE);
		g1.setLayout(new GridLayout(3,false));
		g1.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
		g1.setText("SEVERITY");
		String keys[] = {JmsLogPreferenceConstants.KEY0, JmsLogPreferenceConstants.KEY1, JmsLogPreferenceConstants.KEY2, JmsLogPreferenceConstants.KEY3, JmsLogPreferenceConstants.KEY4, JmsLogPreferenceConstants.KEY4, JmsLogPreferenceConstants.KEY5, JmsLogPreferenceConstants.KEY5, JmsLogPreferenceConstants.KEY6, JmsLogPreferenceConstants.KEY7, JmsLogPreferenceConstants.KEY8, JmsLogPreferenceConstants.KEY9};
		String values[] = {JmsLogPreferenceConstants.VALUE0, JmsLogPreferenceConstants.VALUE1, JmsLogPreferenceConstants.VALUE2, JmsLogPreferenceConstants.VALUE3, JmsLogPreferenceConstants.VALUE4, JmsLogPreferenceConstants.VALUE4, JmsLogPreferenceConstants.VALUE5, JmsLogPreferenceConstants.VALUE5, JmsLogPreferenceConstants.VALUE6, JmsLogPreferenceConstants.VALUE7, JmsLogPreferenceConstants.VALUE8, JmsLogPreferenceConstants.VALUE9};
		String colors[] = {JmsLogPreferenceConstants.COLOR0, JmsLogPreferenceConstants.COLOR1, JmsLogPreferenceConstants.COLOR2, JmsLogPreferenceConstants.COLOR3, JmsLogPreferenceConstants.COLOR4, JmsLogPreferenceConstants.COLOR4, JmsLogPreferenceConstants.COLOR5, JmsLogPreferenceConstants.COLOR5, JmsLogPreferenceConstants.COLOR6, JmsLogPreferenceConstants.COLOR7, JmsLogPreferenceConstants.COLOR8, JmsLogPreferenceConstants.COLOR9};
		Composite c0 = new Composite(g1,SWT.NONE);
		c0.setLayout(new GridLayout(2,false));
		c0.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,3,1));
//		final Combo comb = new Combo(c0, SWT.READ_ONLY|SWT.DROP_DOWN);
//		String[] s = {"xx", "yy"};
//		comb.setItems(s);		
//		for(int i=0;i<s.length;i++){
//			if(s[i].equals(getPreferenceStore().getString(JmsLogPreferenceConstants.COLUMN))){
//				comb.select(i);
//				break;
//			}
//			
//		}
		Composite c02 = new Composite(c0,SWT.NONE);
		c02.setLayout(new GridLayout(1,false));
		c02.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		final StringFieldEditor chooser = new StringFieldEditor(JmsLogPreferenceConstants.COLUMN,"",c02);
		chooser.getTextControl(c02).setVisible(false);
				addField(chooser);
		Composite c1 = new Composite(g1,SWT.NONE);
		c1.setLayout(new GridLayout(1,false));
		c1.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		new Label(c1,SWT.NONE).setText("Key");
		Composite c2 = new Composite(g1,SWT.NONE);
		c2.setLayout(new GridLayout(1,false));
		c2.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		new Label(c2,SWT.NONE).setText("Value");
		Composite c3 = new Composite(g1,SWT.NONE);
		c3.setLayout(new GridLayout(1,false));
		c3.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		new Label(c3,SWT.NONE).setText("Color");
		for(int i= 0;i<keys.length;i++){
			newRow(g1, keys[i], values[i], colors[i]);			
		}
//		comb.addSelectionListener(new SelectionListener(){
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//				chooser.setStringValue(comb.getItem(comb.getSelectionIndex()));
//			}
//
//			public void widgetSelected(SelectionEvent e) {
//				chooser.setStringValue(comb.getItem(comb.getSelectionIndex()));	
//			}
//			
//		});

	}

	private Composite newRow(Group parent, String key, String value, String color){
		Composite c1 = new Composite(parent,SWT.NONE);
		c1.setLayout(new GridLayout(1,false));
		c1.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		StringFieldEditor sfeKey = new StringFieldEditor(key, "",20, c1);
		addField(sfeKey);
		final Composite c2 = new Composite(parent,SWT.NONE);
		c2.setLayout(new GridLayout(1,false));
		c2.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		final StringFieldEditor sfeValue = new StringFieldEditor(value, "",20, c2);
		StringTokenizer st = new StringTokenizer(getPreferenceStore().getString(color),",");
		sfeValue.getTextControl(c2).setBackground(new Color(getFieldEditorParent().getDisplay(),new RGB(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()))));
		addField(sfeValue);
		final Composite c3 = new Composite(parent,SWT.NONE);
		c3.setLayout(new GridLayout(1,false));
		c3.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		final ColorFieldEditor sfeColor = new ColorFieldEditor(color, "", c3);
		sfeColor.getColorSelector().addListener(new IPropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event) {
				sfeColor.getColorSelector().setColorValue((RGB) event.getNewValue());
				sfeValue.getTextControl(c2).setBackground(new Color(getFieldEditorParent().getDisplay(),(RGB) event.getNewValue()));
			}
		});
		addField(sfeColor);
		return parent;
	}
	

	
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
