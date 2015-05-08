/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * A command, which applies position and location changes to widget models.
 *
 * @author Sven Wende
 * @version $Revision: 1.7 $
 *
 */
public final class SetBoundsCommand extends Command {
    /**
     * Stores the new size and location of the widget.
     */
    private final Rectangle _newBounds;

    /**
     * Stores the old size and location.
     */
    private Rectangle _oldBounds;

    /**
     * The element, whose constraints are to be changed.
     */
    private final AbstractWidgetModel _widgetModel;

    private CompoundCommand chain;

    /**
     * Create a command that can resize and/or move a widget model.
     *
     * @param widgetModel
     *            the widget model to manipulate
     * @param newBounds
     *            the new size and location
     */
    public SetBoundsCommand(final AbstractWidgetModel widgetModel,
            final Rectangle newBounds) {
        assert widgetModel != null;
        assert newBounds != null;
        _widgetModel = widgetModel;
        _newBounds = newBounds.getCopy();
        setLabel("Position und Grš§e Šndern");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        if(chain==null) {
            chain = new CompoundCommand();
            chain.add(new SetPropertyCommand(_widgetModel, AbstractWidgetModel.PROP_POS_X, _newBounds.x));
            chain.add(new SetPropertyCommand(_widgetModel, AbstractWidgetModel.PROP_POS_Y, _newBounds.y));
            chain.add(new SetPropertyCommand(_widgetModel, AbstractWidgetModel.PROP_WIDTH, _newBounds.width));
            chain.add(new SetPropertyCommand(_widgetModel, AbstractWidgetModel.PROP_HEIGHT, _newBounds.height));
        }
        chain.execute();

//        // remember old bounds
//        _oldBounds = new Rectangle(new Point(_widgetModel.getX(),
//                _widgetModel.getY()), new Dimension(_widgetModel.getWidth(),
//                _widgetModel.getHeight()));
//
//        doApplyBounds(_newBounds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
//        doApplyBounds(_oldBounds);
        chain.undo();
    }

    /**
     * Applies the specified bounds to the widget model.
     * @param bounds the bounds
     */
    private void doApplyBounds(final Rectangle bounds) {
        // change element size
        _widgetModel.setSize(bounds.width, bounds.height);

        // change location
        _widgetModel.setLocation(bounds.x, bounds.y);
    }
}
