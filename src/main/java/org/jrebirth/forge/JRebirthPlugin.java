/**
 * Get more info at : www.jrebirth.org . Copyright JRebirth.org © 2011-2013 Contact : sebastien.bordes@jrebirth.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.jrebirth.forge;

import java.util.List;
import javax.inject.Inject;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.plugins.DefaultCommand;
import javax.enterprise.event.Event;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.util.Packages;

/**
 *
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 */
@Alias("jrebirth")
@Help("A Forge addon to enable and work on JRebirth framework.")
@RequiresFacet({DependencyFacet.class, JavaSourceFacet.class})
@RequiresProject
public class JRebirthPlugin implements Plugin {

    @Inject
    private ShellPrompt shell;
    @Inject
    private Project project;
    @Inject
    private Event<InstallFacets> install;
    @Inject
    private ShellPrintWriter writer;
    private JavaSourceFacet javaSourceFacet;
    private DependencyFacet dependencyFacet;

    enum CreatioinType {

        MV, MVC, COMMAND, SERVICE, RESOURCE
    }

    @SetupCommand(help = "Installs basic setup to work with JRebirth Framework.")
    public void setup(PipeOut out, @Option(name = "module", shortName = "m") final String moduleName) {
        if (moduleName == null) {
            if (!project.hasFacet(JRebirthFacet.class)) {
                install.fire(new InstallFacets(JRebirthFacet.class));
            }

            if (project.hasFacet(JRebirthFacet.class)) {
                writer.println(ShellColor.GREEN, "JRebirth is configured.");
            }
        } else if (moduleName.equalsIgnoreCase("Presentation")) {

            installDependencys(jrebirthPresentationDependency(), true);
        }

    }

    @DefaultCommand
    public void defaultCommand(final PipeOut out) {
        if (project.hasFacet(JRebirthFacet.class)) {
            out.println("JRebirth is installed.");
        } else {
            out.println("JRebirth is not installed. Use 'jrebirth setup' to install.");
        }
    }

    private void createUiFiles(CreatioinType type, DirectoryResource sourceFolder, String name, PipeOut out) {

        DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax("ui." + name.toLowerCase()));
        if (directory.isDirectory()) {
            out.println(ShellColor.RED, "Unable to Create package. The package '" + directory.toString() + "' is already found");
            return;
        }

        if (type == CreatioinType.MVC) {
        } else {
        }

    }

    private void createNonUiFiles(CreatioinType type, DirectoryResource sourceFolder, String name, PipeOut out) {

        DirectoryResource directory = null;
        //  sourceFolder.getChildDirectory(Packages.toFileSyntax("ui." + name.toLowerCase()));
        String fileType = "";
        switch (type) {
            case COMMAND:
                directory = sourceFolder.getChildDirectory(Packages.toFileSyntax("command."));
                fileType = "";
                break;
            case SERVICE:
                directory = sourceFolder.getChildDirectory(Packages.toFileSyntax("service."));
                fileType = "Service";
                break;
            case RESOURCE:
                directory = sourceFolder.getChildDirectory(Packages.toFileSyntax("resource."));
                fileType = "";
                break;
        }

        if (!directory.isDirectory()) {
            directory.mkdir();
        }

    }

    private void createFiles(CreatioinType type, String name, PipeOut out) {

        if (name == null || name.equals("")) {
            out.println(ShellColor.RED, "Provide a proper name.");
            return;
        }

        DirectoryResource sourceFolder = project.getFacet(JavaSourceFacet.class).getSourceFolder();

        switch (type) {
            case MV:
            case MVC:
                createUiFiles(type, sourceFolder, name, out);
                break;
            case COMMAND:
            case SERVICE:
            case RESOURCE:
                createNonUiFiles(type, sourceFolder, name, out);
                break;
            default:
                break;
        }

    }

    @Command(value = "test", help = "Test command")
    public void testCommand(PipeOut out, @Option(name = "name", shortName = "n") final String name) {
        MetadataFacet metadata = project.getFacet(MetadataFacet.class);
        DirectoryResource sourceFolder = project.getFacet(JavaSourceFacet.class).getSourceFolder();
        String uiPackage = metadata.getTopLevelPackage() + ".ui";
        out.println("TopLevelPackage = " + uiPackage);
        if (name != null) {
            out.println("New Ui Package = " + uiPackage + "." + name.toLowerCase());
            DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(uiPackage + "." + name.toLowerCase()));
            if (directory.isDirectory()) {
                out.println(ShellColor.RED, directory.toString() + " Is already created");
            } else {
                out.println(ShellColor.GREEN, directory.toString() + " Is a new folder");
            }
        }
    }

    @Command(value = "create-mvc", help = "Create Model,View and Controller for the given name")
    public void createMVC(PipeOut out, @Option(name = "name", shortName = "n", required = true) final String name) {
        createFiles(CreatioinType.MVC, name, out);
    }

    @Command(value = "create-mv", help = "Create Model and View for the given name")
    public void createMV(PipeOut out,
            @Option(name = "name", shortName = "n", required = true) final String name) {

        createFiles(CreatioinType.MV, name, out);
    }

    @Command(value = "create-command", help = "Create a command for the given name")
    public void createCommand(PipeOut out,
            @Option(name = "name", shortName = "n", required = true) final String commandName) {
        createFiles(CreatioinType.COMMAND, commandName, out);
    }

    @Command(value = "create-service", help = "Create a service for the given name")
    public void createService(PipeOut out,
            @Option(name = "name", shortName = "n", required = true) final String serviceName) {

        createFiles(CreatioinType.SERVICE, serviceName, out);
    }

    /* TODO: Need to see how to do this. */
    @Command(value = "create-resource", help = "Create a resource for the given name")
    public void createResource(PipeOut out,
            @Option(name = "name", shortName = "n", required = true) final String resourceName) {
        createFiles(CreatioinType.RESOURCE, resourceName, out);
    }

    private static DependencyBuilder jrebirthPresentationDependency() {
        return DependencyBuilder.create().setGroupId("org.jrebirth").setArtifactId("presentation");
    }

    private void installDependencys(DependencyBuilder dependency, boolean askVersion) {
        dependencyFacet = project.getFacet(DependencyFacet.class);

        List<Dependency> versions = dependencyFacet.resolveAvailableVersions(dependency);
        if (askVersion) {
            Dependency dep = shell.promptChoiceTyped("What version do you want to install?", versions);
            dependency.setVersion(dep.getVersion());
        }
        dependencyFacet.addDirectDependency(dependency);

        writer.println(ShellColor.GREEN, dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + " is added to the dependency.");

    }
}
