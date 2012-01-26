package org.csstudio.config.ioconfig.model.pbmodel;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 12.05.2011
 */
public class CopyNodeUnitTest {

    private static final boolean OUTPUT = false;
    private static final boolean INPUT = true;
    private ProfibusSubnetDBO _profibusSubnet;
    private MasterDBO _master;
    private SlaveDBO _slave;


    @Before
    public void setUp() throws PersistenceException {
        Repository.injectIRepository(new DummyRepository());
        _profibusSubnet = new ProfibusSubnetDBO(new IocDBO());
        _profibusSubnet.setName("Subnet");
        _profibusSubnet.localSave();
        _master = new MasterDBO(_profibusSubnet);
        _master.setName("Master");
        _master.localSave();
        _slave = new SlaveDBO(_master);
        _slave.setName("Slave");
        _slave.moveSortIndex((short)1234);
        _slave.localSave();
    }

    @After
    public void tearDown() {
        _slave = null;
        _master = null;
        _profibusSubnet = null;
    }

    @Test
    public void testCopyModule() throws Exception {
        assertEquals("--- Precondition wrong!","@Subnet:1234", _slave.getEpicsAdressString());

        // Copy a Module to the same Slave
        final ModuleDBO module = new ModuleDBO(_slave);
        module.setName("Module");
        final ChannelStructureDBO sco = ChannelStructureDBO.makeChannelStructure(module,OUTPUT,DataType.INT8,"SCO ");
        final ChannelDBO pci = new ChannelDBO(sco," ",INPUT,false, -1);
        pci.setChannelType(DataType.INT16);
        pci.setName("PCI");
        module.localSave();

        assertEquals(1, _slave.getChildren().size());

        final NamedDBClass node = module.copyThisTo(_slave, "");
        // - Test Slave
        assertEquals(2, _slave.getChildren().size());

        final ModuleDBO copyModule = testModule(node);

        // -- Test Children
        assertEquals(module.getChildren().size(), copyModule.getChildren().size());
        assertEquals(module.getPureChannels().size(), copyModule.getPureChannels().size());
        assertEquals(module.getChildren().size(), copyModule.getChildren().size());

        // -- Test PCO Children
        final Set<ChannelDBO> pureChannels = module.getPureChannels();
        final Set<ChannelDBO> copyPureChannels = copyModule.getPureChannels();
        assertEquals(pureChannels, copyPureChannels);
        assertEquals(pureChannels.isEmpty(), copyPureChannels.isEmpty());
        assertEquals(pureChannels.size(), copyPureChannels.size());
        final Iterator<ChannelDBO> iterator = pureChannels.iterator();
        final Iterator<ChannelDBO> copyIterator = copyPureChannels.iterator();
        testPureChannels(iterator, copyIterator);
        testStructureChannels(module, copyModule, iterator, copyIterator);

        // Paste the Module to are other Slave
    }

    private void testStructureChannels(@Nonnull final ModuleDBO module,
                                       @Nonnull final ModuleDBO copyModule,
                                       @Nonnull final Iterator<ChannelDBO> iterator,
                                       @Nonnull final Iterator<ChannelDBO> copyIterator) {
        final Set<ChannelStructureDBO> channelStructs = module.getChildren();
        final Set<ChannelStructureDBO> copyChannelStructs = copyModule.getChildren();
        assertEquals(channelStructs.isEmpty(), copyChannelStructs.isEmpty());
        assertEquals(channelStructs.size(), copyChannelStructs.size());

        while(iterator.hasNext()&&copyIterator.hasNext()) {
            final ChannelDBO channel = iterator.next();
            final ChannelDBO copy = copyIterator.next();
            assertEquals(channel, copy);
            assertEquals(channel.isDigital(), copy.isDigital());
            assertEquals(channel.isInput(), copy.isInput());
            assertEquals(channel.getChannelNumber(), copy.getChannelNumber());
            assertEquals(channel.getParent(), copy.getParent());
            assertEquals(channel.getChannelType(), copy.getChannelType());
            assertEquals(channel.getChSize(), copy.getChSize());
            assertEquals(channel.getCurrenUserParamDataIndex(), copy.getCurrenUserParamDataIndex());
            assertEquals(channel.getDescription(), copy.getDescription());
            assertEquals(channel.getGSDFile(), copy.getGSDFile());
            assertEquals(channel.getIoName(), copy.getIoName());
            assertEquals(channel.getModule(), copy.getModule());
            assertEquals(channel.getName(), copy.getName());
            assertEquals(channel.getParent(), copy.getParent());
            assertEquals(channel.getStruct(), copy.getStruct());
            assertEquals(channel.getVersion(), copy.getVersion());
        }
    }

    private void testPureChannels(@Nonnull final Iterator<ChannelDBO> iterator,
                                  @Nonnull final Iterator<ChannelDBO> copyIterator) {
        while(iterator.hasNext()&&copyIterator.hasNext()) {
            final ChannelDBO channel = iterator.next();
            final ChannelDBO copy = copyIterator.next();
            assertEquals(channel, copy);
            assertEquals(channel.isDigital(), copy.isDigital());
            assertEquals(channel.isInput(), copy.isInput());
            assertEquals(channel.getChannelNumber(), copy.getChannelNumber());
            assertEquals(channel.getParent(), copy.getParent());
            assertEquals(channel.getChannelType(), copy.getChannelType());
            assertEquals(channel.getChSize(), copy.getChSize());
            assertEquals(channel, copy);
            assertEquals(channel, copy);
        }
    }

    @Nonnull
    private ModuleDBO testModule(@Nonnull final NamedDBClass node) {
        // - Test Module
        final ModuleDBO copyModule = (ModuleDBO) node;
        assertEquals(_slave, copyModule.getParent());
        assertEquals("Module", copyModule.getName());
        assertEquals(0, copyModule.getId());
        return copyModule;
    }

}
