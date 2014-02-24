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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * XaResourceCapableTransactionManagerImpl.
 * 
 * @author Galder Zamarreño
 * @since 3.5
 */
public class XaTransactionManagerImpl implements TransactionManager {
   private static final XaTransactionManagerImpl INSTANCE = new XaTransactionManagerImpl();
   private XaTransactionImpl currentTransaction;

   public static XaTransactionManagerImpl getInstance() {
      return INSTANCE;
   }

   public int getStatus() throws SystemException {
      return currentTransaction == null ? Status.STATUS_NO_TRANSACTION : currentTransaction.getStatus();
   }

   public Transaction getTransaction() throws SystemException {
      return currentTransaction;
   }

   public XaTransactionImpl getCurrentTransaction() {
      return currentTransaction;
   }

   public void begin() throws NotSupportedException, SystemException {
      currentTransaction = new XaTransactionImpl(this);
   }

   public Transaction suspend() throws SystemException {
      Transaction suspended = currentTransaction;
      currentTransaction = null;
      return suspended;
   }

   public void resume(Transaction transaction) throws InvalidTransactionException, IllegalStateException,
            SystemException {
      currentTransaction = (XaTransactionImpl) transaction;
   }

   public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
            SecurityException, IllegalStateException, SystemException {
      if (currentTransaction == null) {
         throw new IllegalStateException("no current transaction to commit");
      }
      currentTransaction.commit();
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException {
      if (currentTransaction == null) {
         throw new IllegalStateException("no current transaction");
      }
      currentTransaction.rollback();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException {
      if (currentTransaction == null) {
         throw new IllegalStateException("no current transaction");
      }
      currentTransaction.setRollbackOnly();
   }

   public void setTransactionTimeout(int i) throws SystemException {
   }

   void endCurrent(Transaction transaction) {
      if (transaction == currentTransaction) {
         currentTransaction = null;
      }
   }
}
