/**
 *
 */
package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.List;
import java.util.Map.Entry;

import org.diirt.service.Service;
import org.diirt.service.ServiceMethod;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;


/**
 * @author shroffk
 *
 */
public class ServiceTreeWidget extends Composite implements ISelectionProvider {

    private TreeViewer treeViewer;

    public ServiceTreeWidget(Composite parent, int style) {
        super(parent, style);
        setLayout(new FormLayout());

        Composite composite = new Composite(this, SWT.NONE);
        FormData fd_composite = new FormData();
        fd_composite.bottom = new FormAttachment(100);
        fd_composite.right = new FormAttachment(100);
        fd_composite.top = new FormAttachment(0);
        fd_composite.left = new FormAttachment(0);
        composite.setLayoutData(fd_composite);

        TreeColumnLayout tcl_composite = new TreeColumnLayout();
        composite.setLayout(tcl_composite);

        treeViewer = new TreeViewer(composite, SWT.BORDER);
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer,
                SWT.NONE);
        treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public Image getImage(Object element) {
                return null;
            }

            @Override
            public String getText(Object element) {
                if (element instanceof Service) {
                    return ((Service) element).getName();
                } else if (element instanceof ServiceMethod) {
                    return element.toString();
                } else if (element instanceof Entry) {
                    return ((Entry<String, String>) element).getKey();
                }
                return "";
            }
        });
        TreeColumn trclmnNewColumn = treeViewerColumn.getColumn();
        tcl_composite.setColumnData(trclmnNewColumn, new ColumnWeightData(10,
                ColumnWeightData.MINIMUM_WIDTH, true));
        trclmnNewColumn.setText("Name");

        TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(treeViewer,
                SWT.NONE);
        treeViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public Image getImage(Object element) {
                return null;
            }

            @Override
            public String getText(Object element) {
                if (element instanceof Service) {
                    return ((Service) element).getDescription();
                } else if (element instanceof ServiceMethod) {
                    return ((ServiceMethod) element).getDescription();
                } else if (element instanceof Entry) {
                    return ((Entry<String, String>) element).getValue();
                }
                return "";
            }
        });
        TreeColumn trclmnNewColumn_1 = treeViewerColumn_1.getColumn();
        tcl_composite.setColumnData(trclmnNewColumn_1, new ColumnWeightData(10,
                ColumnWeightData.MINIMUM_WIDTH, true));
        trclmnNewColumn_1.setText("Description");
        treeViewer.setContentProvider(new ServiceTreeContentProvider());
    }

    public void setServiceNames(List<Service> services) {
        treeViewer.setInput(services);
    }

//      private String serviceMethod2String(ServiceMethod serviceMethod) {
//              StringBuffer stringBuffer = new StringBuffer();
//              stringBuffer.append(serviceMethod.getName()).append("(");
//              List<String> arguments = new ArrayList<String>();
//              SortedMap<String, Class<?>> argumentTypesMap = new TreeMap<String, Class<?>>();
//              argumentTypesMap.putAll(serviceMethod.getArgumentTypes());
//              for (Entry<String, Class<?>> argument : argumentTypesMap.entrySet()) {
//                      arguments.add(argument.getValue().getSimpleName() + " "
//                                      + argument.getKey());
//              }
//              stringBuffer.append(Joiner.on(", ").join(arguments));
//              stringBuffer.append(")");
//              stringBuffer.append(": ");
//              List<String> results = new ArrayList<String>();
//              SortedMap<String, Class<?>> resultTypesMap = new TreeMap<String, Class<?>>();
//              resultTypesMap.putAll(serviceMethod.getResultTypes());
//              for (Entry<String, Class<?>> result : resultTypesMap.entrySet()) {
//                      results.add(result.getValue().getSimpleName() + " "
//                                      + result.getKey());
//              }
//              stringBuffer.append(Joiner.on(", ").join(results));
//              return stringBuffer.toString();
//      }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        treeViewer.addSelectionChangedListener(listener);
    }

    @Override
    public ISelection getSelection() {
        return treeViewer.getSelection();
    }

    @Override
    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
        treeViewer.removeSelectionChangedListener(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        treeViewer.setSelection(selection);
        }
}