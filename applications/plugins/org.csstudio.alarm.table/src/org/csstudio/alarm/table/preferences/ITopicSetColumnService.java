/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table.preferences;

import java.util.List;

import javax.annotation.Nonnull;

import org.eclipse.swt.graphics.Font;

/**
 * Stateful service for reading the topic set and column set preferences and
 * for providing the list of columns descriptions.
 * This data is specific for the type of view (log, alarm, verify), therefore three different services are
 * provided. The services are created during start of the bundle and can be retrieved from the plugin.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 24.06.2010
 */
public interface ITopicSetColumnService {

    /**
     * If there is no
     * default the first item is taken. The default tag from the preferences is
     * overwritten if there is a topic set from a previous session.
     *
     * @param topics
     *            raw topic string from preferences
     * @return set of topics for initialization
     */
    @Nonnull
    String getDefaultTopicSet();

    /**
     * Get the font for the given topic set name.
     */
    @Nonnull
    Font getFont(@Nonnull final String topicSetName);

    /**
     * Get corresponding columnSet for the given topicSet
     *
     * @param defaultTopicSet
     * @return
     */
    @Nonnull
    String[] getColumnSet(@Nonnull final String topicSet);

    @Nonnull
    List<TopicSet> getTopicSets();

    /**
     * Read the JMS Topics for the given topicSetName.
     *
     * @param currentTopicSet
     * @return
     */
    @Nonnull
    TopicSet getJMSTopics(@Nonnull final String currentTopicSet);

    /**
     * @return the list with the column descriptions specific to the type of view
     */
    @Nonnull
    List<ColumnDescription> getColumnDescriptions();

}
