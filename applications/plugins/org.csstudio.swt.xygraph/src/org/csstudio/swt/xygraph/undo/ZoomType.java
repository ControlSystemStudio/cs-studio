package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.Activator;
import org.csstudio.swt.xygraph.Messages;
import org.csstudio.swt.xygraph.figures.XYGraphFlags;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**The type of zoom on XYGraph.
 * @author Xihui Chen
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public enum ZoomType{
        /** Interactive Rubberband zoom */
        RUBBERBAND_ZOOM(Messages.Zoom_Rubberband,
		        createImage("icons/RubberbandZoom.png"),
				createImage("icons/RubberbandZoomCursor.png"),
				XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM),
				
		/** Zoom via 'cursors' for horizontal start/end position */		
		HORIZONTAL_ZOOM(Messages.Zoom_Horiz,
		        createImage("icons/HorizontalZoom.png"),
				createImage("icons/HorizontalZoomCursor.png"),
                XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM),
				
		/** Zoom via 'cursors' for vertical start/end position */     
		VERTICAL_ZOOM(Messages.Zoom_Vert,
		        createImage("icons/VerticalZoom.png"),
				createImage("icons/VerticalZoomCursor.png"),
                XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM),
				
		/** Zoom 'in' around mouse pointer */
		ZOOM_IN(Messages.Zoom_In,
		        createImage("icons/ZoomIn.png"),
				createImage("icons/ZoomInCursor.png"),
                XYGraphFlags.COMBINED_ZOOM),

        /** Zoom 'out' around mouse pointer */
        ZOOM_OUT(Messages.Zoom_Out,
                createImage("icons/ZoomOut.png"),
                createImage("icons/ZoomOutCursor.png"),
                XYGraphFlags.COMBINED_ZOOM),

        /** Zoom 'in' around mouse pointer along horizontal axis */
        ZOOM_IN_HORIZONTALLY(Messages.Zoom_InHoriz,
                createImage("icons/ZoomInHoriz.png"),
                createImage("icons/ZoomInHorizCursor.png"),
                XYGraphFlags.SEPARATE_ZOOM),
				
        /** Zoom 'out' around mouse pointer along horizontal axis */
        ZOOM_OUT_HORIZONTALLY(Messages.Zoom_OutHoriz,
                createImage("icons/ZoomOutHoriz.png"),
                createImage("icons/ZoomOutHorizCursor.png"),
                XYGraphFlags.SEPARATE_ZOOM),

        /** Zoom 'in' around mouse pointer along vertical axis */
        ZOOM_IN_VERTICALLY(Messages.Zoom_InVert,
                createImage("icons/ZoomInVert.png"),
                createImage("icons/ZoomInVertCursor.png"),
                XYGraphFlags.SEPARATE_ZOOM),
				
        /** Zoom 'out' around mouse pointer along vertical axes */
        ZOOM_OUT_VERTICALLY(Messages.Zoom_OutVert,
                createImage("icons/ZoomOutVert.png"),
                createImage("icons/ZoomOutVertCursor.png"),
                XYGraphFlags.SEPARATE_ZOOM),
				
        /** Zoom 'out' around mouse pointer */
		PANNING(Messages.Zoom_Pan,
		        createImage("icons/Panning.png"),
				createImage("icons/PanningCursor.png"),
                XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM),
				
        /** Disarm zoom behavior */
		NONE(Messages.Zoom_None,
		        createImage("icons/MouseArrow.png"), null,
                XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM);
		
		final private Image iconImage;
		final private String description;
		final private Cursor cursor;
		final private int flags;
		
		/** Initialize
		 *  @param description Description used for tool tip
		 *  @param iconImage Button icon
		 *  @param cursorImage Cursor when zoom type is selected
         *  @param flags Bitwise 'or' of flags that specify in which zoom
         *               configurations this zoom type should be included
         *  @see XYGraphFlags#COMBINED_ZOOM
         *  @see XYGraphFlags#SEPARATE_ZOOM
		 */
		private ZoomType(final String description, 
				final Image iconImage, final Image cursorImage,
				final int flags){
			this.description = description;
			this.iconImage = iconImage;
			if(cursorImage == null)
				cursor = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);
			else
				cursor = new Cursor(Display.getDefault(), cursorImage.getImageData(), 8, 8);
			XYGraphMediaFactory.getInstance().registerCursor(cursor);
			this.flags = flags;
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

		/** Check if this zoom mode should be offered when a graph was
		 *  created with given flags
		 *  @param flags Flags of the XYGraph tool bar
		 *  @return <code>true</code> if this zoom type applies
		 */
		public boolean useWithFlags(final int flags)
		{
		    return (this.flags & flags) > 0;
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