/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
package org.csstudio.sds.ui.internal.preferences;

import org.csstudio.domain.common.ui.WorkspaceFileFieldEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The preference page for the default display settings.
 * 
 * @author Joerg Rathlev
 */
public final class DefaultDisplayPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {
    
    /**
     * Creates this preference page.
     */
    public DefaultDisplayPreferencePage() {
        super(GRID);
    }
    
    @Override
    protected void createFieldEditors() {
        WorkspaceFileFieldEditor displayPathFieldEditor = new WorkspaceFileFieldEditor(DefaultDisplayPreference.DEFAULT_DISPLAY_PATH
                                                                                                         .getKeyAsString(),
                                                                                                 "Default display file",
                                                                                                 getFieldEditorParent());
        displayPathFieldEditor.setFilter(new ViewerFilter() {
            
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    if (file != null && file.getFileExtension() != null) {
                        return file.getFileExtension().toLowerCase().equals("css-sds");
                    }
                    return false;
                }
                return true;
            }
        });
        addField(displayPathFieldEditor);
        
        addField(new StringFieldEditor(DefaultDisplayPreference.DEFAULT_DISPLAY_ALIAS.getKeyAsString(),
                                       "Alias:",
                                       getFieldEditorParent()));
        addField(new BooleanFieldEditor(DefaultDisplayPreference.OPEN_AS_SHELL.getKeyAsString(),
                                        "Open as shell",
                                        getFieldEditorParent()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(),
                                                 DefaultDisplayPreference.DEFAULT_DISPLAY_PATH
                                                 .getPluginID()));
        setDescription("Set up the default display in which process variables can be opened.");
    }
    
}
