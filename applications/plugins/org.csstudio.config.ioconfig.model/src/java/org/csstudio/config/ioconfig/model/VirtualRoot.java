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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Virtual Root
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 11.05.2011
 */

public final class VirtualRoot extends AbstractNodeSharedImpl<VirtualRoot, FacilityDBO> {

    private static final long serialVersionUID = -296484498706601307L;

    private static final VirtualRoot INSTANCE = new VirtualRoot();
    /**
     * Constructor.
     */
    private VirtualRoot() {
        // Don't instantiate
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public VirtualRoot getParent() {
        throw new UnsupportedOperationException(VirtualRoot.class + " does not feature a parent node.");
    }

    /**
     * have no nodeType
     */
    @Override
    @CheckForNull
    public NodeType getNodeType() {
        return null;
    }

    @Override
    @CheckForNull
    protected VirtualRoot copyParameter(@Nullable final VirtualRoot parent) throws PersistenceException {
        return VirtualRoot.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public FacilityDBO createChild() throws PersistenceException {
        return new FacilityDBO();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(@Nonnull final INodeVisitor visitor) {
        visitor.visit(this);
    }
}
