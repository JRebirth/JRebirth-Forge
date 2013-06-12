/**
 * Get more info at : www.jrebirth.org . Copyright JRebirth.org © 2011-2013
 * Contact : sebastien.bordes@jrebirth.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jrebirth.forge;

import static org.jrebirth.forge.utils.Constants.createPackageIfNotExist;
import static org.jrebirth.forge.utils.Constants.determinePackageAvailability;
import static org.jrebirth.forge.utils.Constants.installDependencies;
import static org.jrebirth.forge.utils.Constants.jrebirthPresentationDependency;
import static org.jrebirth.forge.utils.Constants.determineFileAvailabilty;


import java.util.Locale;
import java.util.Properties;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.velocity.app.Velocity;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.util.Packages;
import org.jrebirth.forge.utils.Constants.CreationType;

/**
 * The main plugin for JRebirth.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 */
@Alias("jrebirth")
@Help("A Forge addon to enable and work on JRebirth framework.")
@RequiresFacet({ DependencyFacet.class, JavaSourceFacet.class })
@RequiresProject
public class JRebirthPlugin implements Plugin {

    /** The shell. */
    @Inject
    private ShellPrompt shell;

    /** The project. */
    @Inject
    private Project project;

    /** The install. */
    @Inject
    private Event<InstallFacets> install;


    static {
        final Properties properties = new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(properties);
    }

    /**
     * The setup command for JRebirth. This adds dependency to the current project
     * 
     * @param out the out
     * @param moduleName the module name
     */
    @SetupCommand(help = "Installs basic setup to work with JRebirth Framework.")
    public void setup(final PipeOut out, @Option(name = "module", shortName = "m", help = "The Module name to be installed.")
    final String moduleName) {

        if (!this.project.hasFacet(JRebirthFacet.class)) {
            this.install.fire(new InstallFacets(JRebirthFacet.class));
        }
        if (moduleName != null) {
            if ("Presentation".equalsIgnoreCase(moduleName)) {

                installDependencies(this.project, this.shell, out, jrebirthPresentationDependency(), true);
            }
        }
    }

    /**
     * If jrebirth command is not executed with any argument this method will be called.
     * 
     * @param out the out
     */
    @DefaultCommand
    public void defaultCommand(final PipeOut out) {
        if (this.project.hasFacet(JRebirthFacet.class)) {
            out.println("JRebirth is installed.");
        } else {
            out.println("JRebirth is not installed. Use 'jrebirth setup' to install.");
        }
    }

