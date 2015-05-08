package com.cosylab.vdct.events;

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

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (18.12.2000 15:26:40)
 * @author Matej Sekoranja
 */
public abstract class EventManager {
    protected Hashtable subscreiber;

/**
 * CommandManager constructor comment.
 */
protected EventManager() {
    subscreiber = new Hashtable();
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:32:53)
 */
public void clear() {
    subscreiber.clear();
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:31:58)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getSubscreiber() {
    return subscreiber;
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:12:53)
 * @param id java.lang.String
 * @param subscreiber java.lang.Object
 */
public void registerSubscreiber(String id, Object newSubscreiber) {
    if (subscreiber.containsKey(id))
        throw new IllegalArgumentException("Error: subscreiber with id '"+id+"' already exists...");
    else subscreiber.put(id, newSubscreiber);
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 16:12:53)
 * @param id java.lang.String
 * @param subscreiber java.lang.Object
 */
public void unregisterSubscreiber(String id, Object newSubscreiber) {
    subscreiber.remove(id);
}
}
