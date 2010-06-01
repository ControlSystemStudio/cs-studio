/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id$
 */
package org.csstudio.alarm.treeView.ldap;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.utility.ldap.model.ContentModel;
import org.csstudio.utility.ldap.model.CreateContentModelException;
import org.csstudio.utility.ldap.model.ILdapTreeComponent;
import org.csstudio.utility.ldap.model.LdapTreeComponent;
import org.csstudio.utility.ldap.model.builder.AbstractContentModelBuilder;

/**
 * Builds a content model from the alarm tree view structure.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 19.05.2010
 */
public final class AlarmTreeContentModelBuilder extends AbstractContentModelBuilder<LdapEpicsAlarmCfgObjectClass> {

    private final IAlarmTreeNode _alarmTreeNode;

    /**
     * Constructor.
     */
    public AlarmTreeContentModelBuilder(@Nonnull final IAlarmTreeNode alarmTreeNode) {
        _alarmTreeNode = alarmTreeNode;
    }

    /**
     * Creates a new node in the content for the given alarm tree node.
     * And then recursively for all of the alarm tree node children.
     *
     * @return the content model
     * @throws InvalidNameException
     */
    @Override
    @Nonnull
    protected ContentModel<LdapEpicsAlarmCfgObjectClass> createContentModel() throws CreateContentModelException {

        ContentModel<LdapEpicsAlarmCfgObjectClass> model;
        try {
            model = new ContentModel<LdapEpicsAlarmCfgObjectClass>(LdapEpicsAlarmCfgObjectClass.ROOT);

            createSubtree(model, _alarmTreeNode, model.getRoot());

            return model;
        } catch (final InvalidNameException e) {
            throw new CreateContentModelException("Error creating content model from alarm tree.", e);
        }
    }

    private static void createSubtree(@Nonnull final ContentModel<LdapEpicsAlarmCfgObjectClass> model,
                                      @Nonnull final IAlarmTreeNode alarmTreeNode,
                                      @Nonnull final ILdapTreeComponent<LdapEpicsAlarmCfgObjectClass> modelParentNode) throws InvalidNameException {

        final LdapEpicsAlarmCfgObjectClass oc = alarmTreeNode.getObjectClass();
        final String modelNodeName = alarmTreeNode.getName();

        final ILdapTreeComponent<LdapEpicsAlarmCfgObjectClass> newModelComponent =
            new LdapTreeComponent<LdapEpicsAlarmCfgObjectClass>(
                    modelNodeName,
                    oc,
                    oc.getNestedContainerClasses(),
                    modelParentNode,
                    new BasicAttributes(), // empty yet in this initial xml structure
                    (LdapName) modelParentNode.getLdapName().add(new Rdn(oc.getRdnType(), modelNodeName)));

        model.addChild(modelParentNode, newModelComponent);

        if (alarmTreeNode instanceof SubtreeNode) {
            for (final IAlarmTreeNode alarmTreeChild : ((SubtreeNode) alarmTreeNode).getChildren()) {
                createSubtree(model, alarmTreeChild, newModelComponent);
            }
        }
    }
}
