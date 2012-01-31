/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.model.service.internal;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.PV2IONameMatcherModelDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 27.01.2012
 */
@Entity
@Table(name = "ddb_Profibus_Channel")
public class Channel4ServicesDBO extends Node4ServicesDBO {
    private static final long serialVersionUID = 1L;
    private String _ioName;
////    private Set<PV2IONameMatcherModelDBO> _pvIoNameModel;
//    private PV2IONameMatcherModelDBO _pvIoNameModel;
//
//    @CheckForNull
////    @ManyToMany(fetch = FetchType.EAGER)
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinTable(name = "dct_Records", joinColumns = @JoinColumn(name = "io_name", referencedColumnName = "ioName", unique = true), inverseJoinColumns = @JoinColumn(name = "nodes_id", referencedColumnName = "io_name"))
////    public Set<PV2IONameMatcherModelDBO> getPvIoNameModel() {
//    public PV2IONameMatcherModelDBO getPvIoNameModel() {
//        return _pvIoNameModel;
//    }
//
////    public void setPvIoNameModel(@Nonnull final Set<PV2IONameMatcherModelDBO> pvIoNameModel) {
//    public void setPvIoNameModel(@Nonnull final PV2IONameMatcherModelDBO pvIoNameModel) {
//        _pvIoNameModel = pvIoNameModel;

    @Transient
    public String getProcesVariable() {
        String epicsName ="";
        try {
            final PV2IONameMatcherModelDBO loadIOName2PVMatcher = Repository.loadIOName2PVMatcher(getIoName());
            if(loadIOName2PVMatcher!=null) {
                epicsName = loadIOName2PVMatcher.getEpicsName();
            }
        } catch (final PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return epicsName;
    }

//    @CheckForNull
//    @Transient
//    public String getProcesVariable() {
//        String epicsName= null;
//        final PV2IONameMatcherModelDBO pvIoNameModel = getPvIoNameModel();
//        if(pvIoNameModel!=null) {
//            epicsName = pvIoNameModel.getEpicsName();
//        }
//        return epicsName;
//    }

//    @CheckForNull
//    @Transient
//    public String getProcesVariable() {
//        String epicsName = null;
//        final Set<PV2IONameMatcherModelDBO> pvIoNameModel = getPvIoNameModel();
//        if(pvIoNameModel!=null) {
//            if(pvIoNameModel.size()>1) {
//                throw new IllegalArgumentException("More then one records found");
//            }
//            epicsName = pvIoNameModel.iterator().next().getEpicsName();
//        }
//        return epicsName;
//    }
//
    @CheckForNull
    public String getIoName() {
        return _ioName;
    }

    public void setIoName(@Nonnull final String ioName) {
        _ioName = ioName;
    }


}
