package org.csstudio.config.ioconfig.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModule;

public class DummyRepository implements IReposetory {

    private int id = 1;

    public String getEpicsAddressString(final String ioName) {
        return null;
    }

    public List<String> getIoNames() {
        return null;
    }

    public List<String> getIoNames(final String iocName) {
        return null;
    }

    public <T> List<T> load(final Class<T> clazz) {
        return null;
    }

    public List<Document> loadDocument() {
        return null;
    }

    public void removeGSDFiles(final GSDFile gsdFile) {

    }

    public <T extends DBClass> void removeNode(final T dbClass) {

    }

    public GSDFile save(final GSDFile gsdFile) {
        return null;
    }

    public <T extends DBClass> T saveOrUpdate(final T dbClass) throws PersistenceException {
        dbClass.setId(id++);
        return null;
    }

    public GSDModule saveWithChildren(final GSDModule gsdModule) throws PersistenceException {
        return null;
    }

    public <T extends DBClass> T update(final T dbClass) {
        return null;
    }

    public Document update(final Document document) {
        return null;
    }

    public <T> T load(final Class<T> clazz, final Serializable id) {
        return null;
    }

    public List<Sensors> loadSensors(final String ioName) {
        return null;
    }

    public Sensors loadSensor(final String ioName, final String selection) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public boolean serach(final Class class1, final int id) {
        return false;
    }

    public List<Integer> getRootPath(final int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document save(final Document document) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getShortChannelDesc(final String ioName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Channel loadChannel(final String ioName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PV2IONameMatcherModel> loadPV2IONameMatcher(final Collection<String> pvName) {
        // TODO Auto-generated method stub
        return null;
    }

}
