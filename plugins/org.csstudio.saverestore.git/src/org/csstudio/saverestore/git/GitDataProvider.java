package org.csstudio.saverestore.git;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.csstudio.saverestore.BaseLevel;
import org.csstudio.saverestore.BeamlineSet;
import org.csstudio.saverestore.BeamlineSetData;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.InvalidCommentException;
import org.csstudio.saverestore.Snapshot;
import org.csstudio.saverestore.VSnapshot;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

public class GitDataProvider implements DataProvider {

    public GitDataProvider() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String[] getBranches() {
        return new String[]{"master"};
    }

    @Override
    public BaseLevel[] getBaseLevels(String branch) {
        List<BaseLevel> isotopes = new ArrayList<>();
//        Element[] elements = Element.values();
//        for (int i = 0; i < 100; i++) {
//            Element e = elements[(int)(Math.random()*elements.length)];
//            int n = e.commonNeutrons + (int)(Math.random()*6)-3;
//            int c = e.commonCharge + (int)(Math.random()*2)-1;
//            Isotope is = Isotope.of(e,n,c);
//            if (isotopes.contains(is)) continue;
//            isotopes.add(is);
//        }
        return isotopes.toArray(new BaseLevel[isotopes.size()]);
    }

    @Override
    public BeamlineSet[] getBeamlineSets(BaseLevel baseLevel, String branch) {
        List<BeamlineSet> beamlineSets = new ArrayList<>();
//        if (baseLevel != null) {

            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Front End","All PVs"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Linac Segments","Seg 1", "Correctors"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Linac Segments","Seg 1", "Quadrupoles"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Linac Segments","Seg 1", "Others"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Linac Segments","Seg 2", "Correctors"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Linac Segments","Seg 3", "Correctors"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Folding Segments","Seg 1"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Folding Segments","Seg 2"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Production Target Systems","Sys 1"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Production Target Systems","Sys 2"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Production Target Systems","Sys 3"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Fragment Separator","Set 1"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Fragment Separator","Set 2"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Fragment Separator","Set 3"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Fast Beam Area","All PVs"}));
            beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[]{"Reaccelerated Beam Area","All PVs"}));

//            for (int i = 0; i < 100; i++) {
//                BeamlineSet set = new BeamlineSet(branch, isotope, new String[]{"Front End","Set" +i + " "
//                            + isotope.element.symbol});
//                beamlineSets.add(set);
//            }
//            beamlineSets.add(new BeamlineSet(branch, isotope, new String[]{"Set" + " " + isotope.element.symbol}));
            Collections.sort(beamlineSets);
//        }
        return beamlineSets.toArray(new BeamlineSet[beamlineSets.size()]);
    }

    @Override
    public Snapshot[] getSnapshots(BeamlineSet set) {
        List<Snapshot> snapshots = new ArrayList<>();
        if (set != null) {
            long time = System.currentTimeMillis();
            snapshots.add(new Snapshot(set,new Date(time), "Aw, the poor puddy tat! He fall down and go... BOOM!","Tweety"));
            time -= 22400000;
            snapshots.add(new Snapshot(set,new Date(time), "I did, I did taw a puddy tat","Tweety"));
            time -= 33400000;
            snapshots.add(new Snapshot(set,new Date(time), "Sufferin succotash","Sylvester"));
            time -= 33400000;
            snapshots.add(new Snapshot(set,new Date(time), "I taw I taw a puddy tat","Tweety"));
            time -= 12400000;
            snapshots.add(new Snapshot(set,new Date(time), "What's up, doc","Bugs Bunny"));
            time -= 12400000;
            snapshots.add(new Snapshot(set,new Date(time), "Wabbit Season!","Daffy Duck"));
            time -= 12400000;
            snapshots.add(new Snapshot(set,new Date(time), "Mine mine mine! It's all mine!","Daffy Duck"));
            time -= 12400000;
            snapshots.add(new Snapshot(set,new Date(time), "Be vewy vewy quiet, I'm hunting wabbits!, He-e-e-e-e!","Elmer Fudd"));


            Collections.sort(snapshots);
        }
        return snapshots.toArray(new Snapshot[snapshots.size()]);
    }

    @Override
    public BeamlineSetData getBeamlineSetContent(BeamlineSet set) {
        List<String> pvList = new ArrayList<>();
        for (int i = 100; i < 200; i++) {
            pvList.add("PV"+i);
        }
        return new BeamlineSetData(set, pvList, "One very special beamline set.");

    }

    @Override
    public void createNewBranch(String originalBranch, String newBranchName) {
        System.out.println("Requested to create a new branch: " + newBranchName);

    }

    @Override
    public void saveBeamlineSet(BeamlineSetData set, String comment) throws InvalidCommentException {
        System.out.println("Requested to save the beamline set: " + comment);

    }

    @Override
    public void saveSnapshot(VSnapshot data, String comment) throws InvalidCommentException {
        System.out.println("Successfully stored");

    }

    @Override
    public void tagSnapshot(Snapshot snapshot, String tagName, String tagMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public VSnapshot getSnapshotContent(Snapshot snapshot) {
        List<String> names = new ArrayList<>();
        List<VType> values = new ArrayList<>();
        for (int i = 100; i < 200; i++) {
//            names.add("PV"+i);
            names.add("demoChannel_" + i);
            int v = (int)(Math.random()*AlarmSeverity.values().length);
            values.add(ValueFactory.newVDouble(((int)(Math.random()*100))/100.,
                    ValueFactory.newAlarm(AlarmSeverity.values()[v], "OK"),
                    ValueFactory.newTime(Timestamp.of(new Date(System.currentTimeMillis()-(long)(Math.random()*10000)))),
                    ValueFactory.displayNone()));

        }
        return VSnapshot.of(snapshot, names, values, Timestamp.of(new Date()));
    }

    @Override
    public boolean areBranchesSupported() {
        return true;
    }

    @Override
    public boolean areBaseLevelsSupported() {
        return false;
    }

    @Override
    public void synchronise() {
        pull();
        push();
    }

    private void pull() {
        //TODO pull all changes from current branch
    }

    private void push() {
        //TODO push all changes to the current branch
    }

    private void assureOnBranch(String branch) {
        //TODO make sure that selected branch is checkedout
    }

}
