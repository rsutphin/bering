package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.dialect.Dialect;
import edu.northwestern.bioinformatics.bering.dialect.Oracle;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

/**
 * @author Rhett Sutphin
 */
public class MigrateTaskHelperTest extends BeringTestCase {
    private MigrateTaskHelper helper = new MigrateTaskHelper(new StubCallbacks());

    public void testNoExceptionForNullDialectName() throws Exception {
        helper.setDialectName(null);
        assertNull("No dialect name => no dialect (delegate to DatabaseAdapter's sniffer)", helper.createDialect());
    }

    public void testWhitespaceStrippedFromDialect() throws Exception {
        helper.setDialectName(Oracle.class.getName() + '\t');
        Dialect d = helper.createDialect();
        assertTrue(d instanceof Oracle);
    }

    public void testSetTargetVersion() throws Exception {
        assertTargetVersionSet(5, 9, "5|9");
        assertTargetVersionSet(null, 11, "11");
        assertTargetVersionSet(null, null, "");
    }

    public void testSetInvalidTargetVersion() throws Exception {
        try {
            helper.setTargetVersion("bad");
            fail("Exception not thrown");
        } catch (BeringTaskException be) {
            assertEquals("Invalid target version (bad).  Should have the form 'R|M', 'R-M', or 'M'.", be.getMessage());
            assertNotNull(be.getCause());
        }
    }

    private void assertTargetVersionSet(Integer expectedTargetRelease, Integer expectedTargetMigration, String targetVersionString) {
        helper.setTargetVersion(targetVersionString);
        assertEquals("Target release wrong", expectedTargetRelease, helper.getTargetRelease());
        assertEquals("Target migration wrong", expectedTargetMigration, helper.getTargetMigration());
    }

    public void testExceptionThrownIfMigrationsDirIsNotADirectory() throws IOException {
        File tempFile = File.createTempFile("MigrateTaskTest", ".tmp");
        try {
            helper.setMigrationsDir(tempFile.getAbsolutePath());
            helper.execute();
            fail("Should have thrown exception");
        } catch (BeringTaskException expected) {
            String expectedMessage = tempFile.getCanonicalPath() + " is not a directory";
            assertTrue("exception message does not start with \"" + expectedMessage + "\": "  + expected.getMessage(),
                    expected.getMessage().startsWith(expectedMessage));
        }
    }

    private static class StubCallbacks implements MigrateTaskHelper.HelperCallbacks {
        private File unresolved;

        public Connection getConnection() {
            return null;
        }

        public File resolve(File f) {
            this.unresolved = f;
            return f;
        }
    }
}
