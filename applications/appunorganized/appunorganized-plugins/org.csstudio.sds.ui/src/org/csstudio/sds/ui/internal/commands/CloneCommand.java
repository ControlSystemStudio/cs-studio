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

import java.util.LinkedList;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.GuideModel;
import org.csstudio.sds.ui.internal.actions.WidgetModelTransfer;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

/**
 * A Command to clone the selected widgets.
 * @author Kai Meyer
 *
 */
public final class CloneCommand extends Command {
    /**
     * The list of {@link AbstractWidgetModel}.
     */
    private List<AbstractWidgetModel> _models;
    /**
     * The list of cloned {@link AbstractWidgetModel}.
     */
    private List<AbstractWidgetModel> _clonedWidgets;
    /**
     * The parent for the AbstractWidgetModels.
     */
    private DisplayModel _parent;
    /**
     * The horizontal Guide.
     */
    private GuideModel _hGuide;
    /**
     * The vertical Guide.
     */
    private GuideModel _vGuide;
    /**
     * The horizontal alignment.
     */
    private int _hAlignment;
    /**
     * The vertical alignment.
     */
    private int _vAlignment;
    /**
     * The difference between the original location and the new location.
     */
    private Dimension _difference;
    /**
     * The internal {@link CompoundCommand}.
     */
    private CompoundCommand _compoundCommand;

    /**
     * Constructor.
     * @param parent
     *             The parent {@link DisplayModel} for the widgets
     */
    public CloneCommand(final DisplayModel parent) {
        super("Clone Widget");
        _models = new LinkedList<AbstractWidgetModel>();
        _parent = parent;
    }

    /**
     * Adds the given {@link AbstractWidgetModel} with the given {@link Rectangle} to this Command.
     * @param model
     *             The AbstractWidgetModel
     * @param newBounds
     *             The new bounds for the AbstractWidgetModel
     */
    public void addPart(final AbstractWidgetModel model, final Rectangle newBounds) {
        _models.add(model);
        _difference = this.calculateDifference(model, newBounds);
    }

    /**
     * Calculates the difference between the original location of the widget and the new location.
     * @param model
     *             The {@link AbstractWidgetModel}
     * @param newBounds
     *             The new bounds for the widget
     * @return Dimension
     *             The difference between the original location of the widget and the new location
     */
    private Dimension calculateDifference(final AbstractWidgetModel model, final Rectangle newBounds) {
        Dimension dim = new Dimension(newBounds.x-model.getX(), newBounds.y-model.getY());
        return dim;
    }

    /**
     * Sets the given {@link GuideModel} for the given orientation.
     * @param guide
     *             The guide
     * @param alignment
     *             The alignment for the guide
     * @param isHorizontal
     *             The orientation of the guide
     */
    public void setGuide(final GuideModel guide, final int alignment, final boolean isHorizontal) {
        if (isHorizontal) {
            _hGuide = guide;
            _hAlignment = alignment;
        } else {
            _vGuide = guide;
            _vAlignment = alignment;
        }
    }

    /**
     * Returns a list with widget models that are currently stored on the
     * clipboard.
     *
     * @return a list with widget models or an empty list
     */
    @SuppressWarnings("unchecked")
    private List<AbstractWidgetModel> getWidgetsFromClipboard() {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        List<AbstractWidgetModel> result = (List<AbstractWidgetModel>) clipboard
                .getContents(WidgetModelTransfer.getInstance());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        clipboard.setContents(new Object[] { _models },
                new Transfer[] { WidgetModelTransfer.getInstance() });

        _clonedWidgets = getWidgetsFromClipboard();

        _compoundCommand = new CompoundCommand();

        for (AbstractWidgetModel widgetModel : _clonedWidgets) {
            if (_difference!=null) {
                widgetModel.setX(widgetModel.getX()+_difference.width);
                widgetModel.setY(widgetModel.getY()+_difference.height);
            } else {
                widgetModel.setX(widgetModel.getX()+10);
                widgetModel.setY(widgetModel.getY()+10);
            }
            _compoundCommand.add(new AddWidgetCommand(_parent, widgetModel));

            if (_hGuide != null) {
                ChangeGuideCommand hGuideCommand = new ChangeGuideCommand(widgetModel, true);
                hGuideCommand.setNewGuide(_hGuide, _hAlignment);
                _compoundCommand.add(hGuideCommand);
            }
            if (_vGuide != null) {
                ChangeGuideCommand vGuideCommand = new ChangeGuideCommand(widgetModel, false);
                vGuideCommand.setNewGuide(_vGuide, _vAlignment);
                _compoundCommand.add(vGuideCommand);
            }
        }
        _compoundCommand.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redo() {
        _compoundCommand.redo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _compoundCommand.undo();
    }

}
