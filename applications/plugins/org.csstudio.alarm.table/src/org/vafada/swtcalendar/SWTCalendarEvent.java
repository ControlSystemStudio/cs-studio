/*
 *  SWTCalendarEvent.java  - The event created when the user changes the date.
 *  Mark Bryan Yu
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

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Event;

import java.util.Calendar;

public class SWTCalendarEvent extends TypedEvent {
    static final long serialVersionUID = -4525931268845275613L;

    public SWTCalendarEvent(Event event) {
        super(event);
    }

    public Calendar getCalendar() {
        return (Calendar) this.data;
    }
}
