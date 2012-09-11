package org.csstudio.utility.toolbox.view.forms;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.csstudio.utility.toolbox.entities.Device;
import org.csstudio.utility.toolbox.entities.Gebaeude;
import org.csstudio.utility.toolbox.entities.KeywordHardware;
import org.csstudio.utility.toolbox.entities.KeywordSoftware;
import org.csstudio.utility.toolbox.entities.LagerBox;
import org.csstudio.utility.toolbox.entities.LagerFach;
import org.csstudio.utility.toolbox.entities.LagerOrt;
import org.csstudio.utility.toolbox.entities.Project;
import org.csstudio.utility.toolbox.entities.Raum;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.services.DeviceService;
import org.csstudio.utility.toolbox.services.GebaeudeService;
import org.csstudio.utility.toolbox.services.KeywordService;
import org.csstudio.utility.toolbox.services.ProjectService;
import org.csstudio.utility.toolbox.services.RaumService;
import org.csstudio.utility.toolbox.services.StoreLookupDataService;

import com.google.inject.Inject;

public class LookupDataAutoCreator {

	@Inject
	private ProjectService projectService;

	@Inject
	private DeviceService deviceService;

	@Inject
	private RaumService raumService;

	@Inject
	private GebaeudeService gebaeudeService;

	@Inject
	private KeywordService keywordService;

	@Inject
	private StoreLookupDataService storeLookupDataService;

	@Inject
	private EntityManager em;

	public void autoCreateProject(String projectName) {
		if (StringUtils.isNotEmpty(projectName)) {
			Option<Project> project = projectService.findByName(projectName);
			if (!project.hasValue()) {
				Project newProject = new Project();
				newProject.setKeyword(projectName);
				em.persist(newProject);
			}
		}
	}

	public void autoCreateDevice(String deviceName) {	
		if (StringUtils.isNotEmpty(deviceName)) {
			Option<Device> device = deviceService.findByName(deviceName);
			if (!device.hasValue()) {
				Device newDevice = new Device();
				newDevice.setKeyword(deviceName);
				em.persist(newDevice);
			}
		}
	}
	
	public void autoCreateGebaeude(String gebaeudeName) {		
		if (StringUtils.isNotEmpty(gebaeudeName)) {
			Option<Gebaeude> gebaeude = gebaeudeService.findByName(gebaeudeName);
			if (!gebaeude.hasValue()) {
				Gebaeude newGebaeude = new Gebaeude();
				newGebaeude.setName(gebaeudeName);
				em.persist(newGebaeude);
			}
		}
	}

	public void autoCreateRaum(String gebaeudeName, String raumName) {		
		if (StringUtils.isNotEmpty(raumName)) {
			Option<Gebaeude> gebaeude = gebaeudeService.findByName(gebaeudeName);
			if (!gebaeude.hasValue()) {
				throw new IllegalStateException("Location not found");
			}
			Option<Raum> raum = raumService.findByNameAndGebauedeId(raumName, gebaeude.get().getGebaeudeId());
			if (!raum.hasValue()) {
				Raum  newRaum = new Raum();
				newRaum.setName(raumName);
				newRaum.setGebaeudeId(gebaeude.get().getGebaeudeId());
				em.persist(newRaum);
			}
		}
	}

	public void autoCreateBox(String lagerName, String boxName) {		
		if (StringUtils.isNotEmpty(boxName)) {
			Option<LagerBox> box = storeLookupDataService.findBoxByName(lagerName, boxName);
			if (!box.hasValue()) {
				LagerBox newLagerBox = new LagerBox();
				newLagerBox.setLagerName(lagerName);
				newLagerBox.setName(boxName);
				em.persist(newLagerBox);
			}
		}
	}

	public void autoCreateShelf(String lagerName, String shelfName) {		
		if (StringUtils.isNotEmpty(shelfName)) {
			Option<LagerFach> fach = storeLookupDataService.findShelfByName(lagerName, shelfName);
			if (!fach.hasValue()) {
				LagerFach newLagerFach = new LagerFach();
				newLagerFach.setLagerName(lagerName);
				newLagerFach.setName(shelfName);
				em.persist(newLagerFach);
			}
		}
	}

	public void autoCreateLocation(String lagerName, String locationName) {		
		if (StringUtils.isNotEmpty(locationName)) {
			Option<LagerOrt> location = storeLookupDataService.findLocationByName(lagerName, locationName);
			if (!location.hasValue()) {
				LagerOrt newLagerOrt = new LagerOrt();
				newLagerOrt.setLagerName(lagerName);
				newLagerOrt.setName(locationName);
				em.persist(newLagerOrt);
			}
		}
	}
	
	public void autoCreateKeyword(Boolean isHardware, String keyword) {		
		if (StringUtils.isNotEmpty(keyword)) {
			if (isHardware) {
				Option<KeywordHardware> hardwareKeyword = keywordService.findByKeywordHardware(keyword);
				if (!hardwareKeyword.hasValue()) {
					KeywordHardware newKeywordHardware = new KeywordHardware();
					newKeywordHardware.setKeyword(keyword);
					em.persist(newKeywordHardware);
				}
			} else {
				Option<KeywordSoftware> softwareKeyword = keywordService.findByKeywordSoftware(keyword);
				if (!softwareKeyword.hasValue()) {
					KeywordSoftware newKeywordSoftware = new KeywordSoftware();
					newKeywordSoftware.setKeyword(keyword);
					em.persist(newKeywordSoftware);
				}	
			}
		}
	}

}
