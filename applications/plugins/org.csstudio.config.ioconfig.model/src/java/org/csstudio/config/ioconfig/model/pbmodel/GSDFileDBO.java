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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFileParser;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ParsedGsdFileModel;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.4 $
 * @since 28.06.2007
 */
@Entity
@Table(name = "ddb_GSD_File")
public class GSDFileDBO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    /** The DB ID. */
    private int _id;
    /** The Name of gsdFile. */
    private String _name;
    /** The Text of gsdFile. */
    private String _gsdFile;
    /** Configured Modules of this GSD File. */
    private Map<Integer, GSDModuleDBO> _gSDModules;
    
    private ParsedGsdFileModel _parsedGsdFileModel;
    
    /** */
    public GSDFileDBO() {
        // Constructor for Hibernate
    }
    
    /**
     * @param name
     *            Name of gsdFile.
     * @param gsdFile
     *            The text of gsdFile.
     * @throws IOException 
     */
    public GSDFileDBO(@Nonnull final String name, @Nonnull final String gsdFile) throws IOException {
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
    public void setId( final int id) {
        this._id = id;
    }
    
    /** @return the Text of gsdFile */
    // Es gibt Problme Text die mehr als 150KB größe in die DB zu schreiben.
    // Das Problem liegt bei log4J. Bei so großen Files darf das Logging nicht
    // auf Debug stehen!
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(nullable = false)
    @Nonnull
    public String getGSDFile() {
        return _gsdFile;
    }
    
    /**
     * @param gsdFile
     *            set the Text of gsdFile.
     * @throws IOException 
     */
    public void setGSDFile(@Nonnull final String gsdFile) throws IOException {
        _gsdFile = gsdFile;
        if(_gsdFile!=null) {
            final GsdFileParser gsdFileParser = new GsdFileParser();
            _parsedGsdFileModel = gsdFileParser.parse(this);
        }
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
    public void setName(@Nonnull final String name) {
        this._name = name;
        Diagnose.addNewLine(_name + "\t" + this.getClass().getSimpleName());
    }
    /**
     *
     * @return a map of the Modules from this GSD File.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "GSDFile", fetch = FetchType.EAGER)
    @OrderBy("moduleId")
    @MapKey(name = "moduleId")
    @CheckForNull
    public Map<Integer, GSDModuleDBO> getGSDModules() {
        return _gSDModules;
    }
    
    /**
     *
     * @param gsdModules
     *            set the Modules for this GSD File.
     */
    public void setGSDModules(@Nullable final Map<Integer, GSDModuleDBO> gsdModules) {
        _gSDModules = gsdModules;
    }

    /**
     * Get a Module of this File.
     *
     * @param indexModule
     *            the index for the given Module.
     * @return the selected Module.
     */
    @CheckForNull
    public GSDModuleDBO getGSDModule(@Nonnull final Integer indexModule) {
        return _gSDModules == null?null:_gSDModules.get(indexModule);
    }
    
    /**
     *
     * @param gSDModule
     *            add a Module to this file.
     */
    public void addGSDModule(@Nonnull final GSDModuleDBO gSDModule) {
        gSDModule.setGSDFile(this);
        if(_gSDModules == null) {
            _gSDModules = new HashMap<Integer, GSDModuleDBO>();
        }
        _gSDModules.put(gSDModule.getModuleId(), gSDModule);
    }
    
    @Transient
    public boolean isMasterNonHN() {
        return getParsedGsdFileModel().isMaster();
    }
    
    @Transient
    public boolean isSlaveNonHN() {
        return getParsedGsdFileModel().isSalve();
    }
    
    /** @return the Name of this gsdFile */
    @Transient
    @Override
    @Nonnull
    public String toString() {
        return getName();
    }
    
    @Transient
    @Nonnull
    public ParsedGsdFileModel getParsedGsdFileModel() {
        return _parsedGsdFileModel;
    }
    
}
