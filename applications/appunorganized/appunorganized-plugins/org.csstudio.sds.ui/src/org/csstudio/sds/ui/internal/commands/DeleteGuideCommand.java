/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.ui.internal.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.GuideModel;
import org.csstudio.sds.model.RulerModel;
import org.eclipse.gef.commands.Command;

/**
 * A Command to delete a guide.
 * @author Kai Meyer
 */
public final class DeleteGuideCommand extends Command {

    /**
     * The RulerModel.
     */
    private RulerModel _parent;
    /**
     * The GuideModel.
     */
    private GuideModel _guide;
    /**
     * A Map of {@link AbstractWidgetModel} and Integers.
     */
    private Map<AbstractWidgetModel, Integer> _oldParts;

    /**
     * Constructor.
     * @param guide
     *             The GuideModel
     * @param parent
     *             The RulerModel
     */
    public DeleteGuideCommand(final GuideModel guide, final RulerModel parent) {
        _guide = guide;
        _parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        _oldParts = new HashMap<AbstractWidgetModel, Integer>(_guide.getMap());
        Iterator<AbstractWidgetModel> iter = _oldParts.keySet().iterator();
        while (iter.hasNext()) {
            _guide.detachPart(iter.next());
        }
        _parent.removeGuide(_guide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _parent.addGuide(_guide);
        Iterator<AbstractWidgetModel> iter = _oldParts.keySet().iterator();
        while (iter.hasNext()) {
            AbstractWidgetModel model = iter.next();
            _guide.attachPart(model, ((Integer)_oldParts.get(model)).intValue());
        }
    }
}
