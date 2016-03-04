/**
 *
 */
package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.diirt.service.Service;
import org.diirt.service.ServiceRegistry;

/**
 * @author shroffk
 *
 */
public class ServicesView extends ViewPart {

    public static final String ID = "org.csstudio.utility.pvmanager.ui.toolbox.ServicesView"; //$NON-NLS-1$
    private ServiceTreeWidget serviceTreeWidget;

    /**
     *
     */
    public ServicesView() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    public void createPartControl(Composite parent) {

        serviceTreeWidget = new ServiceTreeWidget(parent, SWT.NONE);

        List<String> serviceNames = new ArrayList<String>(ServiceRegistry
                .getDefault().getRegisteredServiceNames());
        Collections.sort(serviceNames);
        List<Service> services = new ArrayList<Service>();
        for (String serviceName : serviceNames) {
            services.add(ServiceRegistry.getDefault().findService(serviceName));
        }
        serviceTreeWidget.setServiceNames(services);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        serviceTreeWidget.setFocus();
    }

}