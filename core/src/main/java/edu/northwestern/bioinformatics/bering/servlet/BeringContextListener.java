package edu.northwestern.bioinformatics.bering.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Base class for {@link javax.servlet.ServletContextListener}s which automatically
 * run all outstanding migration scripts at application initialization.
 *
 * @see SpringBeringContextListener
 * @see DeployedMigrator
 * @author Rhett Sutphin
 */
public abstract class BeringContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ServletContext servletContext = servletContextEvent.getServletContext();
            getDeployedMigrator(servletContext).migrate();
        } finally {
            onMigrationComplete();
        }
    }

    /**
     * Template method called after all migrations are complete.  Subclasses may use it
     * to perform cleanup.
     */
    protected void onMigrationComplete() { }

    protected abstract DeployedMigrator getDeployedMigrator(ServletContext servletContext);

    public void contextDestroyed(ServletContextEvent servletContextEvent) { }
}
