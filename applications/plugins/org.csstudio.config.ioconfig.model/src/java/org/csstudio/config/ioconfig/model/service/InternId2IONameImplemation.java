/*
* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.model.service;

import java.util.Collection;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.INode;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 27.07.2010
 */
public class InternId2IONameImplemation implements IInternId2IONameService {

    private static final Logger LOG = LoggerFactory
            .getLogger(InternId2IONameImplemation.class);

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public String getIOName(@Nonnull final String internId) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     */
    @Override
    @CheckForNull
    public INode getNode(@Nonnull final String internId) throws PersistenceException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     * @throws NodeNotFoundException
     */
    @Override
    @Nonnull
    public Map<String, INode> getNodes(@Nonnull final Collection<String> internId) throws PersistenceException, NodeNotFoundException {
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IDocument getDocuments(final String internId) throws PersistenceException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getProcessVariable(final String internId) throws PersistenceException {
        return null;
    }

}
