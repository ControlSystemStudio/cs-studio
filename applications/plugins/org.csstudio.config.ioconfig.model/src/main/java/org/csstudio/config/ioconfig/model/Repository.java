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
 * $Id$
 */
package org.csstudio.config.ioconfig.model;


import java.io.Serializable;
import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;

/**
 * @author gerke
 * @author $Author$
 * @version $Revision$
 * @since 22.03.2007
 */
public final class Repository {

    private static IReposetory _repository = new HibernateReposetory();
    private static List<Document> _documents;
    
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
    public static void injectIRepository(IReposetory repository) {
        _repository = repository;
    }
    
    /**
     * Give a to a ioName the Epics Address String.
     * @param ioName the IO Name.
     * @return the Epics Address String.
     */
    public static String getEpicsAddressString(String ioName) {
        return _repository.getEpicsAddressString(ioName);
    }

    /**
     * 
     * @return a List of all IoNames at the DB.
     */
    public static List<String> getIoNames() {
        return _repository.getIoNames();
    }

    /**
     * @param iocName the name of the Ioc.
     * @return a List of all IoNames from the Ioc with the given name.
     */
    public static List<String> getIoNames(String iocName) {
        return _repository.getIoNames(iocName);
    }

    /**
     * @param <T>
     *            ClassTyp of the Data class
     * @param clazz
     *            The Class Typ.
     * @return All Object of the Table clazz.getName.
     */
    public static <T> List<T> load(Class<T> clazz) {
        return _repository.load(clazz);
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
    public static <T> T load(Class<T> clazz, Serializable id){
        return _repository.load(clazz, id);
    }

    public static List<Sensors> loadSensors(String ioName){
        return _repository.loadSensors(ioName);
    }

    public static Sensors loadSensor(String ioName, String selection){
        return _repository.loadSensor(ioName, selection);
    }

    /**
     * 
     * @param forceRefresh if true load new from the DB, otherwise get the cache.  
     * @return All loaded Document's from the DB.
     */
    public static List<Document> loadDocument(boolean forceRefresh) {
        if(forceRefresh || _documents == null) {
            _documents = _repository.loadDocument();
        }

        return _documents;
    }

    /**
     * @param gsdFile
     *            The GSD File to remove.
     */
    public static void removeGSDFiles(GSDFile gsdFile) {
        _repository.removeGSDFiles(gsdFile);
    }

    /**
     * @param <T>
     *            ClassTyp of the Data class
     * @param dbClass
     *            The Class Typ.
     */
    public static <T extends DBClass> void removeNode(T dbClass) {
        _repository.removeNode(dbClass);        
    }

    /**
     * @param gsdFile
     *            the GSD File that save to DB
     * @return the Saved GSD File.
     */
    public static GSDFile save(GSDFile gsdFile) {
        return _repository.save(gsdFile);
    }

    /**
     * @param document
     *            the document that save to DB
     * @return the Saved document.
     */
    public static Document save(Document document) {
        return _repository.save(document);
    }

    /**
     * @param <T>
     *            ClassTyp of the DBClass
     * @param dbClass
     *            the Data class that save or update.
     * @return the Saved Data class.
     * @throws PersistenceException
     */
    public static <T extends DBClass> T saveOrUpdate(T dbClass) throws PersistenceException {
        return _repository.saveOrUpdate(dbClass);
    }

    /**
     * @param <T>
     *            ClassTyp of the DBClass
     * @param dbClass
     *            the Data class that update to DB
     * @return the Saved Data class.
     */
    public static <T extends DBClass> T update(T dbClass) {
        return _repository.update(dbClass);
    }

    /**
     * @param document
     *            the document that update to DB.
     * @return the update document.
     */
    public static Document update(Document document) {
        return _repository.update(document);
    }
    
    public static List<Integer> getRootPath(int id){
        return _repository.getRootPath(id);
    }

    public static String getShortChannelDesc(String ioName) {
        return _repository.getShortChannelDesc(ioName);
    }
}
