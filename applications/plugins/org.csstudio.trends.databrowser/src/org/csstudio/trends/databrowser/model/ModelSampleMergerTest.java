package org.csstudio.trends.databrowser.model;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;

/** Tests for ModelSampleMerger.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelSampleMergerTest extends TestCase
{
    private static final int N = 10;
    private static final INumericMetaData meta =
        ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, 0, "");
    private static final ISeverity invalid = ValueFactory.createInvalidSeverity();

    private ModelSample makeSample(long l)
    {
        return new ModelSample(
              ValueFactory.createDoubleValue(TimestampFactory.fromDouble(l),
                                        invalid,
                                        "",
                                        meta,
                                        IValue.Quality.Original,
                                        new double[] { l }));
    }

    public void testPassOld()
    {
        ModelSampleArray old = new ModelSampleArray();
        ModelSampleArray add = new ModelSampleArray();
        for (int i=0; i<N; ++i)
            old.add(makeSample(i));
        ModelSampleArray merge = ModelSampleMerger.merge(old, add);
        Assert.assertEquals(N, merge.size());
        Assert.assertTrue(merge.equals(old));
    }

    public void testPassAdd()
    {
        ModelSampleArray old = new ModelSampleArray();
        ModelSampleArray add = new ModelSampleArray();
        for (int i=0; i<N; ++i)
            add.add(makeSample(i));
        ModelSampleArray merge = ModelSampleMerger.merge(old, add);
        Assert.assertEquals(N, merge.size());
        Assert.assertTrue(merge.equals(add));
    }

    public void testAddBeforeOld()
    {
        ModelSampleArray old = new ModelSampleArray();
        ModelSampleArray add = new ModelSampleArray();
        for (int i=0; i<N; ++i)
            add.add(makeSample(i));
        for (int i=0; i<N; ++i)
            old.add(makeSample(2*N+i));
        ModelSampleArray merge = ModelSampleMerger.merge(old, add);
        Assert.assertEquals(2*N, merge.size());
        // new samples...
        Assert.assertTrue(merge.subList(0, N).equals(add));
        // followed by old samples...
        Assert.assertTrue(merge.subList(N, N+N).equals(old));
    }

    public void testOldBeforeAdd()
    {
        ModelSampleArray old = new ModelSampleArray();
        ModelSampleArray add = new ModelSampleArray();
        for (int i=0; i<N; ++i)
            old.add(makeSample(i));
        for (int i=0; i<N; ++i)
            add.add(makeSample(2*N+i));
        ModelSampleArray merge = ModelSampleMerger.merge(old, add);
        Assert.assertEquals(2*N, merge.size());
        // old samples...
        Assert.assertTrue(merge.subList(0, N).equals(old));
        // followed by new samples...
        Assert.assertTrue(merge.subList(N, N+N).equals(add));
    }

    public void testAddOverlapsIntoOld()
    {
        ModelSampleArray old = new ModelSampleArray();
        ModelSampleArray add = new ModelSampleArray();
        for (int i=0; i<N; ++i) // 0, 1, ... 
            add.add(makeSample(i));
        for (int i=0; i<N; ++i) // 5, 15, 25, 35
            old.add(makeSample(5+i*10));
        
        ModelSampleArray merge = ModelSampleMerger.merge(old, add);
        
        Assert.assertEquals(N + N-1, merge.size());
        // new samples...
        Assert.assertTrue(merge.subList(0, N).equals(add));
        // followed by all but the first old samples...
        Assert.assertTrue(merge.subList(N, N+N-1).equals(
                        old.subList(1, N)));
    }

    public void testOldOverlapsIntoAdd()
    {
        ModelSampleArray old = new ModelSampleArray();
        ModelSampleArray add = new ModelSampleArray();
        for (int i=0; i<N; ++i) // 0, 1, ... 
            old.add(makeSample(i));
        for (int i=0; i<N; ++i) // 5, 15, 25, 35
            add.add(makeSample(5+i*10));

        ModelSampleArray merge = ModelSampleMerger.merge(old, add);
        
        Assert.assertEquals(5 + N, merge.size());
        // First 5 old samples...
        Assert.assertTrue(merge.subList(0, 5).equals(
                        old.subList(0, 5)));
        // followed by new samples...
        Assert.assertTrue(merge.subList(5, 5+N).equals(add));
    }

    public void testAddWithinOld()
    {
        ModelSampleArray old = new ModelSampleArray();
        ModelSampleArray add = new ModelSampleArray();
        for (int i=0; i<N; ++i) // 0, 10, 20, ...
            old.add(makeSample(i*10));
        for (int i=0; i<N; ++i) // 5, 4, 7, ...
            add.add(makeSample(5+i));

        /*
        for (ModelSample sample : old)
            System.out.println("Old: " + sample.getX());
        for (ModelSample sample : add)
            System.out.println("Add: " + sample.getX());
        */
        ModelSampleArray merge = ModelSampleMerger.merge(old, add);

        /*
        for (ModelSample sample : merge)
            System.out.println("Merge: " + sample.getX());
        */
        Assert.assertEquals(1 + N + 8, merge.size());
        // First 5 old samples...
        Assert.assertTrue(merge.get(0).equals(old.get(0)));
        // followed by new samples...
        Assert.assertTrue(merge.subList(1, 1+N).equals(add));
        // and more old samples...
        Assert.assertTrue(merge.subList(1+N, 1+N+8).equals(old.subList(2, N)));
    }

    public void testAddCoversOld()
    {
        ModelSampleArray old = new ModelSampleArray();
        ModelSampleArray add = new ModelSampleArray();
        for (int i=0; i<N; ++i) // 5, 4, 7, ...
            old.add(makeSample(5+i));
        for (int i=0; i<N; ++i) // 0, 10, 20, ...
            add.add(makeSample(i*10));
        
        ModelSampleArray merge = ModelSampleMerger.merge(old, add);
        
        // New samples completely replace the old ones....
        Assert.assertEquals(add.size(), merge.size());
        Assert.assertTrue(merge.equals(add));
    }

}
