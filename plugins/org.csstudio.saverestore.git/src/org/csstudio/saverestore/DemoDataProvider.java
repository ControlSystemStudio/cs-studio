package org.csstudio.saverestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

public class DemoDataProvider implements DataProvider {

    private static final String[] BASES = new String[] { "Ag_105_1p", "Sn_116_5n", "Rh_100_2p", "Sn_117_5n" };

    // "Lu_172_2p", "Co_58_1p", "C_10_4p", "Ni_56_2p", "I_126_2n", "Ce_141_3p", "Ce_141_3p", "Db_260_1p", "Co_61_2p",
    // "Cf_250_3p", "Sb_119_2p", "Rn_224_1n", "Ni_61_2p", "Te_127_4p", "B_150_2p", "Ag_106_1p", "Si_29_3p",
    // "Am_240_2p", "La_136_2p", "Pm_145_2p", "Mt_265_1n", "Li_4_1p", "Er_166_2p", "Eu_150_3p", "W_182_4p",
    // "Bh_26_2n2", "Er_168_2p", "Md_259_2p", "Ag_109_1p", "W_183_4p", "Po_207_3p", "Sb_122_3p", "Ni_61_2p",
    // "W_181_4p", "Pa_230_4p", "Ra_223_1p", "Th_233_4p", "Ta_183_5p", "Ir_189_3p", "Au_198_2p", "Sb_122_3p",
    // "La_136_3p", "Pt_196_1p", "Ds_271_3n", "Se_79_2n", "Cr_54_1p", "Zr_93_3p", "Eu_153_3p", "H_3_2n", "Mt_268_1n",
    // "Sc_46_3p", "Ca_41_2p", "Sr_89_2p", "Rn_222_3p", "F_17_2n", "Ti_65_4p", "S_32_3n", "Eu_153_3p", "Pd_107_2p",
    // "Tl_201_2p", "Po_208_4p", "Mt_270", "As_72_4p", "Zn_66_1p", "Cr_50_1p", "Sn_117_4n", "Ce_142_2p",
    // "Fr_222_1p", "Nb_92_5p", "Cr_51_1p", "K_36_4p", "Hf_179_4p", "Re_188_4p", "Th_230_3p", "N_15_3n", "Nb_92_4p",
    // "Os_187_3p", "Pm_142_3p", "S_31_2n", "Au_196_3p", "Lu_173_3p", "W_183_3p", "Nd_143_2p", "Pd_104_1p",
    // "Pr_142_2p", "Rb_85_2n", "Tm_166_2p", "Ta_179_5p", "Ra_223_1p", "Mt_265_1n", "Fm_257_2p", "Hg_199_2p",
    // "Si_27_4p", "Ne_17_1n", "Tc_101_4p", "Cf_249_2p" };

    public DemoDataProvider() {
    }

    @Override
    public void initialise() {

    }

    @Override
    public Branch[] getBranches() {
        return new Branch[] { new Branch("master", "master") };
    }

    @Override
    public BaseLevel[] getBaseLevels(Branch branch) {
        List<BaseLevel> isotopes = new ArrayList<>();
        for (String s : BASES) {
            isotopes.add(new BaseLevel(null, s, s));
        }
        // Element[] elements = Element.values();
        // for (int i = 0; i < 100; i++) {
        // Element e = elements[(int)(Math.random()*elements.length)];
        // int n = e.commonNeutrons + (int)(Math.random()*6)-3;
        // int c = e.commonCharge + (int)(Math.random()*2)-1;
        // Isotope is = Isotope.of(e,n,c);
        // if (isotopes.contains(is)) continue;
        // isotopes.add(is);
        // }
        return isotopes.toArray(new BaseLevel[isotopes.size()]);
    }

