package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.XYGraph;

/**The undoable command to add an annotation.
 * @author Xihui Chen
 *
 */
public class AddAnnotationCommand implements IUndoableCommand {
	
	private XYGraph xyGraph;
	private Annotation annotation;
	
	public AddAnnotationCommand(XYGraph xyGraph, Annotation annotation) {
		this.xyGraph = xyGraph;
		this.annotation = annotation;
	}

	public void redo() {
		xyGraph.addAnnotation(annotation);
	}

	public void undo() {
		xyGraph.removeAnnotation(annotation);
	}
	
	@Override
	public String toString() {
		return "Add Annotation";
	}

}
