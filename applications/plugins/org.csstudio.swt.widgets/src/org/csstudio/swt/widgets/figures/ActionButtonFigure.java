/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.introspection.ActionButtonIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.util.AbstractInputStreamRunnable;
import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.swt.widgets.util.IJobErrorHandler;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.SWTConstants;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

/**
 * A re-written action button figure which can pass mouse event state to action listener.
 * @author Xihui Chen
 *
 */
public class ActionButtonFigure extends Figure implements Introspectable, ITextFigure{

	/**
	 * When it was set as armed, action will be fired when mouse released.
	 */
	private boolean armed = false;

	/**
	 * The status of mouse button.
	 */
	private boolean mousePressed = false;


	/**
	 * Default label font.
	 */
	public static final Font FONT = CustomMediaFactory.getInstance().getFont(
			CustomMediaFactory.FONT_ARIAL);

	/**
	 * The Label for the Button.
	 */
	private Label label;

	private boolean toggleStyle = false;

	private boolean selected = false;

	private boolean toggled =false;

	/**
	 * An Array, which contains the PositionConstants for Center, Top, Bottom, Left, Right.
	 */
	private final int[] alignments = new int[] {PositionConstants.CENTER, PositionConstants.TOP, PositionConstants.BOTTOM, PositionConstants.LEFT, PositionConstants.RIGHT};

	private boolean runMode = false;

	private List<ButtonActionListener> listeners;

	private Image image, grayImage;


	private IPath imagePath;

	private int textAlignment;
	public ActionButtonFigure(boolean runMode) {
		setEnabled(false);
		this.runMode = runMode;
		armed = false;
		mousePressed = false;
		listeners = new ArrayList<ButtonActionListener>();

		setRequestFocusEnabled(true);
		setFocusTraversable(true);

		setLayoutManager(new StackLayout());


		label = new Label(){
			@Override
			public boolean isOpaque() {
				return true;
			}

			/**
			 * If this button has focus, this method paints a focus rectangle.
			 *
			 * @param graphics Graphics handle for painting
			 */
			protected void paintBorder(Graphics graphics) {
				super.paintBorder(graphics);
				if (ActionButtonFigure.this.hasFocus()) {
					graphics.setForegroundColor(ColorConstants.black);
					graphics.setBackgroundColor(ColorConstants.white);

					Rectangle area = getClientArea();
					graphics.drawFocus(area.x, area.y, area.width, area.height);

				}
			}


		};
		label.setBorder(new ButtonBorder());
		add(label);

		if(runMode){
			hookEventHandler(new ButtonEventHandler());
			label.setCursor(Cursors.HAND);	
		}
	}

	public ActionButtonFigure() {
		this(true);
	}

	/**
	 * Registers the given listener as an {@link ButtonActionListener}.
	 *
	 * @param listener The {@link ButtonActionListener} to add
	 */
	public void addActionListener(ButtonActionListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		listeners.add(listener);
	}

	/**
	 * Dispose the resources on this figure.
	 */
	public void dispose() {

		if(image != null){
			image.dispose();
			image = null;
		}
		if(grayImage != null){
			grayImage.dispose();
			grayImage = null;
		}
	}

	/**
	 * Notifies any {@link ButtonActionListener} on this button
	 * that an action has been performed.
	 *
	 */
	protected void fireActionPerformed(int i) {
		for(ButtonActionListener listener : listeners)
			listener.actionPerformed(i);
	}

	/**
	 * @return the imagePath
	 */
	public IPath getImagePath() {
		return imagePath;
	}

	/**
	 * @return the textAlignment
	 */
	public int getTextAlignment() {
		return textAlignment;
	}

	/**
	 * Adds the given {@link ButtonEventHandler} to this button. A {@link ButtonEventHandler}
	 * should be a MouseListener, MouseMotionListener, KeyListener,  and
	 * FocusListener.
	 *
	 * @param handler  The new event handler
	 * @since 2.0
	 */
	protected void hookEventHandler(ButtonEventHandler handler) {
		if (handler == null)
			return;
		addMouseListener(handler);
		addMouseMotionListener(handler);
		addKeyListener(handler);
		addFocusListener(handler);
	}

	protected boolean isArmed() {
		return armed;
	}

	protected boolean isMousePressed() {
		return mousePressed;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}



