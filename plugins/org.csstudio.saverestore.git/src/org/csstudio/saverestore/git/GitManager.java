package org.csstudio.saverestore.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.SerializableBaseLevel;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.git.Result.ChangeType;
import org.csstudio.ui.fx.util.Credentials;
import org.csstudio.ui.fx.util.UsernameAndPasswordDialog;
import org.csstudio.security.SecuritySupport;
import org.diirt.util.array.ArrayBoolean;
import org.diirt.util.array.ArrayByte;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ArrayShort;
import org.diirt.util.array.ListBoolean;
import org.diirt.util.array.ListByte;
import org.diirt.util.array.ListDouble;
import org.diirt.util.array.ListFloat;
import org.diirt.util.array.ListInt;
import org.diirt.util.array.ListLong;
import org.diirt.util.array.ListShort;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Time;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
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
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.ui.PlatformUI;

/**
 * <code>GitManager<code> provide access to the git features required by the save and restore application.
 *
 * @author <a href="mailto:miha.novak@cosylab.com">Miha Novak</a>
 */
public class GitManager {

    private static class DescriptionDateData<T> {
        final List<T> data;
        final List<String> names;
        final String description;
        DescriptionDateData(String description, List<String> names, List<T> data) {
            this.data = data;
            this.names = names;
            this.description = description;
        }
    }

    private final DateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String DESCRIPTION_TAG = "Description:";
    private static final String DATE_TAG = "Date:";

    private static final String H_PV_NAME = "PV";
    private static final String H_TIMESTAMP = "TIMESTAMP";
    private static final String H_STATUS = "STATUS";
    private static final String H_SEVERITY = "SEVERITY";
    private static final String H_VALUE_TYPE = "VALUE_TYPE";
    private static final String H_VALUE = "VALUE";
    private static final String SNAPSHOT_FILE_HEADER = H_PV_NAME + "," + H_TIMESTAMP + ","
                                + H_STATUS + "," + H_SEVERITY + "," + H_VALUE_TYPE + "," + H_VALUE;

    private static final String ARRAY_SPLITTER = "\\;";
    private static final String ENUM_VALUE_SPLITTER = "\\~";

    private static final int BSD_ENTRY_LENGTH = 60;
    private static final int SNP_ENTRY_LENGTH = 500;
    private static final String GIT_PATH_DELIMITER = "/";
    private static final String PARAM_GIT_REVISION = "gitRevision";
    private static final String PARAM_GIT_TAG_NAME = "gitTagName";
    private static final String PARAM_TAG_CREATOR = "tagCreator";

    private static Pattern TAG_PATTERN = Pattern.compile("[\\x00-\\x1F\\x7E-\\xFF()~\\^: /?*\\[\\]@\\\\{\\.{2}]+");

    private Git git;
    private Repository repository;
    private File repositoryPath;
    private boolean automatic = true;
    private boolean localOnly = false;

    public GitManager() {
    }

