/**
 * 
 */
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.extra.ServiceButtonModel;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * @author shroffk
 *
 */
public class ServiceMethodDialog extends Dialog{
    
    private ServiceMethodWidget serviceMethodWidget;
    private final AbstractWidgetModel widgetModel;

    protected ServiceMethodDialog(Shell parentShell, AbstractWidgetModel widgetModel) {
	super(parentShell);	
	setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	this.widgetModel = widgetModel;
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
      Composite container = (Composite) super.createDialogArea(parent);      
      container.setLayout(new FormLayout());            
      serviceMethodWidget = new ServiceMethodWidget(container, SWT.NONE, (ServiceMethodDescription) widgetModel.getPropertyValue(ServiceButtonModel.SERVICE_METHOD));
      FormData fd_serviceMethodWidget = new FormData();
      fd_serviceMethodWidget.right = new FormAttachment(100);
      fd_serviceMethodWidget.top = new FormAttachment(0);
      fd_serviceMethodWidget.left = new FormAttachment(0);
      serviceMethodWidget.setLayoutData(fd_serviceMethodWidget);
      return container;
    }
    
    @Override
    protected Button createButton(Composite parent, int id, String label,
            boolean defaultButton) {     
        return super.createButton(parent, id, label, false);
    }
    
   /**
     * @return the serviceMethodDescription
     */
    public ServiceMethodDescription getServiceMethodDescription() {
	return serviceMethodWidget.getServiceMethodDescription();
    }

}