	/**
	 * @return the runMode
	 */
	public boolean isRunMode() {
		return runMode;
	}

	/**
	 * @return true if the button is pushed down. false otherwise.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @return true if the button is toggled. false otherwise.
	 */
	public boolean isToggled() {
		return toggled;
	}

	public boolean isToggleStyle() {
		return toggleStyle;
	}

	/**
	 * Paints the area of this figure excluded by the borders. Induces a (1,1) pixel shift in
	 * the painting if the  mouse is armed, giving it the pressed appearance.
	 *
	 * @param graphics  Graphics handle for painting
	 * @since 2.0
	 */
	protected void paintClientArea(Graphics graphics) {
		if (isSelected()) {
			graphics.translate(1, 1);
			graphics.pushState();
			super.paintClientArea(graphics);
			graphics.popState();
			graphics.translate(-1, -1);
		} else
			super.paintClientArea(graphics);
	}

	public void removeActionListener(ButtonActionListener listener){
		if(listener != null && listeners.contains(listener))
			listeners.remove(listener);
	}

	/**Set the armed status of the button.
	 * @param armed
	 */
	protected void setArmed(boolean armed) {
		this.armed = armed;
	}


	@Override
	public void setCursor(Cursor cursor) {
		label.setCursor(cursor);
		super.setCursor(cursor);
	}

	@Override
	public void setEnabled(boolean value) {
		if(label != null){
			label.setEnabled(value);
			//update the icon
			if(image != null){
				if(value)
					label.setIcon(image);
				else {
					if(grayImage == null)
						if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
							grayImage = image;
						else
							grayImage = new Image(null, image, SWTConstants.IMAGE_GRAY);
					label.setIcon(grayImage);
				}
			}
		}
		if(runMode)
			super.setEnabled(value);
		repaint();
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		label.revalidate();
	}

	@SuppressWarnings("nls")
    public void setImagePath(final IPath path){
		dispose();
		AbstractInputStreamRunnable uiTask = new AbstractInputStreamRunnable() {
			
			@Override
			public void runWithInputStream(InputStream stream) {
				image = new Image(null, stream);
				try {
					stream.close();
				} catch (IOException e) {
				}
				if(label.isEnabled())
					label.setIcon(image);
				else{
					if(grayImage == null && image != null)
						if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
							grayImage = image;
						else
							grayImage = new Image(null, image, SWTConstants.IMAGE_GRAY);
					label.setIcon(grayImage);
				}
			}
		};
		if(path != null && !path.isEmpty()){
			this.imagePath = path;
			ResourceUtil.pathToInputStreamInJob(path, uiTask, "Load Action Button Icon...", new IJobErrorHandler() {
				
				public void handleError(Exception exception) {
					image = null;
					Activator.getLogger().log(Level.WARNING,
			            "Failed to load image from path" + path, exception);
				}
			});			
		}

		if(label.isEnabled())
			label.setIcon(image);
		else{
			if(grayImage == null && image != null)
				if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
					grayImage = image;
				else
					grayImage = new Image(null, image, SWTConstants.IMAGE_GRAY);
			label.setIcon(grayImage);
		}
	}

	protected void setMousePressed(boolean mousePressed) {
			this.mousePressed = mousePressed;
	}


	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(boolean runMode) {
		this.runMode = runMode;
	}


	public void setSelected(boolean selected) {
		if(this.selected == selected)
			return;
		this.selected = selected;
		repaint();
	}




	/**
	 * Sets the text for the Button.
	 * @param s
	 * 			The text for the button
	 */
	public void setText(final String s) {
		label.setText(s);
	}


	/**
	 * @return the label text.
	 */
	public String getText(){
		return label.getText();
	}


	/**
	 * Sets the alignment of the buttons text.
	 * The parameter is a {@link PositionConstants} (LEFT, RIGHT, TOP, CENTER, BOTTOM)
	 * @param alignment
	 * 			The alignment for the text
	 */
	public void setTextAlignment(final int alignment) {
		this.textAlignment = alignment;
		if (alignment>=0 && alignment<alignments.length) {
			if (alignments[alignment]==PositionConstants.LEFT || alignments[alignment]==PositionConstants.RIGHT) {
				label.setTextPlacement(PositionConstants.NORTH);
			} else {
				label.setTextPlacement(PositionConstants.EAST);
			}
			label.setTextAlignment(alignments[alignment]);
		}
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		setSelected(toggled);
	}

