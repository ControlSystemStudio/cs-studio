package org.csstudio.config.ioconfig.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.hibernate.IRepository;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.service.internal.Channel4ServicesDBO;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 02.08.2011
 */
public class DummyRepository implements IRepository {

    private int _id = 1;

    @Override
    public void close() {
        // Dummy
    }

    @Override
    @Nonnull
    public final String getEpicsAddressString(@Nullable final String ioName) {
        return "DummyRepository";
    }

    @Override
    @Nonnull
    public final List<String> getIoNames() {
        return new ArrayList<String>();
    }

    @Override
    @Nonnull
    public final List<String> getIoNames(@Nullable final String iocName) {
        return new ArrayList<String>();
    }

    @CheckForNull
    public final List<Integer> getRootPath(@SuppressWarnings("unused") @Nullable final int id) {
        return null;
    }

    @Override
    @Nonnull
    public final String getShortChannelDesc(@Nullable final String ioName) {
        return "DummyRepository";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isConnected() {
        return true;
    }

    @Override
    @Nonnull
    public final <T> List<T> load(@Nullable final Class<T> clazz) {
        return new ArrayList<T>();
    }

    @Override
    @CheckForNull
    public final <T> T load(@Nullable final Class<T> clazz, @Nullable final Serializable id) {
        return null;
    }

    @Override
    @CheckForNull
    public final ChannelDBO loadChannel(@Nullable final String ioName) {
        return null;
    }

    @Override
    @Nonnull
    public final List<DocumentDBO> loadDocument() {
        return new ArrayList<DocumentDBO>();
    }

    @Override
    @Nonnull
    public final List<PV2IONameMatcherModelDBO> loadPV2IONameMatcher(@Nullable final Collection<String> pvName) {
        return new ArrayList<PV2IONameMatcherModelDBO>();
    }

    @Override
    @CheckForNull
    public final SensorsDBO loadSensor(@Nullable final String ioName, @Nullable final String selection) {
        return null;
    }

    @Override
    @Nonnull
    public final List<SensorsDBO> loadSensors(@Nullable final String ioName) {
        return new ArrayList<SensorsDBO>();
    }

    @Override
    public void removeGSDFiles(@Nullable final GSDFileDBO gsdFile) {
        // Dummy
    }

    @Override
    public <T extends DBClass> void removeNode(@Nullable final T dbClass) {
        // Dummy
    }

    @Override
    @Nonnull
    public final DocumentDBO save(@Nonnull final DocumentDBO document) {
        return document;
    }

    @Override
    @Nonnull
    public final GSDFileDBO save(@Nonnull final GSDFileDBO gsdFile) {
        return gsdFile;
    }

    @Override
    @Nonnull
    public final <T extends DBClass> T saveOrUpdate(@Nonnull final T dbClass) throws PersistenceException {
        dbClass.setId(_id++);
        return dbClass;
    }

    @CheckForNull
    public final GSDModuleDBO saveWithChildren(@Nullable final GSDModuleDBO gsdModule) {
        return gsdModule;
    }

    @SuppressWarnings("unused")
    public final boolean search(@Nullable final Class<?> class1, final int id) {
        return false;
    }

    @Override
    @Nonnull
    public final DocumentDBO update(@Nonnull final DocumentDBO document) {
        return document;
    }

    @Override
    @Nonnull
    public final <T extends DBClass> T update(@Nonnull final T dbClass) {
        return dbClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public final Channel4ServicesDBO loadChannelWithInternId(@Nonnull final String internId) throws PersistenceException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public PV2IONameMatcherModelDBO loadIOName2PVMatcher(@Nonnull final String ioNames) throws PersistenceException {
        return null;
    }


}
