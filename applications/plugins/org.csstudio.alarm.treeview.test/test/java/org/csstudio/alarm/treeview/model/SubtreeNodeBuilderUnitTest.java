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
 */
package org.csstudio.alarm.treeview.model;

import junit.framework.Assert;

import org.csstudio.alarm.treeview.model.SubtreeNode;
import org.csstudio.alarm.treeview.model.TreeNodeSource;
import org.csstudio.alarm.treeview.model.SubtreeNode.Builder;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.junit.Test;

/**
 * Test for the builder
 * 
 * @author jpenning
 */
public class SubtreeNodeBuilderUnitTest {
    
    @Test
    public void testBuilder() {
        SubtreeNode.Builder builder = new SubtreeNode.Builder("node0",
                                                              LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                                              TreeNodeSource.LDAP);
        SubtreeNode node = builder.build();
        Assert.assertEquals("node0", node.getName());
        Assert.assertEquals(LdapEpicsAlarmcfgConfiguration.COMPONENT,
                            node.getTreeNodeConfiguration());
        Assert.assertEquals(TreeNodeSource.LDAP, node.getSource());
    }
    
    @Test
    public void testBuildWithParent() {
        SubtreeNode parent = new SubtreeNode.Builder("parent",
                                                     LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                                     TreeNodeSource.LDAP).build();
        SubtreeNode.Builder builder = new SubtreeNode.Builder("node0",
                                                              LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                                              TreeNodeSource.LDAP)
                .setParent(parent);
        SubtreeNode node = builder.build();
        // build adds the child to the parent
        Assert.assertEquals(1, parent.getChildren().size());
        Assert.assertSame(node, parent.getChild("node0"));
        Assert.assertSame(parent, node.getParent());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testBuildAddsTwoEqualNodes() {
        SubtreeNode parent = new SubtreeNode.Builder("parent",
                                                     LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                                     TreeNodeSource.LDAP).build();
        // create two nodes with the different names: these can be added to the parent
        SubtreeNode node0 = new SubtreeNode.Builder("node0",
                                                    LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                                    TreeNodeSource.LDAP).setParent(parent).build();
        SubtreeNode node1 = new SubtreeNode.Builder("node1",
                                                    LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                                    TreeNodeSource.LDAP).setParent(parent).build();
        Assert.assertSame(node0, parent.getChild("node0"));
        Assert.assertSame(node1, parent.getChild("node1"));
        
        // we can find out if adding is possible before actually doing the build.
        Assert.assertFalse(parent.canAddChild(node0.getName())); // already exists
        
        // create another node with an already existing name: the new one cannot be added to the parent
        // so an exception is thrown.
        new SubtreeNode.Builder("node0",
                                LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                TreeNodeSource.LDAP).setParent(parent).build();
    }
    
}
