package org.csstudio.swt.xygraph.toolbar;

import java.util.List;

import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**The dialog for adding annotation.
 * @author Xihui Chen
 * @author Kay Kasemir Initial defaults
 */
public class AddAnnotationDialog extends Dialog {
	private AnnotationConfigPage configPage;
	
	protected AddAnnotationDialog(final Shell parentShell, final XYGraph xyGraph) {
		super(parentShell);	
		
        // Allow resize
        setShellStyle(getShellStyle() | SWT.RESIZE);

		// Unique annotation names help when trying to edit/delete annotations.
		// Default name: Annotation 1, Annotation 2, ...
		final int num = xyGraph.getPlotArea().getAnnotationList().size();
        final String name = NLS.bind("Annotation {0}", (num+1));
        
        // If there are traces, default to 'snapping' to the first trace
        final Annotation annotation;
		final List<Trace> traces = xyGraph.getPlotArea().getTraceList();
		if (traces.size() > 0)
            annotation = new Annotation(name, traces.get(0));
        else
		    annotation = new Annotation(name,
	                xyGraph.primaryXAxis, xyGraph.primaryYAxis);

		// Allow user to tweak the settings
	    configPage = new AnnotationConfigPage(xyGraph, annotation);
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
