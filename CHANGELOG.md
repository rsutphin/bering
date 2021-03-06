0.8.1
=====
Fixes
-----
* Ensure that bering can initialize a clean database using PostgreSQL
  9's JDBC adapter. (GH-3)

0.8.0
=====
Fixes
-----
* SeparateApplicationContextBeringContextListener now supports
  context-relative paths in the `beringContextConfigLocation` init
  param.

Changes
-------
* Maven-required `bering-all` parent artifact removed.

Internal
--------
* Build switched to Buildr from Maven 2.
* Project moved to Github from Google Code.

0.7.1
=====
Fixes
-----
* Correct resolution of external scripts inside jars when the path to the jar has spaces in it. (#4)

0.7
===
Features
--------
* Foreign key support using the "references" parameter when adding a column
  to a new or existing table.
* Explicit override of the generated FK constraint name using the
  "referenceName" parameter.
* MS SQL Server dialect.  (Contributed by Eric Wyles.)

Fixes
-----
* Implemented workaround for incorrect URL generation for scripts on Windows

0.6.1
=====
Fixes
-----
Modified default migrationsDir property for all maven mojos to work around
bug in Maven.  (Maven's javadoc indicates that the Directory of a FileSet
is interpreted relative to the POM, but the resources plugin [at least] does
not behave this way.)

0.6
===
Features
--------
* Maven plugin can be provided connection configuration using a class which
  implements DataSourceProvider.  (This is an alternative to plain JDBC 
  properties.)
* Dialect parameter no longer required.  If not provided, Bering will try to
  guess which of its built-in dialects to use.
* Added "external" migration function for executing external sql scripts
  located alongside your migration scripts.
* Added BeringContextListener and DeployedMigrator for automatically running
  migrations at web application startup.
* Added SpringBeringContextListener implementation of BeringContextListener
  for use in applications which use a Spring WebApplicationContext.
* Added SeparateApplicationContextBeringContextListener implementation of
  BeringContextListener for deployments where the main application context
  is dependent on the schema being up to date.
* Added "resources" goal to maven plugin -- automatically registers your
  migrations directory as a resource path for seamless deployment alongside
  your classes.
* Added "numeric" column type.

Minor
----
* Better exception handling / reporting in maven plugin

Technical
---------
* Refactored script discovery to support different ways of accessing
  migrations.  Initial implementation supports file-based loading.
* Added support for loading migrations scripts from the classpath.


0.5
===
* Initial release.  Includes support for PostgreSQL, Oracle, and HSQLDB.
