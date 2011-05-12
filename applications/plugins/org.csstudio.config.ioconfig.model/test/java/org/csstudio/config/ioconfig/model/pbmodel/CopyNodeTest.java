package org.csstudio.config.ioconfig.model.pbmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CopyNodeTest {
    
    private static final boolean OUTPUT = false;
    private static final boolean INPUT = true;
    private ProfibusSubnetDBO _profibusSubnet;
    private MasterDBO _master;
    private SlaveDBO _slave;


    @Test
    public void testCopyModule() throws Exception {
        assertEquals("--- Precondition wrong!","@Subnet:1234", _slave.getEpicsAdressString());
        
        // Copy a Module to the same Slave
        ModuleDBO module = new ModuleDBO(_slave);
        module.setName("Module");
        ChannelStructureDBO sco = ChannelStructureDBO.makeChannelStructure(module,OUTPUT,DataType.INT8,"SCO ");
        ChannelDBO pci = new ChannelDBO(sco,INPUT,false);
        pci.setChannelType(DataType.INT16);
        pci.setName("PCI");
        module.localSave();
        
        assertEquals(1, _slave.getChildren().size());
        
        NamedDBClass node = module.copyThisTo(_slave);
        // - Test Slave
        assertEquals(2, _slave.getChildren().size());
        
        // - Test Module 
        assertTrue(node instanceof ModuleDBO);
        ModuleDBO copyModule = (ModuleDBO) node;
        assertEquals(_slave, copyModule.getParent());
        assertEquals("Module", copyModule.getName());
        assertEquals(0, copyModule.getId());
        
        // TODO: Test Simple and Struct Channle
        // -- Test Children
        assertEquals(module.getChildren().size(), copyModule.getChildren().size());
        assertEquals(module.getPureChannels().size(), copyModule.getPureChannels().size());
        assertEquals(module.getChannelStructs().size(), copyModule.getChannelStructs().size());

        // -- Test PCO Children
        Set<ChannelDBO> pureChannels = module.getPureChannels();
        Set<ChannelDBO> copyPureChannels = copyModule.getPureChannels();
        assertEquals(pureChannels, copyPureChannels);
        assertEquals(pureChannels.isEmpty(), copyPureChannels.isEmpty());
        assertEquals(pureChannels.size(), copyPureChannels.size());
        Iterator<ChannelDBO> iterator = pureChannels.iterator();
        Iterator<ChannelDBO> copyIterator = copyPureChannels.iterator();
        while(iterator.hasNext()&&copyIterator.hasNext()) {
            ChannelDBO channel = iterator.next();
            ChannelDBO copy = copyIterator.next();
            assertEquals(channel, copy);
            assertEquals(channel.isDigital(), copy.isDigital());
            assertEquals(channel.isInput(), copy.isInput());
            assertEquals(channel.getChannelNumber(), copy.getChannelNumber());
            assertEquals(channel.getChannelStructure(), copy.getChannelStructure());
            assertEquals(channel.getChannelType(), copy.getChannelType());
            assertEquals(channel.getChSize(), copy.getChSize());
            assertEquals(channel, copy);
            assertEquals(channel, copy);
        }
        // -- Test PCO Children
        
        Set<ChannelStructureDBO> channelStructs = module.getChannelStructs();
        Set<ChannelStructureDBO> copyChannelStructs = copyModule.getChannelStructs();
        assertEquals(channelStructs.isEmpty(), copyChannelStructs.isEmpty());
        assertEquals(channelStructs.size(), copyChannelStructs.size());
        
        while(iterator.hasNext()&&copyIterator.hasNext()) {
            ChannelDBO channel = iterator.next();
            ChannelDBO copy = copyIterator.next();
            assertEquals(channel, copy);
            assertEquals(channel.isDigital(), copy.isDigital());
            assertEquals(channel.isInput(), copy.isInput());
            assertEquals(channel.getChannelNumber(), copy.getChannelNumber());
            assertEquals(channel.getChannelStructure(), copy.getChannelStructure());
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
        
        // Paste the Module to are other Slave
    }
    
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
    public void setDown() {
        _slave = null;
        _master = null;
        _profibusSubnet = null;
        Repository.injectIRepository(null);
    }
    
}
