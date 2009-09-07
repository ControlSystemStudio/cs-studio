/*
 * Created on Jul 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cosylab.vdct.util;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import com.cosylab.vdct.Settings;


/**
 * @author ilist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DoubleClickProxy implements MouseInputListener {
    static int awt_multiclick_time = 200;
    static int awt_multiclick_smudge = 4;
    
    MouseInputListener listener;
    int clickCount = 1;
    Object lastPeer = null;
    long lastTime = 0;
    int lastx = 0;
    int lasty = 0;
    int lastButton = 0;

    public DoubleClickProxy(MouseInputListener listener) {
        this.listener = listener;
        update();
    }

    public static void update() {
        awt_multiclick_time = Settings.getInstance().getDoubleClickSpeed();
        awt_multiclick_smudge = Settings.getInstance().getDoubleClickSmudge();
    }


    public void mouseClicked(MouseEvent e) {
        listener.mouseClicked(new MouseEvent(e.getComponent(), e.getID(),
                e.getWhen(), e.getModifiers(), e.getX(), e.getY(), clickCount,
                e.isPopupTrigger(), e.getButton()));
    }

    public void mouseEntered(MouseEvent e) {
        clickCount = 0;
        lastTime = 0;
        lastPeer = null;

        listener.mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        clickCount = 0;
        lastTime = 0;
        lastPeer = null;

        listener.mouseExited(e);
    }

    public void mousePressed(MouseEvent e) {
        int x;
        int y;

        x = e.getX();
        y = e.getY();

        if ((lastPeer == e.getSource()) && (lastButton == e.getButton()) &&
                ((e.getWhen() - lastTime) <= awt_multiclick_time)) {
            clickCount++;
        } else {
            clickCount = 1;
            lastPeer = e.getSource();
            lastButton = e.getButton();
            lastx = x;
            lasty = y;
        }

        lastTime = e.getWhen();

        listener.mousePressed(new MouseEvent(e.getComponent(), e.getID(),
                e.getWhen(), e.getModifiers(), e.getX(), e.getY(), clickCount,
                e.isPopupTrigger(), e.getButton()));
    }

    public void mouseReleased(MouseEvent e) {
        listener.mouseReleased(new MouseEvent(e.getComponent(), e.getID(),
                e.getWhen(), e.getModifiers(), e.getX(), e.getY(), clickCount,
                e.isPopupTrigger(), e.getButton()));
    }

    public void mouseDragged(MouseEvent e) {
        int x;
        int y;
        x = e.getX();
        y = e.getY();

        /* If a motion comes in while a multi-click is pending,
         * allow a smudge factor so that moving the mouse by a small
         * amount does not wipe out the multi-click state variables.
         */
        if (!((lastPeer == e.getSource()) &&
                ((e.getWhen() - lastTime) <= awt_multiclick_time) &&
                ((Math.abs(lastx - x) < awt_multiclick_smudge) &&
                (Math.abs(lasty - y) < awt_multiclick_smudge)))) {
            clickCount = 0;
            lastTime = 0;
            lastPeer = null;
            lastx = 0;
            lasty = 0;
        }

        listener.mouseDragged(new MouseEvent(e.getComponent(), e.getID(),
                e.getWhen(), e.getModifiers(), e.getX(), e.getY(), clickCount,
                e.isPopupTrigger(), e.getButton()));
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
        int x;
        int y;
        x = e.getX();
        y = e.getY();

        /* If a motion comes in while a multi-click is pending,
         * allow a smudge factor so that moving the mouse by a small
         * amount does not wipe out the multi-click state variables.
         */
        if (!((lastPeer == e.getSource()) &&
                ((e.getWhen() - lastTime) <= awt_multiclick_time) &&
                ((Math.abs(lastx - x) < awt_multiclick_smudge) &&
                (Math.abs(lasty - y) < awt_multiclick_smudge)))) {
            clickCount = 0;
            lastTime = 0;
            lastPeer = null;
            lastx = 0;
            lasty = 0;
        }

        listener.mouseMoved(new MouseEvent(e.getComponent(), e.getID(),
                e.getWhen(), e.getModifiers(), e.getX(), e.getY(), clickCount,
                e.isPopupTrigger(), e.getButton()));
    }
	
	public static void setAwt_multiclick_smudge(int i) {
		awt_multiclick_smudge = i;
	}

	public static void setAwt_multiclick_time(int i) {
		awt_multiclick_time = i;
	}

	/**
	 * @return
	 */
	public static int getAwt_multiclick_smudge() {
		return awt_multiclick_smudge;
	}

	/**
	 * @return
	 */
	public static int getAwt_multiclick_time() {
		return awt_multiclick_time;
	}

}
