package org.csstudio.saverestore.git;

import static org.csstudio.saverestore.git.CredentialUtilities.getCredentials;
import static org.csstudio.saverestore.git.CredentialUtilities.toCredentialsProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.csstudio.saverestore.BeamlineSetContent;
import org.csstudio.saverestore.DataProvider.ImportType;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.FileUtilities;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.SnapshotContent;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.git.Result.ChangeType;
import org.csstudio.ui.fx.util.Credentials;
import org.diirt.util.time.Timestamp;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.AndRevFilter;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.CommitterRevFilter;
import org.eclipse.jgit.revwalk.filter.MessageRevFilter;
import org.eclipse.jgit.revwalk.filter.OrRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

/**
 * <code>GitManager<code> provide access to the git features required by the save and restore application.
 *
 * @author <a href="mailto:miha.novak@cosylab.com">Miha Novak</a>
 */
public class GitManager {

    @FunctionalInterface
    private static interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c) throws IOException, GitAPIException;
    }

    private static final String GIT_PATH_DELIMITER = "/";
    // tags of git specific parameters for the snapshot
    private static final String PARAM_GIT_REVISION = "gitRevision";
    private static final String PARAM_GIT_TAG_NAME = "gitTagName";
    private static final String PARAM_TAG_CREATOR = "tagCreator";
    private static final String UNKNOWN = "UNKNOWN";

    // the pattern describing all forbidden characters in git tag
    private static final Pattern TAG_PATTERN = Pattern
        .compile("[\\x00-\\x1F\\x7E-\\xFF()~\\^: /?*\\[\\]@\\\\{\\.{2}]+");

    private Git git;
    private Repository repository;
    private File repositoryPath;
    private boolean automatic = true;
    private boolean localOnly = false;

    /**
     * Creates a new manager, but does not initialise it. {@link #initialise(URI, File)} has to be called before
     * anything can be done with this manager.
     */
    public GitManager() {
        // default constructor to allow extensions
    }

    /**
     * Construct a new manager and initialise it using the provided parameters.
     *
     * @param remoteRepo the url to remote repository
     * @param destinationDirectory the local folder into which the remote repository is cloned
     * @throws GitAPIException in case of an error
     */
    public GitManager(URI remoteRepo, File destinationDirectory) throws GitAPIException {
        initialise(remoteRepo, destinationDirectory);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    /**
     * Dispose of all resources allocated by this manager.
     */
    public void dispose() {
        try {
            if (repository != null) {
                repository.close();
                repository = null;
            }
            if (git != null) {
                git.close();
                git = null;
            }
        } catch (Exception e) {
            SaveRestoreService.LOGGER.log(Level.SEVERE, "Git cleanup error.", e);
        }
    }

    /**
     * Enable or disable automatic synchronisation. When enabled new files are automatically pushed to the remote
     * repository and remote changes pulled in as well.
     *
     * @param automatic true if automatic synchronisation should be enabled
     */
    public synchronized void setAutomaticSynchronisation(boolean automatic) {
        if (localOnly && automatic) {
            return;
        }
        this.automatic = automatic;
    }

    /**
     * Initialise this manager by establishing the clone of the remote repository or pull if the destination directory
     * is already a clone of the remote repository. If the destination already exists and its origin is not the same as
     * the given remote repository, the destination directory is first erased and then a fresh clone is made.
     *
     * @param remoteRepository the url to remote git repository
     * @param destinationDirectory the local folder into which the clone will be made
     * @return true if initialisation was successful or false otherwise (cancelled due to lack of permissions)
     * @throws GitAPIException in case of an error
     */
    public synchronized boolean initialise(URI remoteRepository, File destinationDirectory) throws GitAPIException {
        if (!internalInitialise(remoteRepository, destinationDirectory)) {
            deleteFolder(destinationDirectory);
            return internalInitialise(remoteRepository, destinationDirectory);
        }
        return true;
    }

    private synchronized boolean internalInitialise(URI remoteRepository, File destinationDirectory)
        throws GitAPIException {
        repositoryPath = destinationDirectory;
        dispose();
        if (new File(repositoryPath, ".git").exists()) {
            this.git = Git.init().setDirectory(repositoryPath).call();
            this.repository = git.getRepository();
            StoredConfig config = this.repository.getConfig();
            String url = config.getString("remote", "origin", "url");
            if (url == null || !url.equals(remoteRepository.toString())) {
                dispose();
                return false;
            }

            try {
                setBranch(new Branch());
                Credentials credentials = getCredentials(Optional.empty());
                if (credentials != null) {
                    pull(credentials);
                }
            } catch (GitAPIException | IOException e) {
                SaveRestoreService.LOGGER.log(Level.WARNING, e,
                    () -> "Git repository " + remoteRepository + " is not accessible.");
                localOnly = true;
                setAutomaticSynchronisation(false);
            }
        } else {
            Credentials credentials = getCredentials(Optional.empty());
            if (credentials == null) {
                return true;
            }
            CloneCommand cloneCommand = Git.cloneRepository().setURI(remoteRepository.toString())
                .setDirectory(repositoryPath);

            while (true) {
                cloneCommand.setCredentialsProvider(toCredentialsProvider(credentials));
                try {
                    this.git = cloneCommand.call();
                    this.repository = git.getRepository();
                    break;
                } catch (TransportException e) {
                    if (isNotAuthorised(e)) {
                        credentials = getCredentials(Optional.ofNullable(credentials));
                        if (credentials == null) {
                            break;
                        }
                    } else {
                        throw e;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Switch the working branch of the repository to the given branch. If the current branch is already the requested
     * branch, nothing happens.
     *
     * @param branch the branch to switch to
     * @throws GitAPIException if there was an exception during the checkout
     * @throws IOException if the current branch cannot be determined
     */
    public synchronized void setBranch(Branch branch) throws GitAPIException, IOException {
        if (!branch.getShortName().equals(repository.getBranch())) {
            Ref ref = git.checkout().setName(branch.getFullName()).setUpstreamMode(SetupUpstreamMode.TRACK).call();
            if (ref == null) {
                // local branch does not exist. create it
                git.branchCreate().setName(branch.getShortName()).call();
                git.checkout().setName(branch.getShortName()).setUpstreamMode(SetupUpstreamMode.TRACK).call();
            }
        }
    }

    /**
     * Returns the list of all branches in the repository.
     *
     * @return the list of branches
     * @throws GitAPIException if the branches could not be read
     */
    public synchronized List<Branch> getBranches() throws GitAPIException {
        List<Ref> branchesRef = git.branchList().setListMode(ListMode.ALL).call();
        List<Branch> branches = new ArrayList<>(branchesRef.size());
        for (Ref b : branchesRef) {
            String name = b.getName();
            if ("HEAD".equals(name)) {
                continue;
            }
            Branch branch;
            if (name.indexOf('/') > 0) {
                branch = new Branch(name, name.substring(name.lastIndexOf('/') + 1));
            } else {
                branch = new Branch(name, name);
            }
            if (!branches.contains(branch)) {
                branches.add(branch);
            }
        }
        Collections.sort(branches);
        return branches;
    }

    /**
     * Pull all changes from remote repository and push all local changes to remote.
     *
     * @param cp credentials provider, if not provided it will be retrieved via a UI
     * @return true if changes in the local repository were made
     * @throws GitAPIException if there is an error during push or pull
     */
    public synchronized boolean synchronise(Optional<Credentials> cp) throws GitAPIException {
        Credentials c = cp.isPresent() ? cp.get() : getCredentials(Optional.empty());
        if (c != null) {
            Object[] obj = pull(c);
            push((Credentials) obj[0], true);
            return (Boolean) obj[1];
        }
        return false;
    }

    /**
     * Import data from the given beamline set <code>source</code> into the branch and base level provided. The beamline
     * set in this case does not to be a true beamline set - it can also point to a folder containing multiple beamline
     * sets. All files from the source or below it are copied to the branch and base level. If the import type specifies
     * the corresponding snapshots are also imported.
     *
     * @param source the source of data (file or folder)
     * @param toBranch the destination branch
     * @param toBaseLevel the destination base level
     * @param type the type of import (only beamline set, beamline set and last snapsho, beamline set and all snapshots)
     * @return true if successful combined with the type of change that was done to the repository
     * @throws GitAPIException if there was an error executing git actions
     * @throws IOException in case of an IO error
     * @throws ParseException if the snapshot content could not be parsed
     */
    public synchronized Result<Boolean> importData(BeamlineSet source, Branch toBranch, Optional<BaseLevel> toBaseLevel,
        ImportType type) throws GitAPIException, IOException, ParseException {
        boolean oldAutomatic = this.automatic;
        setAutomaticSynchronisation(false);
        Credentials cred = getCredentials(Optional.empty());
        Object[] obj = pull(cred);
        ChangeType change = (Boolean) obj[1] ? ChangeType.PULL : ChangeType.SAVE;
        cred = (Credentials) obj[0];
        try {
            if (source.getName().isEmpty()) {
                // it is a folder
                List<BeamlineSet> sets = getBeamlineSets(source.getBaseLevel(), source.getBranch(),
                    Optional.of(source.getPathAsString()));
                for (BeamlineSet s : sets) {
                    importBeamlineSet(s, toBaseLevel, toBranch, type, cred);
                }
            } else {
                // single beamline set
                importBeamlineSet(source, toBaseLevel, toBranch, type, cred);
            }
        } finally {
            setAutomaticSynchronisation(oldAutomatic);
        }
        push(cred, true);
        return new Result<>(true, change);
    }

    private void importBeamlineSet(BeamlineSet source, Optional<BaseLevel> toBaseLevel, Branch toBranch,
        ImportType type, Credentials cred) throws GitAPIException, IOException, ParseException {
        BeamlineSetData data = loadBeamlineSetData(source, Optional.empty());
        BeamlineSet newSet = new BeamlineSet(toBranch, toBaseLevel, source.getPath(), source.getDataProviderId());
        BeamlineSetData newData = new BeamlineSetData(newSet, data.getPVList(), data.getReadbackList(),
            data.getDeltaList(), data.getDescription());
        String comment = "Imported from " + source.getBranch().getShortName() + "/" + source.getBaseLevel().get() + "/"
            + source.getPathAsString();
        saveBeamlineSet(newData, comment, cred);
        if (type == ImportType.LAST_SNAPSHOT) {
            List<Snapshot> list = getSnapshots(source, 1, Optional.empty());
            if (!list.isEmpty()) {
                Snapshot snapshot = list.get(0);
                VSnapshot snp = loadSnapshotData(snapshot);
                VSnapshot newSnp = new VSnapshot(new Snapshot(newSet), snp.getNames(), snp.getSelected(),
                    snp.getValues(), snp.getReadbackNames(), snp.getReadbackValues(), snp.getDeltas(),
                    snp.getTimestamp());
                saveSnapshot(newSnp, snapshot.getComment(), snapshot.getDate(), snapshot.getOwner());
            }
        } else if (type == ImportType.ALL_SNAPSHOTS) {
            List<Snapshot> list = getSnapshots(source, 0, Optional.empty());
            for (Snapshot s : list) {
                VSnapshot snp = loadSnapshotData(s);
                VSnapshot newSnp = new VSnapshot(new Snapshot(newSet), snp.getNames(), snp.getSelected(),
                    snp.getValues(), snp.getReadbackNames(), snp.getReadbackValues(), snp.getDeltas(),
                    snp.getTimestamp());
                saveSnapshot(newSnp, s.getComment(), s.getDate(), s.getOwner());
            }
        }
    }

    /**
     * Reads and returns the list of all base levels in the current branch.
     *
     * @param branch the branch from which to retrieve base levels
     * @return the list of base levels
     */
    public synchronized List<BaseLevel> getBaseLevels(Branch branch) throws GitAPIException, IOException {
        setBranch(branch);
        File[] files = repositoryPath.listFiles();
        List<BaseLevel> baseLevels = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory() && f.getName().charAt(0) != '.') {
                    baseLevels.add(new BaseLevel(branch, f.getName(), f.getName()));
                }
            }
        }
        return baseLevels;
    }

    /**
     * Returns the list of all available beamline sets in the current branch. The search is done by reading the data on
     * the file system, not by searching the git repository.
     *
     * @param baseLevel the base level for which the beamline sets are requested (optional, if base levels are not used)
     * @param branch the branch to switch to
     * @return the list of beamline sets
     * @throws IOException if the current branch could not be retrieved
     */
    public synchronized List<BeamlineSet> getBeamlineSets(Optional<BaseLevel> baseLevel, Branch branch)
        throws IOException, GitAPIException {
        return getBeamlineSets(baseLevel, branch, Optional.empty());
    }

    /**
     * Returns the list of all available beamline sets in the current branch. The search is done by reading the data on
     * the file system, not by searching the git repository.
     *
     * @param baseLevel the base level for which the beamline sets are requested (optional, if base levels are not used)
     * @param branch the branch to switch to
     * @param basePath the base path at which to look for the beamline sets
     * @return the list of beamline sets
     * @throws IOException if the current branch could not be retrieved
     */
    private List<BeamlineSet> getBeamlineSets(Optional<BaseLevel> baseLevel, Branch branch, Optional<String> basePath)
        throws IOException, GitAPIException {
        setBranch(branch);
        List<BeamlineSet> descriptorList = new ArrayList<>();
        File[] files = repositoryPath.listFiles();
        if (files != null) {
            String base = baseLevel.isPresent() ? baseLevel.get().getStorageName() : null;
            for (File f : files) {
                if (f.getName().equals(base)) {
                    File b = new File(f, FileType.BEAMLINE_SET.directory);
                    if (basePath.isPresent()) {
                        b = new File(b, basePath.get());
                    }
                    List<File> setFiles = new ArrayList<>();
                    gatherBeamlineSets(b, setFiles);
                    String path = repositoryPath.getAbsolutePath();
                    int length = path.length();
                    if (!(path.charAt(length - 1) == '/' || path.charAt(length - 1) == '\\')) {
                        length++;
                    }
                    for (File bf : setFiles) {
                        String s = bf.getAbsolutePath().substring(length);
                        String[] filePathArray = convertStringToPath(s, baseLevel);
                        if (filePathArray.length > 0) {
                            BeamlineSet beamlineSet = new BeamlineSet(branch, baseLevel, filePathArray,
                                GitDataProvider.ID);
                            descriptorList.add(beamlineSet);
                        }
                    }
                    break;
                }
            }
        }
        return descriptorList;
    }

    /**
     * Loads and returns the beamline set data for the provided descriptor and the git revision. If git revision is not
     * specified the head revision is returned.
     *
     * @param descriptor the descriptor for which the data should be returned
     * @param revision optional revision number; if not given head revision is used
     * @return the content of the beamline set file
     * @throws IOException if there was an error reading the contentof the file
     * @throws GitAPIException if setting the branch failed
     */
    public synchronized BeamlineSetData loadBeamlineSetData(BeamlineSet descriptor, Optional<String> revision)
        throws IOException, GitAPIException {
        setBranch(descriptor.getBranch());
        String path = convertPathToString(descriptor, FileType.BEAMLINE_SET);
        try {
            return loadFile(revision, path, FileType.BEAMLINE_SET, BeamlineSetData.class, descriptor);
        } catch (ParseException e) {
            // cannot happen, but just in case, make a log
            SaveRestoreService.LOGGER.log(Level.SEVERE, "Unexpected error when loading beamline set content", e);
            return null;
        }
    }

    /**
     * Returns the list of all snapshots for the given beamline set.
     *
     * @param beamlineSet the beamline set for which the snapshots are requested
     * @param numberOfRevisions the maximum number of snapshot revisions to load
     * @param fromThisOneBack the revision at which to start and then going back
     * @return the list of all snapshot revisions for this beamline set
     * @throws IOException if the commits could not be read
     * @throws GitAPIException if the commits could not be read
     */
    public synchronized List<Snapshot> getSnapshots(BeamlineSet beamlineSet, int numberOfRevisions,
        Optional<Snapshot> fromThisOneBack) throws IOException, GitAPIException {
        setBranch(beamlineSet.getBranch());
        List<Snapshot> snapshots = new ArrayList<>();

        String path = convertPathToString(beamlineSet, FileType.SNAPSHOT);
        path = path.replace(FileType.BEAMLINE_SET.directory, FileType.SNAPSHOT.directory);
        path = path.replace(FileType.BEAMLINE_SET.suffix, FileType.SNAPSHOT.suffix);

        String rev = fromThisOneBack.isPresent() ? fromThisOneBack.get().getParameters().get(PARAM_GIT_REVISION) : null;
        List<RevCommit> fileRevisions = findCommitsFor(path, numberOfRevisions, Optional.ofNullable(rev));
        Map<String, RevTag> tags = loadTagsForRevisions(fileRevisions);
        String branch = beamlineSet.getBranch().getShortName();
        for (RevCommit commit : fileRevisions) {
            String revision = commit.getName();
            if (rev != null && rev.equals(revision)) {
                // do not return the revision that the client already knows
                continue;
            }
            MetaInfo meta = getMetaInfoFromCommit(commit);
            Map<String, String> parameters = new HashMap<>();
            parameters.put(PARAM_GIT_REVISION, revision);
            insertTagData(tags.get(revision), parameters, revision, branch);
            Snapshot snapshot = new Snapshot(beamlineSet, meta.timestamp, meta.comment, meta.creator, parameters);
            snapshots.add(snapshot);
        }
        return snapshots;
    }

    /**
     * Loads the data from the snapshot revision.
     *
     * @param snapshot the snapshot descriptor to read
     * @return the content of the snapshot
     * @throws ParseException if
     * @throws IOException
     */
    public synchronized VSnapshot loadSnapshotData(Snapshot snapshot)
        throws ParseException, IOException, GitAPIException {
        setBranch(snapshot.getBeamlineSet().getBranch());
        String path = convertPathToString(snapshot.getBeamlineSet(), FileType.SNAPSHOT);
        return loadFile(Optional.ofNullable(snapshot.getParameters().get(PARAM_GIT_REVISION)), path, FileType.SNAPSHOT,
            VSnapshot.class, snapshot);
    }

    /**
     * Save the beamline set and commit it.
     *
     * @param data the contents of the beamline set file
     * @param comment the commit comment
     * @return the saved beamline set and change type describing what kind of updates were made to the repository
     * @throws IOException if writing to the file failed
     * @throws GitAPIException if commiting the file failed
     */
    public synchronized Result<BeamlineSetData> saveBeamlineSet(BeamlineSetData data, String comment)
        throws IOException, GitAPIException {
        return saveBeamlineSet(data, comment, null);
    }

    /**
     * Save the beamline set and commit it.
     *
     * @param data the contents of the beamline set file
     * @param comment the commit comment
     * @param cred optional credentials to use, if null a popup will be displayed
     * @return the saved beamline set and change type describing what kind of updates were made to the repository
     * @throws IOException if writing to the file failed
     * @throws GitAPIException if commiting the file failed
     */
    private Result<BeamlineSetData> saveBeamlineSet(BeamlineSetData data, String comment, Credentials cred)
        throws IOException, GitAPIException {
        BeamlineSetData bsd = null;
        ChangeType change = ChangeType.NONE;
        save: {
            Credentials cp = cred == null ? getCredentials(Optional.empty()) : cred;
            if (cp != null) {
                change = ChangeType.SAVE;
                if (automatic) {
                    Object[] obj = pull(cp);
                    cp = (Credentials) obj[0];
                    change = (Boolean) obj[1] ? ChangeType.PULL : change;
                    if (cp == null) {
                        break save;
                    }
                }
                setBranch(data.getDescriptor().getBranch());
                String relativePath = convertPathToString(data.getDescriptor(), FileType.BEAMLINE_SET);
                writeToFile(relativePath, repositoryPath, FileType.BEAMLINE_SET, data);
                commit(relativePath, new MetaInfo(comment, cp.getUsername(), UNKNOWN, null, null));
                if (automatic) {
                    push(cp, false);
                }
                bsd = data;
            }
        }
        return new Result<>(bsd, change);
    }

    /**
     * Delete the beamline set from the repository. Beamline set and snapshot files are deleted.
     *
     * @param data the set to delete
     * @param comment the comment why the set was deleted
     * @return the result of the action
     * @throws IOException in case of an error
     * @throws GitAPIException in case of an error
     */
    public synchronized Result<BeamlineSet> deleteBeamlineSet(BeamlineSet set, String comment)
        throws IOException, GitAPIException {
        BeamlineSet deleted = null;
        ChangeType change = ChangeType.NONE;
        delete: {
            Credentials cp = getCredentials(Optional.empty());
            if (cp != null) {
                setBranch(set.getBranch());
                change = ChangeType.SAVE;
                if (automatic) {
                    Object[] obj = pull(cp);
                    cp = (Credentials) obj[0];
                    change = (Boolean) obj[1] ? ChangeType.PULL : change;
                    if (cp == null) {
                        break delete;
                    }
                }
                String relativePath = convertPathToString(set, FileType.BEAMLINE_SET);
                if (deleteFile(relativePath, repositoryPath)) {
                    deleted = set;
                    commit(relativePath, new MetaInfo(comment, cp.getUsername(), UNKNOWN, null, null));
                    // delete also the snapshot file
                    relativePath = convertPathToString(set, FileType.SNAPSHOT);
                    deleteFile(relativePath, repositoryPath);
                    commit(relativePath, new MetaInfo(comment, cp.getUsername(), null, null, null));
                    if (automatic) {
                        push(cp, false);
                    }
                }
            }
        }
        return new Result<>(deleted, change);
    }

    /**
     * Save the snapshot data and commit the file as a new revision.
     *
     * @param snapshot the snapshot data
     * @param comment the comment for the commit
     * @return saved snapshot and change type describing what kind of updates were made to the repository
     * @throws IOException if writing the file failed
     * @throws GitAPIException if committing the file failed
     */
    public synchronized Result<VSnapshot> saveSnapshot(VSnapshot snapshot, String comment)
        throws IOException, GitAPIException {
        return saveSnapshot(snapshot, comment, null, null);
    }

    /**
     * Save the snapshot data and commit the file as a new revision.
     *
     * @param snapshot the snapshot data
     * @param comment the comment for the commit
     * @param time the time at which the snapshot commit should be authored
     * @param user the username of the commit author
     * @return saved snapshot and change type describing what kind of updates were made to the repository
     * @throws IOException if writing the file failed
     * @throws GitAPIException if committing the file failed
     */
    private Result<VSnapshot> saveSnapshot(VSnapshot snapshot, String comment, Date time, String user)
        throws IOException, GitAPIException {
        VSnapshot vsnp = null;
        ChangeType change = ChangeType.NONE;
        save: {
            Credentials cp = getCredentials(Optional.empty());
            if (cp != null) {
                change = ChangeType.SAVE;
                if (automatic) {
                    Object[] obj = pull(cp);
                    cp = (Credentials) obj[0];
                    change = (Boolean) obj[1] ? ChangeType.PULL : change;
                    if (cp == null) {
                        break save;
                    }
                }
                setBranch(snapshot.getBeamlineSet().getBranch());
                Snapshot descriptor = snapshot.getSnapshot().get();
                String relativePath = convertPathToString(descriptor.getBeamlineSet(), FileType.SNAPSHOT);
                writeToFile(relativePath, repositoryPath, FileType.SNAPSHOT, snapshot);
                MetaInfo info = commit(relativePath,
                    new MetaInfo(comment, user == null ? cp.getUsername() : user, UNKNOWN, time, null));
                if (automatic) {
                    push(cp, false);
                }
                Map<String, String> parameters = new HashMap<>();
                parameters.put(PARAM_GIT_REVISION, info.revision);
                Snapshot snp = new Snapshot(descriptor.getBeamlineSet(), info.timestamp, info.comment, info.creator,
                    parameters);
                vsnp = new VSnapshot(snp, snapshot.getNames(), snapshot.getSelected(), snapshot.getValues(),
                    snapshot.getReadbackNames(), snapshot.getReadbackValues(), snapshot.getDeltas(),
                    snapshot.getTimestamp());
            }
        }
        return new Result<>(vsnp, change);
    }

    /**
     * Creates a new branch with the given name. The new branch is based on the current one.
     *
     * @param oldBranch the base branch from which we want to create a new one
     * @param branch the branch to create
     * @throws GitAPIException in case of an error
     * @throws IOException in case of an error
     */
    public synchronized Branch createBranch(Branch oldBranch, String branch) throws GitAPIException, IOException {
        setBranch(oldBranch);
        git.branchCreate().setName(branch).call();
        return new Branch(branch, branch);
    }

    /**
     * Save the snapshot data and commit the file as a new revision.
     *
     * @param snapshot the snapshot (should contain the git revision number)
     * @param name the name of the tag
     * @param message the message for the tag
     * @return tagged snapshot and change type describing what kind of updates were made to the repository
     * @throws IOException if writing the file failed
     * @throws GitAPIException if committing the file failed
     */
    public synchronized Result<Snapshot> tagSnapshot(Snapshot snapshot, String name, String message)
        throws IOException, GitAPIException, DataProviderException {
        if (name != null && TAG_PATTERN.matcher(name).replaceAll("").length() != name.length()) {
            throw new DataProviderException("Tag name contains invalid characters.");
        }
        Snapshot snp = null;
        ChangeType change = ChangeType.NONE;
        tag: {
            Credentials cp = getCredentials(Optional.empty());
            if (cp != null) {
                setBranch(snapshot.getBeamlineSet().getBranch());
                change = ChangeType.SAVE;
                if (automatic) {
                    Object[] obj = pull(cp);
                    cp = (Credentials) obj[0];
                    change = (Boolean) obj[1] ? ChangeType.PULL : change;
                    if (cp == null) {
                        break tag;
                    }
                }
                // remove the existing tag
                String revision = snapshot.getParameters().get(PARAM_GIT_REVISION);
                RevCommit commit = getCommitFromRevision(revision);
                RevTag existingTag = loadTagsForRevisions(Arrays.asList(commit)).get(revision);
                if (existingTag != null) {
                    git.tagDelete().setTags(existingTag.getTagName()).call();
                    RefSpec refSpec = new RefSpec().setSource(null)
                        .setDestination("refs/tags/" + existingTag.getTagName());
                    git.push().setCredentialsProvider(toCredentialsProvider(cp)).setRefSpecs(refSpec).call();
                }

                Map<String, String> parameters = new HashMap<>();
                parameters.put(PARAM_GIT_REVISION, revision);
                if (name != null && !name.isEmpty()) {
                    String gitTagName = composeTagName(snapshot.getBeamlineSet().getBranch(),
                        snapshot.getBeamlineSet().getBaseLevel(), snapshot.getBeamlineSet().getPath(), name);
                    PersonIdent tagger = new PersonIdent(cp.getUsername(), UNKNOWN);
                    git.tag().setName(gitTagName).setMessage(message).setTagger(tagger).setObjectId(commit).call();
                    if (automatic) {
                        push(cp, true);
                    }
                    parameters.put(PARAM_GIT_TAG_NAME, gitTagName);
                    parameters.put(Snapshot.TAG_NAME, name);
                    parameters.put(Snapshot.TAG_MESSAGE, message);
                    parameters.put(PARAM_TAG_CREATOR, cp.getUsername());
                }
                snp = new Snapshot(snapshot.getBeamlineSet(), snapshot.getDate(), snapshot.getComment(),
                    snapshot.getOwner(), parameters);
            }
        }
        return new Result<>(snp, change);
    }

    /**
     * Load the tags for the list of revisions provided as parameter. The tags are returned in a map, where the key is
     * the revision name and the value is the actual tag object.
     *
     * @param revisions the list of revisions for which to load the tags
     * @return the map of all revision and tags pairs
     * @throws GitAPIException if there was an error loading the tags
     * @throws IOException in case of an IO error
     */
    private Map<String, RevTag> loadTagsForRevisions(List<RevCommit> revisions) throws GitAPIException, IOException {
        Map<String, Ref> tags = repository.getTags();
        Map<String, RevTag> ret = new HashMap<>();
        try (RevWalk walk = new RevWalk(repository)) {
            for (RevCommit rev : revisions) {
                String s = Git.wrap(repository).describe().setTarget(rev).call();
                Ref tt = tags.get(s);
                if (tt != null) {
                    RevTag t = walk.parseTag(tt.getObjectId());
                    ret.put(rev.getName(), t);
                }
            }
        }
        return ret;
    }

    /**
     * Commit the file stored under the relative path.
     *
     * @param relativePath the path to the file relative to the repository root
     * @param metaInfo meta information
     *
     * @return the actual meta information read back from the system
     * @throws GitAPIException if there is an error during commit
     */
    private MetaInfo commit(String relativePath, MetaInfo metaInfo) throws GitAPIException {
        git.add().addFilepattern(relativePath).call();
        CommitCommand command = git.commit().setMessage(metaInfo.comment);
        if (metaInfo.timestamp == null) {
            command.setCommitter(metaInfo.creator, metaInfo.eMail);
        } else {
            command.setCommitter(
                new PersonIdent(metaInfo.creator, metaInfo.eMail, metaInfo.timestamp, TimeZone.getTimeZone("GMT")));
        }
        RevCommit commit = command.call();
        return getMetaInfoFromCommit(commit);
    }

    /**
     * Push the local commits to remote repository.
     *
     * @param credentials credentials provider to use for pushing
     * @param pushTags true if tags should be pushed as well or false if tags can be skipped
     * @return credentials that worked
     * @throws GitAPIException if there was an error during push
     */
    private Credentials push(Credentials credentials, boolean pushTags) throws GitAPIException {
        if (localOnly) {
            return null;
        }
        Credentials cred = credentials;
        while (true) {
            try {
                git.push().setCredentialsProvider(toCredentialsProvider(cred)).call();
                break;
            } catch (TransportException e) {
                if (isNothingToPush(e)) {
                    // if there are no changes the message is Nothing to push
                    break;
                } else if (isNotAuthorised(e)) {
                    // if the authorisation failed repeat
                    cred = getCredentials(Optional.ofNullable(cred));
                    if (cred == null) {
                        return null;
                    }
                } else {
                    throw e;
                }
            }
        }
        while (pushTags) {
            // if tags should be pushed, they should be in a separate push command, because the command below pushes
            // only the tags and not the actual changes
            try {
                git.push().setCredentialsProvider(toCredentialsProvider(cred)).setPushTags().call();
                break;
            } catch (TransportException e) {
                if (isNothingToPush(e)) {
                    break;
                } else if (isNotAuthorised(e)) {
                    cred = getCredentials(Optional.ofNullable(cred));
                    if (cred == null) {
                        return null;
                    }
                } else {
                    throw e;
                }
            }
        }
        return cred;
    }

    /**
     * Pull the changes from remote repository.
     *
     * @param credentials the credentials to use when fetching and pull from remote repository
     * @return an array of size 2: credentials that worked or null if cancelled and a Boolean describing if there were
     *         any changes pulled from the remote repository
     * @throws GitAPIException if there was an error during pull
     */
    private Object[] pull(Credentials credentials) throws GitAPIException {
        if (localOnly) {
            return new Object[] { null, false };
        }
        Credentials cred = credentials;
        while (true) {
            try {
                FetchResult fetch = git.fetch().setCredentialsProvider(toCredentialsProvider(cred))
                    .setTagOpt(TagOpt.FETCH_TAGS).call();
                PullResult pull = git.pull().setCredentialsProvider(toCredentialsProvider(cred))
                    .setStrategy(MergeStrategy.THEIRS).call();
                boolean changed = !fetch.getTrackingRefUpdates().isEmpty()
                    || !pull.getFetchResult().getTrackingRefUpdates().isEmpty();
                return new Object[] { cred, changed };
            } catch (TransportException e) {
                if (isNotAuthorised(e)) {
                    cred = getCredentials(Optional.ofNullable(cred));
                    if (cred == null) {
                        return new Object[] { null, false };
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     * Retrieves the commit for given revision.
     *
     * @param revision revision
     *
     * @return commit for given revision.
     *
     * @throws IOException if exception occurs.
     */
    private RevCommit getCommitFromRevision(String revision) throws IOException {
        try (RevWalk revWalk = new RevWalk(repository)) {
            ObjectId commitId = repository.resolve(revision);
            return revWalk.parseCommit(commitId);
        }
    }

    /**
     * @return the head commit.
     *
     * @throws IOException if exception occurs
     */
    private RevCommit getHeadCommit() throws IOException {
        try (RevWalk revWalk = new RevWalk(repository)) {
            return revWalk.parseCommit(repository.resolve(Constants.HEAD));
        }
    }

    /**
     * Retrieves the specified number of revisions of the given file. If the requested number is less than 1, all
     * revisions are returned.
     *
     * @param filePath file path
     * @param numberOfSnapshots number of snapshots revisions to load
     * @param fromRevisionBack the revision at which to start
     *
     * @return all revisions of the given file.
     */
    private List<RevCommit> findCommitsFor(String filePath, int numberOfsnapshots, Optional<String> fromRevisionBack)
        throws GitAPIException, IOException {
        List<RevCommit> commitsList = new ArrayList<>();
        ObjectId obj = fromRevisionBack.isPresent() ? ObjectId.fromString(fromRevisionBack.get())
            : repository.resolve(Constants.HEAD);
        LogCommand log = git.log().add(obj).addPath(filePath);
        if (numberOfsnapshots > 0) {
            // in case we are not going from the head, increase the number of logs by one, because we
            // don't need to first revision, which is already the same as fromRevisionBack
            int num = fromRevisionBack.isPresent() ? numberOfsnapshots + 1 : numberOfsnapshots;
            log.setMaxCount(num);
        }
        Iterable<RevCommit> commits = log.call();
        // in theory diff is not needed here if everybody only used the Save and Restore application on this repository
        // however, if someone manually changed the path to a file there can be an issue. Doing a diff increases the
        // search time for ~70%
        try (RevWalk revWalk = new RevWalk(repository); ObjectReader objectReader = repository.newObjectReader()) {
            for (RevCommit commit : commits) {
                boolean renamed = false;
                AbstractTreeIterator oldTreeIterator = new EmptyTreeIterator();
                if (commit.getParents().length != 0) {
                    RevCommit parentCommit = revWalk.parseCommit(commit.getParents()[0].getId());
                    oldTreeIterator = new CanonicalTreeParser(null, objectReader, parentCommit.getTree());
                }
                AbstractTreeIterator newTreeIterator = new CanonicalTreeParser(null, objectReader, commit.getTree());
                try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                    diffFormatter.setRepository(repository);
                    diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
                    diffFormatter.setDetectRenames(true);
                    List<DiffEntry> diffs = diffFormatter.scan(oldTreeIterator, newTreeIterator);
                    for (DiffEntry diff : diffs) {
                        if (diff.getChangeType() == org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
                            || diff.getChangeType() == org.eclipse.jgit.diff.DiffEntry.ChangeType.RENAME) {
                            renamed = true;
                            break;
                        }
                    }
                }
                if (!renamed) {
                    commitsList.add(commit);
                }
            }
        }
        return commitsList;
    }

    /**
     * Find all snapshots that are tagged and their tag name or message matches the given partial name or message. The
     * partial name or message can also be a regular expression.
     *
     * @param partialTagNameOrMessage the partial message or name to match
     * @param branch the branch on which the tag has to be located to be accepted
     * @param start only commits done after start will be accepted
     * @param end only commits done before end will be accepted
     * @return the list of tags
     * @throws GitAPIException in case of a git error
     * @throws IOException in case of an IO error
     */
    public synchronized List<Snapshot> findSnapshotsByTag(String partialTagNameOrMessage, Branch branch,
        Optional<Date> start, Optional<Date> end) throws GitAPIException, IOException {
        final Pattern pattern = Pattern.compile(".*" + partialTagNameOrMessage.toLowerCase(Locale.UK) + ".*");
        return findSnapshotsByTag(branch, start, end, (w, r, n) -> {
            String tagName = n.substring(n.indexOf('(') + 1, n.length() - 1).toLowerCase(Locale.UK);
            if (pattern.matcher(tagName).matches()) {
                return w.parseTag(r.getObjectId());
            } else {
                RevTag tag = w.parseTag(r.getObjectId());
                String message = tag.getFullMessage().toLowerCase(Locale.UK).replace("\n", " ");
                return pattern.matcher(message).matches() ? tag : null;
            }
        });
    }

    /**
     * Find all snapshots that are tagged and their tag message matches the given partial message. The partial message
     * can also be a regular expression.
     *
     * @param partialMessage the partial message to match
     * @param branch the branch on which the tag has to be located to be accepted
     * @param start only commits done after start will be accepted
     * @param end only commits done before end will be accepted
     * @return the list of tags
     * @throws GitAPIException in case of a git error
     * @throws IOException in case of an IO error
     */
    public synchronized List<Snapshot> findSnapshotsByTagMessage(String partialMessage, Branch branch,
        Optional<Date> start, Optional<Date> end) throws GitAPIException, IOException {
        final Pattern pattern = Pattern.compile(".*" + partialMessage.toLowerCase(Locale.UK) + ".*");
        return findSnapshotsByTag(branch, start, end, (w, r, n) -> {
            RevTag tag = w.parseTag(r.getObjectId());
            String message = tag.getFullMessage().toLowerCase().replace("\n", " ");
            return pattern.matcher(message).matches() ? tag : null;
        });
    }

    /**
     * Find all snapshots that are tagged and their tag name matches the given partial message. The partial message can
     * also be a regular expression.
     *
     * @param partialTagName the partial tag to match
     * @param branch the branch on which the tag has to be located to be accepted
     * @param start only commits done after start will be accepted
     * @param end only commits done before end will be accepted
     * @return the list of tags
     * @throws GitAPIException in case of a git error
     * @throws IOException in case of an IO error
     */
    public synchronized List<Snapshot> findSnapshotsByTagName(String partialTagName, Branch branch,
        Optional<Date> start, Optional<Date> end) throws GitAPIException, IOException {
        final Pattern pattern = Pattern.compile(".*" + partialTagName.toLowerCase(Locale.UK) + ".*");
        return findSnapshotsByTag(branch, start, end, (w, r, n) -> {
            String tagName = n.substring(n.indexOf('(') + 1, n.length() - 1).toLowerCase();
            return pattern.matcher(tagName).matches() ? w.parseTag(r.getObjectId()) : null;
        });
    }

    /**
     * Find all snapshots that are tagged and can be matched by the given trifunction.
     *
     * @param branch the name of the branch on which the snapshot should be located
     * @param start only commits done after start will be accepted
     * @param end only commits done before end will be accepted
     * @param f function that receives the revision walk, the tag reference, the nice tag name and returns the actual
     *            revision tag if the tag is accepted or null if rejected
     * @return the list of all snapshots that match criterion
     * @throws GitAPIException in case of a Git related error
     * @throws IOException in case of an IO error
     */
    private List<Snapshot> findSnapshotsByTag(Branch branch, Optional<Date> start, Optional<Date> end,
        TriFunction<RevWalk, Ref, String, RevTag> f) throws GitAPIException, IOException {
        setBranch(branch);
        List<Snapshot> snapshots = new ArrayList<>();
        Map<String, Ref> tags = repository.getTags();
        String branchName = new StringBuilder(branch.getShortName().length() + 2).append('(')
            .append(branch.getShortName()).append(')').toString();
        try (RevWalk walk = new RevWalk(repository); ObjectReader objectReader = repository.newObjectReader()) {
            RevFilter timeFilter = null;
            if (start.isPresent() && end.isPresent()) {
                timeFilter = CommitTimeRevFilter.between(start.get(), end.get());
            } else if (start.isPresent()) {
                timeFilter = CommitTimeRevFilter.after(start.get());
            } else if (end.isPresent()) {
                timeFilter = CommitTimeRevFilter.before(end.get());
            }
            for (Map.Entry<String, Ref> r : tags.entrySet()) {
                String name = r.getKey();
                // check if the tag branch name is correct
                if (name.charAt(0) == '(') {
                    if (name.startsWith(branchName)) {
                        name = name.substring(name.indexOf(')') + 1);
                    } else {
                        continue;
                    }
                }
                RevTag tag = f.apply(walk, r.getValue(), name);
                if (tag != null) {
                    String revision = tag.getObject().getId().getName();
                    RevCommit commit = getCommitFromRevision(revision);
                    if (timeFilter != null) {
                        try {
                            if (!timeFilter.include(walk, commit)) {
                                continue;
                            }
                        } catch (StopWalkException e) {
                            // thrown by the filter, because it expects that commit times are ordered. That is generally
                            // true, but not in this case, because we are not walking through the tree
                            continue;
                        }
                    }
                    getPathFromCommit(commit, walk, objectReader)
                        .ifPresent(p -> pathToBeamline(p, repositoryPath, branch, FileType.SNAPSHOT).ifPresent(e -> {
                            MetaInfo meta = getMetaInfoFromCommit(commit);
                            Map<String, String> parameters = new HashMap<>();
                            insertTagData(tag, parameters, revision, branch.getShortName());
                            snapshots.add(new Snapshot(e, meta.timestamp, meta.comment, meta.creator, parameters));
                        }));
                }
            }
        }
        return snapshots;
    }

    private Optional<String> getPathFromCommit(RevCommit commit, RevWalk walk, ObjectReader objectReader)
        throws GitAPIException, IOException {
        // Utility method to get the path to the snapshot file that changed in the given commit.
        // Should always at most one.
        AbstractTreeIterator oldTreeIterator = new EmptyTreeIterator();
        if (commit.getParents().length != 0) {
            RevCommit parentCommit = walk.parseCommit(commit.getParents()[0].getId());
            oldTreeIterator = new CanonicalTreeParser(null, objectReader, parentCommit.getTree());
        }
        AbstractTreeIterator newTreeIterator = new CanonicalTreeParser(null, objectReader, commit.getTree());
        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repository);
            diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
            List<DiffEntry> diffs = diffFormatter.scan(oldTreeIterator, newTreeIterator);
            for (DiffEntry diff : diffs) {
                if (diff.getChangeType() == org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
                    || diff.getChangeType() == org.eclipse.jgit.diff.DiffEntry.ChangeType.RENAME
                    || !diff.getNewPath().endsWith(FileType.SNAPSHOT.suffix)) {
                    continue;
                }
                return Optional.of(diff.getNewPath());
            }
        }
        return Optional.empty();
    }

    /**
     * Find all snapshot that are stored with the comment that contains the partial text or were created by the user
     * whose username contains the partial text and were created during the given time period. If time range is
     * provided, only commits that belong to that time range are search and any snapshot with a comment or user that
     * contain the partial text and is located on the given branch matches the criteria. This method is faster than
     * making separate search for user and comment and combining the results, because this method only traverses the
     * revision tree once.
     *
     * @param partialText the partial comment or username that we search for
     * @param branch the branch on which to search
     * @param byComment indicates if search is made by comment
     * @param byUser indicates if search is made by username
     * @param start the start date of the time window to search
     * @param end the end date of the time window to search
     * @return the list of all snapshots that match the comment criterion
     * @throws IOException in case of an error
     * @throws GitAPIException in case of branch checkout or tags loading error
     */
    public synchronized List<Snapshot> findSnapshotsByCommentOrUser(String partialText, final Branch branch,
        boolean byComment, boolean byUser, Optional<Date> start, Optional<Date> end)
            throws IOException, GitAPIException {
        List<Snapshot> snapshots = new ArrayList<>();
        setBranch(branch);
        List<RevCommit> revisions = new ArrayList<>();
        try (RevWalk revWalk = new RevWalk(repository); ObjectReader objectReader = repository.newObjectReader()) {
            revWalk.markStart(getHeadCommit());
            RevFilter userCommentFilter = null;
            if (byComment && byUser) {
                userCommentFilter = OrRevFilter.create(MessageRevFilter.create(partialText),
                    CommitterRevFilter.create(partialText));
            } else if (byComment) {
                userCommentFilter = MessageRevFilter.create(partialText);
            } else if (byUser) {
                userCommentFilter = CommitterRevFilter.create(partialText);
            }
            RevFilter timeFilter = null;
            if (start.isPresent() && end.isPresent()) {
                timeFilter = CommitTimeRevFilter.between(start.get(), end.get());
            } else if (start.isPresent()) {
                timeFilter = CommitTimeRevFilter.after(start.get());
            } else if (end.isPresent()) {
                timeFilter = CommitTimeRevFilter.before(end.get());
            }
            if (userCommentFilter == null && timeFilter == null) {
                throw new IllegalArgumentException("No search parameters provided.");
            } else if (userCommentFilter != null && timeFilter != null) {
                revWalk.setRevFilter(AndRevFilter.create(userCommentFilter, timeFilter));
            } else if (userCommentFilter != null) {
                revWalk.setRevFilter(userCommentFilter);
            } else {
                revWalk.setRevFilter(timeFilter);
            }
            for (RevCommit commit : revWalk) {
                AbstractTreeIterator oldTreeIterator = new EmptyTreeIterator();
                if (commit.getParents().length != 0) {
                    oldTreeIterator = new CanonicalTreeParser(null, objectReader, commit.getParents()[0].getTree());
                }
                AbstractTreeIterator newTreeIterator = new CanonicalTreeParser(null, objectReader, commit.getTree());
                try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                    diffFormatter.setRepository(repository);
                    diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
                    diffFormatter.setDetectRenames(true);
                    List<DiffEntry> diffs = diffFormatter.scan(oldTreeIterator, newTreeIterator);
                    for (DiffEntry diff : diffs) {
                        if (diff.getChangeType() == org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE
                            || diff.getChangeType() == org.eclipse.jgit.diff.DiffEntry.ChangeType.RENAME
                            || !diff.getNewPath().endsWith(FileType.SNAPSHOT.suffix)) {
                            continue;
                        }
                        pathToBeamline(diff.getNewPath(), repositoryPath, branch, FileType.SNAPSHOT).ifPresent(e -> {
                            MetaInfo mi = getMetaInfoFromCommit(commit);
                            Map<String, String> parameters = new HashMap<>();
                            parameters.put(PARAM_GIT_REVISION, mi.revision);
                            revisions.add(commit);
                            snapshots.add(new Snapshot(e, mi.timestamp, mi.comment, mi.creator, parameters));
                        });
                    }
                }
            }
        }
        final Map<String, RevTag> tags = loadTagsForRevisions(revisions);
        final List<Snapshot> ret = new ArrayList<>();
        final String branchName = branch.getShortName();
        snapshots.forEach(s -> {
            String revision = s.getParameters().get(PARAM_GIT_REVISION);
            if (tags.get(revision) == null) {
                ret.add(s);
            } else {
                Map<String, String> parameters = new HashMap<>(s.getParameters());
                insertTagData(tags.get(revision), parameters, revision, branchName);
                ret.add(new Snapshot(s.getBeamlineSet(), s.getDate(), s.getComment(), s.getOwner(), parameters));
            }
        });
        return ret;
    }

    /**
     * Read the contents of the file.
     *
     * @param revision the revision to load
     * @param path the path to the file (relative to the repository root)
     * @param fileType the type of file that is being loaded
     * @param type the return type
     * @param descriptor descriptor of the returned data (BeamlineSet or Snapshot)
     * @return the content of the file
     *
     * @throws ParseException if parsing the timestamp data failed (for snapshots only)
     * @throws IOException if reading the data failed
     */
    private <T> T loadFile(Optional<String> revision, String path, FileType fileType, Class<T> type, Object descriptor)
        throws ParseException, IOException {
        RevCommit revCommit = revision.isPresent() ? getCommitFromRevision(revision.get()) : getHeadCommit();
        try (ObjectReader objectReader = repository.newObjectReader(); TreeWalk treeWalk = new TreeWalk(objectReader)) {
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            treeParser.reset(objectReader, revCommit.getTree());
            int treeIndex = treeWalk.addTree(treeParser);
            treeWalk.setFilter(PathFilter.create(path));
            treeWalk.setRecursive(true);
            if (treeWalk.next()) {
                AbstractTreeIterator iterator = treeWalk.getTree(treeIndex, AbstractTreeIterator.class);
                ObjectId objectId = iterator.getEntryObjectId();
                ObjectLoader objectLoader = objectReader.open(objectId);
                if (fileType == FileType.BEAMLINE_SET) {
                    MetaInfo meta = getMetaInfoFromCommit(revCommit);
                    try (InputStream stream = objectLoader.openStream()) {
                        BeamlineSetContent bsc = FileUtilities.readFromBeamlineSet(stream);
                        BeamlineSetData bsd = new BeamlineSetData((BeamlineSet) descriptor, bsc.getNames(),
                            bsc.getReadbacks(), bsc.getDeltas(), bsc.getDescription(), meta.comment, meta.timestamp);
                        return type.cast(bsd);
                    }
                } else if (fileType == FileType.SNAPSHOT) {
                    try (InputStream stream = objectLoader.openStream()) {
                        SnapshotContent sc = FileUtilities.readFromSnapshot(stream);
                        Timestamp snapshotTime = Timestamp.of(sc.getDate());
                        VSnapshot vs = new VSnapshot((Snapshot) descriptor, sc.getNames(), sc.getSelected(),
                            sc.getData(), sc.getReadbacks(), sc.getReadbackData(), sc.getDeltas(), snapshotTime);
                        return type.cast(vs);
                    }
                }
            }
        }
        throw new FileNotFoundException("Snapshot file '" + path + "' could not be found.");
    }

    // --------------------------------------------------------------------------------------------------
    //
    // Utility methods.
    //
    // --------------------------------------------------------------------------------------------------

    /**
     * Retrieves and returns meta info for given commit.
     *
     * @param revCommit commit from which meta info are retrieved
     *
     * @return meta info for given commit.
     */
    private static MetaInfo getMetaInfoFromCommit(RevCommit revCommit) {
        PersonIdent personIdent = revCommit.getCommitterIdent();
        String comment = revCommit.getFullMessage();
        String creator = personIdent.getName();
        Date commitTimestamp = personIdent.getWhen();
        String email = personIdent.getEmailAddress();
        String revision = revCommit.getName();
        return new MetaInfo(comment, creator, email, commitTimestamp, revision);
    }

    /**
     * Creates (if not exists) file and writes content.
     *
     * @param filePath file path relative to the repository
     * @param repositoryPath the path to the repository root
     * @param fileSuffix file suffix
     * @param dataObject object from which content is generated
     *
     * @throws IOException when exception occurs.
     */
    private static void writeToFile(String filePath, File repositoryPath, FileType fileType, Object dataObject)
        throws IOException {
        Path path = Paths.get(repositoryPath.getAbsolutePath(), filePath);
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            path = Files.createFile(path);
        }
        String content = generateContent(dataObject, fileType);
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Delete the file at the given path.
     *
     * @param filePath the path relative to the repository path
     * @param repositoryPath the path to the root of the repository
     * @return true if the file was deleted or false otherwise
     * @throws IOException in case of an error
     */
    private static boolean deleteFile(String filePath, File repositoryPath) throws IOException {
        Path path = Paths.get(repositoryPath.getAbsolutePath(), filePath);
        if (!Files.exists(path)) {
            return false;
        }
        Files.delete(path);
        return true;
    }

    /**
     * Converts the <code>pathToFile</code> to a beamline set. The repository is expected to be checkout on the correct
     * branch. If the path is valid so that the beamline set can be determined and if the file actually still exists at
     * the HEAD of the branch, it is returned. If the path is not valid, or the file does not exist, an empty object is
     * returned.
     *
     * @param pathToFile the path to file
     * @param repositoryPath the path to the root of the repository
     * @param branch the branch for the beamline set
     * @param fromType the type of the file under the given path
     * @return the beamline set if found or empty if not found
     */
    private static Optional<BeamlineSet> pathToBeamline(String pathToFile, File repositoryPath, Branch branch,
        FileType fromType) {
        String[] p = pathToFile.split(GIT_PATH_DELIMITER);
        BaseLevel baseLevel = null;
        String[] newPath = null;
        for (int i = 0; i < p.length; i++) {
            if (fromType.directory.equals(p[i])) {
                StringBuilder baseLevelName = new StringBuilder(pathToFile.length());
                List<String> bsPath = new ArrayList<>(p.length);
                for (int j = 0; j < i; j++) {
                    baseLevelName.append(GIT_PATH_DELIMITER).append(p[j]);
                }
                if (baseLevelName.length() > 0) {
                    String bl = baseLevelName.substring(1);
                    baseLevel = new BaseLevel(branch, bl, bl);
                }
                for (int j = i + 1; j < p.length; j++) {
                    bsPath.add(p[j]);
                }
                newPath = bsPath.toArray(new String[bsPath.size()]);
                if (newPath.length > 0) {
                    newPath[newPath.length - 1] = newPath[newPath.length - 1].replace(fromType.suffix,
                        FileType.BEAMLINE_SET.suffix);
                }
                break;
            }
        }
        if (newPath == null || newPath.length == 0) {
            return Optional.empty();
        }
        BeamlineSet beamlineSet = new BeamlineSet(branch, Optional.ofNullable(baseLevel), newPath, GitDataProvider.ID);
        String path = convertPathToString(beamlineSet, FileType.BEAMLINE_SET);
        File fullPath = new File(repositoryPath, path);
        return fullPath.exists() ? Optional.of(beamlineSet) : Optional.empty();
    }

    /**
     * Recursively gathers the beamline sets that are located in any of the subfolder of the given file. All valid files
     * are placed into the <code>sets</code> list.
     *
     * @param file the parent file to start the search from
     * @param sets the list containing all found beamline set files
     */
    private static void gatherBeamlineSets(File file, List<File> sets) {
        if (file != null && file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                Arrays.sort(files);
                for (File f : files) {
                    if (f.isDirectory()) {
                        gatherBeamlineSets(f, sets);
                    } else if (f.isFile()
                        && f.getName().toLowerCase(Locale.UK).endsWith(FileType.BEAMLINE_SET.suffix)) {
                        sets.add(f);
                    }
                }
            }
        }
    }

    /**
     * Generates file content. File type is determined by file suffix.
     *
     * @param dataObject the object from which content is generated
     * @param fileType file suffix
     *
     * @return generated file content.
     */
    private static String generateContent(Object dataObject, FileType fileType) {
        if (fileType == FileType.BEAMLINE_SET && dataObject instanceof BeamlineSetData) {
            return FileUtilities.generateBeamlineSetContent((BeamlineSetData) dataObject);
        } else if (fileType == FileType.SNAPSHOT && dataObject instanceof VSnapshot) {
            return FileUtilities.generateSnapshotFileContent((VSnapshot) dataObject);
        }
        throw new IllegalArgumentException(
            "The data '" + dataObject + "' does not match the file type '" + fileType + "'.");
    }

    /**
     * Convert the path to an array. The array contains the segments relative to the BeamlineSets or Snapshots folder
     * within the base level or root.
     *
     * @param path the path to parse
     * @param level optional base level; if given the path will be relative to the {@link BaseLevel#getStorageName()}
     *            /DIR_NAME, otherwise the path is relative to /DIR_NAME, where DIR_NAME is Snapshots or BeamlineSets
     * @return the path segments
     */
    private static String[] convertStringToPath(String path, Optional<BaseLevel> level) {
        if (path != null && !path.isEmpty()) {
            String newPath = path.replace("\\", GIT_PATH_DELIMITER);
            String[] pp = newPath.split(GIT_PATH_DELIMITER);
            if (level.isPresent()) {
                if (pp.length < 3) {
                    return new String[0];
                } else if (pp[0].equals(level.get().getStorageName())) {
                    String[] ret = new String[pp.length - 2];
                    System.arraycopy(pp, 2, ret, 0, ret.length);
                    return ret;
                }
            } else {
                if (pp.length < 2) {
                    return new String[0];
                }
                String[] ret = new String[pp.length - 1];
                System.arraycopy(pp, 1, ret, 0, ret.length);
                return ret;
            }

        }
        return new String[0];
    }

    /**
     * Converts the path of the descriptor to the path relative to the repository root.
     *
     * @param descriptor the descriptor for which the path is being converted
     * @param fileType the type of file for which the path is requested
     * @return the path relative to the repository
     */
    private static String convertPathToString(BeamlineSet descriptor, FileType fileType) {
        final StringBuilder sb = new StringBuilder(200);
        String[] path = descriptor.getPath();
        descriptor.getBaseLevel().ifPresent(c -> sb.append(c.getStorageName()).append(GIT_PATH_DELIMITER));
        sb.append(fileType.directory).append(GIT_PATH_DELIMITER);
        for (int i = 0; i < path.length - 1; i++) {
            sb.append(path[i]).append(GIT_PATH_DELIMITER);
        }
        if (path.length > 0) {
            String last = path[path.length - 1];
            if (fileType == FileType.BEAMLINE_SET) {
                last = last.replace(FileType.SNAPSHOT.suffix, FileType.BEAMLINE_SET.suffix);
            } else {
                last = last.replace(FileType.BEAMLINE_SET.suffix, FileType.SNAPSHOT.suffix);
            }
            sb.append(last);
            if (!last.toLowerCase(Locale.UK).endsWith(fileType.suffix)) {
                sb.append(fileType.suffix);
            }
        }
        return sb.substring(0, sb.length());
    }

    /**
     * Checks if the exception was thrown because we are not authorised to perform the action.
     *
     * @param e the exception to check
     * @return true if we are not authorised or false if it was a different kind of exception
     */
    private static boolean isNotAuthorised(TransportException e) {
        return e.getMessage().toLowerCase(Locale.UK).contains("not authorized");
    }

    /**
     * Checks if the exception was thrown when we tried to push a branch that had nothing to push.
     *
     * @param e exception to check
     * @return true if there was nothing to push, false if it was a different kind of exception
     */
    private static boolean isNothingToPush(TransportException e) {
        return e.getMessage().toLowerCase(Locale.UK).contains("nothing to push");
    }

    /**
     * Transform the path of the file and the proposed tag name to a tag name acceptable by this repository. The tag
     * name is composed as a path to the file delimited with '/', followed by the provided tag name in parenthesis (e.g.
     * path/to/tagged/snapshot/(tagName))
     *
     * @param branch the branch on which the tag is created
     * @param path the path of the file that we want to tag
     * @param tagName the name of the tag
     * @return the tag name that should be used to tag the file
     */
    private static String composeTagName(Branch branch, Optional<BaseLevel> baseLevel, String[] path, String tagName) {
        StringBuilder sb = new StringBuilder(255);
        sb.append('(').append(branch.getShortName()).append(')');
        baseLevel.ifPresent(e -> sb.append(e.getStorageName()).append('/'));
        for (int i = 0; i < path.length; i++) {
            String str = path[i];
            if (i == path.length - 1) {
                str = str.replace(FileType.BEAMLINE_SET.suffix, FileType.SNAPSHOT.suffix);
            }
            str = TAG_PATTERN.matcher(str).replaceAll("");
            if (str.charAt(0) == '.') {
                str = str.substring(1);
            }
            if (str.charAt(str.length() - 1) == '.') {
                str = str.substring(0, str.length() - 1);
            }
            if (str.endsWith(".lock")) {
                str = str.substring(0, str.length() - 5);
            }
            sb.append(str).append('/');
        }
        sb.append('(').append(tagName).append(')');
        return sb.toString();
    }

    /**
     * Check if the tags contain a tag for the given revision and if yes, fill in the parameters map with the important
     * tag information.
     *
     * @param tag the tag to parse and add to parameters
     * @param parameters the current parameters
     * @param revision the revision hash
     * @param branchName the branch name for which the tag should be loaded
     */
    private static void insertTagData(RevTag tag, Map<String, String> parameters, String revision, String branchName) {
        parameters.put(PARAM_GIT_REVISION, revision);
        if (tag != null) {
            String niceTagName = tag.getTagName();
            boolean acceptTag = true;
            if (niceTagName.charAt(0) == '(') {
                String branch = niceTagName.substring(1, niceTagName.indexOf(')'));
                if (!branch.equals(branchName)) {
                    acceptTag = false;
                }
            }
            if (acceptTag) {
                int idx = niceTagName.lastIndexOf('(');
                if (idx > 1) {
                    niceTagName = niceTagName.substring(niceTagName.lastIndexOf('(') + 1, niceTagName.length() - 1);
                }
                parameters.put(Snapshot.TAG_NAME, niceTagName);
                parameters.put(PARAM_GIT_TAG_NAME, tag.getTagName());
                parameters.put(Snapshot.TAG_MESSAGE, tag.getFullMessage());
                parameters.put(PARAM_TAG_CREATOR, tag.getTaggerIdent().getName());
            }
        }
    }

    /**
     * Delete the entire folder including all subfolders and files.
     *
     * @param folder the folder to delete
     */
    static void deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteFolder(f);
                    } else {
                        f.delete();
                    }
                }
            }
            folder.delete();
        }
    }
}
