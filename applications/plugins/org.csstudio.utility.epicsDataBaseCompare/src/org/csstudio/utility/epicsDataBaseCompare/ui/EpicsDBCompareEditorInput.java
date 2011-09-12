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
import java.util.SortedMap;

import javax.annotation.Nonnull;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;

/**
 * @author hrickens
 * @since 07.09.2011
 */
public class EpicsDBCompareEditorInput extends CompareEditorInput {

    private final EpicsDBNode _leftNode;
    private final EpicsDBNode _rightNode;

    /**
     * Constructor.
     * @param configuration
     * @param epicsDBFileRight
     * @param epicsDBFileLeft
     */
    public EpicsDBCompareEditorInput(@Nonnull final CompareConfiguration configuration,
                                     @Nonnull final EpicsDBFile epicsDBFileLeft,
                                     @Nonnull final EpicsDBFile epicsDBFileRight) {
        super(configuration);
        _leftNode = buildEpicsNode(epicsDBFileLeft);
        _rightNode = buildEpicsNode(epicsDBFileRight);
    }

    @Nonnull
    private EpicsDBNode buildEpicsNode(@Nonnull final EpicsDBFile epicsDBFile) {
        int startPos = 0;
        final String text = epicsDBFile.getSortetText();
        final Document document = new Document(text);
        final EpicsDBNode epicsDBNode = new EpicsDBNode(null,
                        1,
                        epicsDBFile.getFileName(),
                        document,
                        0,
                        text.length(),
                        epicsDBFile.getFileName());
        final SortedMap<String, EpicsRecord> records = epicsDBFile.getRecords();
        for (final EpicsRecord record : records.values()) {
            final Document recordDocument = new Document(record.getSortetText());
            final int length = recordDocument.getLength();
            final EpicsDBNode epicsRecordNode = new EpicsDBNode(epicsDBNode,
                            2,
                            record.getRecordName(),
                            recordDocument,
                            0,
                            length,
                            record.getRecordName());
            epicsDBNode.addChild(epicsRecordNode);
            startPos+=length;
            for (final Field field : record.getFilds()) {
                final Document fieldDocument = new Document(record.getSortetText());
                final EpicsDBNode epicsFieldNode = new EpicsDBNode(epicsRecordNode,
                                                                    3,
                                                                    record.getRecordName()+field.getField(),
                                                                    fieldDocument,
                                                                    0,
                                                                    fieldDocument.getLength(),
                                                                    field.getField());
                epicsRecordNode.addChild(epicsFieldNode);
            }
        }
        return epicsDBNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected Object prepareInput(@Nonnull final IProgressMonitor monitor) throws InvocationTargetException,
                                                                 InterruptedException {
        return new DiffNode(_leftNode, _rightNode);
    }
}
