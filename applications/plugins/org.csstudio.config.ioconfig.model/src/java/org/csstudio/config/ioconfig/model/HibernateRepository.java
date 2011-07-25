package org.csstudio.config.ioconfig.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for a Hibernate Repository.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.9 $
 * @since 03.06.2009
 */
public class HibernateRepository implements IRepository {
    
    private final IHibernateManager _instance;
    protected static final Logger LOG = LoggerFactory.getLogger(HibernateRepository.class);
    
    /**
     * 
     * Constructor.
     * @param hibernateManager the hibernateManager or null for default manager.
     */
    public HibernateRepository(@CheckForNull IHibernateManager hibernateManager) {
        if(hibernateManager!=null) {
            _instance = hibernateManager;
        } else {
            _instance = HibernateManager.getInstance();
        }
    }
    
    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.9 $
     * @since 08.04.2010
     */
    private static final class EpicsAddressHibernateCallback implements IHibernateCallback {
        private final String _ioName;
        
        protected EpicsAddressHibernateCallback(@Nonnull final String ioName) {
            _ioName = ioName;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        @Nonnull
        public String execute(@Nonnull final Session session) {
            final Query query = session.createQuery("select channel.epicsAddressString from "
                    + ChannelDBO.class.getName() + "  as channel where channel.ioName like '"
                    + _ioName + "'");
            
            final List<String> channels = doQuery(query);
            if (channels.size() < 1) {
                return "%%% IO-Name (" + _ioName + ") NOT found! %%%";
            } else if (channels.size() > 1) {
                StringBuilder sb = new StringBuilder("%%% IO-Name (");
                sb.append(_ioName);
                sb.append(" NOT Unique! %%% ");
                for (String string : channels) {
                    sb.append(" ,");
                    sb.append(string);
                }
                return sb.toString();
            }
            return channels.get(0);
        }
        
        @Nonnull
        @SuppressWarnings("unchecked")
        private List<String> doQuery(@Nonnull final Query query) {
            return query.list();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Nonnull
    public final GSDModuleDBO saveWithChildren(@Nonnull final GSDModuleDBO gsdModule) throws PersistenceException {
        try {
            _instance.doInDevDBHibernateLazy(new IHibernateCallback() {
                private Session _session;
                
                @SuppressWarnings("unchecked")
                @Override
                @Nonnull
                public GSDModuleDBO execute(@Nonnull final Session session) {
                    _session = session;
                    Set<ModuleChannelPrototypeDBO> values = gsdModule.getModuleChannelPrototypeNH();
                    _session.saveOrUpdate(gsdModule);
                    if ((values != null) && (values.size() > 0)) {
                        saveChildren(values);
                    }
                    _session.flush();
                    return gsdModule;
                }
                
                private void saveChildren(@Nonnull final Set<ModuleChannelPrototypeDBO> moduleChannelPrototypes) {
                    for (ModuleChannelPrototypeDBO prototype : moduleChannelPrototypes) {
                        _session.saveOrUpdate(prototype);
                    }
                }
            });
            return gsdModule;
        } catch (HibernateException he) {
            LOG.warn("Can't save", he);
            throw new PersistenceException(he);
        }
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public final <T extends DBClass> T saveOrUpdate(@Nonnull final T dbClass) throws PersistenceException {
        try {
            _instance.doInDevDBHibernateLazy(new IHibernateCallback() {
                
                @SuppressWarnings("unchecked")
                @Override
                @Nonnull
                public T execute(@Nonnull final Session session) {
                    session.saveOrUpdate(dbClass);
                    return dbClass;
                }
                
            });
            return dbClass;
        } catch (HibernateException he) {
            LOG.warn("Save or update failed", he);
            PersistenceException persistenceException = new PersistenceException(he);
            throw persistenceException;
        }
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public final <T extends DBClass> T update(@Nonnull final T dbClass) throws PersistenceException {
        
        _instance.doInDevDBHibernateLazy(new IHibernateCallback() {
            
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public T execute(@Nonnull final Session session) {
                dbClass.setUpdatedOn(new Date());
                session.update(dbClass);
                session.flush();
                return dbClass;
            }
            
        });
        return dbClass;
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @CheckForNull
    public final <T> List<T> load(@Nonnull final Class<T> clazz) throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public List<T> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("from " + clazz.getName());
                final List<T> nodes = query.list();
                
                if (nodes.isEmpty()) {
                    return null;
                }
                return nodes;
            }
        };
        return _instance.doInDevDBHibernateLazy(hibernateCallback);
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @CheckForNull
    public final <T> T load(@Nonnull final Class<T> clazz, @Nonnull final Serializable id) throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public T execute(@Nonnull final Session session) {
                final List<T> nodes = session
                        .createQuery("select c from " + clazz.getName() + " c where c.id = :id")
                        .setString("id", id.toString()).list();
                
                if (nodes.isEmpty()) {
                    return null;
                }
                
                return nodes.get(0);
            }
        };
        return _instance.doInDevDBHibernateLazy(hibernateCallback);
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public final <T extends DBClass> void removeNode(@Nonnull final T dbClass) throws PersistenceException {
        _instance.doInDevDBHibernateLazy(new IHibernateCallback() {
            
            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public Object execute(@Nonnull final Session session) {
                session.delete(dbClass);
                return null;
            }
            
        });
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public final GSDFileDBO save(@Nonnull final GSDFileDBO gsdFile) throws PersistenceException {
        
        _instance.doInDevDBHibernateLazy(new IHibernateCallback() {
            
            @SuppressWarnings("unchecked")
            @Override
            @Nonnull
            public GSDFileDBO execute(@Nonnull final Session session) {
                session.saveOrUpdate(gsdFile);
                return gsdFile;
            }
            
        });
        return gsdFile;
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public final void removeGSDFiles(@Nonnull final GSDFileDBO gsdFile) throws PersistenceException {
        _instance.doInDevDBHibernateLazy(new IHibernateCallback() {
            
            @SuppressWarnings("unchecked")
            @Override
            @CheckForNull
            public Object execute(@Nonnull final Session session) {
                session.delete(gsdFile);
                return null;
            }
            
        });
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @CheckForNull
    public final List<DocumentDBO> loadDocument() throws PersistenceException {
        return _instance.doInDevDBHibernateLazy(new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public List<DocumentDBO> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("from " + DocumentDBO.class.getName()
                        + " where length(image) > 0");
                final List<DocumentDBO> nodes = query.list();
                if (nodes.isEmpty()) {
                    return null;
                }
                return nodes;
            }
            
        });
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public final DocumentDBO save(@Nonnull final DocumentDBO document) throws PersistenceException {
        
        _instance.doInDevDBHibernateLazy(new IHibernateCallback() {
            
            @SuppressWarnings("unchecked")
            @Override
            @Nonnull
            public DocumentDBO execute(@Nonnull final Session session) {
                session.saveOrUpdate(document);
                session.flush();
                return document;
            }
            
        });
        return document;
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public final DocumentDBO update(@Nonnull final DocumentDBO document) throws PersistenceException {
        
        _instance.doInDevDBHibernateLazy(new IHibernateCallback() {
            
            @SuppressWarnings("unchecked")
            @Override
            @Nonnull
            public DocumentDBO execute(@Nonnull final Session session) {
                document.setUpdateDate(new Date());
                session.update(document);
                session.flush();
                return document;
            }
            
        });
        return document;
    }
    
    /**
     * Get the Epics Address string to an IO Name. It the name not found return the string '$$$
     * IO-Name NOT found! $$$'.
     *
     * @param ioName
     *            the IO-Name.
     * @return the Epics Adress for the given IO-Name.
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public final String getEpicsAddressString(@Nonnull final String ioName) throws PersistenceException {
        IHibernateCallback hibernateCallback = new EpicsAddressHibernateCallback(ioName);
        String epicsAddressString = _instance.doInDevDBHibernateEager(hibernateCallback);
        return epicsAddressString==null?"":epicsAddressString;
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public final List<String> getIoNames() throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public List<String> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("select channel.ioName from "
                        + ChannelDBO.class.getName() + " as channel");
                final List<String> ioNames = query.list();
                return ioNames;
            }
        };
        List<String> ioNameList = _instance.doInDevDBHibernateEager(hibernateCallback);
        return ioNameList==null?new ArrayList<String>():ioNameList;
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public final List<String> getIoNames(@Nonnull final String iocName) throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public List<String> execute(@Nonnull final Session session) {
                // TODO: Der IOC name wird noch nicht mir abgefragt!
                final Query query = session.createQuery("select channel.ioName from "
                        + ChannelDBO.class.getName() + " as channel");
                final List<String> ioNames = query.list();
                return ioNames;
            }
        };
        List<String> doInDevDBHibernateEager = _instance.doInDevDBHibernateEager(hibernateCallback);
        if(doInDevDBHibernateEager == null) {
            doInDevDBHibernateEager = new ArrayList<String>();
        }
        return doInDevDBHibernateEager;
    }
    
    @Override
    @Nonnull
    public final String getShortChannelDesc(@Nonnull final String ioName) throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public String execute(@Nonnull final Session session) {
                final Query query = session.createQuery("select channel.description from "
                        + ChannelDBO.class.getName() + " as channel where channel.ioName like ?");
                query.setString(0, ioName); // Zero-Based!
                
                final List<String> descList = query.list();
                if ((descList == null) || descList.isEmpty()) {
                    return "";
                }
                String string = descList.get(0);
                if ((string == null) || string.isEmpty()) {
                    return "";
                }
                String[] split = string.split("[\r\n]");
                if (split[0].length() > 40) {
                    return split[0].substring(0, 40);
                }
                return split[0];
            }
        };
        String doInDevDBHibernateEager = _instance.doInDevDBHibernateEager(hibernateCallback);
        if(doInDevDBHibernateEager == null) {
            doInDevDBHibernateEager="";
        }
        return doInDevDBHibernateEager;
    }
    
    @Override
    @Nonnull
    public final List<SensorsDBO> loadSensors(@Nonnull final String ioName) throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public List<SensorsDBO> execute(@Nonnull final Session session) {
                final Query query = session.createQuery("from " + SensorsDBO.class.getName()
                        + " as sensors where sensors.ioName like ?");
                query.setString(0, ioName); // Zero-Based!
                
                final List<SensorsDBO> sensors = query.list();
                return sensors;
            }
        };
        List<SensorsDBO> doInDevDBHibernateEager = _instance.doInDevDBHibernateEager(hibernateCallback);
        if(doInDevDBHibernateEager==null) {
            doInDevDBHibernateEager = new ArrayList<SensorsDBO>();
        }
        return doInDevDBHibernateEager;
    }
    
    @Override
    @CheckForNull
    public final SensorsDBO loadSensor(@Nonnull final String ioName, @Nonnull final String selection) throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @Override
            @SuppressWarnings("unchecked")
            @CheckForNull
            public SensorsDBO execute(@Nonnull final Session session) {
                String statment = "select" + " s" + " from " + SensorsDBO.class.getName() + " s"
                        + ", " + ChannelDBO.class.getName() + " c"
                        + " where c.currentValue like s.id" + " and c.ioName like '" + ioName + "'";
                final Query query = session.createQuery(statment);
                final List<SensorsDBO> sensors = query.list();
                if ((sensors == null) || (sensors.size() < 1)) {
                    return null;
                }
                return sensors.get(0);
            }
        };
        return _instance.doInDevDBHibernateEager(hibernateCallback);
    }
    
    @CheckForNull
    @Nonnull
    public final List<Integer> getRootPath(final int id) throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @SuppressWarnings("unchecked")
            @Override
            @Nonnull
            public List<Integer> execute(@Nonnull final Session session) {
                int level = 0;
                int searchId = id;
                String statment = "select node.parent_Id  from ddb_node node where node.id like ?";
                final List<Integer> rootPath = new ArrayList<Integer>();
                rootPath.add(searchId);
                SQLQuery query = session.createSQLQuery(statment);
                while (searchId > 0) {
                    query.setInteger(0, searchId); // Zero-Based!
                    BigDecimal uniqueResult = (BigDecimal) query.uniqueResult();
                    if ((uniqueResult == null) || (level++ > 10)) {
                        break;
                    }
                    searchId = uniqueResult.intValue();
                    rootPath.add(searchId);
                }
                return rootPath;
            }
        };
        List<Integer> doInDevDBHibernateLazy = _instance.doInDevDBHibernateLazy(hibernateCallback);
        return doInDevDBHibernateLazy==null?new ArrayList<Integer>():doInDevDBHibernateLazy;
    }
    
    @Override
    @CheckForNull
    public final ChannelDBO loadChannel(@Nullable final String ioName) throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @SuppressWarnings("unchecked")
            @Override
            @CheckForNull
            public ChannelDBO execute(@Nonnull final Session session) {
                if (ioName == null) {
                    return null;
                }
                Query createQuery = session.createQuery("select c from "
                        + ChannelDBO.class.getName() + " c where c.ioName like ?");
                createQuery.setString(0, ioName);
                final ChannelDBO nodes = (ChannelDBO) createQuery.uniqueResult();
                return nodes;
            }
        };
        return _instance.doInDevDBHibernateLazy(hibernateCallback);
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @CheckForNull
    public final List<PV2IONameMatcherModelDBO> loadPV2IONameMatcher(@Nullable final Collection<String> pvName) throws PersistenceException {
        IHibernateCallback hibernateCallback = new IHibernateCallback() {
            @SuppressWarnings("unchecked")
            @Override
            @CheckForNull
            public List<PV2IONameMatcherModelDBO> execute(@Nonnull final Session session) {
                if ((pvName == null) || pvName.isEmpty()) {
                    return null;
                }
                StringBuilder statement = new StringBuilder("select pv from ")
                        .append(PV2IONameMatcherModelDBO.class.getName()).append(" pv where ");
                boolean notFirst = false;
                for (String string : pvName) {
                    if (notFirst) {
                        statement.append(" OR ");
                    }
                    statement.append(String.format("pv.epicsName = '%s'", string));
                    notFirst = true;
                }
                return session.createQuery(statement.toString()).list();
            }
        };
        return _instance.doInDevDBHibernateEager(hibernateCallback);
    }
    
    @Override
    public final void close() {
        _instance.closeSession();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isConnected() {
        return _instance.isConnected();
    }
    
}
