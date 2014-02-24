/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.test.cache.infinispan.tm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

/**
 * XaResourceCapableTransactionImpl.
 * 
 * @author Galder Zamarreño
 * @since 3.5
 */
public class XaTransactionImpl implements Transaction {
   private static final Log log = LogFactory.getLog(XaTransactionImpl.class);
   private int status;
   private LinkedList synchronizations;
   private Connection connection; // the only resource we care about is jdbc connection
   private final XaTransactionManagerImpl jtaTransactionManager;
   private List<XAResource> enlistedResources = new ArrayList<XAResource>();
   private Xid xid = new XaResourceCapableTransactionXid();

   public XaTransactionImpl(XaTransactionManagerImpl jtaTransactionManager) {
      this.jtaTransactionManager = jtaTransactionManager;
      this.status = Status.STATUS_ACTIVE;
   }

   public int getStatus() {
      return status;
   }

   public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
            IllegalStateException, SystemException {

      if (status == Status.STATUS_MARKED_ROLLBACK) {
         log.trace("on commit, status was marked for rollback-only");
         rollback();
      } else {
         status = Status.STATUS_PREPARING;

         for (int i = 0; i < synchronizations.size(); i++) {
            Synchronization s = (Synchronization) synchronizations.get(i);
            s.beforeCompletion();
         }
         
         runXaResourcePrepare();

         status = Status.STATUS_COMMITTING;

         if (connection != null) {
            try {
               connection.commit();
               connection.close();
            } catch (SQLException sqle) {
               status = Status.STATUS_UNKNOWN;
               throw new SystemException();
            }
         }
         
         runXaResourceCommitTx();

         status = Status.STATUS_COMMITTED;

         for (int i = 0; i < synchronizations.size(); i++) {
            Synchronization s = (Synchronization) synchronizations.get(i);
            s.afterCompletion(status);
         }

         // status = Status.STATUS_NO_TRANSACTION;
         jtaTransactionManager.endCurrent(this);
      }
   }

   public void rollback() throws IllegalStateException, SystemException {
      status = Status.STATUS_ROLLEDBACK;

      if (connection != null) {
         try {
            connection.rollback();
            connection.close();
         } catch (SQLException sqle) {
            status = Status.STATUS_UNKNOWN;
            throw new SystemException();
         }
      }
      
      runXaResourceRollback();

      for (int i = 0; i < synchronizations.size(); i++) {
         Synchronization s = (Synchronization) synchronizations.get(i);
         s.afterCompletion(status);
      }

      // status = Status.STATUS_NO_TRANSACTION;
      jtaTransactionManager.endCurrent(this);
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException {
      status = Status.STATUS_MARKED_ROLLBACK;
   }

   public void registerSynchronization(Synchronization synchronization) throws RollbackException,
            IllegalStateException, SystemException {
      // todo : find the spec-allowable statuses during which synch can be registered...
      if (synchronizations == null) {
         synchronizations = new LinkedList();
      }
      synchronizations.add(synchronization);
   }

   public void enlistConnection(Connection connection) {
      if (this.connection != null) {
         throw new IllegalStateException("Connection already registered");
      }
      this.connection = connection;
   }

   public Connection getEnlistedConnection() {
      return connection;
   }

   public boolean enlistResource(XAResource xaResource) throws RollbackException, IllegalStateException,
            SystemException {
      enlistedResources.add(xaResource);
      try {
         xaResource.start(xid, 0);
      } catch (XAException e) {
         log.error("Got an exception", e);
         throw new SystemException(e.getMessage());
      }
      return true;
   }

   public boolean delistResource(XAResource xaResource, int i) throws IllegalStateException, SystemException {
      throw new SystemException("not supported");
   }
   
   public Collection<XAResource> getEnlistedResources() {
      return enlistedResources;
   }
   
   private boolean runXaResourcePrepare() throws SystemException {
      Collection<XAResource> resources = getEnlistedResources();
      for (XAResource res : resources) {
         try {
            res.prepare(xid);
         } catch (XAException e) {
            log.trace("The resource wants to rollback!", e);
            return false;
         } catch (Throwable th) {
            log.error("Unexpected error from resource manager!", th);
            throw new SystemException(th.getMessage());
         }
      }
      return true;
   }
   
   private void runXaResourceRollback() {
      Collection<XAResource> resources = getEnlistedResources();
      for (XAResource res : resources) {
         try {
            res.rollback(xid);
         } catch (XAException e) {
            log.warn("Error while rolling back",e);
         }
      }
   }

   private boolean runXaResourceCommitTx() throws HeuristicMixedException {
      Collection<XAResource> resources = getEnlistedResources();
      for (XAResource res : resources) {
         try {
            res.commit(xid, false);//todo we only support one phase commit for now, change this!!!
         } catch (XAException e) {
            log.warn("exception while committing",e);
            throw new HeuristicMixedException(e.getMessage());
         }
      }
      return true;
   }
   
   private static class XaResourceCapableTransactionXid implements Xid {
      private static AtomicInteger txIdCounter = new AtomicInteger(0);
      private int id = txIdCounter.incrementAndGet();

      public int getFormatId() {
         return id;
      }

      public byte[] getGlobalTransactionId() {
         throw new IllegalStateException("TODO - please implement me!!!"); //todo implement!!!
      }

      public byte[] getBranchQualifier() {
         throw new IllegalStateException("TODO - please implement me!!!"); //todo implement!!!
      }

      @Override
      public String toString() {
         return getClass().getSimpleName() + "{" +
               "id=" + id +
               '}';
      }
   }
}
