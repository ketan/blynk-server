package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static java.util.function.Function.identity;


/**
 * Class responsible for saving/reading user data to/from disk.
 *
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 6:53 PM
 */
public class FileManager {

    private static final Logger log = LogManager.getLogger(FileManager.class);
    private static final String USER_FILE_EXTENSION = ".user";
    private static final String ORG_FILE_EXTENSION = ".org";

    private static final String DELETED_DATA_DIR_NAME = "deleted";
    private static final String BACKUP_DATA_DIR_NAME = "backup";
    private static final String ORGANIZATION_DATA_DIR_NAME = "organizations";
    private static final String CLONE_DATA_DIR_NAME = "clone";

    /**
     * Folder where all user profiles are stored locally.
     */
    private Path dataDir;
    private Path orgDataDir;
    private Path deletedDataDir;
    private Path backupDataDir;
    private String cloneDataDir;
    private final String host;

    public FileManager(String dataFolder, String host) {
        if (dataFolder == null || dataFolder.isEmpty() || dataFolder.equals("/path")) {
            System.out.println("WARNING : '" + dataFolder + "' does not exists. "
                    + "Please specify correct -dataFolder parameter.");
            dataFolder = Paths.get(System.getProperty("java.io.tmpdir"), "blynk").toString();
            System.out.println("Your data may be lost during server restart. Using temp folder : " + dataFolder);
        }
        try {
            Path dataFolderPath = Paths.get(dataFolder);
            this.dataDir = createDirectories(dataFolderPath);
            this.orgDataDir = createDirectories(Paths.get(dataFolder, ORGANIZATION_DATA_DIR_NAME));
            this.deletedDataDir = createDirectories(Paths.get(dataFolder, DELETED_DATA_DIR_NAME));
            this.backupDataDir = createDirectories(Paths.get(dataFolder, BACKUP_DATA_DIR_NAME));
            this.cloneDataDir = createDirectories(Paths.get(dataFolder, CLONE_DATA_DIR_NAME)).toString();
        } catch (Exception e) {
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "blynk");

            System.out.println("WARNING : could not find folder '" + dataFolder + "'. "
                    + "Please specify correct -dataFolder parameter.");
            System.out.println("Your data may be lost during server restart. Using temp folder : "
                    + tempDir.toString());

            try {
                this.dataDir = createDirectories(tempDir);
                this.orgDataDir = createDirectories(Paths.get(this.dataDir.toString(), ORGANIZATION_DATA_DIR_NAME));
                this.deletedDataDir = createDirectories(
                        Paths.get(this.dataDir.toString(), DELETED_DATA_DIR_NAME));
                this.backupDataDir = createDirectories(
                        Paths.get(this.dataDir.toString(), BACKUP_DATA_DIR_NAME));
                this.cloneDataDir = createDirectories(
                        Paths.get(this.dataDir.toString(), CLONE_DATA_DIR_NAME)).toString();
            } catch (Exception ioe) {
                throw new RuntimeException(ioe);
            }
        }

