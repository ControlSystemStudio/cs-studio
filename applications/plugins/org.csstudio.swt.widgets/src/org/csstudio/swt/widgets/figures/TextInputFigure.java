/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.csstudio.swt.widgets.datadefinition.IManualStringValueChangeListener;
import org.csstudio.swt.widgets.util.DateTimePickerDialog;
import org.csstudio.swt.widgets.util.SingleSourceHelper;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class TextInputFigure extends TextFigure {

	public enum SelectorType {		
		NONE("None", null),
		FILE("File", openFileImg),
		DATETIME("Datetime", calendarImg);
		
		public String description;
		
		public Image icon;
		
		private SelectorType(String description, Image icon) {
			this.description = description;
			this.icon = icon;
		}
		
		@Override
		public String toString() {
			return description;
		}
		
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(SelectorType p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}
	
	public enum FileSource{		
		WORKSPACE("Workspace"),
		LOCAL("Local File System");
		
		public String description;
		
		private FileSource(String description) {
			this.description = description;
		}		
		
		@Override
		public String toString() {
			return description;
		}
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(FileSource p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}
	
	public enum FileReturnPart{		
		FULL_PATH("Full Path"),
		NAME_EXT("Name & Extension"),
		NAME_ONLY("Name Only"),
		DIRECTORY("Directory");
		
		public String description;
		
		private FileReturnPart(String description) {
			this.description = description;
		}
		
		@Override
		public String toString() {
			return description;
		}
		
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(FileReturnPart p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}
	
	
	private static final Image
	openFileImg = createImage("icons/openFile.png"), //$NON-NLS-1$
	calendarImg = createImage("icons/calendar.gif"); //$NON-NLS-1$
	
	private static final int SELECTOR_WIDTH = 25; 
	
	
	private String dateTimeFormat = "yyyy-MM-dd HH:mm:ss"; //$NON-NLS-1$
	
	private Date dateTime;
	
	private Button selector;
	
	private SelectorType  selectorType = SelectorType.NONE;
	private FileSource fileSource = FileSource.WORKSPACE;
	private FileReturnPart fileReturnPart = FileReturnPart.FULL_PATH;	
	private List<IManualStringValueChangeListener> selectorListeners;
	
	private String startPath;
	
	private String currentPath;

	public TextInputFigure() {
		this(false);
	}
	
	public TextInputFigure(boolean runMode) {
		super(runMode);			
		selectorListeners = new ArrayList<IManualStringValueChangeListener>(1);
	}
	
	
	@Override
	protected void layout() {
		super.layout();
		if(selector != null && selector.isVisible()){
			Rectangle clientArea = getClientArea();
			selector.setBounds(new Rectangle(clientArea.x + clientArea.width - SELECTOR_WIDTH, 
					clientArea.y, SELECTOR_WIDTH, clientArea.height));			
		}
	}
	
	public void addManualValueChangeListener(IManualStringValueChangeListener listener){
		if(listener != null)
			selectorListeners.add(listener);
	}
	
	/**
	 * Inform all slider listeners, that the manual value has changed.
	 * 
	 * @param newManualValue
	 *            the new manual value
	 */
	public void fireManualValueChange(final String newManualValue) {		
		
			for (IManualStringValueChangeListener l : selectorListeners) {
				l.manualValueChanged(newManualValue);
			}
	}
	

	/**
	 * @return the dateTimeFormat
	 */
	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	/**
	 * @param dateTimeFormat the dateTimeFormat to set
	 */
	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}
	
	/**
	 * @return the startPath
	 */
	public String getStartPath() {
		return startPath;
	}

	@Override
	protected Rectangle getTextArea() {
		Rectangle textArea;
		if(selector != null && selector.isVisible()){
			Rectangle clientArea = getClientArea();			
			textArea = new Rectangle(clientArea.x, clientArea.y, 
					clientArea.width-SELECTOR_WIDTH, clientArea.height);
		}else
			textArea = getClientArea();
		return textArea;
	}
	
	/**
	 * @param startPath the startPath to set
	 */
	public void setStartPath(String startPath) {
		this.startPath = startPath;
	}

	/**
	 * @return the selectorType
	 */
	public SelectorType getSelectorType() {
		return selectorType;
	}

	/**
	 * @param selectorType the selectorType to set
	 */
	public void setSelectorType(SelectorType selectorType) {
		this.selectorType = selectorType;
		if(selectorType != SelectorType.NONE){
			if(selector != null){
				remove(selector);
			}	
			selector = new Button(selectorType.icon);
			selectorListeners = new ArrayList<IManualStringValueChangeListener>();
			selector.addActionListener(new SelectorListener());
			add(selector);			
		}else{
			if(selector != null){
				remove(selector);
				selector = null;
			}
		}
	}

	/**
	 * @return the fileSource
	 */
	public FileSource getFileSource() {
		return fileSource;
	}

	/**
	 * @param fileSource the fileSource to set
	 */
	public void setFileSource(FileSource fileSource) {
		this.fileSource = fileSource;
	}

	/**
	 * @return the fileReturnPart
	 */
	public FileReturnPart getFileReturnPart() {
		return fileReturnPart;
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension superSize = super.getPreferredSize(wHint, hHint);
		if(superSize.height < SELECTOR_WIDTH)
			superSize.height = SELECTOR_WIDTH;
		superSize.width += SELECTOR_WIDTH+1;
		return superSize;
	}
	
	/**
	 * @param fileReturnPart the fileReturnPart to set
	 */
	public void setFileReturnPart(FileReturnPart fileReturnPart) {
		this.fileReturnPart = fileReturnPart;
	}
	
	public String getCurrentPath() {
		return currentPath;
	}
	
	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	private static Image createImage(String name) {
		InputStream stream = TextInputFigure.class.getResourceAsStream(name);
		Image image = new Image(null, stream);
		try {
			stream.close();
		} catch (IOException ioe) {
		}
		return image;
	}

	private final class SelectorListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			switch (getSelectorType()) {						
			case FILE:
				SingleSourceHelper.handleTextInputFigureFileSelector(TextInputFigure.this);
				break;
			case DATETIME:
				DateTimePickerDialog dialog = new DateTimePickerDialog(Display.getCurrent().getActiveShell());
				if(dateTime != null)
					dialog.setDateTime(dateTime);
				if(dialog.open() == Window.OK){
					dateTime = dialog.getDateTime();
					try {
						setText(new SimpleDateFormat(dateTimeFormat).format(dateTime));
					} catch (Exception e) {
						String msg = NLS.bind(
								"Failed to return datetime. The datetime format {0} might be incorrect.\n" +
								e.getMessage(),	dateTimeFormat);
						MessageDialog.openError(null, "Failed", msg);
						break;
					}
					fireManualValueChange(getText());
				}
				break;
			case NONE:
			default:
				break;
			}
		}
	}
	
	
	
	
	
}
