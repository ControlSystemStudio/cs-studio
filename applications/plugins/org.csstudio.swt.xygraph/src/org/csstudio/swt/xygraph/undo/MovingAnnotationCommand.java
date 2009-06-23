package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.figures.Annotation;
import org.eclipse.draw2d.geometry.Point;

public class MovingAnnotationCommand implements IUndoableCommand {
	
	private Annotation annotation;
	private Point beforeMovePosition;
	private Point afterMovePosition;
	private ISample beforeMoveSnappedSample;
	private ISample afterMoveSnappedSample;
	
	
	public MovingAnnotationCommand(Annotation annotation) {
		this.annotation = annotation;
	}
	
	public void redo() {
		if(annotation.isFree())
			annotation.setCurrentPosition(afterMovePosition);
		else
			annotation.setCurrentSnappedSample(afterMoveSnappedSample);		
	}

	public void undo() {
		if(annotation.isFree())
			annotation.setCurrentPosition(beforeMovePosition);
		else
			annotation.setCurrentSnappedSample(beforeMoveSnappedSample);
	}
	
	/**
	 * @param beforeMovePosition the beforeMovePosition to set
	 */
	public void setBeforeMovePosition(Point beforeMovePosition) {
		this.beforeMovePosition = beforeMovePosition;
	}

	/**
	 * @param afterMovePosition the afterMovePosition to set
	 */
	public void setAfterMovePosition(Point afterMovePosition) {
		this.afterMovePosition = afterMovePosition;
	}

	/**
	 * @param beforeMoveSnappedSample the beforeMoveSnappedSample to set
	 */
	public void setBeforeMoveSnappedSample(ISample beforeMoveSnappedSample) {
		this.beforeMoveSnappedSample = beforeMoveSnappedSample;
	}

	/**
	 * @param afterMoveSnappedSample the afterMoveSnappedSample to set
	 */
	public void setAfterMoveSnappedSample(ISample afterMoveSnappedSample) {
		this.afterMoveSnappedSample = afterMoveSnappedSample;
	}
	
	@Override
	public String toString() {
		return "Move Annotation";
	}


}
