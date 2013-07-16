/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.propsheet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/** Widget that shows a blob of color and acts like a button
 *  @author Kay Kasemir
 */
public class ColorBlob extends Canvas implements PaintListener, DisposeListener
{
	/** The color */
	private Color color;
	private SelectionAdapter selected = null;

	/** Initialize
	 *  @param parent Parent widget
	 */
	public ColorBlob(final Composite parent, final RGB rgb)
	{
		super(parent, SWT.BORDER);
		color = new Color(getDisplay(), rgb);
		addDisposeListener(this);
		addPaintListener(this);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				if (selected != null)
					selected.widgetSelected(null);
			}
		});
	}

	public void addSelectionListener(SelectionAdapter selected)
	{
		this.selected  = selected;
	}

	/** @param rgb Set the color displayed in the widget */
	public void setColor(final RGB rgb)
	{
	    color.dispose();
		color = new Color(getDisplay(), rgb);
		redraw();
	}

	/** @see DisposeListener */
	@Override
    public void widgetDisposed(final DisposeEvent e)
	{
		color.dispose();
	}

	/** @see PaintListener */
	@Override
    public void paintControl(final PaintEvent e)
	{
		final GC gc = e.gc;
		gc.setBackground(color);
		final Rectangle area = getClientArea();
		gc.fillRectangle(area);
	}

}