	/**
	 * Set the style of the Button.
	 * @param style false = Push, true=Toggle.
	 */
	public void setToggleStyle(final boolean style){
		  toggleStyle = style;

	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new ActionButtonIntrospector().getBeanInfo(this.getClass());
	}

	public interface ButtonActionListener extends EventListener{
		public void actionPerformed(int i);
	}

	/**
	 * The border for a button which has different paint based on the button pressed status.
	 */
	class ButtonBorder
		extends SchemeBorder
	{


	/**
	 * Provides for a scheme to represent the borders of clickable figures like buttons.
	 * Though similar to the {@link SchemeBorder.Scheme Scheme} it supports an extra set of
	 * borders for the pressed states.
	 */
	 class ButtonScheme
		extends Scheme
	{
		private Color
			highlightPressed[] = null,
			shadowPressed[] = null;

		/**
		 * Constructs a new button scheme where the input colors are the colors for the
		 * top-left and bottom-right sides of the  border. These colors serve as the colors
		 * when the border is in a pressed state too. The width of each side is determined by
		 * the number of colors passed in as input.
		 *
		 * @param highlight  Colors for the top-left sides of the border
		 * @param shadow     Colors for the bottom-right sides of the border
		 * @since 2.0
		 */
		public ButtonScheme(Color[] highlight, Color[] shadow) {
			highlightPressed = this.highlight = highlight;
			shadowPressed = this.shadow = shadow;
			init();
		}

		/**
		 * Constructs a new button scheme where the input colors are the colors for the
		 * top-left and bottom-right sides of the  border, for the normal and pressed states.
		 * The width of  each side is determined by the number of colors passed in  as input.
		 *
		 * @param hl   Colors for the top-left sides of the border
		 * @param sh   Colors for the bottom-right sides of the border
		 * @param hlp  Colors for the top-left sides of the border when figure is pressed
		 * @param shp  Colors for the bottom-right sides of the border when figure is pressed
		 * @since 2.0
		 */
		public ButtonScheme(Color[] hl, Color[] sh, Color[] hlp, Color[] shp) {
			highlight = hl;
			shadow = sh;
			highlightPressed = hlp;
			shadowPressed = shp;
			init();
		}

		/**
		 * Calculates and returns the Insets for this border. The calculations are based on
		 * the number of normal and pressed, highlight and shadow colors.
		 *
		 * @return  The insets for this border
		 * @since 2.0
		 */
		protected Insets calculateInsets() {
			int br = 1 + Math.max(getShadow().length, getHighlightPressed().length);
			int tl = Math.max(getHighlight().length, getShadowPressed().length);
			return new Insets(tl, tl, br, br);
		}

		/**
		 * Calculates and returns the opaque state of this border.
		 * <p>
		 * Returns false in the following conditions:
		 * <ul>
		 * 		<li> The number of highlight colors is different than the the number of
		 * 		shadow colors.
		 * 		<li> The number of pressed highlight colors is different than the number of
		 * 		pressed shadow colors.
		 * 		<li> Any of the highlight and shadow colors are set to <code>null</code>
		 * 		<li> Any of the pressed highlight and shadow colors are set to
		 * 		<code>null</code>
		 * </ul>
		 * This is done so that the entire region under the figure is properly covered.
		 *
		 * @return  The opaque state of this border
		 * @since 2.0
		 */
		protected boolean calculateOpaque() {
			if (!super.calculateOpaque())
				return false;
			if (getHighlight().length != getShadowPressed().length)
				return false;
			if (getShadow().length != getHighlightPressed().length)
				return false;
			Color [] colors = getHighlightPressed();
			for (int i = 0; i < colors.length; i++)
				if (colors[i] == null)
					return false;
			colors = getShadowPressed();
			for (int i = 0; i < colors.length; i++)
				if (colors[i] == null)
					return false;
			return true;
		}

		/**
		 * Returns the pressed highlight colors of this border.
		 *
		 * @return  Colors as an array of Colors
		 * @since 2.0
		 */
		protected Color[] getHighlightPressed() {
			return highlightPressed;
		}

		/**
		 * Returns the pressed highlight colors of this border.
		 *
		 * @return  Colors as an array of Colors
		 * @since 2.0
		 */
		protected Color[] getHighlightReleased() {
			return getHighlight();
		}

		/**
		 * Returns the pressed shadow colors of this border.
		 *
		 * @return  Colors as an array of Colors
		 * @since 2.0
		 */
		protected Color[] getShadowPressed() {
			return shadowPressed;
		}

		/**
		 * Returns the pressed shadow colors of this border.
		 *
		 * @return  Colors as an array of Colors
		 * @since 2.0
		 */
		protected Color[] getShadowReleased() {
			return getShadow();
		}
	}

	 /**
		 * Regular button scheme
	 */
	ButtonScheme BUTTON = new ButtonScheme(
			new Color[] {buttonLightest},
			DARKEST_DARKER
	);
	/**
	 * Constructs a ButtonBorder with a predefined button scheme set as its default.
	 *
	 * @since 2.0
	 */
	public ButtonBorder() {
		setScheme(BUTTON);
	}


	/**
	 * Paints this border with the help of the set scheme, the model of the clickable figure,
	 * and other inputs. The scheme is used in conjunction with the state of the model to get
	 * the appropriate colors for the border.
	 *
	 * @param figure The Clickable that this border belongs to
	 * @param graphics The graphics used for painting
	 * @param insets The insets
	 */
	public void paint(IFigure figure, Graphics graphics, Insets insets) {

		ButtonScheme colorScheme = (ButtonScheme)getScheme();

		Color tl[], br[];
		if (isSelected()) {
			tl = colorScheme.getShadowPressed();
			br = colorScheme.getHighlightPressed();
		} else {
			tl = colorScheme.getHighlightReleased();
			br = colorScheme.getShadowReleased();
		}

		paint(graphics, figure, insets, tl, br);
	}

	}

