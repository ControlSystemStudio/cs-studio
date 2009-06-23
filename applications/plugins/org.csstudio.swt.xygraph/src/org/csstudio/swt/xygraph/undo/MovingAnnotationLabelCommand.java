package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.figures.Annotation;

public class MovingAnnotationLabelCommand implements IUndoableCommand {

	private Annotation annotation;

	private double beforeDx, beforeDy, afterDx, afterDy;
	
	
	public MovingAnnotationLabelCommand(Annotation annotation) {
		this.annotation = annotation;
	}

	public void redo() {
		annotation.setdxdy(afterDx, afterDy);
	}

	public void undo() {
		annotation.setdxdy(beforeDx, beforeDy);
	}
	
	public void setBeforeMovingDxDy(double dx, double dy){
		beforeDx = dx;
		beforeDy = dy;
	}
	
	public void setAfterMovingDxDy(double dx, double dy){
		afterDx = dx;
		afterDy = dy;
	}
	
	@Override
	public String toString() {
		return "Move Annotation Label";
	}
}
