/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.csstudio.opibuilder.editparts.PVWidgetEditpartDelegate;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.swt.widgets.Display;

/** Singleton that makes ('subscribed') widgets blink if they have a BEAST Alarm PV and require acknowledgement.
 *
 * @author Boris Versic
 */
public enum WidgetBlinker {
    INSTANCE;

    private final boolean enabled = PreferencesHelper.isOpiBeastAlarmsEnabled();
    private final int period = PreferencesHelper.getOpiBeastAlarmsBlinkPeriod();
    private final Set<PVWidgetEditpartDelegate> widgets = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
    private final Display display = Display.getDefault();
    private boolean active = false;
    private int beastBlinkState = 0;

    void blink(boolean firstBlink)
    {
        if (!enabled) return;

        int delay;
        if (firstBlink == false)
            delay = period;
        else {
            /* Requirement is to begin blinking "on the nearest second"; subsequent blinks are with a fixed period, not aligned to
             * expected time points (e.g. for 2Hz: starts at 1.0sec then every 500ms -- instead of calculating and trying to hit the right
             * times: 1.0s, 1.5s, 2.0s, 2.5s..).
             * If the next full second is very close (<100ms), the blinking will be scheduled to begin on the next full second.
             */
            delay = (int)(1000 - (java.lang.System.currentTimeMillis() % 1000));
            if (delay < 100) delay += 1000;

            // begin with default color
            // -> the runnable first toggles the value, so we set it to 1 (severity color) for the first blink
            beastBlinkState = 1;
        }

        display.timerExec(delay, ()->
        {
            if (!active || display.isDisposed()) return;

            beastBlinkState ^= 1;
            synchronized (widgets) {
                widgets.forEach((pvw) -> display.asyncExec(() -> pvw.performBeastBlink(beastBlinkState)));
            }
            blink(false);
        });
    }

    /** Add a widget's PV editpart delegate to the WidgetBlinker.
     * Important: the Beast Datasource listener is configured to be notified on the display (UI) thread;
     * WidgetBlinker expects this -> there are no checks to make sure the pvw.performBeastBlink() will be
     * called on the `correct` thread.
     *
     * @param pvw PVWidgetEditpartDelegate to add to the list of widgets for blinking
     */
    public void add(PVWidgetEditpartDelegate pvw)
    {
        if (!enabled) return; // ignore `subscribers` if BEAST Alarms in OPIs functionality is disabled

        // sync is not needed for the single add(), but we don't want to begin blinking
        // multiple times if two add's are called concurrently
        synchronized(widgets) {
            widgets.add(pvw);
            if (!active)
            {
                active = true;
                blink(true);
            }
        }
    }

    /** Remove given PV editpart delegate from the WidgetBlinker (if it exists in the blink subscribers list).
     *
     * @param pvw PVWidgetEditpartDelegate to remove from the list of widgets for blinking
     */
    public void remove(PVWidgetEditpartDelegate pvw)
    {
        synchronized (widgets) {
            widgets.remove(pvw);
            active = !widgets.isEmpty();
        };
    }

    /** Check whether the given PV editpart delegate exists in the `blink subscribers` list.
     *
     * @param pvw PVWidgetEditpartDelegate to check for existence in the list of widgets for blinking
     * @return <code>true</code> if WidgetBlinker's blink list contains pvw, <code>false</code> otherwise.
     */
    public boolean isBlinking(PVWidgetEditpartDelegate pvw) {
        return widgets.contains(pvw);
    }
}
