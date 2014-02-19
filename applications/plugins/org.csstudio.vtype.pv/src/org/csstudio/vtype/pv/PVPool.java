/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.vtype.pv.RefCountMap.ReferencedEntry;

/** Pool of {@link PV}s
 * 
 *  <p>Maintains PVs, with a reference count.
 *  
 *  <p>A PV is referred to by different names:
 *  <ul>
 *  <li>The name provided by the user: "fred" or "ca://fred",
 *      with or without prefix.
 *      May also contain parameters: "loc://x(3.14)" or "loc://x(14)".
 *  <li>Name used by the type-dependent implementation: "fred"
 *  <li>
 *  </ul>
 *  
 *  <p>The PV and the pool use the name provided by the user,
 *  because that way <code>PV.getName()</code> will always return
 *  the expected name.
 *  On the downside, this could create the same underlying PV twice,
 *  with and without the prefix.
 *  
 *  <p>Note also that "loc://x(3.14)" and "loc://x(14)" will be treated
 *  as different PVs.
 *
 *  @author Kay Kasemir
 */
public class PVPool
{
    /** Separator between PV type indicator and rest of PV name.
     *  <p>
     *  This one is URL-like, and works OK with EPICS PVs because
     *  those are unlikely to contain "://" themselves, while
     *  just ":" for example is likely to be inside the PV name
     */
    final protected static String SEPARATOR = "://";

    /** Map of PV type prefixes to PV factories */
    final private static Map<String, PVFactory> factories = new HashMap<>();
    
    /** Default PV name type prefix */
    private static String default_type;

    /** PV Pool
     *  SYNC on 'pool':
     *  Otherwise, two threads concurrently looking for a new PV would both add it.
     */
    final private static RefCountMap<String, PV> pool = new RefCountMap<>();
   
    /** Singleton */
    private PVPool()
    {
    }

    /** Set default PV name type prefix.
     *  
     *  <p>Should be called <u>after</u> adding all factories, because
     *  factory added last will be the default.
     *  
     *  @param type Default PV name type prefix to use if none is provided in a PV name
     */
    public static void setDefaultType(final String type)
    {
        default_type = type;
    }

    /** Add a PV Factory
     * 
     *  <p>As a side effect, also makes that last added factory the default
     *  
     *  @param factory {@link PVFactory} that the pool can use
     */
    public static void addPVFactory(final PVFactory factory)
    {
        factories.put(factory.getType(), factory);
        setDefaultType(factory.getType());
    }
    
    /** Obtain a PV
     * 
     *  <p>Obtains existing PV of that name from pool,
     *  or creates new PV if no existing PV found.
     *  
     *  @param name PV name, where prefix might be used to determine the type
     *  @return {@link PV}
     *  @throws Exception on error
     *  @see #releasePV(PV)
     */
    public static PV getPV(final String name) throws Exception
    {
        PV pv;
        synchronized (pool)
        {
            pv = pool.get(name);
            if (pv == null)
            {
                pv = createPV(name);
                pool.put(name, pv);
            }
        }
        return pv;
    }
    
    /** Create 
     * 
     * @param name
     * @return
     * @throws Exception
     */
    private static PV createPV(final String name) throws Exception
    {
        final String[] prefix_base = analyzeName(name);
        final PVFactory factory = factories.get(prefix_base[0]);
        if (factory == null)
            throw new Exception(name + " has unknown PV type '" + prefix_base[0] + "'");
        return factory.createPV(name, prefix_base[1]);
    }

    /** Analyze PV name
     *  @param name PV Name, "base..." or  "prefix://base..."
     *  @return Array with type (or default) and base name
     */
    private static String[] analyzeName(final String name)
    {
        final String type, base;
        final int sep = name.indexOf(SEPARATOR);
        if (sep > 0)
        {
            type = name.substring(0, sep);
            base = name.substring(sep+SEPARATOR.length());
        }
        else
        {
            type = default_type;
            base = name;
        }
        return new String[] { type, base };
    }
    
    /** @param pv PV to be released */
    public static void releasePV(final PV pv)
    {
        final int references;
        synchronized (pool)
        {
            references = pool.release(pv.getName());
        }
        if (references == 0)
            pv.close();
    }
    
    /** @return PVs currently in the pool with reference count information */
    public static Collection<ReferencedEntry<PV>> getPVReferences()
    {
        synchronized (pool)
        {
            return pool.getEntries();
        }
    }
}
