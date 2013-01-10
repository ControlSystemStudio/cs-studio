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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * The class represents one module of a GSD file.
 * 
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 28.03.2011
 */
public class GsdModuleModel2 extends AbstractGsdPropertyModel {
    
    private final String _name;
    private final List<Integer> _value;
    private int _moduleNo;
    private ParsedGsdFileModel _parent;
    
    /**
     * Constructor.
     * @param line
     */
    public GsdModuleModel2(@Nonnull final String name, @Nonnull final List<Integer> value) {
        _name = name;
        _value = value;
    }
    
    @Override
    @CheckForNull
    public ExtUserPrmData getExtUserPrmData(@Nonnull final Integer index) {
        return getParent().getExtUserPrmData(index);
    }
    
    @Nonnull
    public Integer getModuleNumber() {
        return _moduleNo;
    }
    
    @Nonnull
    public String getName() {
        return _name;
    }
    
    @Nonnull
    public ParsedGsdFileModel getParent() {
        return _parent;
    }
    
    @Nonnull
    public List<Integer> getValue() {
        return _value;
    }
    
    @Nonnull
    public String getValueAsString() {
        return GsdFileParser.intList2HexString(_value);
    }
    
    public void setModuleNumber(final int moduleNo) {
        _moduleNo = moduleNo;
    }
    
    public void setParent(@Nonnull final ParsedGsdFileModel parent) {
        _parent = parent;
    }
    
    @Override
    @Nonnull
    public final String toString() {
        return getName();
    }
}