    public GitManager(URI remoteRepo, File destinationDirectory) throws GitAPIException {
        initialise(remoteRepo, destinationDirectory);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            if (repository != null) {
                repository.close();
            }
            if (git != null) {
                git.close();
            }
        } catch (Exception e) {
            SaveRestoreService.LOGGER.log(Level.SEVERE, "Git cleanup error", e);
        }
        super.finalize();
    }

    /**
     * Enable or disable automatic synchronisation. When enabled new files are automatically pushed to the remote
     * repository and remote changes pulled in as well.
     *
     * @param automatic true if automatic synchronisation should be enabled
     */
    public void setAutomaticSynchronisation(boolean automatic) {
        if (localOnly && automatic) {
            return;
        }
        this.automatic = automatic;
    }

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
        if (repositoryPath.exists()) {
            try(Git git = Git.init().setDirectory(repositoryPath).call()) {
                this.git = git;
                this.repository = git.getRepository();
                StoredConfig config = this.repository.getConfig();
                String url = config.getString("remote","origin","url");
                if (url == null || !url.equals(remoteRepository.toString())) {
                    repository.close();
                    return false;
                }

                try {
                    Credentials credentials = getCredentials(Optional.empty());
                    if (credentials != null) {
                        pull(credentials);
                    }
                } catch (GitAPIException e) {
                    SaveRestoreService.LOGGER.log(Level.WARNING, "Git repository " + remoteRepository
                            + " is not accessible.", e);
                    localOnly = true;
                    setAutomaticSynchronisation(false);
                }
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
                try (Git git = cloneCommand.call()) {
                    this.git = git;
                    this.repository = git.getRepository();
                    break;
                } catch (TransportException e) {
                    if (isNotAuthorized(e)) {
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
        if (!branch.equals(repository.getBranch())) {
            Ref ref = git.checkout().setName(branch.getFullName()).setUpstreamMode(SetupUpstreamMode.TRACK).call();
            if (ref == null) {
                //local branch does not exist. create it
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
            if ("HEAD".equals(name)) continue;
            Branch branch;
            if (name.indexOf('/') > 0) {
                branch = new Branch(name, name.substring(name.lastIndexOf('/')+1));
            } else {
                branch = new Branch(name,name);
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
     * @return true is changes in the local repository were made
     * @throws GitAPIException if there is an error during push or pull
     */
    public synchronized boolean synchronise(Optional<Credentials> cp) throws GitAPIException {
        Credentials c = cp.isPresent() ? cp.get() : getCredentials(Optional.empty());
        if (c != null) {
            Object[] obj = pull(c);
            push((Credentials)obj[0],true);
            return (Boolean)obj[1];
        }
        return false;
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
        for (File f : files) {
            if (f.isDirectory() && f.getName().charAt(0) != '.') {
                baseLevels.add(new SerializableBaseLevel(f.getName(), f.getName(), branch));
            }
        }
        return baseLevels;
    }

    /**
     * Returns the list of all available beamline sets in the current branch. The search is done by reading the data
     * on the file system, not by searching the git repository.
     *
     * @param baseLevel the base level for which the beamline sets are requested (optional, if base levels are not used)
     * @param branch the branch to switch to
     * @return the list of beamline sets
     * @throws IOException if the current branch could not be retrieved
     */
    public synchronized List<BeamlineSet> getBeamlineSets(Optional<BaseLevel> baseLevel, Branch branch)
            throws IOException, GitAPIException {
        setBranch(branch);
        List<BeamlineSet> descriptorList = new ArrayList<>();
        File[] files = repositoryPath.listFiles();
        String base = baseLevel.isPresent() ? baseLevel.get().getStorageName() : null;
        for (File f : files) {
            if (f.getName().equals(base)) {
                File b = new File(f,FileType.BEAMLINE_SET.directory);
                List<File> setFiles = new ArrayList<>();
                gatherBeamlineSets(b, setFiles);
                String path = repositoryPath.getAbsolutePath();
                int length = path.length();
                if (!(path.charAt(length-1) == '/' || path.charAt(length-1) == '\\')) {
                    length++;
                }
                for (File bf : setFiles) {
                    String s = bf.getAbsolutePath().substring(length);
                    String[] filePathArray = convertStringToPath(s,baseLevel);
                    if (filePathArray != null) {
                        BeamlineSet beamlineSet = new BeamlineSet(branch, baseLevel, filePathArray);
                        descriptorList.add(beamlineSet);
                    }
                }
                break;
            }
        }
        return descriptorList;
    }

    private synchronized void gatherBeamlineSets(File file, List<File> sets) {
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                gatherBeamlineSets(f, sets);
            } else if (f.isFile() && f.getName().toLowerCase().endsWith(FileType.BEAMLINE_SET.suffix)) {
                sets.add(f);
            }
        }
    }

    /**
     *
     * @param baseLevel
     * @return
     * @throws IOException
     * @deprecated direct file system scanning is much faster
     */
    @Deprecated
    public synchronized List<BeamlineSet> getBeamlineSets2(Optional<BaseLevel> baseLevel) throws IOException {
        List<BeamlineSet> descriptorList = new ArrayList<>();
        RevCommit revCommit = getHeadCommit();
        try (TreeWalk treeWalk = new TreeWalk(repository.newObjectReader())) {
            String branch = repository.getBranch();
            treeWalk.addTree(revCommit.getTree());
            treeWalk.setFilter(PathSuffixFilter.create(FileType.BEAMLINE_SET.suffix));
            treeWalk.setRecursive(true);
            Branch bb = new Branch(branch,branch);
            while (treeWalk.next()) {
                String filePath = treeWalk.getPathString();
                String[] filePathArray = convertStringToPath(filePath,baseLevel);
                if (filePathArray != null) {
                    BeamlineSet beamlineSet = new BeamlineSet(bb, baseLevel, filePathArray);
                    descriptorList.add(beamlineSet);
                }
            }
        }
        return descriptorList;
    }

    /**
     * Loads and returns the beamline set data for the provided descriptor and the git revision. If git revision
     * is not specified the head revision is returned.
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
            //cannot happen, but just in case, make a log
            SaveRestoreService.LOGGER.log(Level.SEVERE, "Unexpected error when loading beamline set content", e);
            return null;
        }
    }

    /**
     * Returns the list of all snapshots for the given beamline set.
     *
     * @param beamlineSet the beamline set for which the snapshots are requested
     * @return the list of all snapshot revisions for this beamline set
     * @throws IOException if the commits could not be read
     * @throws GitAPIException if the commits could not be read
     */
    public synchronized List<Snapshot> getSnapshots(BeamlineSet beamlineSet) throws IOException, GitAPIException {
        setBranch(beamlineSet.getBranch());
        List<Snapshot> snapshots = new ArrayList<>();

        String path = convertPathToString(beamlineSet, FileType.SNAPSHOT);
        path = path.replace(FileType.BEAMLINE_SET.directory, FileType.SNAPSHOT.directory);
        path = path.replace(FileType.BEAMLINE_SET.suffix, FileType.SNAPSHOT.suffix);

        List<String> fileRevisions = findCommitsFor(path, Optional.empty());
        Map<String,RevTag> tags = loadTagsForRevisions(fileRevisions);
        for (String revision : fileRevisions) {
            RevCommit commit = getCommitFromRevision(revision);
            MetaInfo meta = getMetaInfoFromCommit(commit);
            Map<String,String> parameters = new HashMap<>();
            parameters.put(PARAM_GIT_REVISION, revision);
            RevTag tag = tags.get(revision);
            if (tag != null) {
                String niceTagName = tag.getTagName();
                int idx = niceTagName.lastIndexOf('(');
                if (idx > 1) {
                    niceTagName = niceTagName.substring(niceTagName.lastIndexOf('(')+1,niceTagName.length()-1);
                }
                parameters.put(Snapshot.TAG_NAME, niceTagName);
                parameters.put(PARAM_GIT_TAG_NAME, tag.getTagName());
                parameters.put(Snapshot.TAG_MESSAGE, tag.getFullMessage());
                parameters.put(PARAM_TAG_CREATOR, tag.getTaggerIdent().getName());
            }
            Snapshot snapshot = new Snapshot(beamlineSet,meta.timestamp,meta.comment,meta.creator,parameters);
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
        String path = convertPathToString(snapshot.getBeamlineSet(),FileType.SNAPSHOT);
        return loadFile(Optional.ofNullable(snapshot.getParameters().get(PARAM_GIT_REVISION)), path,
                FileType.SNAPSHOT, VSnapshot.class, snapshot);
    }

    /**
     * Save the beamline set and commit it.
     *
     * @param data the contents of the beamline set file
     * @param comment the commit comment
     * @return the saved beamline set and change type describing what kind of updates were made
     *          to the repository
     * @throws IOException if writing to the file failed
     * @throws GitAPIException if commiting the file failed
     */
    public synchronized Result<BeamlineSetData> saveBeamlineSet(BeamlineSetData data, String comment)
            throws IOException, GitAPIException {
        setBranch(data.getDescriptor().getBranch());
        String relativePath = convertPathToString(data.getDescriptor(),FileType.BEAMLINE_SET);
        writeToFile(relativePath, FileType.BEAMLINE_SET, data);
        Credentials cp = getCredentials(Optional.empty());
        if (cp != null) {
            ChangeType change = ChangeType.SAVE;
            if (automatic) {
                Object[] obj = pull(cp);
                cp = (Credentials)obj[0];
                change = (Boolean)obj[1] ? ChangeType.PULL : change;
            }
            commit(relativePath,new MetaInfo(comment, cp.getUsername(), null, null));
            if (automatic) {
                push(cp,false);
//                change = synchronise(Optional.of(cp)) ? ChangeType.PULL : change;
            }
            return new Result<>(data,change);
        }
        return new Result<>(null, ChangeType.NONE);
    }

    /**
     * Delete the beamline set from the repository. Only the beamline set is delete, the snapshot file remains.
     *
     * @param data the set to delete
     * @param comment the comment why the set was deleted
     * @return the result of the action
     * @throws IOException in case of an error
     * @throws GitAPIException in case of an error
     */
    public synchronized Result<BeamlineSet> deleteBeamlineSet(BeamlineSet set, String comment)
            throws IOException, GitAPIException {
        setBranch(set.getBranch());
        String relativePath = convertPathToString(set,FileType.BEAMLINE_SET);
        Credentials cp = getCredentials(Optional.empty());
        if (cp != null) {
            ChangeType change = ChangeType.SAVE;
            if (automatic) {
                Object[] obj = pull(cp);
                cp = (Credentials)obj[0];
                change = (Boolean)obj[1] ? ChangeType.PULL : change;
            }
            if (deleteFile(relativePath)) {
                commit(relativePath, new MetaInfo(comment, cp.getUsername(), null, null));
                if (automatic) {
                    push(cp,false);
                }
                return new Result<>(set,change);
            } else if (change == ChangeType.PULL) {
                return new Result<>(null,change);
            }
        }
        return new Result<>(null,ChangeType.NONE);
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
    public synchronized Result<VSnapshot> saveSnapshot(VSnapshot snapshot, String comment) throws IOException, GitAPIException {
        setBranch(snapshot.getBeamlineSet().getBranch());
        Snapshot descriptor = snapshot.getSnapshot().get();
        String relativePath = convertPathToString(descriptor.getBeamlineSet(),FileType.SNAPSHOT);
        writeToFile(relativePath, FileType.SNAPSHOT, snapshot);
        Credentials cp = getCredentials(Optional.empty());
        if (cp != null) {
            ChangeType change = ChangeType.SAVE;
            if (automatic) {
                Object[] obj = pull(cp);
                cp = (Credentials)obj[0];
                change = (Boolean)obj[1] ? ChangeType.PULL : change;
            }
            MetaInfo info = commit(relativePath,new MetaInfo(comment, cp.getUsername(), null, null));
            if (automatic) {
                push(cp,false);
//                change = synchronise(Optional.of(cp)) ? ChangeType.PULL : change;
            }
            Snapshot snp = new Snapshot(descriptor.getBeamlineSet(),info.timestamp,info.comment,info.creator);
            VSnapshot vsnp = new VSnapshot(snp, snapshot.getNames(), snapshot.getValues(), snapshot.getTimestamp());
            return new Result<>(vsnp,change);
        }
        return new Result<>(null,ChangeType.NONE);
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
        return new Branch(branch,branch);
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
    public synchronized Result<Snapshot> tagSnapshot(Snapshot snapshot, String name, String message) throws IOException,
                    GitAPIException, DataProviderException {
        if (name != null && TAG_PATTERN.matcher(name).replaceAll("").length() != name.length()) {
            throw new DataProviderException("Tag name contains invalid characters.");
        }
        setBranch(snapshot.getBeamlineSet().getBranch());
        Credentials cp = getCredentials(Optional.empty());
        if (cp != null) {
            ChangeType change = ChangeType.SAVE;
            if (automatic) {
                Object[] obj = pull(cp);
                cp = (Credentials)obj[0];
                change = (Boolean)obj[1] ? ChangeType.PULL : change;
                if (cp == null) {
                    return new Result<Snapshot>(null, ChangeType.NONE);
                }
            }
            String revision = snapshot.getParameters().get(PARAM_GIT_REVISION);
            RevCommit commit = getCommitFromRevision(revision);
            RevTag existingTag = loadTagsForRevisions(Arrays.asList(revision)).get(revision);
            if (existingTag != null) {
                git.tagDelete().setTags(existingTag.getTagName()).call();
                RefSpec refSpec = new RefSpec().setSource(null).setDestination("refs/tags/"+existingTag.getTagName());
                git.push().setCredentialsProvider(toCredentialsProvider(cp)).setRefSpecs(refSpec).call();
            }
            Snapshot snp;
            if (name != null && !name.isEmpty()) {
                String gitTagName = composeTagName(snapshot.getBeamlineSet().getPath(),name);
                PersonIdent tagger = new PersonIdent(cp.getUsername(), "UNKNOWN");
                git.tag().setName(gitTagName).setMessage(message).setTagger(tagger).setObjectId(commit).call();
                if (automatic) {
                    push(cp,true);
    //                change = synchronise(Optional.of(cp)) ? ChangeType.PULL : change;
                }
                Map<String,String> parameters = new HashMap<>();
                parameters.put(PARAM_GIT_REVISION, revision);
                parameters.put(PARAM_GIT_TAG_NAME, gitTagName);
                parameters.put(Snapshot.TAG_NAME, name);
                parameters.put(Snapshot.TAG_MESSAGE, message);
                parameters.put(PARAM_TAG_CREATOR, cp.getUsername());
                snp = new Snapshot(snapshot.getBeamlineSet(),
                        snapshot.getDate(), snapshot.getComment(), snapshot.getOwner(),parameters);
            } else {
                Map<String,String> parameters = new HashMap<>();
                parameters.put(PARAM_GIT_REVISION, revision);
                snp = new Snapshot(snapshot.getBeamlineSet(),
                        snapshot.getDate(), snapshot.getComment(), snapshot.getOwner(),parameters);
            }
            return new Result<>(snp,change);
        }
        return new Result<>(null,ChangeType.NONE);
    }

    public static void main(String[] args) throws IllegalStateException, GitAPIException, IOException, ParseException {
        String myTagName = "haha@dsa{}()";
        System.out.println(TAG_PATTERN.matcher(myTagName).matches());

  }

    private String composeTagName(String[] path, String tagName) {
        StringBuilder sb = new StringBuilder(255);
        for (int i = 0; i < path.length; i++) {
            String str = TAG_PATTERN.matcher(path[i]).replaceAll("");
            if (str.charAt(0) == '.') {
                str = str.substring(0);
            }
            if (str.charAt(str.length()-1) == '.') {
                str = str.substring(0, str.length()-1);
            }
            if (str.endsWith(".lock")) {
                str = str.substring(0,str.length()-5);
            }
            sb.append(str).append('/');
        }
        sb.append('(').append(tagName).append(')');
        return sb.toString();
    }

    // --------------------------------------------------------------------------------------------------
    //
    // Methods for working with GIT
    //
    // --------------------------------------------------------------------------------------------------

    private Map<String,RevTag> loadTagsForRevisions(List<String> revisions) throws GitAPIException, IOException {
        Map<String,Ref> tags = repository.getTags();
        Map<String,RevTag> ret = new HashMap<>();
        try (RevWalk walk = new RevWalk(repository)) {
            for (String rev : revisions) {
                String s = Git.wrap(repository).describe().setTarget(ObjectId.fromString(rev)).call();
                Ref tt = tags.get(s);
                if (tt != null) {
                    RevTag t = walk.parseTag(tt.getObjectId());
                    ret.put(rev, t);
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
        RevCommit commit = git.commit().setCommitter(metaInfo.creator, metaInfo.eMail)
                .setMessage(metaInfo.comment).call();
        return getMetaInfoFromCommit(commit);
    }

    /**
     * Push the local commits to remote repository.
     *
     * @param cred credentials provider to use for pushing
     * @param pushTags true if tags should be pushed as well or false if tags can be skipped
     * @return credentials that worked
     * @throws GitAPIException if there was an error during push
     */
    private synchronized Credentials push(Credentials cred, boolean pushTags) throws GitAPIException {
        if (localOnly) {
            return null;
        }

        while(pushTags) {
            try {
                git.push().setCredentialsProvider(toCredentialsProvider(cred)).call();
                break;
            } catch (TransportException e) {
                if (isNothingToPush(e)) {
                    //if there are no changes the message is Nothing to push
                    break;
                } else if (isNotAuthorized(e)) {
                    //if the authorization failed the message is not authorized, in that case repeat
                    cred = getCredentials(Optional.ofNullable(cred));
                    if (cred == null) {
                        return null;
                    }
                } else {
                    throw e;
                }
            }
        }
        while(true) {
            try {
                git.push().setCredentialsProvider(toCredentialsProvider(cred)).setPushTags().call();
                break;
            } catch (TransportException e) {
                if (isNothingToPush(e)) {
                    break;
                } else if (isNotAuthorized(e)) {
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
     * @param cred the credentials to use when fetching and pull from remote repository
     * @return an array of size 2: credentials that worked or null if cancelled and a Boolean describing
     *          if there were any changes pulled from the remote repo
     * @throws GitAPIException if there was an error during pull
     */
    private synchronized Object[] pull(Credentials cred) throws GitAPIException {
        if (localOnly) {
            return new Object[]{null,false};
        }
        while(true) {
            try {
                FetchResult fetch = git.fetch().setCredentialsProvider(toCredentialsProvider(cred))
                        .setTagOpt(TagOpt.FETCH_TAGS).call();
                PullResult pull = git.pull().setCredentialsProvider(toCredentialsProvider(cred))
                        .setStrategy(MergeStrategy.THEIRS).call();
                boolean changed = !fetch.getTrackingRefUpdates().isEmpty() ||
                        !pull.getFetchResult().getTrackingRefUpdates().isEmpty();
                return new Object[]{cred,changed};
            } catch (TransportException e) {
                if (isNotAuthorized(e)) {
                    cred = getCredentials(Optional.ofNullable(cred));
                    if (cred == null) {
                        return new Object[]{null,false};
                    }
                } else {
                    throw e;
                }
            }
        }
    }

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
        return new MetaInfo(comment, creator, email, commitTimestamp);
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
     * Retrieves all revisions of the given file.
     *
     * @param filePath file path
     *
     * @return all revisions of the given file.
     */
    private List<String> findCommitsFor(String filePath, Optional<String> fromRevisionBack) throws GitAPIException, IOException {
        List<String> commitsList = new ArrayList<>();
        ObjectId obj = fromRevisionBack.isPresent() ? ObjectId.fromString(fromRevisionBack.get()) : repository.resolve(Constants.HEAD);
        LogCommand log = git.log().add(obj).addPath(filePath).setMaxCount(30);
        Iterable<RevCommit> commits = log.call();
        for (RevCommit commit : commits) {
            commitsList.add(commit.getName());
        }
        return commitsList;
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
    private <T> T loadFile(Optional<String> revision, String path, FileType fileType, Class<T> type,
            Object descriptor) throws ParseException, IOException {
        RevCommit revCommit = revision.isPresent() ? getCommitFromRevision(revision.get()) : getHeadCommit();
        ObjectReader objectReader = repository.newObjectReader();
        try (TreeWalk treeWalk = new TreeWalk(objectReader)) {
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
                        DescriptionDateData<?> ddp = readFromBeamlineSet(stream);
                        BeamlineSetData bsd = new BeamlineSetData((BeamlineSet)descriptor,ddp.names,ddp.description,
                                meta.comment,meta.timestamp);
                        return type.cast(bsd);
                    }
                } else if (fileType == FileType.SNAPSHOT) {
                    try (InputStream stream = objectLoader.openStream()) {
                        DescriptionDateData<VType> ddp = readFromSnapshot(stream);
                        Timestamp snapshotTime = Timestamp.of(TIMESTAMP_FORMATTER.parse(ddp.description));
                        VSnapshot vs = new VSnapshot((Snapshot)descriptor, ddp.names, ddp.data, snapshotTime);
                        return type.cast(vs);
                    }
                }
            }
        }
        return null;
    }

    // --------------------------------------------------------------------------------------------------
    //
    // Private methods for working with files: READ FROM FILE METHODS
    //
    // --------------------------------------------------------------------------------------------------
    /**
     * Read the contents of the beamline set from the input stream.
     *
     * @param stream the source of data
     * @return the data, where the description is the description read from the file and there are no data, just names
     * @throws IOException if there was an error reading the file content
     */
    private static DescriptionDateData<?> readFromBeamlineSet(InputStream stream) throws IOException {
        StringBuilder description = new StringBuilder(400);
        List<String> names = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        boolean isDescriptionLine = false;
        String line = null;
        String[] header = null;
        int namesIndex = -1;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            } else if (header == null && line.charAt(0) == '#') {
                line = line.substring(1).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (isDescriptionLine) {
                    description.append(line).append('\n');
                } else {
                    if (line.contains(DESCRIPTION_TAG)) {
                        isDescriptionLine = true;
                    }
                }
            } else if (header == null) {
                isDescriptionLine = false;
                header = line.split("\\,");
                for (int i = 0; i < header.length; i++) {
                    if (H_PV_NAME.equals(header[i])) {
                        namesIndex = i;
                    }
                }
            } else {
                String[] split = line.split("\\,", header.length);
                names.add(split[namesIndex]);
            }
        }
        return new DescriptionDateData<>(description.toString().trim(), names,null);
    }

    /**
     * Read the contents of the snapshot from the given input stream.
     *
     * @param stream the source of data
     * @return the data, where the description contains the timestamp of the snapshot, names contain the pv names,
     *          and data are the pv values
     * @throws IOException if reading the file failed
     */
    private static DescriptionDateData<VType> readFromSnapshot(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        String date = null;
        List<String> names = new ArrayList<>();
        List<VType> data = new ArrayList<>();
        String line = null;
        String[] header = null;
        Map<String,Integer> headerMap = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            } else if (header == null && line.charAt(0) == '#') {
                int idx = line.indexOf(DATE_TAG);
                if (idx > -1) {
                    date = line.substring(idx + DATE_TAG.length()).trim();
                }
            } else if (header == null) {
                header = line.split("\\,");
                for (int i = 0; i < header.length; i++) {
                    headerMap.put(header[i].toUpperCase(), Integer.valueOf(i));
                }
            } else {
                //there are no fields in here that may contain a comma
                String[] split = line.split("\\,");
                String name = trim(split[headerMap.get(H_PV_NAME)]);
                String timestamp = trim(split[headerMap.get(H_TIMESTAMP)]);
                String status = trim(split[headerMap.get(H_STATUS)]);
                String severity = trim(split[headerMap.get(H_SEVERITY)]);
                String valueType = trim(split[headerMap.get(H_VALUE_TYPE)]);
                String value = trim(split[headerMap.get(H_VALUE)]);

                data.add(piecesToVType(timestamp, status, severity, value, valueType));
                names.add(name);
            }
        }
        return new DescriptionDateData<>(date, names, data);
    }

    private static String trim(String value) {
        value = value.trim();
        if (value.charAt(0) == '"') {
            value = value.substring(1, value.length()-1);
        }
        return value;
    }

    // --------------------------------------------------------------------------------------------------
    //
    // Private methods for working with files: WRITE TO FILE METHODS
    //
    // --------------------------------------------------------------------------------------------------

    /**
     * Creates (if not exists) file and writes content.
     *
     * @param filePath file path
     * @param fileSuffix file suffix
     * @param dataObject object from which content is generated
     *
     * @throws IOException when exception occurs.
     */
    private void writeToFile(String filePath, FileType fileType, Object dataObject) throws IOException {
        Path path = Paths.get(repositoryPath.getAbsolutePath(), filePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            path = Files.createFile(path);
        }
        String content = generateContent(dataObject, fileType);
        Files.write(path, content.getBytes());
    }

    /**
     * Delete the file at the given path.
     *
     * @param filePath the path
     * @return true if the file was deleted or false otherwise
     * @throws IOException in case of an error
     */
    private boolean deleteFile(String filePath) throws IOException {
        Path path = Paths.get(repositoryPath.getAbsolutePath(), filePath);
        if (!Files.exists(path)) {
            return false;
        }
        Files.delete(path);
        return true;
    }

    /**
     * Generates file content. File type is determined by file suffix.
     *
     * @param dataObject the object from which content is generated
     * @param fileType file suffix
     *
     * @return generated file content.
     */
    private String generateContent(Object dataObject, FileType fileType) {
        if (fileType == FileType.BEAMLINE_SET) {
            if (dataObject instanceof BeamlineSetData) {
                return generateBeamlineSetContent((BeamlineSetData) dataObject);
            }
        } else if (fileType == FileType.SNAPSHOT) {
            if (dataObject instanceof VSnapshot) {
                return generateSnapshotFileContent((VSnapshot) dataObject);
            }
        }
        throw new IllegalArgumentException("The data '" + dataObject + "' does not match the file type '"
                    + fileType + "'.");
    }

    /**
     * Generates beamline set file content and returns it.
     *
     * @param data beamline set data to transform to string
     *
     * @return generated beamline set file content
     */
    private String generateBeamlineSetContent(BeamlineSetData data) {
        String description = data.getDescription();
        description = description.replaceAll("\n", "# ");
        List<String> pvs = data.getPVList();
        final StringBuilder sb = new StringBuilder(BSD_ENTRY_LENGTH * pvs.size());
        sb.append("# ").append(DESCRIPTION_TAG).append("\n# ");
        sb.append(description).append("\n#\n");
        sb.append(H_PV_NAME).append('\n');
        pvs.forEach(e -> sb.append(e).append('\n'));
        return sb.toString();
    }


    /**
     * Generates snapshot file content and returns it.
     *
     * @param data snapshot file data
     *
     * @return generated snapshot file content
     */
    private String generateSnapshotFileContent(VSnapshot data) {
        List<VType> values = data.getValues();
        List<String> names = data.getNames();
        StringBuilder sb = new StringBuilder(SNP_ENTRY_LENGTH * names.size());
        sb.append("# Date: ").append(TIMESTAMP_FORMATTER.format(data.getTimestamp().toDate())).append('\n');
        sb.append(SNAPSHOT_FILE_HEADER).append('\n');
        for (int i = 0; i < names.size(); i++) {
            sb.append(createSnapshotFileEntry(names.get(i),values.get(i))).append('\n');
        }
        return sb.toString();
    }

    /**
     * Converts given name and data into a string formatted for the snapshot file and returns that string
     *
     * @param entry snapshot entry
     *
     * @return into string converted given snapshot entry data.
     */
    private static String createSnapshotFileEntry(String name, VType data) {
        StringBuilder sb = new StringBuilder(SNP_ENTRY_LENGTH);
        sb.append(name).append(',');
        if (data instanceof Time) {
            sb.append(((Time)data).getTimestamp());
        }
        sb.append(',');
        if (data instanceof Alarm) {
            sb.append(((Alarm)data).getAlarmName()).append(',');
            sb.append(((Alarm)data).getAlarmSeverity()).append(',');
        } else {
            sb.append(",,");
        }
        sb.append(vtypeToStringType(data)).append(',');
        sb.append('\"').append(Utilities.toRawStringValue(data)).append('\"');
        return sb.toString();
    }

    // --------------------------------------------------------------------------------------------------
    //
    // Utility methods.
    //
    // --------------------------------------------------------------------------------------------------

    /**
     * Convert the path to an array. The array contains the segments relative to the BeamlineSets or Snapshots folder
     * within the base level or root.
     *
     * @param path the path to parse
     * @param level optional base level; if given the path will be relative to the
     *              {@link BaseLevel#getStorageName()}/DIR_NAME, otherwise the path is relative to /DIR_NAME,
     *              where DIR_NAME is Snapshots or BeamlineSets
     * @return the path segments
     */
    private static String[] convertStringToPath(String path, Optional<BaseLevel> level) {
        if (path != null && !path.isEmpty()) {
            path = path.replace("\\", GIT_PATH_DELIMITER);
            String[] pp = path.split(GIT_PATH_DELIMITER);
            if (level.isPresent()) {
                if (pp.length < 3) return null;
                if (pp[0].equals(level.get().getStorageName())) {
                    String[] ret = new String[pp.length - 2];
                    System.arraycopy(pp, 2, ret, 0, ret.length);
                    return ret;
                }
            } else {
                if (pp.length < 2) return null;
                String[] ret = new String[pp.length - 1];
                System.arraycopy(pp, 1, ret, 0, ret.length);
                return ret;
            }

        }
        return null;
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
        for (int i = 0; i < path.length-1; i++) {
            sb.append(path[i]).append(GIT_PATH_DELIMITER);
        }
        if (path.length > 0) {
            String last = path[path.length-1];
            if (fileType == FileType.BEAMLINE_SET) {
                last = last.replace(FileType.SNAPSHOT.suffix, FileType.BEAMLINE_SET.suffix);
            } else {
                last = last.replace(FileType.BEAMLINE_SET.suffix, FileType.SNAPSHOT.suffix);
            }
            if (!last.toLowerCase().endsWith(fileType.suffix)) {
                last += fileType.suffix;
            }
            sb.append(last);
        }
        return sb.substring(0, sb.length());
    }

    /**
     * Converts a single entry to the VType.
     *
     * @param timestamp the timestamp of the entry, given in sec.nano format
     * @param status the alarm status
     * @param severity the alarm severity
     * @param value the raw value
     * @param valueType the value type
     * @return VType that contains all parameters and matches the type provided by <code>valueType</code>
     */
    private static VType piecesToVType(String timestamp, String status, String severity, String value, String valueType) {
        String[] t = timestamp.split("\\.");
        Time time = ValueFactory.newTime(Timestamp.of(Long.parseLong(t[0]),Integer.parseInt(t[1])));
        Alarm alarm = ValueFactory.newAlarm(AlarmSeverity.valueOf(severity.toUpperCase()),status);
        Display display = ValueFactory.newDisplay(0d,0d,0d,null,null,0d,0d,0d,0d,0d);
        ValueType vtype = ValueType.forName(valueType);

        String[] valueAndLabels = value.split(ENUM_VALUE_SPLITTER);
        if (valueAndLabels.length > 0) {
            if (valueAndLabels[0].charAt(0) == '[') {
                valueAndLabels[0] = valueAndLabels[0].substring(1,valueAndLabels[0].length()-1);
            }
            if (valueAndLabels.length > 1) {
                valueAndLabels[1] = valueAndLabels[1].substring(1, valueAndLabels[1].length()-1);
            }
        }
        value = valueAndLabels[0];
        switch(vtype) {
            case DOUBLE_ARRAY :
                String[] sd = value.split(ARRAY_SPLITTER);
                double[] dd = new double[sd.length];
                for (int i = 0; i < sd.length; i++) {
                    dd[i] = Double.parseDouble(sd[i]);
                }
                ListDouble datad = new ArrayDouble(dd);
                return ValueFactory.newVDoubleArray(datad, alarm, time, display);
            case FLOAT_ARRAY :
                String[] sf = value.split(ARRAY_SPLITTER);
                float[] df = new float[sf.length];
                for (int i = 0; i < sf.length; i++) {
                    df[i] = Float.parseFloat(sf[i]);
                }
                ListFloat dataf = new ArrayFloat(df);
                return ValueFactory.newVFloatArray(dataf, alarm, time, display);
            case LONG_ARRAY :
                String[] sl = value.split(ARRAY_SPLITTER);
                long[] dl = new long[sl.length];
                for (int i = 0; i < sl.length; i++) {
                    dl[i] = Long.parseLong(sl[i]);
                }
                ListLong datal = new ArrayLong(dl);
                return ValueFactory.newVLongArray(datal, alarm, time, display);
            case INT_ARRAY :
                String[] si = value.split(ARRAY_SPLITTER);
                int[] di = new int[si.length];
                for (int i = 0; i < si.length; i++) {
                    di[i] = Integer.parseInt(si[i]);
                }
                ListInt datai = new ArrayInt(di);
                return ValueFactory.newVIntArray(datai, alarm, time, display);
            case SHORT_ARRAY :
                String[] ss = value.split(ARRAY_SPLITTER);
                short[] ds = new short[ss.length];
                for (int i = 0; i < ss.length; i++) {
                    ds[i] = Short.parseShort(ss[i]);
                }
                ListShort datas = new ArrayShort(ds);
                return ValueFactory.newVShortArray(datas, alarm, time, display);
            case BYTE_ARRAY :
                String[] sb = value.split(ARRAY_SPLITTER);
                byte[] db = new byte[sb.length];
                for (int i = 0; i < sb.length; i++) {
                    db[i] = Byte.parseByte(sb[i]);
                }
                ListByte datab = new ArrayByte(db);
                return ValueFactory.newVNumberArray(datab, alarm, time, display);
            case ENUM_ARRAY :
                String[] se = value.split(ARRAY_SPLITTER);
                List<String> labels = Arrays.asList(valueAndLabels[1].split(ARRAY_SPLITTER));
                int[] de = new int[se.length];
                for (int i = 0; i < se.length; i++) {
                    de[i] = labels.indexOf(se[i]);
                }
                ListInt datae = new ArrayInt(de);
                return ValueFactory.newVEnumArray(datae, labels, alarm, time);
            case STRING_ARRAY :
                String[] str = value.split(ARRAY_SPLITTER);
                return ValueFactory.newVStringArray(Arrays.asList(str), alarm, time);
            case BOOLEAN_ARRAY :
                String[] sbo = value.split(ARRAY_SPLITTER);
                boolean[] dbo = new boolean[sbo.length];
                for (int i = 0; i < sbo.length; i++) {
                    dbo[i] = Boolean.parseBoolean(sbo[i]);
                }
                ListBoolean databo = new ArrayBoolean(dbo);
                return ValueFactory.newVBooleanArray(databo, alarm, time);
            case DOUBLE :
                return ValueFactory.newVDouble(Double.parseDouble(value), alarm, time, display);
            case FLOAT :
                return ValueFactory.newVFloat(Float.parseFloat(value), alarm, time, display);
            case LONG :
                return ValueFactory.newVLong(Long.parseLong(value), alarm, time, display);
            case INT :
                return ValueFactory.newVInt(Integer.parseInt(value), alarm, time, display);
            case SHORT :
                return ValueFactory.newVShort(Short.parseShort(value), alarm, time, display);
            case BYTE :
                return ValueFactory.newVByte(Byte.parseByte(value), alarm, time, display);
            case BOOLEAN :
                return ValueFactory.newVBoolean(Boolean.parseBoolean(value), alarm, time);
            case STRING :
                return ValueFactory.newVString(value, alarm, time);
            case ENUM :
                List<String> lbls = Arrays.asList(valueAndLabels[1]);
                return ValueFactory.newVEnum(lbls.indexOf(value), lbls, alarm, time);
        }

        throw new IllegalArgumentException("Unknown data type " + valueType + ".");
    }

    /**
     * Transforms the vtype to a string representing only the type of the vtype (e.g. double, string_array etc.).
     *
     * @see ValueType#name
     * @param type the type to transform
     * @return the value type as string
     */
    private static String vtypeToStringType(VType type) {
        for (ValueType t : ValueType.values()) {
            if (t.instanceOf(type)) {
                return t.name;
            }
        }
        throw new IllegalArgumentException("Unknown data type " + type.getClass() + ".");
    }

    private Credentials getCredentials(Optional<Credentials> previous) {
        final Credentials[] provider = new Credentials[1];
        Subject subj = SecuritySupport.getSubject();
        final String currentUser = previous.isPresent() ? previous.get().getUsername() :
                subj == null ? null : SecuritySupport.getSubjectName(subj);
        org.eclipse.swt.widgets.Display.getDefault().syncExec(() -> {
            String username = Activator.getInstance().getUsername(Optional.ofNullable(currentUser));
            char[] password = Activator.getInstance().getPassword(Optional.ofNullable(currentUser),username);
            if (username == null || password == null) {
                UsernameAndPasswordDialog dialog = new UsernameAndPasswordDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), username,
                        "Please, provide the username and password to access Save and Restore git repository");
                dialog.openAndWat().ifPresent(e -> {
                    provider[0] = e;
                    if (e.isRemember()) {
                        Activator.getInstance().storeCredentials(Optional.ofNullable(currentUser),e.getUsername(), e.getPassword());
                    }
                });
            } else {
                provider[0] = new Credentials(username, password, false);
            }
        });
        return provider[0];
    }

    private static CredentialsProvider toCredentialsProvider(Credentials cred) {
        return cred == null ? null : new UsernamePasswordCredentialsProvider(cred.getUsername(), cred.getPassword());
    }

    private static boolean isNotAuthorized(TransportException e) {
        return e.getMessage().toLowerCase().contains("not authorized");
    }

    private static boolean isNothingToPush(TransportException e) {
        return e.getMessage().toLowerCase().contains("nothing to push");
    }

    static void deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if(files != null) {
                for(File f: files) {
                    if(f.isDirectory()) {
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