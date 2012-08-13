/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.array;

/**
 * Utilities to work with number collections.
 *
 * @author carcassi
 */
public class CollectionNumbers {
    
    /**
     * If available, return the array wrapped by the collection - USE WITH
     * CAUTION AS IT EXPOSES THE INTERNAL STATE OF THE COLLECTION. This
     * is provided in case an external routine for computation
     * requires you to use array, and you want to avoid the copy
     * for performance reason.
     * 
     * @param coll the collection
     * @return the array or null
     */
    public static float[] wrappedFloatArray(CollectionNumber coll) {
        if (coll instanceof ArrayFloat) {
            return ((ArrayFloat) coll).wrappedArray();
        }
        
        return null;
    }
    
    /**
     * If available, return the array wrapped by the collection - USE WITH
     * CAUTION AS IT EXPOSES THE INTERNAL STATE OF THE COLLECTION. This
     * is provided in case an external routine for computation
     * requires you to use array, and you want to avoid the copy
     * for performance reason.
     * 
     * @param coll the collection
     * @return the array or null
     */
    public static double[] wrappedDoubleArray(CollectionNumber coll) {
        if (coll instanceof ArrayDouble) {
            return ((ArrayDouble) coll).wrappedArray();
        }
        
        return null;
    }
    
    /**
     * If available, return the array wrapped by the collection - USE WITH
     * CAUTION AS IT EXPOSES THE INTERNAL STATE OF THE COLLECTION. This
     * is provided in case an external routine for computation
     * requires you to use array, and you want to avoid the copy
     * for performance reason.
     * 
     * @param coll the collection
     * @return the array or null
     */
    public static byte[] wrappedByteArray(CollectionNumber coll) {
        if (coll instanceof ArrayByte) {
            return ((ArrayByte) coll).wrappedArray();
        }
        
        return null;
    }
    
    /**
     * If available, return the array wrapped by the collection - USE WITH
     * CAUTION AS IT EXPOSES THE INTERNAL STATE OF THE COLLECTION. This
     * is provided in case an external routine for computation
     * requires you to use array, and you want to avoid the copy
     * for performance reason.
     * 
     * @param coll the collection
     * @return the array or null
     */
    public static short[] wrappedShortArray(CollectionNumber coll) {
        if (coll instanceof ArrayShort) {
            return ((ArrayShort) coll).wrappedArray();
        }
        
        return null;
    }
    
    /**
     * If available, return the array wrapped by the collection - USE WITH
     * CAUTION AS IT EXPOSES THE INTERNAL STATE OF THE COLLECTION. This
     * is provided in case an external routine for computation
     * requires you to use array, and you want to avoid the copy
     * for performance reason.
     * 
     * @param coll the collection
     * @return the array or null
     */
    public static int[] wrappedIntArray(CollectionNumber coll) {
        if (coll instanceof ArrayInt) {
            return ((ArrayInt) coll).wrappedArray();
        }
        
        return null;
    }
    
    /**
     * If available, return the array wrapped by the collection - USE WITH
     * CAUTION AS IT EXPOSES THE INTERNAL STATE OF THE COLLECTION. This
     * is provided in case an external routine for computation
     * requires you to use array, and you want to avoid the copy
     * for performance reason.
     * 
     * @param coll the collection
     * @return the array or null
     */
    public static long[] wrappedLongArray(CollectionNumber coll) {
        if (coll instanceof ArrayLong) {
            return ((ArrayLong) coll).wrappedArray();
        }
        
        return null;
    }
    
    /**
     * Copies the content of the collection to an array.
     * 
     * @param coll the collection
     * @return the array
     */
    public static float[] floatArrayCopyOf(CollectionNumber coll) {
        float[] data = new float[coll.size()];
        IteratorNumber iter = coll.iterator();
        int index = 0;
        while (iter.hasNext()) {
            data[index] = iter.nextFloat();
            index++;
        }
        return data;
    }
    
    /**
     * Copies the content of the collection to an array.
     * 
     * @param coll the collection
     * @return the array
     */
    public static double[] doubleArrayCopyOf(CollectionNumber coll) {
        double[] data = new double[coll.size()];
        IteratorNumber iter = coll.iterator();
        int index = 0;
        while (iter.hasNext()) {
            data[index] = iter.nextDouble();
            index++;
        }
        return data;
    }
    
    /**
     * Copies the content of the collection to an array.
     * 
     * @param coll the collection
     * @return the array
     */
    public static byte[] byteArrayCopyOf(CollectionNumber coll) {
        byte[] data = new byte[coll.size()];
        IteratorNumber iter = coll.iterator();
        int index = 0;
        while (iter.hasNext()) {
            data[index] = iter.nextByte();
            index++;
        }
        return data;
    }
    
    /**
     * Copies the content of the collection to an array.
     * 
     * @param coll the collection
     * @return the array
     */
    public static short[] shortArrayCopyOf(CollectionNumber coll) {
        short[] data = new short[coll.size()];
        IteratorNumber iter = coll.iterator();
        int index = 0;
        while (iter.hasNext()) {
            data[index] = iter.nextShort();
            index++;
        }
        return data;
    }
    
    /**
     * Copies the content of the collection to an array.
     * 
     * @param coll the collection
     * @return the array
     */
    public static int[] intArrayCopyOf(CollectionNumber coll) {
        int[] data = new int[coll.size()];
        IteratorNumber iter = coll.iterator();
        int index = 0;
        while (iter.hasNext()) {
            data[index] = iter.nextInt();
            index++;
        }
        return data;
    }
    
    /**
     * Copies the content of the collection to an array.
     * 
     * @param coll the collection
     * @return the array
     */
    public static long[] longArrayCopyOf(CollectionNumber coll) {
        long[] data = new long[coll.size()];
        IteratorNumber iter = coll.iterator();
        int index = 0;
        while (iter.hasNext()) {
            data[index] = iter.nextLong();
            index++;
        }
        return data;
    }
}
