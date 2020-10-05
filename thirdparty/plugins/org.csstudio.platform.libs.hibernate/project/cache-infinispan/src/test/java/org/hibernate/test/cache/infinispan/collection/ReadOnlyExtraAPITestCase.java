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
package org.hibernate.test.cache.infinispan.collection;

import org.hibernate.cache.access.AccessType;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;

/**
 * ReadOnlyExtraAPITestCase.
 * 
 * @author Galder Zamarreño
 * @since 3.5
 */
public class ReadOnlyExtraAPITestCase extends TransactionalExtraAPITestCase {

   public ReadOnlyExtraAPITestCase(String name) {
      super(name);
   }

   private static CollectionRegionAccessStrategy localAccessStrategy;
   
   @Override
   protected AccessType getAccessType() {
       return AccessType.READ_ONLY;
   }
   
   @Override
   protected CollectionRegionAccessStrategy getCollectionAccessStrategy() {
       return localAccessStrategy;
   }
   
   @Override
   protected void setCollectionAccessStrategy(CollectionRegionAccessStrategy strategy) {
       localAccessStrategy = strategy;
   }
   
   /**
    * Test method for {@link TransactionalAccess#lockItem(java.lang.Object, java.lang.Object)}.
    */
   @Override
   public void testLockItem() {
       try {
           getCollectionAccessStrategy().lockItem(KEY, new Integer(1));
           fail("Call to lockItem did not throw exception");
       }
       catch (UnsupportedOperationException expected) {}
   }

   /**
    * Test method for {@link TransactionalAccess#lockRegion()}.
    */
   @Override
   public void testLockRegion() {
       try {
           getCollectionAccessStrategy().lockRegion();
           fail("Call to lockRegion did not throw exception");
       }
       catch (UnsupportedOperationException expected) {}
   }

}
