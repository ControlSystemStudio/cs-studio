package org.csstudio.dct.util;

import java.util.Map;

import org.csstudio.dct.metamodel.Factory;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.model.commands.AddInstanceCommand;
import org.csstudio.dct.model.commands.AddPrototypeCommand;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.Record;
import org.csstudio.dct.model.internal.RecordFactory;

public class UseCase {
	
	public static Project createProject() {
		// create a project
		Project project = new Project("Hera Ring");
		
		project.setDbdVersion("1.0.a");
		project.setIoc("HeraIoc");
		
		// create a folder structure
		Folder prototypesFolder = new Folder("Prototypes");
		project.addMember(prototypesFolder);
		
		Folder instancesFolder = new Folder("Instances");
		project.addMember(instancesFolder);

		
		/** create prototype A **/
		Prototype prototypeA = new Prototype("Pump");
		new AddPrototypeCommand(prototypesFolder, prototypeA).execute();

		// ... with parameters
		prototypeA.addParameter(new Parameter("building", ""));
		prototypeA.addParameter(new Parameter("nr", ""));
		
		// ... with records
		IRecord recordA1 = RecordFactory.createRecord("ai", "Pump_$building$_$nr$");
		recordA1.addField("FOPR", ".1");
		recordA1.addField("HOLO", "${max}");
		recordA1.addField("LOLO", "${min}");
		new AddRecordCommand(prototypeA, recordA1).execute();
		
		
		/** create a prototype B **/
		Prototype prototypeB = new Prototype("ChillerStation");
		new AddPrototypeCommand(prototypesFolder, prototypeB).execute();
		
		// ... with parameters
		prototypeB.addParameter(new Parameter("name", ""));
		prototypeB.addParameter(new Parameter("max", ""));
		prototypeB.addParameter(new Parameter("min", ""));
		
		// ... with records
		IRecord recordB1 = RecordFactory.createRecord("ai", "Chiller_$name$_1");
		recordB1.addField("SCAN", ".1");
		recordB1.addField("HOPR", "$max$}");
		recordB1.addField("LOPR", "$min$");
		new AddRecordCommand(prototypeB, recordB1).execute();
		
		IRecord recordB2 = RecordFactory.createRecord("ai", "Chiller_$name$_2");
		recordB2.addField("SCAN", ".1");
		recordB2.addField("HOPR", "$max$");
		recordB2.addField("LOPR", "$min$");
		new AddRecordCommand(prototypeB, recordB2).execute();
		
		// ... with an instance of prototype A
		IInstance instanceA1 = new Instance(prototypeA);
		new AddInstanceCommand(prototypeB, instanceA1).execute();
		instanceA1.setParameterValue("building", "hera");
		
		/** create instances of Prototyp B **/
		IInstance instanceB1 = new Instance(prototypeB);
		new AddInstanceCommand(instancesFolder, instanceB1).execute();
		instanceB1.setParameterValue("name", "haus_A");
		instanceB1.getRecords().get(0).addField("SCAN", ".2");
		
		
		IInstance instanceB2 = new Instance(prototypeB);
		new AddInstanceCommand(instancesFolder, instanceB2).execute();
		instanceB2.setParameterValue("name", "haus_B");
		instanceB2.getRecords().get(0).addField("SCAN", ".3");
		
		return project;
	}
	public static void main(String[] args) {
		// create a project
		Project project = createProject();
		
		// iterate the model
		iterate(project);
		
		// print hierarchy
		printHierarchy(project, 0);
		
	}
	
	public static void iterate(IFolder folder) {
		for(IFolderMember member : folder.getMembers()) {
			
			if(member instanceof IFolder) {
				iterate((IFolder) member);
			} else if (member instanceof IInstance) {
				iterate((Instance) member);
			}
			
		}
	}
	
	public static void iterate(IInstance instance) {
		for(IRecord r : instance.getRecords()) {
			Map<String, Object> fields = r.getFinalFields();
			
			System.out.println("----");
			for(String key : fields.keySet()) {
				System.out.println(key + "=" + fields.get(key));
			}
			
			for(IInstance in : instance.getInstances()) {
				iterate(in);
			}
		}
	}
	
	public static void printHierarchy(IFolder folder, int s) {
		renderArrow(s, folder);
		
		for(IFolderMember member : folder.getMembers()) {
			
			if(member instanceof IFolder) {
				printHierarchy((IFolder) member, s+1);
			} else if (member instanceof IRecordContainer) {
				printHierachy((IRecordContainer) member, s+1);
			}
			
		}
	}
	
	private static void printHierachy(IRecordContainer recordContainer, int s) {
		renderArrow(s, recordContainer);
		
//		for(IRecordContainer child : recordContainer.getInstances()) {
//			printHierachy(child, s+1);
//		}
		
		for(IRecord r : recordContainer.getRecords()) {
			renderArrow(s+1, r);
			
			for(String key : r.getFields().keySet()) {
				renderArrow(s+2, key+"="+r.getField(key));
			}
		}

	}
	
	
	private static void renderArrow (int length, Object name) {
		StringBuffer sb = new StringBuffer();
		for(int i =0; i<=length*2;i++) {
			sb.append(" ");
		}
		sb.append("-> ");
		sb.append(name);
		
		System.out.println(sb.toString());
	}
}
