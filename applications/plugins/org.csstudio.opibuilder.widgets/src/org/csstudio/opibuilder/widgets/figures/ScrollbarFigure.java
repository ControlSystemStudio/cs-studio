package org.csstudio.opibuilder.widgets.figures;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.ScrollBar;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

public class ScrollbarFigure extends ScrollBar {


	public ScrollbarFigure() {
		 
		setRequestFocusEnabled(true);
		setFocusTraversable(true);		
		addKeyListener(new KeyListener() {
				
				public void keyReleased(KeyEvent ke) {				
				}
				
				public void keyPressed(KeyEvent ke) {
					if((ke.keycode == SWT.ARROW_UP && !isHorizontal()) || 
							(ke.keycode == SWT.ARROW_LEFT && isHorizontal()))
						stepUp();
					else if((ke.keycode == SWT.ARROW_DOWN && !isHorizontal()) || 
							(ke.keycode == SWT.ARROW_RIGHT && isHorizontal()))
						stepDown();
					else if((ke.keycode == SWT.PAGE_DOWN && !isHorizontal())||
							(ke.keycode == SWT.PAGE_UP && isHorizontal()))
						pageDown();
					else if((ke.keycode == SWT.PAGE_UP && !isHorizontal())||
							(ke.keycode == SWT.PAGE_DOWN && isHorizontal()))						
						pageUp();
				}
			});
			
		addFocusListener(new FocusListener() {
				
				public void focusLost(FocusEvent fe) {
					repaint();
				}
				
				public void focusGained(FocusEvent fe) {
					repaint();
				}
		});
		
		
	}
	
	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		if(hasFocus()){
			graphics.setForegroundColor(ColorConstants.black);
			graphics.setBackgroundColor(ColorConstants.white);

			Rectangle area = getClientArea();					
			graphics.drawFocus(area.x, area.y, area.width-1, area.height-1);
		}
	}
	
	@Override
	public void setThumb(IFigure figure) {		
		figure.addMouseListener(new MouseListener.Stub(){
			@Override
			public void mousePressed(MouseEvent me) {
				if(!hasFocus())
					requestFocus();
			}
		});
		super.setThumb(figure);
	}
	
	@Override
	public void setPageDown(Clickable down) {
		hookFocusListener(down);
		super.setPageDown(down);
	}
	
	@Override
	public void setPageUp(Clickable up) {
		hookFocusListener(up);
		super.setPageUp(up);
	}

	/**
	 * @param up
	 */
	private void hookFocusListener(Clickable up) {
		up.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				if(!hasFocus())
					requestFocus();				
			}
		});
	}
	@Override
	public void setUpClickable(Clickable up) {
		hookFocusListener(up);
		super.setUpClickable(up);
	}
	
	@Override
	public void setDownClickable(Clickable down) {
		hookFocusListener(down);
		super.setDownClickable(down);
	}
	
	private void pageDown() {
		setValue(getValue() + getPageIncrement()); 
	}

	private void pageUp() {
		setValue(getValue() - getPageIncrement());
	}
	
	//synchronize this method to avoid the race condition which
	//could be caused from manual operation and inner update from new PV value.
	@Override
	public synchronized void setValue(int v) {
		super.setValue(v);
	}
}
