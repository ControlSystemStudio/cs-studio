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
package org.csstudio.config.ioconfig.commands;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.editorparts.ChannelEditor;
import org.csstudio.config.ioconfig.editorparts.ChannelStructureEditor;
import org.csstudio.config.ioconfig.editorparts.FacilityEditor;
import org.csstudio.config.ioconfig.editorparts.IocEditor;
import org.csstudio.config.ioconfig.editorparts.MasterEditor;
import org.csstudio.config.ioconfig.editorparts.ModuleEditor;
import org.csstudio.config.ioconfig.editorparts.SlaveEditor;
import org.csstudio.config.ioconfig.editorparts.SubnetEditor;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.INodeVisitor;
import org.csstudio.config.ioconfig.model.VirtualLeaf;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.VirtualRoot;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;

/**
 * Visitor to provide the node specific editor id. 
 * 
 * @author bknerr
 * @since 10.06.2011
 */
public enum EditorIdNodeVisitor implements INodeVisitor {
    INSTANCE;
    
    private String _id = null;
    
    /**
     * Constructor.
     */
    private EditorIdNodeVisitor() {
        // Empty
    }
    
    @CheckForNull
    public String getId() {
        return _id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final VirtualRoot node) {
        // Don't do anything
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final FacilityDBO node) {
        _id = FacilityEditor.ID;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final IocDBO node) {
        _id = IocEditor.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final ProfibusSubnetDBO node) {
        _id = SubnetEditor.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final MasterDBO node) {
        _id = MasterEditor.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final SlaveDBO node) {
        _id = SlaveEditor.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final ModuleDBO node) {
        _id = ModuleEditor.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final ChannelStructureDBO node) {
        _id = ChannelStructureEditor.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final ChannelDBO node) {
        _id = ChannelEditor.ID;
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(@Nonnull final VirtualLeaf node) {
        // Don't do anything
    }
    
}
