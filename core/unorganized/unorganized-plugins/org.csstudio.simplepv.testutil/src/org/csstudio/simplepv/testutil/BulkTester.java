/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.testutil;

import java.util.Calendar;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.SimplePVLayer;

/**
 * Test operations on large number of PVs.
 *
 * @author Xihui Chen
 *
 */
public class BulkTester {

    private int totalPVs = 1000;
    private String pvFactoryId;
    private AtomicInteger updates;
    private PVNameProvider pvNameProvider;

    /**
     * Create a tester.
     *
     * @param pvFactoryId
     *            pv factory id.
     * @param totalPVs
     *            Total number of PVs to be tested.
     * @throws Exception
     */
    public BulkTester(String pvFactoryId, int totalPVs, PVNameProvider pvNameProvider)
            throws Exception {
        this.pvFactoryId = pvFactoryId;
        updates = new AtomicInteger(0);
        this.totalPVs = totalPVs;
        this.pvNameProvider = pvNameProvider;
    }

    public void testAll() throws Exception {
        //start stop start stop
        testOpenClose();
        testOpenClose();
    }

    protected void testOpenClose() throws Exception {
        IPV[] pvs = new IPV[totalPVs];
        final CountDownLatch latch = new CountDownLatch(totalPVs);
        final HashSet<IPV> hashSet = new HashSet<>();
        long startTime = Calendar.getInstance().getTimeInMillis();
        for (int i = 1; i <= totalPVs; i++) {
            IPV pv = SimplePVLayer.getPVFactory(pvFactoryId).createPV(pvNameProvider.getPVName(i));
            pvs[i - 1] = pv;
            hashSet.add(pv);
            pv.start();
            pv.addListener(new IPVListener.Stub() {
                @Override
                public void valueChanged(IPV pv) {
                    updates.incrementAndGet();
                }

                @Override
                public void connectionChanged(IPV pv) {
                    if (pv.isConnected()) {
                        latch.countDown();
                        hashSet.remove(pv);
                    }
                }
            });


        }
        long stopTime = Calendar.getInstance().getTimeInMillis();
        System.out.println("It took " + (stopTime - startTime) + " ms to create " + totalPVs
                + " pvs.");

        try {
            if (!latch.await(20, TimeUnit.SECONDS)) {
                for (IPV pv : hashSet) {
                    System.out.println(pv.getName() + " didn't trigger latch while it is "
                            + (pv.isConnected() ? "connected" : "not connected"));
                }
                Assert.fail("" + latch.getCount() + " pvs cannot connect in 20 seconds.");
            }
        } finally {
            for (int i = 0; i < totalPVs; i++) {
                if (!pvs[i].isConnected())
                    System.out.println(pvs[i].getName() + " is not connected.");
                pvs[i].stop();
            }
        }

    }

    public interface PVNameProvider {
        /**
         * Get PVName based on the index.
         *
         * @param index
         * @return the pv name.
         */
        public String getPVName(int index);
    }

}
