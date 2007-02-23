/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.diag.rmtcontrol.Preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PageControl extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PageControl() {
		super(FieldEditorPreferencePage.GRID);
	}
	@Override
	protected void createFieldEditors() {
//		// Control 1
//		Group c1 = new Group(getFieldEditorParent(),SWT.NONE);
//		c1.setText("Control 1");
//		c1.setLayout(new GridLayout(4,false));
//		c1.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name1 = new StringFieldEditor(SampleService.Control1_Name,"Name", c1);
//		StringFieldEditor xml1 = new StringFieldEditor(SampleService.Control1_XML,"XML", c1);
//		addField(name1);
//		addField(xml1);
//		// Control 2
//		Group c2 = new Group(getFieldEditorParent(),SWT.NONE);
//		c2.setText("Control 2");
//		c2.setLayout(new GridLayout(4,false));
//		c2.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name2 = new StringFieldEditor(SampleService.Control2_Name,"Name", c2);
//		StringFieldEditor xml2 = new StringFieldEditor(SampleService.Control2_XML,"XML", c2);
//		addField(name2);
//		addField(xml2);
//		// Control 3
//		Group c3 = new Group(getFieldEditorParent(),SWT.NONE);
//		c3.setText("Control 3");
//		c3.setLayout(new GridLayout(4,false));
//		c3.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name3 = new StringFieldEditor(SampleService.Control3_Name,"Name", c3);
//		StringFieldEditor xml3 = new StringFieldEditor(SampleService.Control3_XML,"XML", c3);
//		addField(name3);
//		addField(xml3);
//		// Control 4
//		Group c4 = new Group(getFieldEditorParent(),SWT.NONE);
//		c4.setText("Control 4");
//		c4.setLayout(new GridLayout(4,false));
//		c4.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name4 = new StringFieldEditor(SampleService.Control4_Name,"Name", c4);
//		StringFieldEditor xml4 = new StringFieldEditor(SampleService.Control4_XML,"XML", c4);
//		addField(name4);
//		addField(xml4);
//		// Control 5
//		Group c5 = new Group(getFieldEditorParent(),SWT.NONE);
//		c5.setText("Control 5");
//		c5.setLayout(new GridLayout(4,false));
//		c5.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name5 = new StringFieldEditor(SampleService.Control5_Name,"Name", c5);
//		StringFieldEditor xml5 = new StringFieldEditor(SampleService.Control5_XML,"XML", c5);
//		addField(name5);
//		addField(xml5);
//		// Control 6
//		Group c6 = new Group(getFieldEditorParent(),SWT.NONE);
//		c6.setText("Control 6");
//		c6.setLayout(new GridLayout(4,false));
//		c6.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name6 = new StringFieldEditor(SampleService.Control6_Name,"Name", c6);
//		StringFieldEditor xml6 = new StringFieldEditor(SampleService.Control6_XML,"XML", c6);
//		addField(name6);
//		addField(xml6);
//		// Control 7
//		Group c7 = new Group(getFieldEditorParent(),SWT.NONE);
//		c7.setText("Control 7");
//		c7.setLayout(new GridLayout(4,false));
//		c7.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name7 = new StringFieldEditor(SampleService.Control7_Name,"Name", c7);
//		StringFieldEditor xml7 = new StringFieldEditor(SampleService.Control7_XML,"XML", c7);
//		addField(name7);
//		addField(xml7);
//		// Control 8
//		Group c8 = new Group(getFieldEditorParent(),SWT.NONE);
//		c8.setText("Control 8");
//		c8.setLayout(new GridLayout(4,false));
//		c8.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name8 = new StringFieldEditor(SampleService.Control8_Name,"Name", c8);
//		StringFieldEditor xml8 = new StringFieldEditor(SampleService.Control8_XML,"XML", c8);
//		addField(name8);
//		addField(xml8);
//		// Control 9
//		Group c9 = new Group(getFieldEditorParent(),SWT.NONE);
//		c9.setText("Control 9");
//		c9.setLayout(new GridLayout(4,false));
//		c9.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name9 = new StringFieldEditor(SampleService.Control9_Name,"Name", c9);
//		StringFieldEditor xml9 = new StringFieldEditor(SampleService.Control9_XML,"XML", c9);
//		addField(name9);
//		addField(xml9);
//		// Control 10
//		Group c10 = new Group(getFieldEditorParent(),SWT.NONE);
//		c10.setText("Control 10");
//		c10.setLayout(new GridLayout(4,false));
//		c10.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name10 = new StringFieldEditor(SampleService.Control10_Name,"Name", c10);
//		StringFieldEditor xml10 = new StringFieldEditor(SampleService.Control10_XML,"XML", c10);
//		addField(name10);
//		addField(xml10);
//		// Control 11
//		Group c11 = new Group(getFieldEditorParent(),SWT.NONE);
//		c11.setText("Control 11");
//		c11.setLayout(new GridLayout(4,false));
//		c11.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name11 = new StringFieldEditor(SampleService.Control11_Name,"Name", c11);
//		StringFieldEditor xml11 = new StringFieldEditor(SampleService.Control11_XML,"XML", c11);
//		addField(name11);
//		addField(xml11);
//		// Control 12
//		Group c12 = new Group(getFieldEditorParent(),SWT.NONE);
//		c12.setText("Control 12");
//		c12.setLayout(new GridLayout(4,false));
//		c12.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
//		StringFieldEditor name12 = new StringFieldEditor(SampleService.Control12_Name,"Name", c12);
//		StringFieldEditor xml12 = new StringFieldEditor(SampleService.Control12_XML,"XML", c12);
//		addField(name12);
//		addField(xml12);
//
//


	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
