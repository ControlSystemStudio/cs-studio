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
package org.csstudio.opibuilder.commands;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.csstudio.opibuilder.model.RulerModel;
import org.eclipse.gef.commands.Command;

/**
 * A Command to delete a guide.
 * @author Kai Meyer (original author), Xihui Chen (since import from SDS 2009/9)
 */
public final class DeleteGuideCommand extends Command {

    /**
     * The RulerModel.
     */
    private RulerModel parent;
    /**
     * The GuideModel.
     */
    private GuideModel guide;
    /**
     * A Map of {@link AbstractWidgetModel} and Integers.
     */
    private Map<AbstractWidgetModel, Integer> oldParts;

    /**
     * Constructor.
     * @param guide
     *             The GuideModel
     * @param parent
     *             The RulerModel
     */
    public DeleteGuideCommand(final GuideModel guide, final RulerModel parent) {
        this.guide = guide;
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        oldParts = new HashMap<AbstractWidgetModel, Integer>(guide.getMap());
        Iterator<AbstractWidgetModel> iter = oldParts.keySet().iterator();
        while (iter.hasNext()) {
            guide.detachPart(iter.next());
        }
        parent.removeGuide(guide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        parent.addGuide(guide);
        Iterator<AbstractWidgetModel> iter = oldParts.keySet().iterator();
        while (iter.hasNext()) {
            AbstractWidgetModel model = iter.next();
            guide.attachPart(model, ((Integer)oldParts.get(model)).intValue());
        }
    }
}
