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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 07.09.2011
 */
public class EpicsDBNode extends DocumentRangeNode implements ITypedElement {

    private final String _name;
    private final int _typeCode;

    /**
     * Constructor.
     * @param parent
     * @param typeCode
     * @param id
     * @param document
     * @param start
     * @param length
     */
    public EpicsDBNode(@Nullable final DocumentRangeNode parent,
                       final int typeCode,
                       @Nonnull final String id,
                       @Nonnull final IDocument document,
                       final int start,
                       final int length, @Nonnull final String name) {
        super(parent, typeCode, id, document, start, length);
        _typeCode = typeCode;
        _name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public Image getImage() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getType() {
        return ""+_typeCode;
    }

}
