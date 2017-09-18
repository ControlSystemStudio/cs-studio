/**
 *
 */
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.extra.ServiceButtonModel;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 *
 */
public class ServiceMethodDialog extends Dialog{

    private ServiceMethodWidget serviceMethodWidget;
    private final AbstractWidgetModel widgetModel;

    protected ServiceMethodDialog(Shell parentShell, AbstractWidgetModel widgetModel) {
    super(parentShell);
    setShellStyle(getShellStyle()| SWT.RESIZE | SWT.MAX);
    this.widgetModel = widgetModel;
    }


    @Override
    protected boolean isResizable() {
          return true;
    }


    @Override
    protected Control createDialogArea(Composite parent) {

      Composite container = (Composite) super.createDialogArea(parent);
      GridLayout contGl = (GridLayout) container.getLayout();
      contGl.marginWidth = 2;
      contGl.marginHeight = 2;

      GridData contGd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
      contGd.heightHint = 500;
      contGd.widthHint = 900;
      serviceMethodWidget = new ServiceMethodWidget(container, SWT.NONE, (ServiceMethodDescription) widgetModel.getPropertyValue(ServiceButtonModel.SERVICE_METHOD));
      serviceMethodWidget.setLayoutData(contGd);

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
