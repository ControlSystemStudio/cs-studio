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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.GuideModel;
import org.csstudio.sds.util.GuideUtil;
import org.eclipse.gef.commands.Command;

/**
 * A command to change a Guide.
 * @author Kai Meyer
 */
public final class ChangeGuideCommand extends Command {

    /**
     * The AbstzractWidgetModel.
     */
    private AbstractWidgetModel _model;
    /**
     * The old guide.
     */
    private GuideModel _oldGuide;
    /**
     * The new guide.
     */
    private GuideModel _newGuide;
    /**
     * The old alignment.
     */
    private int _oldAlign;
    /**
     * The new alignment.
     */
    private int _newAlign;
    /**
     * The orientation of the guide.
     */
    private boolean _horizontal;

    /**
     * Constructor.
     * @param model
     *             The AbstractWidgetModel
     * @param horizontalGuide
     *             The horizontal guide
     */
    public ChangeGuideCommand(final AbstractWidgetModel model, final boolean horizontalGuide) {
        super();
        _model = model;
        _horizontal = horizontalGuide;
    }

    /**
     * Changes the guide.
     * @param newGuide
     *             The new guide
     * @param newAlignment
     *             The new alignment
     */
    protected void changeGuide(final GuideModel newGuide, final int newAlignment) {
        // You need to re-attach the part even if the oldGuide and the newGuide are the same
        // because the alignment could have changed
        if (newGuide != null) {
            newGuide.attachPart(_model, newAlignment);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // Cache the old values
        _oldGuide = GuideUtil.getInstance().getGuide(_model, _horizontal);
        if (_oldGuide != null) {
            _oldAlign = _oldGuide.getAlignment(_model);
        }
        redo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redo() {
        changeGuide(_newGuide, _newAlign);
    }

    /**
     * Sets the new guide.
     * @param guide
     *             The new guide
     * @param alignment
     *             The new alignment
     */
    public void setNewGuide(final GuideModel guide, final int alignment) {
        _newGuide = guide;
        _newAlign = alignment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        changeGuide(_oldGuide, _oldAlign);
    }

}
