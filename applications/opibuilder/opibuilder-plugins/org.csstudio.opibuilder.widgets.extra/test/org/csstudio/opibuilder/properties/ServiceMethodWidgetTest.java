/**
 *
 */
package org.csstudio.opibuilder.properties;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author shroffk
 *
 */
public class ServiceMethodWidgetTest extends ApplicationWindow {

    public ServiceMethodWidgetTest() {
    super(null);
    addToolBar(SWT.FLAT | SWT.WRAP);
    addMenuBar();
    addStatusLine();
    }

    @Override
    protected Control createContents(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    container.setLayout(new GridLayout(1, false));

    Map<String, String> argumentPvs = new HashMap<String, String>();
    argumentPvs.put("arg1", "loc://${DID}_pv1");
    argumentPvs.put("arg2", "loc://${DID}_pv2");
    Map<String, String> resultPvs = new HashMap<String, String>();
    resultPvs.put("result1", "loc://${DID}_result_1");
    resultPvs.put("result2", "loc://${DID}_result_2");
    ServiceMethodDescription testServiceMethodDescription = ServiceMethodDescription
        .createServiceMethodDescription("Test Service", "Test Method",
            "This is a test service and method", argumentPvs,
            resultPvs);
    ServiceMethodWidget serviceMethodWidget = new ServiceMethodWidget(
        container, SWT.NONE, testServiceMethodDescription);
    serviceMethodWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
        true, 1, 1));
    return container;
    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {

    try {
        ServiceMethodWidgetTest window = new ServiceMethodWidgetTest();
        window.setBlockOnOpen(true);
        window.open();
        Display.getCurrent().dispose();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

}
