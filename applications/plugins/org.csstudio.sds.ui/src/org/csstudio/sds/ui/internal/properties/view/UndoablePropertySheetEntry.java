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
 package org.csstudio.sds.ui.internal.properties.view;

import java.text.MessageFormat;
import java.util.EventObject;

import org.csstudio.sds.ui.internal.commands.SetDynamicsDescriptorCommand;
import org.csstudio.sds.ui.internal.localization.Messages;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.ForwardUndoCompoundCommand;

/**
 * <p>
 * UndoablePropertySheetEntry provides undo support for changes made to
 * IPropertySources by the {@link PropertySheetViewer}. Clients can construct a
 * {@link PropertySheetPage} and use this class as the root entry. All changes
 * made to property sources displayed on that page will be done using the
 * provided command stack.
 * <p>
 * <b>NOTE:</b> If you intend to use an IPropertySourceProvider for a
 * PropertySheetPage whose root entry is an instance of of
 * UndoablePropertySheetEntry, you should set the IPropertySourceProvider on
 * that root entry, rather than the PropertySheetPage.
 */
public final class UndoablePropertySheetEntry extends PropertySheetEntry {

    /**
     * A command stack listener.
     */
    private CommandStackListener _commandStackListener;

    /**
     * A command stack.
     */
    private CommandStack _stack;

    /**
     * Private default constructor.
     */
    private UndoablePropertySheetEntry() {
    }

    /**
     * Constructs the root entry using the given command stack.
     *
     * @param stack
     *            the command stack
     * @since 3.1
     */
    public UndoablePropertySheetEntry(final CommandStack stack) {
        setCommandStack(stack);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PropertySheetEntry createChildEntry() {
        return new UndoablePropertySheetEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (_stack != null) {
            _stack.removeCommandStackListener(_commandStackListener);
        }
        super.dispose();
    }

    /**
     * Gets the command stack.
     *
     * @return the command stack
     */
    CommandStack getCommandStack() {
        // only the root has, and is listening too, the command stack
        if (getParent() != null) {
            return ((UndoablePropertySheetEntry) getParent()).getCommandStack();
        }
        return _stack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPropertyValue() {
        CompoundCommand cc = new CompoundCommand();
        ResetValueCommand restoreCmd;

        if (getParent() == null) {
            // root does not have a default value
            return;
        }

        // Use our parent's values to reset our values.
        boolean change = false;
        Object[] objects = getParent().getValues();
        for (int i = 0; i < objects.length; i++) {
            IPropertySource source = getPropertySource(objects[i]);
            if (source.isPropertySet(getDescriptor().getId())) {
                // source.resetPropertyValue(getDescriptor()getId());
                restoreCmd = new ResetValueCommand();
                restoreCmd.setTarget(source);
                restoreCmd.setPropertyId(getDescriptor().getId());
                cc.add(restoreCmd);
                change = true;
            }
        }
        if (change) {
            getCommandStack().execute(cc);
            refreshFromRoot();
        }
    }

    /**
     * Sets the command stack.
     *
     * @param stack
     *            the command stack
     */
    void setCommandStack(final CommandStack stack) {
        this._stack = stack;
        _commandStackListener = new CommandStackListener() {
            public void commandStackChanged(final EventObject e) {
                refreshFromRoot();
            }
        };
        stack.addCommandStackListener(_commandStackListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void valueChanged(final PropertySheetEntry child) {
        valueChanged((UndoablePropertySheetEntry) child,
                new ForwardUndoCompoundCommand(MessageFormat.format(
                        Messages.SetPropertyValueCommand_label,
                        new Object[] { child.getDescriptor().getDisplayName(),
                                child.getValues().length }).trim()));
    }

    /**
     * Called, when a value is changed.
     *
     * @param child
     *            the child property sheet entry
     * @param command
     *            a compound command, which should collect the single
     *            SetValueCommands, when multiple properties are changed in one
     *            step
     */
    void valueChanged(final UndoablePropertySheetEntry child,
            final CompoundCommand command) {
        CompoundCommand cc = new CompoundCommand();
        command.add(cc);

        SetValueCommand setCommand;
        for (int i = 0; i < getValues().length; i++) {
            setCommand = new SetValueCommand(child.getDisplayName(), child
                    .getDescriptor().getId(), child.getValues()[i],
                    getPropertySource(getValues()[i]));
            cc.add(setCommand);
        }

        // inform our parent
        if (getParent() != null) {
            ((UndoablePropertySheetEntry) getParent()).valueChanged(this,
                    command);
        } else {
            // I am the root entry
            _stack.execute(command);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dynamicDescriptorChanged(final PropertySheetEntry child) {
        dynamicDescriptorChanged((UndoablePropertySheetEntry) child,
                new ForwardUndoCompoundCommand(MessageFormat.format(
                        Messages.SetDynamicsDescriptorCommand_label,
                        new Object[] { child.getDescriptor().getDisplayName(),
                                child.getValues().length }).trim()));
    }

    /**
     * Called, when a dynamics descriptor is changed.
     *
     * @param child
     *            the child property sheet entry
     * @param command
     *            a compound command, which should collect the single
     *            SetValueCommands, when multiple properties are changed in one
     *            step
     */
    void dynamicDescriptorChanged(final UndoablePropertySheetEntry child,
            final CompoundCommand command) {
        CompoundCommand cc = new CompoundCommand();
        command.add(cc);

        SetDynamicsDescriptorCommand setCommand;
        for (int i = 0; i < getValues().length; i++) {
            setCommand = new SetDynamicsDescriptorCommand(child
                    .getDisplayName(), child.getDescriptor().getId(), child
                    .getDynamicsDescriptor(i),
                    getPropertySource(getValues()[i]));
            cc.add(setCommand);
        }

        // inform our parent
        if (getParent() != null) {
            ((UndoablePropertySheetEntry) getParent()).valueChanged(this,
                    command);
        } else {
            // I am the root entry
            _stack.execute(command);
        }
    }
}