    @Override
    public BeamlineSet[] getBeamlineSets(Optional<BaseLevel> baseLevel, Branch branch) {
        List<BeamlineSet> beamlineSets = new ArrayList<>();
        String id = "org.csstudio.saverestore.git.dataprovider1";

        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Front End", "All PVs" }, id));
        beamlineSets
            .add(new BeamlineSet(branch, baseLevel, new String[] { "Linac Segments", "Seg 1", "Correctors" }, id));
        beamlineSets
            .add(new BeamlineSet(branch, baseLevel, new String[] { "Linac Segments", "Seg 1", "Quadrupoles" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Linac Segments", "Seg 1", "Others" }, id));
        beamlineSets
            .add(new BeamlineSet(branch, baseLevel, new String[] { "Linac Segments", "Seg 2", "Correctors" }, id));
        beamlineSets
            .add(new BeamlineSet(branch, baseLevel, new String[] { "Linac Segments", "Seg 3", "Correctors" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Folding Segments", "Seg 1" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Folding Segments", "Seg 2" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Production Target Systems", "Sys 1" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Production Target Systems", "Sys 2" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Production Target Systems", "Sys 3" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Fragment Separator", "Set 1" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Fragment Separator", "Set 2" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Fragment Separator", "Set 3" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Fast Beam Area", "All PVs" }, id));
        beamlineSets.add(new BeamlineSet(branch, baseLevel, new String[] { "Reaccelerated Beam Area", "All PVs" }, id));

        // for (int i = 0; i < 100; i++) {
        // BeamlineSet set = new BeamlineSet(branch, isotope, new String[]{"Front End","Set" +i + " "
        // + isotope.element.symbol});
        // beamlineSets.add(set);
        // }
        // beamlineSets.add(new BeamlineSet(branch, isotope, new String[]{"Set" + " " + isotope.element.symbol}));
        Collections.sort(beamlineSets);

        return beamlineSets.toArray(new BeamlineSet[beamlineSets.size()]);
    }

    @Override
    public Snapshot[] getSnapshots(BeamlineSet set, boolean all, Optional<Snapshot> fromThisOneBack) {
        List<Snapshot> snapshots = new ArrayList<>();
        if (set != null) {
            long time = System.currentTimeMillis();
            snapshots.add(
                new Snapshot(set, new Date(time), "Aw, the poor puddy tat! He fall down and go... BOOM!", "Tweety"));
            time -= 22400000;
            snapshots.add(new Snapshot(set, new Date(time), "I did, I did taw a puddy tat", "Tweety"));
            time -= 33400000;
            snapshots.add(new Snapshot(set, new Date(time), "Sufferin succotash", "Sylvester"));
            time -= 33400000;
            snapshots.add(new Snapshot(set, new Date(time), "I taw I taw a puddy tat", "Tweety"));
            time -= 12400000;
            snapshots.add(new Snapshot(set, new Date(time), "What's up, doc", "Bugs Bunny"));
            time -= 12400000;
            snapshots.add(new Snapshot(set, new Date(time), "Wabbit Season!", "Daffy Duck"));
            time -= 12400000;
            snapshots.add(new Snapshot(set, new Date(time), "Mine mine mine! It's all mine!", "Daffy Duck"));
            time -= 12400000;
            snapshots.add(new Snapshot(set, new Date(time), "Be vewy vewy quiet, I'm hunting wabbits!, He-e-e-e-e!",
                "Elmer Fudd"));

            Collections.sort(snapshots);
        }
        return snapshots.toArray(new Snapshot[snapshots.size()]);
    }

    @Override
    public BeamlineSetData getBeamlineSetContent(BeamlineSet set) {
        List<String> pvList = new ArrayList<>();
        for (int i = 100; i < 200; i++) {
            pvList.add("PV" + i);
        }
        return new BeamlineSetData(set, pvList, new ArrayList<>(0), new ArrayList<>(0),
            "One very special beamline set.");

    }

    @Override
    public String createNewBranch(Branch originalBranch, String newBranchName) {
        System.out.println("Requested to create a new branch: " + newBranchName);
        return newBranchName;
    }

    @Override
    public BeamlineSetData saveBeamlineSet(BeamlineSetData set, String comment) {
        System.out.println("Requested to save the beamline set: " + comment);
        return set;
    }

    @Override
    public VSnapshot saveSnapshot(VSnapshot data, String comment) {
        System.out.println("Successfully stored");
        return data;

    }

    @Override
    public Snapshot tagSnapshot(Snapshot snapshot, Optional<String> tagName, Optional<String> tagMessage) {
        System.out.println("Snapshot tagged");
        return snapshot;
    }

    @Override
    public VSnapshot getSnapshotContent(Snapshot snapshot) {
        List<String> names = new ArrayList<>();
        List<VType> values = new ArrayList<>();
        List<Boolean> selected = new ArrayList<>();
        names.add("demoChannel_98");
        int v = (int) (Math.random() * AlarmSeverity.values().length);
        values.add(ValueFactory.newVBoolean(true, ValueFactory.newAlarm(AlarmSeverity.values()[v], "OK"),
            ValueFactory.newTime(Timestamp.of(new Date(System.currentTimeMillis() - (long) (Math.random() * 10000))))));

        names.add("demoChannel_99");
        v = (int) (Math.random() * AlarmSeverity.values().length);
        values.add(ValueFactory.newVEnum(1, Arrays.asList("Test", "Off", "On", "Active"),
            ValueFactory.newAlarm(AlarmSeverity.values()[v], "OK"),
            ValueFactory.newTime(Timestamp.of(new Date(System.currentTimeMillis() - (long) (Math.random() * 10000))))));

        for (int i = 100; i < 200; i++) {
            // names.add("PV"+i);
            names.add("demoChannel_" + i);
            v = (int) (Math.random() * AlarmSeverity.values().length);
            values.add(ValueFactory.newVDouble(((int) (Math.random() * 100)) / 100.,
                ValueFactory.newAlarm(AlarmSeverity.values()[v], "OK"),
                ValueFactory
                    .newTime(Timestamp.of(new Date(System.currentTimeMillis() - (long) (Math.random() * 10000)))),
                ValueFactory.displayNone()));
            selected.add(Boolean.TRUE);

        }
        return new VSnapshot(snapshot, names, selected, values, new ArrayList<>(0), new ArrayList<>(0),
            new ArrayList<>(0), Timestamp.of(new Date()));
    }

    @Override
    public boolean areBranchesSupported() {
        return false;
    }

    @Override
    public boolean areBaseLevelsSupported() {
        return false;
    }

    @Override
    public boolean synchronise() {
        pull();
        push();
        return true;
    }

    private void pull() {
        // TODO pull all changes from current branch
    }

    private void push() {
        // TODO push all changes to the current branch
    }

    @Override
    public void addCompletionNotifier(CompletionNotifier notifier) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeCompletionNotifier(CompletionNotifier notifier) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isReinitSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean reinitialise() throws DataProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean deleteBeamlineSet(BeamlineSet set, String comment) throws DataProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean importData(BeamlineSet source, Branch toBranch, Optional<BaseLevel> toBaseLevel, ImportType type)
        throws DataProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Snapshot[] findSnapshots(String expression, Branch branch, List<SearchCriterion> criteria)
        throws DataProviderException {
        return new Snapshot[0];
    }
}
