/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.widgets.symbol.multistate.ControlMultiSymbolFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * Symbol browser for {@link ControlMultiSymbolFigure}.
 * Allow user to choose a different state (symbol)
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class SymbolBrowser extends Composite {
	
	private Map<String, ImageData> images;
	private List<String> states;
	
	private String currentState;
	private Color currentStateColor = new Color(null, 131, 133, 131);
	
	private Button prevButton, nextButton, selectButton;

	private Label label;
	private Image image;
	private int currentIndex = 0;

	private int width = 100;
	private int height = 100;

	private int BTN_WIDTH = 40;
	private int LABEL_HEIGHT = 30;
	
	public SymbolBrowser(Composite parent, int style) {
		super(parent, style);
		images = new HashMap<String, ImageData>();
		states = new ArrayList<String>();
		
		// Initialize main Composite
		Color c = new Color(null, 131, 133, 131);
		setBackground(c);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SymbolBrowser.this.widgetDisposed(e);
			}
		});

		// Initialize children
		prevButton = new Button(this, SWT.ARROW | SWT.LEFT);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		prevButton.setLayoutData(gridData);
		prevButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				previous();
			}
		});
		
		selectButton = new Button(this, SWT.PUSH);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		selectButton.setLayoutData(gridData);
		
		nextButton = new Button(this, SWT.ARROW | SWT.RIGHT);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		nextButton.setLayoutData(gridData);
		nextButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				next();
			}
		});
		
		label = new Label(this, SWT.CENTER);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		label.setLayoutData(gridData);
	}

	@Override
	public Point getSize() {
		return new Point(width, height);
	}
	
	/**
	 * Draw current state & image in the middle button.
	 */
	public void initCurrentDisplay() {
		if (states.isEmpty()) {
			return; // do nothing
		}
		String state = states.get(currentIndex);
		if(state == null) {
			return; // do nothing
		}
		ImageData data = images.get(state);
		if(data == null) {
			return; // do nothing
		}
		dispose();
		label.setText(state);
		image = new Image(Display.getDefault(), data);
		selectButton.setImage(image);
		if (state.equals(currentState)) {
			selectButton.setBackground(currentStateColor);
		} else {
			selectButton.setBackground(null);
		}
		selectButton.redraw();
		this.width = data.width + 10 + BTN_WIDTH * 2;
		this.height = data.height + 10 + LABEL_HEIGHT;
		layout(true);
	}
	
	public synchronized void clear() {
		states.clear();
		images.clear();
	}
	
	public synchronized void dispose() {
		if (image != null && image.isDisposed()) {
			image.dispose();
			image = null;
		}
	}

	/**
	 * Called when widget is disposed.
	 * @param e
	 */
	private void widgetDisposed(DisposeEvent e) {
		dispose();
	}
	
	/**
	 * Called when next button is pushed.
	 */
	private void next() {
		currentIndex = currentIndex + 1;
		if (currentIndex == states.size()) {
			currentIndex = 0;
		}
		initCurrentDisplay();
	}

	/**
	 * Called when previous button is pushed.
	 */
	private void previous() {
		currentIndex = currentIndex - 1;
		if (currentIndex == -1) {
			currentIndex = states.size() - 1;
		}
		initCurrentDisplay();
	}
	
	/**
	 * Define the current displayed state.
	 * @param currentState
	 */
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
		if (states.contains(currentState)) {
			currentIndex = states.indexOf(currentState);
		}
	}
	
	public String getSelection() {
		return states.get(currentIndex);
	}
	
	public void addSelectionListener(Listener listener) {
		selectButton.addListener(SWT.Selection, listener);
	}
	
	public void addImage(String label, ImageData imageData) {
		if (label == null || imageData == null) {
			return;
		}
		this.states.add(label);
		this.images.put(label, imageData);
	}
	
	public boolean isEmpty() {
		return (images == null) || (images.isEmpty());
	}
	
}
