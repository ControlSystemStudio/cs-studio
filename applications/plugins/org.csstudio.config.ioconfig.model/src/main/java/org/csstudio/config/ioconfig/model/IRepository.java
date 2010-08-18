package org.csstudio.config.ioconfig.model;

import java.io.Serializable;
import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
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
    GSDFile save(final GSDFile gsdFile);

    /**
     * @param gsdFile
     *            The GSD File to remove.
     */
    void removeGSDFiles(final GSDFile gsdFile);

    /**
     * @return All loaded Document's from the DB.
     */
    List<Document> loadDocument();

    /**
     * @param document
     *            the document that save to DB
     * @return the Saved document.
     */
    Document save(final Document document);

    /**
     * @param document
     *            the document that update to DB.
     * @return the update document.
     */
    Document update(final Document document);

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

    List<Sensors> loadSensors(String ioName);

    Sensors loadSensor(String ioName, String selection);

    List<Integer> getRootPath(int id);

    String getShortChannelDesc(String ioName);

    Channel loadChannel(String ioName);

    void close();

}
