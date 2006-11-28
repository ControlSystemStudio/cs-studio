package org.csstudio.platform.ui.internal.dnd.compatibility.demo;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Simple 'Text' drag source
 *  @author Kay Kasemir
 */
public class TextDragSource implements DragSourceListener
{
	private String text;
    private DragSource source;

	
	public TextDragSource(final Control control, final String text)
	{
	    this.text = text;
		source = new DragSource(control, DND.DROP_COPY);
		source.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		source.addDragListener(this);
	}

	public void dragStart(final DragSourceEvent event)
	{
	}

	public void dragSetData(final DragSourceEvent event)
	{
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = text;
		}
	}

	public void dragFinished(final DragSourceEvent event)
	{
	}
}
