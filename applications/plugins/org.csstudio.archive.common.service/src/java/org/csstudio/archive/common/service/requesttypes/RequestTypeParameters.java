/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.service.requesttypes;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * This class shall be created for any service implementor separately. Clients may only read and 
 * modify the values of the contained parameters, but not 'add' or delete parameters. 
 * 
 * @author bknerr
 * @since 19.01.2011
 */
public class RequestTypeParameters {
   
    private final Map<String, ArchiveRequestTypeParameter<?>> _params = Maps.newHashMap();
    
    /**
     * Constructor.
     * @param params
     */
    public RequestTypeParameters(ArchiveRequestTypeParameter<?>[] params) {
        for (ArchiveRequestTypeParameter<?> param : params) {
            _params.put(param.getName(), param);
        }
    }

    /**
     * Sets the request type parameter with the given identifier name to the given value.
     * 
     * If the param with the given does not exist or, if not so, the value's class type does not 
     * match the params required class, obtained by {@link ArchiveRequestTypeParameter#getClassType} 
     * an exception is thrown.
     *   
     * @param name
     * @param value
     * @throws Exception
     */
    public void setParam(@Nonnull final String name, 
                         @Nonnull final Object value) throws Exception {
        
        ArchiveRequestTypeParameter<?> param = _params.get(name);
        if (value.equals(param)) {
            return; // identity
        }
        if (param == null) {
            throw new Exception("Parameter with identifying name " + name + " unknown.");
        }
        Class<?> clazz = param.getValueClassType();
        if (value.getClass() != clazz) { // TODO (bknerr) : check whether isAssignableFrom is better in any way
            throw new Exception("Value object's class type " + value.getClass().getName() + 
                                " does not match required " + clazz.getName());
        }
    }

    @CheckForNull
    public ArchiveRequestTypeParameter<?> getParam(@Nonnull final String name) {
        return _params.get(name);
    }
    
    /**
     * Returns a copy of the (read-only) parameters.
     * 
     * @return a copy of the contained values.
     */
    @Nonnull
    public ImmutableSet<ArchiveRequestTypeParameter<?>> values() {
        return ImmutableSet.<ArchiveRequestTypeParameter<?>>builder().addAll(_params.values()).build();
    }
    
    /**
     * Returns a newly created EntrySet with keys (request parameter identifier) and values 
     * (request parameters).
     * @return a copy (not a view!) of the entry set
     */
    @Nonnull
    public ImmutableSet<Entry<String, ArchiveRequestTypeParameter<?>>> entrySet() {
        return ImmutableSet.<Entry<String, ArchiveRequestTypeParameter<?>>>builder().addAll(_params.entrySet()).build();
    }
    
}
