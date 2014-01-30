
package org.csstudio.shift.viewer;

import org.csstudio.autocomplete.ui.AutoCompleteWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;



public class AutoCompleteSearchProviderView extends ViewPart {
    private Text text;

    public AutoCompleteSearchProviderView() {
    }

    /** View ID defined in plugin.xml */
    public static final String ID = "org.csstudio.shift.ui.AutoCompleteSearchProviderView"; //$NON-NLS-1$

    @Override
    public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
	
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setText("Search:");
	
		text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		// TODO Auto-generated method stub
		new AutoCompleteWidget(text, "ShiftSearch");
    }

    @Override
    public void setFocus() {
	// TODO Auto-generated method stub

    }

}
