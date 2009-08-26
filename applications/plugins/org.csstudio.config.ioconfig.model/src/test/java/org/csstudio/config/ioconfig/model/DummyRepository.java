package org.csstudio.config.ioconfig.model;

import java.io.Serializable;
import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModule;

public class DummyRepository implements IReposetory {

    private int id = 1;

    public String getEpicsAddressString(String ioName) {
        return null;
    }

    public List<String> getIoNames() {
        return null;
    }

    public List<String> getIoNames(String iocName) {
        return null;
    }

    public <T> List<T> load(Class<T> clazz) {
        return null;
    }

    public List<Document> loadDocument() {
        return null;
    }

    public void removeGSDFiles(GSDFile gsdFile) {

    }

    public <T extends DBClass> void removeNode(T dbClass) {

    }

    public GSDFile save(GSDFile gsdFile) {
        return null;
    }

    public Document save(Document document) {
        return null;
    }

    public <T extends DBClass> T saveOrUpdate(T dbClass) throws PersistenceException {
        dbClass.setId(id++);
        return null;
    }

    public GSDModule saveWithChildren(GSDModule gsdModule) throws PersistenceException {
        return null;
    }

    public <T extends DBClass> T update(T dbClass) {
        return null;
    }

    public Document update(Document document) {
        return null;
    }

    public <T> T load(Class<T> clazz, Serializable id) {
        return null;
    }

    public List<FacilityLight> loadFacilityLight() {
        return null;
    }

    public List<Sensors> loadSensors(String ioName) {
        return null;
    }

    public Sensors loadSensor(String ioName, String selection) {
        return null;
    }

    public boolean serach(Class class1, int id) {
        return false;
    }

    public List<Integer> getRootPath(int id) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
