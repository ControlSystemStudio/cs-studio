/*
 *  RepeatingButton.java  - A push button that repeats selection event based on timer.
 *  Author: Sergey Prigogin
 *  swtcalendar.sourceforge.net
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in the
 *  Software without restriction, including without limitation the rights to use, copy,
 *  modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the
 *  following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL SIMON TATHAM BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.vafada.swtcalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import java.util.ArrayList;

/**
 * Push button that repeats selection event based on timer.
 */
public class RepeatingButton extends Button {
    public static final int DEFAULT_INITIAL_REPEAT_DELAY = 200; // Milliseconds
    public static final int DEFAULT_REPEAT_DELAY = 50;          // Milliseconds
    private int initialRepeatDelay = DEFAULT_INITIAL_REPEAT_DELAY;
    private int repeatDelay = DEFAULT_REPEAT_DELAY;
    private ArrayList<SelectionListener> selectionListeners =
        new ArrayList<SelectionListener>(3);
    private Repeater repeater;

    /**
     * @param parent Parent container.
     * @param style  Button style.
     */
    public RepeatingButton(Composite parent, int style) {
        super(parent, style);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent event) {
                cancelRepeater();

                if (event.button == 1) { // Left click
                    buttonPressed(event.stateMask, event.time);

                    repeater = new Repeater(event.stateMask);
                    getDisplay().timerExec(initialRepeatDelay, repeater);
                }
            }

            @Override
            public void mouseUp(MouseEvent event) {
                if (event.button == 1) { // Left click
                    cancelRepeater();
                }
            }
        });

        addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseExit(MouseEvent e) {
                cancelRepeater();
            }
        });
    }

    @Override
    public void addSelectionListener(SelectionListener listener) {
        selectionListeners.add(listener);
    }

    @Override
    public void removeSelectionListener(SelectionListener listener) {
        selectionListeners.remove(listener);
    }

    /**
     * @return Returns the initial repeat delay in milliseconds.
     */
    public int getInitialRepeatDelay() {
        return initialRepeatDelay;
    }

    /**
     * @param initialRepeatDelay The new initial repeat delay in milliseconds.
     */
    public void setInitialRepeatDelay(int initialRepeatDelay) {
        this.initialRepeatDelay = initialRepeatDelay;
    }

    /**
     * @return Returns the repeat delay in millisecons.
     */
    public int getRepeatDelay() {
        return repeatDelay;
    }

    /**
     * @param repeatDelay The new repeat delay in milliseconds.
     */
    public void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    private void buttonPressed(int stateMask, int time) {
        SelectionListener[] listeners = new SelectionListener[selectionListeners.size()];
        selectionListeners.toArray(listeners);
        for (int i = 0; i < listeners.length; i++) {
            SelectionListener l = listeners[i];
            Event event = new Event();
            event.type = SWT.Selection;
            event.display = getDisplay();
            event.widget = this;
            event.stateMask = stateMask;
            event.time = time;
            l.widgetSelected(new SelectionEvent(event));
        }
    }

    private void cancelRepeater() {
        if (repeater != null) {
            repeater.cancel();
            repeater = null;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Widget#checkSubclass()
     */
    @Override
    protected void checkSubclass()
    {
        /* NOP */
    }


    private class Repeater implements Runnable {
        private boolean canceled;
        private int stateMask;

        public Repeater(int stateMask) {
            super();
            this.stateMask = stateMask;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            if (!canceled) {
                buttonPressed(stateMask, (int) System.currentTimeMillis());

                getDisplay().timerExec(repeatDelay, this);
            }
        }

        public void cancel() {
            canceled = true;
        }
    }
}