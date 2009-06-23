package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.XYGraph;

/**The undoable command to remove an annotation.
 * @author Xihui Chen
 *
 */
public class RemoveAnnotationCommand implements IUndoableCommand {
	
	private XYGraph xyGraph;
	private Annotation annotation;
	
	public RemoveAnnotationCommand(XYGraph xyGraph, Annotation annotation) {
		this.xyGraph = xyGraph;
		this.annotation = annotation;
	}

	public void redo() {
		xyGraph.removeAnnotation(annotation);
	}

	public void undo() {		
		xyGraph.addAnnotation(annotation);
	}
	
	@Override
	public String toString() {
		return "Remove Annotation";
	}

}
