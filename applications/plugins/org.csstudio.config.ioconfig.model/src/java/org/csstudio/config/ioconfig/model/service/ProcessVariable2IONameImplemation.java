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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.INode;
import org.csstudio.config.ioconfig.model.PV2IONameMatcherModelDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 27.07.2010
 */
public class ProcessVariable2IONameImplemation implements IProcessVariable2IONameService {

    private static final Logger LOG = LoggerFactory
            .getLogger(ProcessVariable2IONameImplemation.class);

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     */
    @Override
    @CheckForNull
    public String getIOName(@Nonnull final String pvName) throws PersistenceException {
        final ArrayList<String> pvNames = new ArrayList<String>();
        pvNames.add(pvName);
        final List<PV2IONameMatcherModelDBO> pv2ioNameMatchers = getPV2IONameMatchers(pvNames);
        String ioName = null;
        if(pv2ioNameMatchers!=null&&pv2ioNameMatchers.size()>0) {
            final PV2IONameMatcherModelDBO pv2ioNameMatcherModelDBO = pv2ioNameMatchers.get(0);
            if(pv2ioNameMatcherModelDBO!=null) {
                ioName = pv2ioNameMatcherModelDBO.getIoName();
            }
        }
        return ioName;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     */
    @Override
    @CheckForNull
    public INode getNode(@Nonnull final String pvName) throws PersistenceException {
        final ArrayList<String> list = new ArrayList<String>();
        list.add(pvName);
        final List<PV2IONameMatcherModelDBO> pv2ioNameMatchers = getPV2IONameMatchers(list);
        if(pv2ioNameMatchers!=null) {
            for (final PV2IONameMatcherModelDBO pv2ioNameMatcher : pv2ioNameMatchers) {
                ChannelDBO value;
                try {
                    value = Repository.loadChannel(pv2ioNameMatcher.getIoName());
                    return value;
                } catch (final PersistenceException e) {
                    LOG.error("Can't load Channel", e);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     * @throws NodeNotFoundException
     */
    @Override
    @Nonnull
    public Map<String, INode> getNodes(@Nonnull final Collection<String> pvName) throws PersistenceException, NodeNotFoundException {
        final Map<String, INode> nodes = new HashMap<String, INode>();
        final List<PV2IONameMatcherModelDBO> pv2ioNameMatchers = getPV2IONameMatchers(pvName);
        if (pv2ioNameMatchers != null && !pv2ioNameMatchers.isEmpty()) {
            for (final PV2IONameMatcherModelDBO pv2ioNameMatcher : pv2ioNameMatchers) {
                ChannelDBO value;
                value = Repository.loadChannel(pv2ioNameMatcher.getIoName());
                if(value != null) {
                    nodes.put(pv2ioNameMatcher.getEpicsName(), value);
                } else {
                    throw new NodeNotFoundException(NodeNotFoundException.State.DeviceDB);
                }
            }
        } else {
            throw new NodeNotFoundException(NodeNotFoundException.State.DCT);
        }
        return nodes;
    }

    @CheckForNull
    private List<PV2IONameMatcherModelDBO>
            getPV2IONameMatchers(@Nonnull final Collection<String> pvName) throws PersistenceException {
        List<PV2IONameMatcherModelDBO> matchers;
        matchers = Repository.loadPV2IONameMatcher(pvName);
        return matchers;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     */
    @Override
    @Nonnull
    public Map<String,String> getPVNames(@Nonnull final Collection<String> ioNames) throws PersistenceException {
        final HashMap<String, String> pvNames = new HashMap<String, String>();
        final List<PV2IONameMatcherModelDBO> loadIOName2PVMatcher = Repository.loadIOName2PVMatcher(ioNames);
        if(loadIOName2PVMatcher!=null) {
            for (final PV2IONameMatcherModelDBO pv2ioNameMatcherModelDBO : loadIOName2PVMatcher) {
                pvNames.put(pv2ioNameMatcherModelDBO.getIoName(), pv2ioNameMatcherModelDBO.getEpicsName());
            }
        }
        return pvNames;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     */
    @Override
    @CheckForNull
    public String getPVName(@Nonnull final String ioName) throws PersistenceException {
        final ArrayList<String> ioNames = new ArrayList<String>();
        ioNames.add(ioName);
        final Map<String, String> pvNames = getPVNames(ioNames);
        return pvNames.get(ioName);
    }

}
