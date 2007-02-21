package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.DatabaseAdapter;
import edu.northwestern.bioinformatics.bering.Main;
import edu.northwestern.bioinformatics.bering.dialect.Dialect;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Rhett Sutphin
 */
public class MigrateTaskHelper {
    private String migrationsDir;
    private String dialectName;

    private Integer targetMigration;
    private Integer targetRelease;

    private HelperCallbacks callbacks;

    public MigrateTaskHelper(HelperCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void execute() {
        Main command = new Main();
        command.setRootDir(createMigrationDirectory().getAbsolutePath());
        DatabaseAdapter adapter = createAdapter();
        command.setAdapter(adapter);
        command.migrate(getTargetRelease(), getTargetMigration());
        adapter.close();
    }

    private DatabaseAdapter createAdapter() {
        return new DatabaseAdapter(callbacks.getConnection(), createDialect());
    }

    // package-level for testing
    Dialect createDialect() {
        String d = getDialectName();
        if (d != null) d = d.trim();
        try {
            return (Dialect) Class.forName(d).newInstance();
        } catch (InstantiationException e) {
            throw new BeringTaskException("Could not create an instance of dialect " + d, e);
        } catch (IllegalAccessException e) {
            throw new BeringTaskException("Could not create an instance of dialect " + d, e);
        } catch (ClassNotFoundException e) {
            throw new BeringTaskException("Could not find dialect class " + d, e);
        } catch (ClassCastException e) {
            throw new BeringTaskException("Class " + d + " does not implement " + Dialect.class.getName(), e);
        }
    }

    private File createMigrationDirectory() {
        File migrationDirectory = new File(getMigrationsDir());
        if (!migrationDirectory.isAbsolute()) {
            migrationDirectory = callbacks.resolve(migrationDirectory);
        }
        if (!migrationDirectory.isDirectory()) {
            try {
                throw new BeringTaskException(migrationDirectory.getCanonicalPath() + " is not a directory");
            } catch (IOException e) {
                throw new BeringTaskException("bad migration directory name: " + migrationDirectory.toString(), e);
            }
        }
        return migrationDirectory;
    }

    public String getMigrationsDir() {
        return migrationsDir;
    }

    public void setMigrationsDir(String migrationsDir) {
        this.migrationsDir = migrationsDir;
    }

    public String getDialectName() {
        return dialectName;
    }

    public void setDialectName(String dialect) {
        if (dialect == null || dialect.trim().length() == 0) return;
        this.dialectName = dialect;
    }

    public void setTargetVersion(String version) {
        PropertyEditor editor = new TargetVersionEditor();
        try {
            editor.setAsText(version);
        } catch (IllegalArgumentException e) {
            throw new BeringTaskException("Invalid target version (" + version
                + ").  Should have the form 'R|M', 'R-M', or 'M'.", e);
        }
        Integer[] parsedVersion = (Integer[]) editor.getValue();
        targetRelease   = parsedVersion[0];
        targetMigration = parsedVersion[1];
    }

    public Integer getTargetMigration() {
        return targetMigration;
    }

    public Integer getTargetRelease() {
        return targetRelease;
    }

    public static interface HelperCallbacks {
        /** Return the connection that should be used during execution. */
        Connection getConnection();

        /** Resolve the given relative directory against the default base directory. */
        File resolve(File f);
    }
}
