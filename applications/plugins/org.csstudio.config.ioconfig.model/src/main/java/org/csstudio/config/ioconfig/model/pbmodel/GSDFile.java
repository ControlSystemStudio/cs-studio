/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.Keywords;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFactory;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdMasterModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdSlaveModel;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 28.06.2007
 */
@Entity
@Table(name = "ddb_GSD_File")
public class GSDFile {

	/** The DB ID. */
	private int _id;
	/** The Name of gsdFile. */
	private String _name;
	/** The Text of gsdFile. */
	private String _gsdFile;
	/** Configured Modules of this GSD File. */
	private Map<Integer, GSDModule> _gSDModules;
	/**
	 * If only true when this file config a Profibus Master.
	 */
	private Boolean _master;
	/**
	 * If only true when this file config a Profibus Slave.
	 */
	private Boolean _slave;

	@Column(nullable = true)
	public Boolean getMaster() {
		return _master;
	}

	@Transient
	public Boolean isMasterNonHN() {
		if (getMaster() == null) {//||(!isSlave()&&!getMaster())) {
			paresFile();
		}
		return getMaster();
	}

	public void setMaster(Boolean master) {
		_master = master;
	}

	@Column(nullable = true)
	public Boolean isSlave() {
		return _slave;
	}

	@Transient
	public Boolean isSlaveNonHN() {
		if (isSlave() == null) {// ||(!isSlave()&&!getMaster())) {
			paresFile();
		}
		return isSlave();
	}

	public void setSlave(Boolean slave) {
		_slave = slave;
	}

	/** */
	public GSDFile() {

	}

	/**
	 * @param name
	 *            Name of gsdFile.
	 * @param gsdFile
	 *            The text of gsdFile.
	 */
	public GSDFile(String name, String gsdFile) {
		setName(name);
		setGSDFile(gsdFile);
	}

	/** @return the ID. */
	@Id
	@GeneratedValue
	public int getId() {
		return _id;
	}

	/**
	 * @param id
	 *            set the ID.
	 */
	public void setId(int id) {
		this._id = id;
	}

	/** @return the Text of gsdFile */
	// Es gibt Problme Text die mehr als 150KB größe in die DB zu schreiben.
	// Das Problem liegt bei log4J. Bei so großen Files darf das Logging nicht
	// auf Debug stehen!
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(nullable = false)
	public String getGSDFile() {
		return _gsdFile;
	}

	/**
	 * @param gsdFile
	 *            set the Text of gsdFile.
	 */
	public void setGSDFile(String gsdFile) {
		this._gsdFile = gsdFile;
	}

	/** @return the Name of gsdFile */
	@Column(unique = true, nullable = false)
	public String getName() {
		return _name;
	}

	/**
	 * @param name
	 *            set the Name of gsdFile.
	 */
	public void setName(String name) {
		this._name = name;
		long time = new Date().getTime();
        long l = time-NamedDBClass._oldTime;
        if(l>50)
            NamedDBClass._diagString.append(": \t\t"+time+"\t"+l+"\t"+_name+"\t"+this.getClass().getSimpleName()+"\r\n");
        NamedDBClass._oldTime = time;
	}

	/**
	 * 
	 * @return a map of the Modules from this GSD File.
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "GSDFile", fetch = FetchType.EAGER)
	@OrderBy("moduleId")
	@MapKey(name = "moduleId")
	public Map<Integer, GSDModule> getGSDModules() {
		return _gSDModules;
	}

	/**
	 * 
	 * @param gsdModules
	 *            set the Modules for this GSD File.
	 */
	public void setGSDModules(Map<Integer, GSDModule> gsdModules) {
		_gSDModules = gsdModules;
	}

	/**
	 * 
	 * @param gSDModule
	 *            add a Module to this file.
	 */
	public void addGSDModule(GSDModule gSDModule) {
		gSDModule.setGSDFile(this);
		if (_gSDModules == null) {
			_gSDModules = new HashMap<Integer, GSDModule>();
		}
		_gSDModules.put(gSDModule.getModuleId(), gSDModule);
	}

	/**
	 * Get a Module of this File.
	 * 
	 * @param indexModule
	 *            the index for the given Module.
	 * @return the selected Module.
	 */
	public GSDModule getGSDModule(Integer indexModule) {
		if (_gSDModules == null) {
			_gSDModules = new HashMap<Integer, GSDModule>();
		}
		return _gSDModules.get(indexModule);
	}

	/**
	 * Parse this file to set Master and Slave flag.
	 */
	@Transient
	private void paresFile() {
		GsdSlaveModel slave = GsdFactory.makeGsdSlave(this);
		setSlave(slave != null && slave.getType() == Keywords.GSDFileTyp.Slave);
		slave = null;
		GsdMasterModel master = GsdFactory.makeGsdMaster(this.getGSDFile());
		setMaster(master != null
				&& master.getType() == Keywords.GSDFileTyp.Master);
		master = null;
		Repository.save(this);
	}

	/** @return the Name of this gsdFile */
	@Transient
	@Override
	public String toString() {
		return getName();
	}

}
