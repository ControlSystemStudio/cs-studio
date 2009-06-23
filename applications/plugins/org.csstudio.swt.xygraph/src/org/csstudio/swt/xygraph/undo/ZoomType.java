package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.Activator;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**The type of zoom on XYGraph.
 * @author Xihui Chen
 *
 */
public enum ZoomType{
		RUBBERBAND_ZOOM("Rubberband Zoom", createImage("icons/RubberbandZoom.png"),
				createImage("icons/RubberbandZoomCursor.png")),
		HORIZONTAL_ZOOM("Horizontal Zoom", createImage("icons/HorizontalZoom.png"),
				createImage("icons/HorizontalZoomCursor.png")),
		VERTICAL_ZOOM("Vertical Zoom",  createImage("icons/VerticalZoom.png"),
				createImage("icons/VerticalZoomCursor.png")),
		ZOOM_IN("Zoom In",  createImage("icons/ZoomIn.png"),
				createImage("icons/ZoomInCursor.png")),
		ZOOM_OUT("Zoom Out",  createImage("icons/ZoomOut.png"),
				createImage("icons/ZoomOutCursor.png")),		
		PANNING("Panning",  createImage("icons/Panning.png"),
				createImage("icons/PanningCursor.png")),
		NONE("None", createImage("icons/MouseArrow.png"), null);
		
		private Image iconImage;
		private String description;
		private Cursor cursor;
		private ZoomType(String description, 
				Image iconImage, Image cursorImage){
			this.description = description;
			this.iconImage = iconImage;
			if(cursorImage == null)
				cursor = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);
			else
				cursor = new Cursor(Display.getDefault(), cursorImage.getImageData(), 8, 8);
		}
		
		private ZoomType(String description){
			this.description = description;
		}
		/**
		 * @return the iconImageData
		 */
		public Image getIconImage() {
			return iconImage;
		}
		
		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}
		
		/**
		 * @return the cursor
		 */
		public Cursor getCursor() {
			return cursor;
		}
		
		
		private static Image createImage(String path) {			
			Image image = XYGraphMediaFactory.getInstance().getImageFromPlugin(Activator.getDefault(),
					Activator.PLUGIN_ID, path);				
			return image;
		}

		
		@Override
		public String toString() {
			return description;
		}
	}