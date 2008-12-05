/*
 * Created on Jul 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cosylab.vdct.util;

import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

/**
 * @author ilist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DoubleClickProxyTest extends JFrame {

	public DoubleClickProxyTest() {
		DoubleClickProxy.setAwt_multiclick_time(1000);
		
		DoubleClickProxy proxy = new DoubleClickProxy(new MouseInputListener() {

			public void mouseClicked(MouseEvent e) {
				System.out.println("clicked: "+e.getClickCount());
				
			}

			public void mouseEntered(MouseEvent e) {
				
			}

			public void mouseExited(MouseEvent e) {
				
			}

			public void mousePressed(MouseEvent e) {
				System.out.println("pressed: "+e.getClickCount());
				
			}

			public void mouseReleased(MouseEvent e) {
				System.out.println("released: "+e.getClickCount());
				
			}

			public void mouseDragged(MouseEvent e) {
				
			}

			public void mouseMoved(MouseEvent e) {
				
			}
			
		});
		addMouseListener(proxy);
		addMouseMotionListener(proxy);
		
		setSize(200,200);
		setVisible(true);
		
	}

	public static void main(String[] args) {
		new DoubleClickProxyTest();
	}
}
