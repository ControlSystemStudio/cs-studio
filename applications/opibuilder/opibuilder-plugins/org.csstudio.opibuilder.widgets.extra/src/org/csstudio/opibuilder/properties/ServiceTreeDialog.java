/**
 *
 */
package org.csstudio.opibuilder.properties;

import static org.csstudio.opibuilder.properties.ServiceMethodDescription.createServiceMethodDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.utility.pvmanager.ui.toolbox.ServiceTreeWidget;
import org.diirt.service.Service;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceRegistry;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
/**
 * @author shroffk
 *
 */
public class ServiceTreeDialog extends Dialog{

    private ServiceTreeWidget serviceTreeWidget;

    private ServiceMethodDescription serviceMethodDescription;

    protected ServiceTreeDialog(Shell parentShell) {
    super(parentShell);
    setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite container = (Composite) super.createDialogArea(parent);

        Composite rootComposite = new Composite(container, SWT.NONE | SWT.DOUBLE_BUFFERED);
        GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        rootComposite.setLayoutData(gd_composite);
        gd_composite.heightHint = 300;
        gd_composite.widthHint = 600;
        rootComposite.setLayout(new FormLayout());

        serviceTreeWidget = new ServiceTreeWidget(rootComposite, SWT.NONE);
        FormData fd_serviceTreeWidget = new FormData();
        fd_serviceTreeWidget.bottom = new FormAttachment(100);
        fd_serviceTreeWidget.top = new FormAttachment(0);
        fd_serviceTreeWidget.left = new FormAttachment(0);
        fd_serviceTreeWidget.right = new FormAttachment(100);
        serviceTreeWidget.setLayoutData(fd_serviceTreeWidget);
        List<String> serviceNames = new ArrayList<String>(ServiceRegistry.getDefault().getRegisteredServiceNames());
        Collections.sort(serviceNames);
        List<Service> services = new ArrayList<Service>();
        for (String serviceName : serviceNames) {
            services.add(ServiceRegistry.getDefault().findService(serviceName));
        }
        serviceTreeWidget.setServiceNames(services);
        serviceTreeWidget.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() instanceof TreeSelection) {
                    TreePath[] treeSelection = ((TreeSelection) event.getSelection()).getPaths();
                    if (treeSelection[0].getSegmentCount() == 2) {
                        serviceMethodDescription = createServiceMethodDescription(
                                ((Service) treeSelection[0].getFirstSegment()).getName(),
                                (ServiceMethod) treeSelection[0].getLastSegment());
                    }
                }
                getShell().pack();
            }
        });
        return container;
    }

    /**
     *
     * @return
     */
    public ServiceMethodDescription getSelectedServiceMethodDescription() {
    return serviceMethodDescription;
    }



}
