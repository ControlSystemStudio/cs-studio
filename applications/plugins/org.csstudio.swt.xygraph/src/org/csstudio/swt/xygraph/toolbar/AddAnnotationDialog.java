package org.csstudio.swt.xygraph.toolbar;

import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class AddAnnotationDialog extends Dialog {
	
	private AnnotationConfigPage configPage;
	
	protected AddAnnotationDialog(Shell parentShell, XYGraph xyGraph) {
		super(parentShell);	
		configPage = new AnnotationConfigPage(xyGraph, new Annotation("Annotation",
				xyGraph.primaryXAxis, xyGraph.primaryYAxis));
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add Annotation");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite parent_composite = (Composite) super.createDialogArea(parent);
        final Composite composite = new Composite(parent_composite, SWT.NONE);
		configPage.createPage(composite);		
		return parent_composite;
	}
	
	@Override
	protected void okPressed() {	
		configPage.applyChanges();
		super.okPressed();
	}

	/**
	 * @return the annotation
	 */
	public Annotation getAnnotation() {
		return configPage.getAnnotation();
	}
	
}
