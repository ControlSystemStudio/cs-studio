package com.cosylab.vdct.events.commands;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.objects.LinkSource;
import com.cosylab.vdct.graphics.objects.VisibleObject;

/**
 * Insert the class' description here.
 * Creation

/**
 * Insert the class' description here.
 * Creation date: (3.2.2001 13:28:11)
 * @author Matej Sekoranja
 */

public class LinkCommand extends com.cosylab.vdct.events.Command {
    private DrawingSurface drawingSurface;
    private LinkSource field = null;
    private VisibleObject obj = null;
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 22:43:26)
 * @param drawingSurface com.cosylab.vdct.graphics.DrawingSurface
 */
public LinkCommand(DrawingSurface drawingSurface) {
    this.drawingSurface=drawingSurface;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 22:42:23)
 */
public void execute() {
    drawingSurface.linkCommand(obj, field);
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 13:29:40)
 * @param record com.cosylab.vdct.graphics.objects.VisibleObject
 * @param field com.cosylab.vdct.graphics.objects.LinkSource
 */
public void setData(VisibleObject obj, LinkSource field) {
    this.field=field;
    this.obj=obj;
}

}
