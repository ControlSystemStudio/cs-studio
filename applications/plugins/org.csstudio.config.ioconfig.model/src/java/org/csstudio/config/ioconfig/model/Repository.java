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
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;

/**
 * @author gerke
 * @author $Author: hrickens $
 * @version $Revision: 1.5 $
 * @since 22.03.2007
 */
public final class Repository {

    private static IRepository _REPOSITORY = new HibernateRepository();
    private static List<DocumentDBO> _DOCUMENTS;

    /**
     * Default Constructor.
     */
    private Repository() {
        //Default Constructor.
    }

    /**
     * For use different Repositories can inject the {@link IRepository}.<br>
     * e.g. Dummy Repositories for tests.<br>
     * The Default {@link IReposetory} is the {@link HibernateReposetory}.
     * @param repository the repository to inject.
     */
    public static void injectIRepository(@Nonnull final IRepository repository) {
        _REPOSITORY = repository;
    }

    /**
     * Give a to a ioName the Epics Address String.
     * @param ioName the IO Name.
     * @return the Epics Address String.
     */
    @Nonnull 
    public static String getEpicsAddressString(@Nonnull final String ioName) throws PersistenceException {
        return _REPOSITORY.getEpicsAddressString(ioName);
    }

    /**
     *
     * @return a List of all IoNames at the DB.
     */
    @Nonnull
    public static List<String> getIoNames() throws PersistenceException {
        return _REPOSITORY.getIoNames();
    }

    /**
     * @param iocName the name of the Ioc.
     * @return a List of all IoNames from the Ioc with the given name.
     */
    @Nonnull 
    public static List<String> getIoNames(@Nonnull final String iocName) throws PersistenceException {
        return _REPOSITORY.getIoNames(iocName);
    }

    /**
     * @param <T>
     *            ClassTyp of the Data class
     * @param clazz
     *            The Class Typ.
     * @return All Object of the Table clazz.getName.
     */
    @Nonnull 
    public static <T> List<T> load(@Nonnull final Class<T> clazz) throws PersistenceException {
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
    @CheckForNull
    public static <T> T load(@Nonnull final Class<T> clazz, @Nonnull final Serializable id) throws PersistenceException{
        return _REPOSITORY.load(clazz, id);
    }

    /**
     *  Load all Sensors from the Id ioName.
     * @param ioName the Key IO-Name for the search Sensors.
     * @return a {@link List} of {@link SensorsDBO}
     */
    @Nonnull 
    public static List<SensorsDBO> loadSensors(@Nonnull final String ioName) throws PersistenceException {
        return _REPOSITORY.loadSensors(ioName);
    }

    /**
     *  Load the selected Sensor from the Id ioName.
     * @param ioName the Key IO-Name for the search Sensors.
     * @param selection the selection of the Sensor.
     * @return a {@link List} of {@link SensorsDBO}
     */
    @CheckForNull
    public static SensorsDBO loadSensor(@Nonnull final String ioName, @Nonnull final String selection) throws PersistenceException {
        return _REPOSITORY.loadSensor(ioName, selection);
    }

    /**
     *
     * @param forceRefresh if true load new from the DB, otherwise get the cache.
     * @return All loaded Document's from the DB.
     */
    @Nonnull 
    public static List<DocumentDBO> loadDocument(final boolean forceRefresh) throws PersistenceException {
        if(forceRefresh || _DOCUMENTS == null) {
            _DOCUMENTS = _REPOSITORY.loadDocument();
        }

        return _DOCUMENTS;
    }

    /**
     * @param gsdFile
     *            The GSD File to remove.
     */
    public static void removeGSDFiles(@Nonnull final GSDFileDBO gsdFile) throws PersistenceException {
        _REPOSITORY.removeGSDFiles(gsdFile);
    }

    /**
     * @param <T>
     *            ClassTyp of the Data class
     * @param dbClass
     *            The Class Typ.
     */
    @CheckForNull
    public static <T extends DBClass> void removeNode(@Nonnull final T dbClass) throws PersistenceException {
        _REPOSITORY.removeNode(dbClass);
    }

    /**
     * @param gsdFile
     *            the GSD File that save to DB
     * @return the Saved GSD File.
     */
    @Nonnull 
    public static GSDFileDBO save(@Nonnull final GSDFileDBO gsdFile) throws PersistenceException {
        return _REPOSITORY.save(gsdFile);
    }

    /**
     * @param document
     *            the document that save to DB
     * @return the Saved document.
     */
    @Nonnull 
    public static DocumentDBO save(@Nonnull final DocumentDBO document) throws PersistenceException {
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
    @Nonnull 
    public static <T extends DBClass> T saveOrUpdate(@Nonnull final T dbClass) throws PersistenceException {
        return _REPOSITORY.saveOrUpdate(dbClass);
    }

    /**
     * @param <T>
     *            ClassTyp of the DBClass
     * @param dbClass
     *            the Data class that update to DB
     * @return the Saved Data class.
     */
    @Nonnull 
    public static <T extends DBClass> T update(@Nonnull final T dbClass) throws PersistenceException {
        return _REPOSITORY.update(dbClass);
    }

    /**
     * @param document
     *            the document that update to DB.
     * @return the update document.
     */
    @Nonnull 
    public static DocumentDBO update(@Nonnull final DocumentDBO document) throws PersistenceException {
        return _REPOSITORY.update(document);
    }

    /**
     * Load the Channel selected by the IO Name
     * @param ioName the selection IO-Name.
     * @return The the selected Channel or null when not found!
     */
    @CheckForNull
    public static ChannelDBO loadChannel(@Nonnull final String ioName) throws PersistenceException {
        final ChannelDBO loadChannel = _REPOSITORY.loadChannel(ioName);
        return loadChannel;
    }

    /**
     * Load the short Description (max. 40 character) selected by the IO Name.
     * @param ioName the selection IO-Name.
     * @return The the short Description or null when not found!
     */
    @Nonnull 
    public static String getShortChannelDesc(@Nonnull final String ioName) throws PersistenceException {
        return _REPOSITORY.getShortChannelDesc(ioName);
    }

    @CheckForNull
    public static List<PV2IONameMatcherModelDBO> loadPV2IONameMatcher(@Nonnull final Collection<String> pvName) throws PersistenceException {
        return _REPOSITORY.loadPV2IONameMatcher(pvName);
    }


    /**
     * Close all resources that the Repository need.
     * e.g. DB Sessions
     */
    public static void close() {
        _REPOSITORY.close();
    }

    /**
     * @return
     */
    public static boolean isConnected() {
        return _REPOSITORY==null ? false : _REPOSITORY.isConnected();
    }

}
