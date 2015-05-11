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

import java.util.Iterator;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.GuideModel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

/**
 * A Command to move a Guide.
 * @author Kai Meyer
 */
public final class MoveGuideCommand extends Command {
    /**
     * The distance.
     */
    private int _pDelta;
    /**
     * The guide, which position has changed.
     */
    private GuideModel _guide;

    /**
     * Constructor.
     * @param guide
     *             the guide, which position has changed
     * @param pDelta
     *             the distance
     */
    public MoveGuideCommand(final GuideModel guide, final int pDelta) {
        _pDelta = pDelta;
        _guide = guide;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        _guide.setPosition(_guide.getPosition() + _pDelta);
        Iterator<AbstractWidgetModel> iter = _guide.getAttachedModels().iterator();
        while (iter.hasNext()) {
            AbstractWidgetModel model = iter.next();
            Point location = new Point(model.getX(), model.getY());
            if (_guide.isHorizontal()) {
                location.y += _pDelta;
            } else {
                location.x += _pDelta;
            }
            model.setLocation(location.x, location.y);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _guide.setPosition(_guide.getPosition() - _pDelta);
        Iterator<AbstractWidgetModel> iter = _guide.getAttachedModels().iterator();
        while (iter.hasNext()) {
            AbstractWidgetModel model = iter.next();
            Point location = new Point(model.getX(), model.getY());
            if (_guide.isHorizontal()) {
                location.y -= _pDelta;
            } else {
                location.x -= _pDelta;
            }
            model.setLocation(location.x, location.y);
        }
    }

}