        this.host = host;
        log.info("Using data dir '{}'", dataDir);
    }

    public Path getDataDir() {
        return dataDir;
    }

    public Path generateOrgFileName(int orgId) {
        return Paths.get(orgDataDir.toString(), orgId + ORG_FILE_EXTENSION);
    }

    public Path generateFileName(String email) {
        return Paths.get(dataDir.toString(), email + USER_FILE_EXTENSION);
    }

    public Path generateBackupFileName(String email, int orgId) {
        return Paths.get(backupDataDir.toString(), email + "." + orgId + ".user."
                + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    public boolean deleteOrg(int orgId) {
        Path file = generateOrgFileName(orgId);
        try {
            FileUtils.move(file, this.deletedDataDir);
        } catch (IOException e) {
            log.debug("Failed to move file. {}", e.getMessage());
            return false;
        }
        return true;
    }

    public boolean delete(String email) {
        Path file = generateFileName(email);
        try {
            FileUtils.move(file, this.deletedDataDir);
        } catch (IOException e) {
            log.debug("Failed to move file. {}", e.getMessage());
            return false;
        }
        return true;
    }

    public void override(Organization org, boolean isFancy) throws IOException {
        Path path = generateOrgFileName(org.id);
        JsonParser.writeOrg(path.toFile(), org, isFancy);
    }

    public void overrideUserFile(User user, boolean isFancy) throws IOException {
        Path path = generateFileName(user.email);

        JsonParser.writeUser(path.toFile(), user, isFancy);
    }

    public ConcurrentMap<Integer, Organization> deserializeOrganizations() {
        log.debug("Starting reading organizations DB.");

        final File[] files = orgDataDir.toFile().listFiles();

        ConcurrentMap<Integer, Organization> temp;
        if (files != null) {
            temp = Arrays.stream(files).parallel()
                    .filter(file -> file.isFile() && file.getName().endsWith(ORG_FILE_EXTENSION))
                    .flatMap(file -> {
                        try {
                            Organization org = JsonParser.parseOrganization(file);
                            org.resetDeviceStatus();
                            return Stream.of(org);
                        } catch (IOException ioe) {
                            log.error("Error parsing file '{}'. Error : {}", file, ioe.getMessage());
                        }
                        throw new RuntimeException("Error reading organization.");
                    })
                    .collect(Collectors.toConcurrentMap(org -> org.id, identity()));

        } else {
            temp = new ConcurrentHashMap<>();
        }

        log.debug("Reading organization DB finished.");
        return temp;
    }

    /**
     * Loads all user profiles one by one from disk using dataDir as starting point.
     *
     * @return mapping between username and it's profile.
     */
    public ConcurrentMap<String, User> deserializeUsers() {
        log.debug("Starting reading user DB.");

        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**" + USER_FILE_EXTENSION);
        ConcurrentMap<String, User> temp;
        try {
            temp = Files.walk(dataDir, 1).parallel()
                    .filter(path -> Files.isRegularFile(path) && pathMatcher.matches(path))
                    .flatMap(path -> {
                        try {
                            User user = JsonParser.parseUserFromFile(path);
                            makeProfileChanges(user);

                            return Stream.of(user);
                        } catch (IOException ioe) {
                            String errorMessage = ioe.getMessage();
                            log.error("Error parsing file '{}'. Error : {}", path, errorMessage);
                            if (errorMessage != null
                                    && (errorMessage.contains("end-of-input")
                                    || errorMessage.contains("Illegal character"))) {
                                return restoreFromBackup(path.getFileName());
                            }
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toConcurrentMap((user) -> user.email, identity()));
        } catch (Exception e) {
            log.error("Error reading user profiles from disk. {}", e.getMessage());
            throw new RuntimeException(e);
        }

        log.debug("Reading user DB finished.");
        return temp;
    }

    private Stream<User> restoreFromBackup(Path restoreFileNamePath) {
        log.info("Trying to recover from backup...");
        String filename = restoreFileNamePath.toString();
        try {
            File[] files = backupDataDir.toFile().listFiles(
                    (dir, name) -> name.startsWith(filename)
            );

            File backupFile = FileUtils.getLatestFile(files);
            if (backupFile == null) {
                log.info("Didn't find any files for recovery :(.");
                return Stream.empty();
            }
            log.info("Found {}. You are lucky today :).", backupFile.getAbsoluteFile());

            User user = JsonParser.parseUserFromFile(backupFile);
            makeProfileChanges(user);
            //profile saver thread is launched after file manager is initialized.
            //so making sure user profile will be saved
            //this is not very important as profile will be updated by user anyway.
            user.lastModifiedTs = System.currentTimeMillis() + 10 * 1000;
            log.info("Restored.", backupFile.getAbsoluteFile());
            return Stream.of(user);
        } catch (Exception e) {
            //ignore
            log.error("Restoring from backup failed. {}", e.getMessage());
        }
        return Stream.empty();
    }

    //public is for tests only
    public void makeProfileChanges(User user) {
        if (user.email == null) {
            user.email = user.name;
        }
        user.ip = host;
    }

    public Map<String, Integer> getUserProfilesSize() {
        Map<String, Integer> userProfileSize = new HashMap<>();
        File[] files = dataDir.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(USER_FILE_EXTENSION)) {
                    userProfileSize.put(file.getName(), (int) file.length());
                }
            }
        }
        return userProfileSize;
    }

    public boolean writeCloneProjectToDisk(String token, String json) {
        try {
            Path path = Paths.get(cloneDataDir, token);
            Files.write(path, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            return true;
        } catch (Exception e) {
            log.error("Error saving cloned project to disk. {}", e.getMessage());
        }
        return false;
    }

    public String readClonedProjectFromDisk(String token) {
        Path path = Paths.get(cloneDataDir, token);
        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Didn't find cloned project on disk. Path {}. Reason {}", path.toString(), e.getMessage());
        }
        return null;
    }
}
