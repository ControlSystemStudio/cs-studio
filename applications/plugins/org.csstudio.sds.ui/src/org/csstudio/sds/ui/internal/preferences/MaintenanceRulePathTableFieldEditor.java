/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.sds.ui.internal.preferences;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.domain.common.ui.AbstractTableFieldEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * The class is no longer needed!
 * 
 * This class serves only as an example of using the {@link AbstractTableFieldEditor}
 * 
 * @author Rickens Helge
 * @author $Author: $
 * @since 16.03.2011

 */
public class MaintenanceRulePathTableFieldEditor extends AbstractTableFieldEditor {
    
    /**
     * Constructor.
     */
    protected MaintenanceRulePathTableFieldEditor() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createColumns() {
        final TableViewer tableViewer = getTableViewer();
        
        tableViewer.setContentProvider(new ArrayContentProvider());
        
        TableViewerColumn tvc;
        
        tvc = new TableViewerColumn(tableViewer, SWT.NONE);
        tvc.getColumn().setText("R-Typ");
        tvc.getColumn().setWidth(50);
        tvc.setLabelProvider(new ColumnLabelProvider() {
            /**
             * {@inheritDoc}
             */
            @Override
            public String getText(Object element) {
                if (element instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> parameterList = (List<String>) element;
                    if (parameterList.size() > 1) {
                        return parameterList.get(0);
                    }
                }
                return super.getText(element);
            }
        });
        tvc.setEditingSupport(new EditingSupport(tableViewer) {
            
            @Override
            protected void setValue(Object element, Object value) {
                @SuppressWarnings("unchecked")
                List<String> propertList = (List<String>) element;
                propertList.set(0, (String) value);
                tableViewer.refresh(element);
            }
            
            @Override
            protected Object getValue(Object element) {
                @SuppressWarnings("unchecked")
                List<String> propertList = (List<String>) element;
                return propertList.get(0);
            }
            
            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tableViewer.getTable());
            }
            
            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
        });
        
        tvc = new TableViewerColumn(tableViewer, SWT.NONE);
        tvc.getColumn().setText("Faceplate Path");
        tvc.getColumn().setWidth(250);
        tvc.setLabelProvider(new ColumnLabelProvider() {
            /**
             * {@inheritDoc}
             */
            @Override
            public String getText(Object element) {
                if (element instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> propertList = (List<String>) element;
                    if (propertList.size() > 1) {
                        return propertList.get(1);
                    }
                }
                return super.getText(element);
            }
        });
        
        tvc.setEditingSupport(new EditingSupport(tableViewer) {
            
            @Override
            protected void setValue(Object element, Object value) {
                @SuppressWarnings("unchecked")
                List<String> propertList = (List<String>) element;
                switch(propertList.size()) {
                    case 0:
                        propertList.add("");
                        //$FALL-THROUGH$
                    case 1:
                        propertList.add((String) value);
                        break;
                    default:
                        propertList.set(1, (String) value);
                }
                tableViewer.refresh(element);
            }
            
            @Override
            protected Object getValue(Object element) {
                @SuppressWarnings("unchecked")
                List<String> propertList = (List<String>) element;
                String path = "";
                if(propertList.size()>1) {
                    path = propertList.get(1);
                }
                return path;
            }
            
            @Override
            protected CellEditor getCellEditor(Object elem) {
                return new DialogCellEditor(tableViewer.getTable()) {
                    
                    @Override
                    protected Object openDialogBox(Control cellEditorWindow) {
                        final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(cellEditorWindow
                                                                                                   .getShell(),
                                                                                           new WorkbenchLabelProvider(),
                                                                                           new WorkbenchContentProvider());
                        IWorkspace workspace = ResourcesPlugin.getWorkspace();
                        dialog.setInput(workspace.getRoot());
                        dialog.setTitle("Select Maintence Display");
                        dialog.setAllowMultiple(false);
                        dialog.setBlockOnOpen(true);
                        dialog.setMessage("Choose the Maintence Display:");
                        dialog.setStatusLineAboveButtons(true);
                        String stringValue = (String) getValue();
                        IResource findMember = workspace.getRoot().findMember(stringValue);
                        dialog.setInitialSelection(findMember);
                        dialog.setValidator(new ISelectionStatusValidator() {
                            
                            @Override
                            public IStatus validate(Object[] selection) {
                                if (selection != null && selection.length > 0) {
                                    Object object = selection[0];
                                    if (object instanceof IFile) {
                                        return Status.OK_STATUS;
                                    }
                                }
                                return new Status(IStatus.ERROR, "org.csstudio.sds.ui", "Please select a SDS-File");
                            }
                        });
                        
                        dialog.addFilter(new ViewerFilter() {
                            
                            @Override
                            public boolean select(Viewer viewer,
                                                  Object parentElement,
                                                  Object element) {
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
                        
                        int open = dialog.open();
                        switch (open) {
                            case Window.OK:
                                IFile firstResult = (IFile) dialog.getFirstResult();
                                IPath fullPath = firstResult.getFullPath();
                                String string = fullPath.toString();
                                return string;
                        }
                        return null;
                    }
                };
            }
            
            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTableSettingsToPreferenceString() {
        Table table = getTable();
        if (0 <= table.getItems().length) {
            List<List<String>> parseString = new ArrayList<List<String>>(table.getItems().length);
            
            for (TableItem item : table.getItems()) {
                ArrayList<String> currentColumnTableSet = new ArrayList<String>(2);
                currentColumnTableSet.add(item.getText(0));
                currentColumnTableSet.add(item.getText(1));
                parseString.add(currentColumnTableSet);
            }
            setPreferenceStructure(parseString);
        }
    }
    
}
