package org.csstudio.utility.nameSpaceBrowser.ui;

import org.csstudio.utility.nameSpaceBrowser.utility.Automat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class MainView extends ViewPart {
	public static final String ID = MainView.class.getName();
	private static String defaultPVFilter =""; //$NON-NLS-1$
	CSSView cssview;

	public MainView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent,SWT.H_SCROLL);
		Composite c = new Composite(sc,SWT.NONE);
		sc.setContent(c);
	    sc.setExpandVertical(true);
		c.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1,1));
		c.setLayout(new GridLayout(1,false));
		cssview = new CSSView(c, new Automat(),getSite(),defaultPVFilter);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setDefaultPVFilter(String defaultFilter) {
		defaultPVFilter = defaultFilter;
		cssview.setDefaultFilter(defaultPVFilter);

	}


}









