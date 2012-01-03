/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.epicsDataBaseCompare.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.compare.ICompareContainer;
import org.eclipse.compare.ICompareNavigator;
import org.eclipse.compare.structuremergeviewer.DiffContainer;
import org.eclipse.compare.structuremergeviewer.DiffElement;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.ICompareInputChangeListener;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.services.IServiceLocator;

/**
 * @author hrickens
 * @since 07.09.2011
 */
public final class DiffContainerBuilder {

    /**
     * Constructor.
     */
    private DiffContainerBuilder() {
        // Constructor.
    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @since 07.09.2011
     */
    private static final class NamedDiffContainer extends DiffContainer implements ICompareContainer{
        private final String _name;

        public NamedDiffContainer(@Nullable final IDiffContainer parent, @Nonnull final DiffKind kind, @Nonnull final String name) {
            super(parent, kind.getKind());
            _name = name;
        }

        @Override
        @Nonnull
        public String getName() {
            return _name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run(final boolean fork, final boolean cancelable, @Nullable final IRunnableWithProgress runnable) throws InvocationTargetException,
                                                                                         InterruptedException {
            // TODO Auto-generated method stub

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addCompareInputChangeListener(@Nullable final ICompareInput input,
                                                  @Nullable final ICompareInputChangeListener listener) {
            // TODO Auto-generated method stub

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeCompareInputChangeListener(@Nullable final ICompareInput input,
                                                     @Nullable final ICompareInputChangeListener listener) {
            // TODO Auto-generated method stub

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void registerContextMenu(@Nullable final MenuManager menu, @Nullable final ISelectionProvider selectionProvider) {
            // TODO Auto-generated method stub

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setStatusMessage(@Nullable final String message) {
            // TODO Auto-generated method stub

        }

        /**
         * {@inheritDoc}
         */
        @Override
        @CheckForNull
        public IActionBars getActionBars() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @CheckForNull
        public IServiceLocator getServiceLocator() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @CheckForNull
        public ICompareNavigator getNavigator() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void runAsynchronously(@Nullable final IRunnableWithProgress runnable) {
            // TODO Auto-generated method stub

        }

        /**
         * {@inheritDoc}
         */
        @Override
        @CheckForNull
        public IWorkbenchPart getWorkbenchPart() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @since 07.09.2011
     */
    private static final class NamedDiffElement extends DiffElement {
        private final String _name;
        /**
         * Constructor.
         * @param parent
         * @param kind
         */
        public NamedDiffElement(@Nullable final IDiffContainer parent, @Nonnull final DiffKind kind, @Nonnull final String name) {
            super(parent, kind.getKind());
            _name = name;
        }

        @Override
        @Nonnull
        public String getName() {
            return _name;
        }
    }

    @SuppressWarnings("unused")
    @Nonnull
    public static DiffContainer build(@Nonnull final EpicsDBFile epicsDBFileLeft, @Nonnull final EpicsDBFile epicsDBFileRight, final boolean left) {
        final DiffContainer rootDiffContainer = new NamedDiffContainer(null, DiffKind.OK_PLUS, epicsDBFileLeft.getFileName());
        final Collection<EpicsRecord> records = epicsDBFileLeft.getRecords().values();
        for (final EpicsRecord epicsRecord : records) {
            DiffKind recordKind = DiffKind.OK_PLUS;
            final EpicsRecord record = epicsDBFileRight.getRecord(epicsRecord.getRecordName());
            if(record==null) {
                recordKind = getDiffKinde4(left);
            }
            final DiffContainer recordDiffElement = new NamedDiffContainer(rootDiffContainer,recordKind, epicsRecord.getRecordName());
            final Collection<Field> filds = epicsRecord.getFilds();
            for (final Field field : filds) {
                DiffKind fieldKind = DiffKind.OK_PLUS;
                DiffKind valueKind = DiffKind.OK_PLUS;
                if(record!=null) {
                    final Field fieldRight = record.getField(field.getField());
                    if(fieldRight==null) {
                        fieldKind = getDiffKinde4(left);
                    } else {
                        if(!field.getValue().equals(fieldRight.getValue())) {
                            valueKind = DiffKind.RED_NONE;
                            fieldKind = DiffKind.RED_NONE;
                            recordKind= DiffKind.RED_NONE;
                        }
                    }
                }
                final DiffContainer fieldDiffElement = new NamedDiffContainer(recordDiffElement, fieldKind,  field.getField());
                new NamedDiffElement(fieldDiffElement, valueKind, field.getValue());
            }
            recordDiffElement.setKind(recordKind.getKind());
        }
        return rootDiffContainer;
    }

    /**
     * @param left
     * @return
     */
    @Nonnull
    private static DiffKind getDiffKinde4(final boolean left) {
        DiffKind recordKind;
        if(left) {
            recordKind = DiffKind.RIGHT_PLUS;
        } else {
            recordKind = DiffKind.LEFT_PLUS;

        }
        return recordKind;
    }

}
