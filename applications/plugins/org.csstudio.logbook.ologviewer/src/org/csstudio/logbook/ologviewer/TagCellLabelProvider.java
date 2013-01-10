package org.csstudio.logbook.ologviewer;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import edu.msu.nscl.olog.api.Log;

public class TagCellLabelProvider extends StyledCellLabelProvider {

	private static final Display DISPLAY= Display.getCurrent();
	
	// private static int IMAGE_SIZE= 16;
	private static final Image TAGGED = new Image(DISPLAY, Activator
			.getImageDescriptor("icons/tagged.png").getImageData());

	private final String tagName;
	
	public TagCellLabelProvider(){
		this.tagName = "Timing Systems";
	}

	public TagCellLabelProvider(String tagName) {
		this.tagName = tagName;
	}

	public void update(ViewerCell cell) {

		Log log = (Log) cell.getElement();
		if (log.getTag(tagName) != null) {
			cell.setImage(TAGGED);
		}
		super.update(cell);
	}

	protected void measure(Event event, Object element) {
		super.measure(event, element);
	}
}