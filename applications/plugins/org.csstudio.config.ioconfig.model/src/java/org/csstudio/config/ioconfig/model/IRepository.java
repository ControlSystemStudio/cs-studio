package org.csstudio.config.ioconfig.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;

/**
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.5 $
 * @since 30.04.2009
 */
public interface IRepository {

    /**
     * @param <T>
     *            ClassTyp of the DBClass
     * @param dbClass
     *            the Data class that save or update.
     * @return the Saved Data class.
     * @throws PersistenceException
     */
    <T extends DBClass> T saveOrUpdate(final T dbClass) throws PersistenceException;

    /**
     * @param <T>
     *            ClassTyp of the DBClass
     * @param dbClass
     *            the Data class that update to DB
     * @return the Saved Data class.
     */
    <T extends DBClass> T update(final T dbClass);

    /**
     * @param <T>
     *            ClassTyp of the Data class
     * @param clazz
     *            The Class Typ.
     * @return All Object of the Table clazz.getName.
     */
    <T> List<T> load(final Class<T> clazz);

    /**
     *
     * @param <T>
     *            ClassTyp of the Data class
     * @param clazz
     *            The Class Typ.
     * @param id
     *            The DB Id of the object.
     * @return The Object of the Table clazz.getName with the given id.
     */
    <T> T load(final Class<T> clazz, Serializable id);

    /**
     * @param <T>
     *            ClassTyp of the Data class
     * @param dbClass
     *            The Class Typ.
     */
    <T extends DBClass> void removeNode(final T dbClass);

    /**
     * @param gsdFile
     *            the GSD File that save to DB
     * @return the Saved GSD File.
     */
    GSDFileDBO save(final GSDFileDBO gsdFile);

    /**
     * @param gsdFile
     *            The GSD File to remove.
     */
    void removeGSDFiles(final GSDFileDBO gsdFile);

    /**
     * @return All loaded Document's from the DB.
     */
    List<DocumentDBO> loadDocument();

    /**
     * @param document
     *            the document that save to DB
     * @return the Saved document.
     */
    DocumentDBO save(final DocumentDBO document);

    /**
     * @param document
     *            the document that update to DB.
     * @return the update document.
     */
    DocumentDBO update(final DocumentDBO document);

    /**
     * Give a to a ioName the Epics Address String.
     * @param ioName the IO Name.
     * @return the Epics Address String.
     */
    String getEpicsAddressString(final String ioName);

    /**
     *
     * @return a List of all IoNames at the DB.
     */
    List<String> getIoNames();

    /**
     * @param iocName the name of the Ioc.
     * @return a List of all IoNames from the Ioc with the given name.
     */
    List<String> getIoNames(String iocName);

    /**
     *  Load all Sensors from the Id ioName.
     * @param ioName the Key IO-Name for the search Sensors.
     * @return a {@link List} of {@link SensorsDBO}
     */
    List<SensorsDBO> loadSensors(String ioName);

    /**
     *  Load the selected Sensor from the Id ioName.
     * @param ioName the Key IO-Name for the search Sensors.
     * @param selection the selection of the Sensor.
     * @return a {@link List} of {@link SensorsDBO}
     */
    SensorsDBO loadSensor(String ioName, String selection);

    /**
     * Load the short Description (max. 40 character) selected by the IO Name.
     * @param ioName the selection IO-Name.
     * @return The the short Description or null when not found!
     */
    String getShortChannelDesc(String ioName);

    /**
     * Load the Channel selected by the IO Name
     * @param ioName the selection IO-Name.
     * @return The the selected Channel or null when not found!
     */
    ChannelDBO loadChannel(String ioName);

    /**
     * @param pvName
     * @return
     */
    List<PV2IONameMatcherModelDBO> loadPV2IONameMatcher(Collection<String> pvName);

    /**
     * Close all resources that the Repository need.
     * e.g. DB Sessions
     */
    void close();


}
