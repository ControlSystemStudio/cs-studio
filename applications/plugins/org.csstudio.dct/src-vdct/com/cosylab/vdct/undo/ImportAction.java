/*
 * Copyright (c) 2004 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */
package com.cosylab.vdct.undo;

import java.util.HashMap;
import java.util.Iterator;

import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.graphics.objects.VisibleObject;

/**
 * <code>ImportAction</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ImportAction extends ActionObject {


    private HashMap importedObjects;
    private Group parent;

    /**
     * TODO DOCUMENT ME!
     * @param object
     */
    public ImportAction(HashMap importedVisibleObjects, Group parent) {
        this.importedObjects = importedVisibleObjects;
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.undo.ActionObject#redoAction()
     */
    protected void redoAction() {
        Iterator it = importedObjects.keySet().iterator();
        while(it.hasNext()) {
            String key = (String) it.next();
            parent.addSubObject(key, (VisibleObject) importedObjects.get(key), false);
        }

    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.undo.ActionObject#undoAction()
     */
    protected void undoAction() {
        Iterator it = importedObjects.keySet().iterator();
        while(it.hasNext()) {
            parent.removeObject((String) it.next());
        }
        parent.forceValidation();
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.undo.ActionObject#getDescription()
     */
    public String getDescription() {
        StringBuffer bf = new StringBuffer();
        Iterator it = importedObjects.keySet().iterator();
        while(it.hasNext()) {
            bf.append(it.next());
            bf.append(" ");
        }
        return "Imported objects: " + bf.toString();
    }
}
