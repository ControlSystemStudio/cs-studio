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
/*
 * $Id$
 */
package org.csstudio.platform.ui.internal.statisticview;

import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 18.07.2007
 */
public class PreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    private ScopedPreferenceStore _prefStore;

    /**
     * @param style
     */
    public PreferencePage() {
        super(GRID);
        _prefStore = new ScopedPreferenceStore(new InstanceScope(),
                CSSPlatformUiPlugin.getDefault().getBundle().getSymbolicName());
        
        setPreferenceStore(_prefStore);
//        setPreferenceStore(CSSPlatformUiPlugin.getDefault().getPreferenceStore());
        setDescription("TODO"); //TODO: set the DescriptionText
    }

    /** {@inheritDoc}*/
    @Override
    protected void createFieldEditors() {
        getFieldEditorParent().setSize(300, 400);
        
        ListEditor le = new ListEditor(PreferenceConstants.STATISTICVIEW_COLUMNS,
        		"Legt die Reihenfolge der Spalten fest.", getFieldEditorParent()){ //$NON-NLS-1$

            public String[] parseString(String stringList){
                return stringList.split(","); //$NON-NLS-1$
            }

            public String getNewInputObject(){
                InputDialog inputDialog = new InputDialog(getFieldEditorParent().getShell(), "Tst3", "Tst4", "", null); //$NON-NLS-1$
                if (inputDialog.open() == Window.OK) {
                    return inputDialog.getValue();
                }
                return null;
            }

            public String createList(String[] items){
                String temp = ""; //$NON-NLS-1$
                for(int i = 0; i < items.length;i++){
                    temp = temp + items[i] + ","; //$NON-NLS-1$
                }
                return temp;
            }


        };
         Composite buttonComp = le.getButtonBoxControl(getFieldEditorParent());
         Control[] buttons = buttonComp.getChildren();
         buttons[0].setVisible(false);
         buttons[1].setVisible(false);
        addField(le);


    }

    /** {@inheritDoc}*/
    public void init(final IWorkbench workbench) {
        // TODO Auto-generated method stub

    }

}
