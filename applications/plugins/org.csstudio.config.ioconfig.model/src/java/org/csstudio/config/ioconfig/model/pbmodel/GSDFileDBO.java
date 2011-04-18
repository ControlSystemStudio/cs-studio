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
 * $Id: GSDFile.java,v 1.4 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
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

import org.csstudio.config.ioconfig.model.Diagnose;
import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFactory;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFileParser;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdMasterModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdSlaveModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ParsedGsdFileModel;
import org.csstudio.platform.logging.CentralLogger;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.4 $
 * @since 28.06.2007
 */
@Entity
@Table(name = "ddb_GSD_File")
public class GSDFileDBO {

	/** The DB ID. */
	private int _id;
	/** The Name of gsdFile. */
	private String _name;
	/** The Text of gsdFile. */
	private String _gsdFile;
	/** Configured Modules of this GSD File. */
	private Map<Integer, GSDModuleDBO> _gSDModules;
	/**
	 * If only true when this file config a Profibus Master.
	 */
	private Boolean _master;
	/**
	 * If only true when this file config a Profibus Slave.
	 */
	private Boolean _slave;
	
	private ParsedGsdFileModel _parsedGsdFileModel;

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

	public void setMaster(final Boolean master) {
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

	public void setSlave(final Boolean slave) {
		_slave = slave;
	}

	/** */
	public GSDFileDBO() {

	}

	/**
	 * @param name
	 *            Name of gsdFile.
	 * @param gsdFile
	 *            The text of gsdFile.
	 */
	public GSDFileDBO(final String name, final String gsdFile) {
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
	public void setId(final int id) {
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
	public void setGSDFile(final String gsdFile) {
		this._gsdFile = gsdFile;
	}

	/** @return the Name of gsdFile */
	@Column(unique = true, nullable = false)
	@Nonnull
	public String getName() {
		return _name;
	}

	/**
	 * @param name
	 *            set the Name of gsdFile.
	 */
	public void setName(final String name) {
		this._name = name;
		Diagnose.addNewLine(_name+"\t"+this.getClass().getSimpleName());
	}

	/**
	 *
	 * @return a map of the Modules from this GSD File.
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "GSDFile", fetch = FetchType.EAGER)
	@OrderBy("moduleId")
	@MapKey(name = "moduleId")
	public Map<Integer, GSDModuleDBO> getGSDModules() {
		return _gSDModules;
	}

	/**
	 *
	 * @param gsdModules
	 *            set the Modules for this GSD File.
	 */
	public void setGSDModules(final Map<Integer, GSDModuleDBO> gsdModules) {
		_gSDModules = gsdModules;
	}

	/**
	 *
	 * @param gSDModule
	 *            add a Module to this file.
	 */
	public void addGSDModule(final GSDModuleDBO gSDModule) {
		gSDModule.setGSDFile(this);
		if (_gSDModules == null) {
			_gSDModules = new HashMap<Integer, GSDModuleDBO>();
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
	public GSDModuleDBO getGSDModule(final Integer indexModule) {
		if (_gSDModules == null) {
			_gSDModules = new HashMap<Integer, GSDModuleDBO>();
		}
		return _gSDModules.get(indexModule);
	}

	/**
	 * Parse this file to set Master and Slave flag.
	 * @throws PersistenceException 
	 */
	@Transient
	private void paresFile() {
		GsdSlaveModel slave = GsdFactory.makeGsdSlave(this);
		setSlave((slave != null) && (slave.getType() == GSDFileTypes.Slave));
		slave = null;
		GsdMasterModel master = GsdFactory.makeGsdMaster(this.getGSDFile());
		setMaster((master != null)
				&& (master.getType() == GSDFileTypes.Master));
		master = null;
		try {
            Repository.save(this);
        } catch (PersistenceException e) {
            CentralLogger.getInstance().error(this, e);
        }
	}

	/** @return the Name of this gsdFile */
	@Transient
	@Override
	public String toString() {
		return getName();
	}


	@Transient
    public ParsedGsdFileModel getParsedGsdFileModel() throws IOException {
	    if(_parsedGsdFileModel == null) {
	        GsdFileParser gsdFileParser = new GsdFileParser();
	        _parsedGsdFileModel =  gsdFileParser.parse(this);
	    }
        return _parsedGsdFileModel;
    }

}
