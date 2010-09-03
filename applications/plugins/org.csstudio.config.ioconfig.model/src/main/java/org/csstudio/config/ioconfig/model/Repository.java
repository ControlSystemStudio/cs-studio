/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: Repository.java,v 1.5 2010/08/20 13:33:04 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model;


import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;

import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;

/**
 * @author gerke
 * @author $Author: hrickens $
 * @version $Revision: 1.5 $
 * @since 22.03.2007
 */
public final class Repository {

    private static IReposetory _REPOSITORY = new HibernateReposetory();
    private static List<Document> _DOCUMENTS;

    /**
     * Default Constructor.
     */
    private Repository() {}

    /**
     * For use different Repositories can inject the {@link IRepository}.<br>
     * e.g. Dummy Repositories for tests.<br>
     * The Default {@link IReposetory} is the {@link HibernateReposetory}.
     * @param repository the repository to inject.
     */
    public static void injectIRepository(final IReposetory repository) {
        _REPOSITORY = repository;
    }

    /**
     * Give a to a ioName the Epics Address String.
     * @param ioName the IO Name.
     * @return the Epics Address String.
     */
    public static String getEpicsAddressString(final String ioName) {
        return _REPOSITORY.getEpicsAddressString(ioName);
    }

    /**
     *
     * @return a List of all IoNames at the DB.
     */
    public static List<String> getIoNames() {
        return _REPOSITORY.getIoNames();
    }

    /**
     * @param iocName the name of the Ioc.
     * @return a List of all IoNames from the Ioc with the given name.
     */
    public static List<String> getIoNames(final String iocName) {
        return _REPOSITORY.getIoNames(iocName);
    }

    /**
     * @param <T>
     *            ClassTyp of the Data class
     * @param clazz
     *            The Class Typ.
     * @return All Object of the Table clazz.getName.
     */
    public static <T> List<T> load(final Class<T> clazz) {
        return _REPOSITORY.load(clazz);
    }

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
    public static <T> T load(final Class<T> clazz, final Serializable id){
        return _REPOSITORY.load(clazz, id);
    }

    /**
     *  Load all Sensors from the Id ioName.
     * @param ioName the Key IO-Name for the search Sensors.
     * @return a {@link List} of {@link Sensors}
     */
    public static List<Sensors> loadSensors(final String ioName){
        return _REPOSITORY.loadSensors(ioName);
    }

    /**
     *  Load the selected Sensor from the Id ioName.
     * @param ioName the Key IO-Name for the search Sensors.
     * @param selection the selection of the Sensor.
     * @return a {@link List} of {@link Sensors}
     */
    public static Sensors loadSensor(final String ioName, final String selection){
        return _REPOSITORY.loadSensor(ioName, selection);
    }

    /**
     *
     * @param forceRefresh if true load new from the DB, otherwise get the cache.
     * @return All loaded Document's from the DB.
     */
    public static List<Document> loadDocument(final boolean forceRefresh) {
        if(forceRefresh || (_DOCUMENTS == null)) {
            _DOCUMENTS = _REPOSITORY.loadDocument();
        }

        return _DOCUMENTS;
    }

    /**
     * @param gsdFile
     *            The GSD File to remove.
     */
    public static void removeGSDFiles(final GSDFile gsdFile) {
        _REPOSITORY.removeGSDFiles(gsdFile);
    }

    /**
     * @param <T>
     *            ClassTyp of the Data class
     * @param dbClass
     *            The Class Typ.
     */
    public static <T extends DBClass> void removeNode(final T dbClass) {
        _REPOSITORY.removeNode(dbClass);
    }

    /**
     * @param gsdFile
     *            the GSD File that save to DB
     * @return the Saved GSD File.
     */
    public static GSDFile save(final GSDFile gsdFile) {
        return _REPOSITORY.save(gsdFile);
    }

    /**
     * @param document
     *            the document that save to DB
     * @return the Saved document.
     */
    public static Document save(final Document document) {
        return _REPOSITORY.save(document);
    }

    /**
     * @param <T>
     *            ClassTyp of the DBClass
     * @param dbClass
     *            the Data class that save or update.
     * @return the Saved Data class.
     * @throws PersistenceException
     */
    public static <T extends DBClass> T saveOrUpdate(final T dbClass) throws PersistenceException {
        return _REPOSITORY.saveOrUpdate(dbClass);
    }

    /**
     * @param <T>
     *            ClassTyp of the DBClass
     * @param dbClass
     *            the Data class that update to DB
     * @return the Saved Data class.
     */
    public static <T extends DBClass> T update(final T dbClass) {
        return _REPOSITORY.update(dbClass);
    }

    /**
     * @param document
     *            the document that update to DB.
     * @return the update document.
     */
    public static Document update(final Document document) {
        return _REPOSITORY.update(document);
    }

    /**
     * Load the Channel selected by the IO Name
     * @param ioName the selection IO-Name.
     * @return The the selected Channel or null when not found!
     */
    @CheckForNull
    public static Channel loadChannel(final String ioName) {
        Channel loadChannel = _REPOSITORY.loadChannel(ioName);
        return loadChannel;
    }

    /**
     * Load the short Description (max. 40 character) selected by the IO Name.
     * @param ioName the selection IO-Name.
     * @return The the short Description or null when not found!
     */
    public static String getShortChannelDesc(final String ioName) {
        return _REPOSITORY.getShortChannelDesc(ioName);
    }

    @CheckForNull
    public static List<PV2IONameMatcherModel> loadPV2IONameMatcher(final Collection<String> pvName) {
        return _REPOSITORY.loadPV2IONameMatcher(pvName);
    }


    /**
     * Close all resources that the Repository need.
     * e.g. DB Sessions
     */
    public static void close() {
        _REPOSITORY.close();
    }

}
