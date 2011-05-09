package org.csstudio.display.pvmanager.pvtable.editors;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.csdata.ProcessVariableName;
import org.csstudio.display.pvmanager.pvtable.PVTableModel;
import org.csstudio.display.pvmanager.pvtable.PVTableStaXParser;
import org.csstudio.display.pvmanager.pvtable.PVTableModel.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PVTableFileReadUnitTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File file;
	
	@Before
	public void setUp() throws Exception {
		file = temporaryFolder.newFile("test.css-pvtable");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void readWritePVTableFile(){
		List<ProcessVariableName> testList = new ArrayList<ProcessVariableName>();
		testList.add(new ProcessVariableName("sim://noise"));
		testList.add(new ProcessVariableName("sim://gaussian"));
		testList.add(new ProcessVariableName("sim://String"));
		PVTableModel testModel = new PVTableModel();
		for (ProcessVariableName processVariableName : testList) {
			testModel.addPVName(processVariableName);
		}
		writePVTableFile(Arrays.asList(testModel.getItems()), file.getAbsolutePath());
		
		List<ProcessVariableName> list = PVTableStaXParser.readPVTableFile(file.getAbsolutePath());
		
		assertTrue(list.equals(testList));		
	}
	
	public void writePVTableFile(List<Item> list, String fileName) {
		try {
			PVTableStaXParser.createByteBuffer(list).writeTo(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
