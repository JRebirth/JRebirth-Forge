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

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jrebirth.forge.utils.Constants;

// TODO: Auto-generated Javadoc
/**
 * JRebirth Facet
 * 
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 */
@Alias("org.jrebirth")
@RequiresFacet({ DependencyFacet.class })
public class JRebirthFacet extends BaseFacet {

    /** The shell. */
    @Inject
    private ShellPrompt shell;

    /** The dependency facet. */
    private DependencyFacet dependencyFacet;

    /** The writer. */
    @Inject
    private ShellPrintWriter writer;

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.forge.project.Facet#install()
     */
    @Override
    public boolean install() {
        this.dependencyFacet = this.project.getFacet(DependencyFacet.class);

        this.dependencyFacet.addRepository("JRebirth Maven Repository", "http://repo.jrebirth.org/libs-release");
        this.dependencyFacet.addRepository("JRebirth Maven Snapshot Repository", "http://repo.jrebirth.org/libs-snapshot");
        
        Constants.installDependencies(project, shell, writer, jrebirthCoreDependency(), false);
        Constants.installDependencies(project, shell, writer, javafxDependency(), false);

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.forge.project.Facet#isInstalled()
     */
    @Override
    public boolean isInstalled() {
        if (this.project.hasFacet(JRebirthFacet.class)) {
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.forge.project.facets.BaseFacet#uninstall()
     */
    @Override
    public boolean uninstall() {
        this.dependencyFacet = this.project.getFacet(DependencyFacet.class);
        this.dependencyFacet.removeDependency(jrebirthCoreDependency());
        this.dependencyFacet.removeDependency(javafxDependency());
        this.dependencyFacet.removeRepository("JRebirth Maven Repository");
        return true;
    }

   
    /**
     * Jrebirth core dependency.
     * 
     * @return the dependency builder
     */
    private static DependencyBuilder jrebirthCoreDependency() {
        return DependencyBuilder.create().setGroupId("org.jrebirth").setArtifactId("core").setVersion("0.7.4-SNAPSHOT");
    }

    /**
     * Javafx dependency.
     * 
     * @return the dependency builder
     */
    private static DependencyBuilder javafxDependency() {
        return DependencyBuilder.create().setGroupId("javafx").setArtifactId("jfxrt").setVersion("2.2").
                setScopeType("system").setSystemPath("${java.home}/lib/jfxrt.jar");
    }
}