	class ButtonEventHandler extends MouseMotionListener.Stub
		implements
			MouseListener,
			KeyListener,
			FocusListener
	{

		private int mouseState;
		public void focusGained(FocusEvent fe) {
			repaint();
		}

		public void focusLost(FocusEvent fe) {
			repaint();
			setArmed(false);
			setMousePressed(false);
			if(isToggleStyle())
				setSelected(isToggled());
			else
				setSelected(false);
		}

		public void keyPressed(KeyEvent ke) {
			if (ke.character == ' ' || ke.character == '\r') {
				setArmed(true);
				setSelected(true);
			}
		}

		public void keyReleased(KeyEvent ke) {
			if (ke.character == ' ' || ke.character == '\r') {
				if(isArmed()){
					if(isToggleStyle())
						setToggled(!isToggled());
					else
						setSelected(false);
					fireActionPerformed(0);
				}
				setArmed(false);
			}

		}

		public void mouseDoubleClicked(MouseEvent me) {}

		public void mouseDragged(MouseEvent me) {
			if (isArmed()) {
				if(!containsPoint(me.getLocation())){
					setArmed(false);
					if(!isToggled())
						setSelected(false);
				}
			}else if(containsPoint(me.getLocation())){
				setArmed(true);
				setSelected(true);
			}
		}

		public void mousePressed(MouseEvent me) {
			if (me.button != 1)
				return;
			mouseState = me.getState();
			requestFocus();
			setArmed(true);
			setMousePressed(true);
			setSelected(true);
			me.consume();
		}

		public void mouseReleased(MouseEvent me) {
			if (me.button != 1)
				return;
			if(isArmed()){
				if(isToggleStyle()){
					setToggled(!isToggled());
				}
				else
					setSelected(false);
				fireActionPerformed(mouseState);
			}
			setArmed(false);
			setMousePressed(false);

			me.consume();
		}
		
		@Override
		public void mouseEntered(MouseEvent me) {
				Color backColor = label.getBackgroundColor();
				RGB darkColor = GraphicsUtil.mixColors(backColor.getRGB(),
						new RGB(255, 255, 255), 0.5);
				label.setBackgroundColor(CustomMediaFactory.getInstance()
						.getColor(darkColor));
		}

		@Override
		public void mouseExited(MouseEvent me) {
				label.setBackgroundColor(getBackgroundColor());
			
		}

	}







}
