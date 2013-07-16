package org.csstudio.askap.navigator.model;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Branch implements Cloneable {
	private static final Logger logger = Logger.getLogger(Branch.class.getName());
	
	private String name;
	private Branch branches[];
	private String link;
	private String opiName;
	
	// easier to serialize and deserialize string arrays than Map<>
	private String macros[][];

	public Branch(String name, Branch branches[], String macros[][], String opiName) {
		this.name = name;
		this.branches = branches;
		this.macros = macros;
		this.opiName = opiName;
	}
	
	public Branch(String name, String link, String macros[][]) {
		this.name = name;
		this.link = link;
		this.macros = macros;
	}
	
	public Branch() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Branch[] getBranches() {
		return branches;
	}

	public void setBranches(Branch[] branches) {
		this.branches = branches;
	}

	public String getOpiName() {
		return opiName;
	}

	public void setOpiName(String opiName) {
		this.opiName = opiName;
	}

	public String[][] getMacros() {
		return macros;
	}

	public void setMacros(String[][] macros) {
		this.macros = macros;
	}

	public void addMacros(String[][] newMacros) {
		if (newMacros==null || newMacros.length==0) {
			return;
		}
		
		if (macros==null || macros.length==0) {
			macros = newMacros;
			return;
		}
		
		if (newMacros!=null && newMacros.length>=0) {
			String oldMacros[][] = macros;
			macros = new String[oldMacros.length+newMacros.length][2];
	
			int i = 0;
			for (String macro[] : oldMacros) {
				macros[i] = macro;
				i++;
			}
			
			for (String macro[] : newMacros) {
				macros[i] = macro;
				i++;
			}
		}
	}

	public void setupMacros(String[][] parentMacro, View view) {
		addMacros(parentMacro);
		Branch linkedBranch = null;
		
		// if it's a link, need to make a hardcopy then set macros
		if (link!=null && link.trim().length()>0) {
			for (Branch branch : view.getBranches()) {
				if (branch.getName().equals(link)) {
					linkedBranch = branch;
					break;
				}
			}
		}
		
		
		if (linkedBranch!=null && linkedBranch.getBranches()!=null) {
			branches = new Branch[linkedBranch.getBranches().length];
			for (int i=0; i<linkedBranch.getBranches().length; i++) {
				try {
					branches[i] = (Branch) linkedBranch.getBranches()[i].clone();
				} catch (CloneNotSupportedException e) {
					logger.log(Level.WARNING, "Could not clone Branch - " + linkedBranch.getBranches()[i].getName());
				}
			}
				
			addMacros(linkedBranch.getMacros());
		}
		

		if (branches==null)
			return;
		
		for (Branch branch : branches) {
			branch.setupMacros(macros, view);
		}
	}
	
	@Override 
	public Object clone() throws CloneNotSupportedException {
		
		String newMacros[][] = null;
		if (macros!=null) {
			newMacros = new String[macros.length][2];
			for (int i=0; i<macros.length; i++) {
				newMacros[i][0] = macros[i][0];
				newMacros[i][1] = macros[i][1];
			}
		}

		Branch newBranches[] = null;
		if (branches!=null) {
			newBranches = new Branch[branches.length];
			for (int i=0; i<branches.length; i++) {
				newBranches[i] = (Branch) branches[i].clone();
			}
		}
		
		Branch branch = new Branch(name, newBranches, newMacros, opiName);
		branch.link = link;
		
		return branch;
	}
		
}
