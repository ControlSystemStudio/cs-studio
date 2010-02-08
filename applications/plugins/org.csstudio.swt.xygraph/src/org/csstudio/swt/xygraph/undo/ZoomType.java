package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.Activator;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**The type of zoom on XYGraph.
 * @author Xihui Chen
 * @author Kay Kasemir
 */
public enum ZoomType{
        /** Interactive Rubberband zoom */
		RUBBERBAND_ZOOM("Rubberband Zoom", createImage("icons/RubberbandZoom.png"),
				createImage("icons/RubberbandZoomCursor.png")),
				
		/** Zoom via 'cursors' for horizontal start/end position */		
		HORIZONTAL_ZOOM("Horizontal Zoom", createImage("icons/HorizontalZoom.png"),
				createImage("icons/HorizontalZoomCursor.png")),
				
		/** Zoom via 'cursors' for vertical start/end position */     
		VERTICAL_ZOOM("Vertical Zoom",  createImage("icons/VerticalZoom.png"),
				createImage("icons/VerticalZoomCursor.png")),
				
		/** Zoom 'in' around mouse pointer */
		ZOOM_IN("Zoom In",  createImage("icons/ZoomIn.png"),
				createImage("icons/ZoomInCursor.png")),

        /** Zoom 'in' around mouse pointer along horizontal axis */
        ZOOM_IN_HORIZONTALLY("Zoom In Horizontally",  createImage("icons/ZoomInHoriz.png"),
                createImage("icons/ZoomInCursor.png")),
				
        /** Zoom 'in' around mouse pointer along vertical axis */
        ZOOM_IN_VERTICALLY("Zoom In Vertically",  createImage("icons/ZoomInVert.png"),
                createImage("icons/ZoomInCursor.png")),
				
		/** Zoom 'out' around mouse pointer */
		ZOOM_OUT("Zoom Out",  createImage("icons/ZoomOut.png"),
				createImage("icons/ZoomOutCursor.png")),

        /** Zoom 'out' around mouse pointer along horizontal axis */
        ZOOM_OUT_HORIZONTALLY("Zoom Out Horizontally",  createImage("icons/ZoomOutHoriz.png"),
                createImage("icons/ZoomOutCursor.png")),

        /** Zoom 'out' around mouse pointer along vertical axes */
        ZOOM_OUT_VERTICALLY("Zoom Out Vertically",  createImage("icons/ZoomOutVert.png"),
                createImage("icons/ZoomOutCursor.png")),
				
        /** Zoom 'out' around mouse pointer */
		PANNING("Panning",  createImage("icons/Panning.png"),
				createImage("icons/PanningCursor.png")),
				
        /** Disarm zoom behavior */
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