    /**
     * Creates Java files for user interface mainly for Model, Controller and View.
     * 
     * @param type the type
     * @param topLevelPackage the top level package
     * @param sourceFolder the source folder
     * @param name the name
     * @param out the out
     */
    private void createUiFiles(final CreationType type, final String topLevelPackage, final DirectoryResource sourceFolder, final String name, final PipeOut out) {

        DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + type.getPackageName()));

        createPackageIfNotExist(directory, "UI", out);

        final DirectoryResource beansDirectory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + CreationType.BEAN.getPackageName()));

        createPackageIfNotExist(beansDirectory, "beans", out);

        directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + type.getPackageName() + "." + name.toLowerCase(Locale.ENGLISH)));

        determinePackageAvailability(directory, out);

        final String javaStandardClassName = String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1, name.length()));

       
        determineFileAvailabilty(project,beansDirectory,CreationType.BEAN,javaStandardClassName,topLevelPackage,out,"",".java");
        
        determineFileAvailabilty(project,directory,type,javaStandardClassName,topLevelPackage,out,"Model","Model.java");
                
        determineFileAvailabilty(project,directory,type,javaStandardClassName,topLevelPackage,out,"View","View.java");

    
        if (type == CreationType.MVC) {
            
            determineFileAvailabilty(project,directory,type,javaStandardClassName,topLevelPackage,out,"Controller","Controller.java");

        }
    }

    /**
     * Creates FXML and controller files.
     * 
     * @param creationType the creation type
     * @param topLevelPackage the top level package
     * @param sourceFolder the source folder
     * @param name the name
     * @param out the out
     */
    private void createUiFxmlFiles(final CreationType creationType, final String topLevelPackage, final DirectoryResource sourceFolder, final String name, final PipeOut out) {

        DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + creationType.getPackageName()));

        if (!directory.isDirectory()) {
            out.println(ShellColor.BLUE, "The FXML UI package does not exist. Creating it.");
            directory.mkdirs();
        }

        directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + creationType.getPackageName() + "." + name.toLowerCase(Locale.ENGLISH)));

        determinePackageAvailability(directory, out);

    }

    /**
     * Creates Java files for Command, Service etc.
     *
     * @param type the type
     * @param topLevelPackage the top level package
     * @param sourceFolder the source folder
     * @param fileName the file name
     * @param out the out
     */
    private void createNonUiFiles(final CreationType type, final String topLevelPackage, final DirectoryResource sourceFolder, final String fileName, final PipeOut out) {

        DirectoryResource directory = null;
        String finalName = "";
        // Convert first character to upper case
        finalName = String.valueOf(fileName.charAt(0)).toUpperCase().concat(fileName.substring(1, fileName.length()));
        try {

            if (!"service".contains(finalName) && !"Service".contains(finalName)) {
                finalName = finalName.concat("Service");
            }

            directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + type.getPackageName() + "."));

            if (directory != null && !directory.isDirectory()) {
                out.println(ShellColor.BLUE, "The " + type.getPackageName() + " package does not exist. Creating it.");
                directory.mkdir();
            }
            
            determineFileAvailabilty(project,directory,type,finalName,topLevelPackage,out,"",".java");

        } catch (final Exception e) {
            out.println(ShellColor.RED, "Could not create files.");
        }
    }

    /**
     * Creates the files.
     * 
     * @param type the type
     * @param name the name
     * @param out the out
     */
    private void createFiles(final CreationType type, final String name, final PipeOut out) {

        final MetadataFacet metadata = this.project.getFacet(MetadataFacet.class);
        final DirectoryResource sourceFolder = this.project.getFacet(JavaSourceFacet.class).getSourceFolder();

        switch (type) {
            case MV:
            case MVC:
                createUiFiles(type, metadata.getTopLevelPackage(), sourceFolder, name, out);
                break;
            case FXML:
                createUiFxmlFiles(type, metadata.getTopLevelPackage(), sourceFolder, name, out);
                break;
            case COMMAND:
            case SERVICE:
            case RESOURCE:
                createNonUiFiles(type, metadata.getTopLevelPackage(), sourceFolder, name, out);
                break;
            default:
                break;
        }
    }

    /**
     * Command to create Model, View and Controller.
     * 
     * @param out the out
     * @param name the name
     */
    @Command(value = "mvc-create", help = "Create Model,View and Controller for the given name")
    public void createMVC(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the MVC Group to be created.")
            final String name

            ) {
        createFiles(CreationType.MVC, name, out);
    }

    /**
     * Creates the fxml.
     * 
     * @param out the out
     * @param name the name
     */
    @Command(value = "fxml-create", help = "Create FXML and Controller for the given name")
    public void createFXML(final PipeOut out, @Option(name = "name", shortName = "n", required = true, help = "Name of the FXML Group to be created.")
    final String name) {
        createFiles(CreationType.FXML, name, out);
    }

    /**
     * Creates the mv.
     * 
     * @param out the out
     * @param name the name
     */
    @Command(value = "mv-create", help = "Create Model and View for the given name")
    public void createMV(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the MV Group to be created.")
            final String name) {

        createFiles(CreationType.MV, name, out);
    }

    /**
     * Creates the command.
     * 
     * @param out the out
     * @param commandName the command name
     */
    @Command(value = "command-create", help = "Create a command for the given name")
    public void createCommand(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Command to be created.")
            final String commandName) {
        createFiles(CreationType.COMMAND, commandName, out);
    }

    /**
     * Creates the service.
     * 
     * @param out the out
     * @param serviceName the service name
     */
    @Command(value = "service-create", help = "Create a service for the given name")
    public void createService(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Service to be created.")
            final String serviceName) {

        createFiles(CreationType.SERVICE, serviceName, out);
    }

    /**
     * Creates the resource.
     * 
     * @param out the out
     * @param resourceName the resource name
     */
    @Command(value = "resource-create", help = "Create a resource for the given name")
    public void createResource(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Resource to be created.")
            final String resourceName) {
        createFiles(CreationType.RESOURCE, resourceName, out);
    }

}
