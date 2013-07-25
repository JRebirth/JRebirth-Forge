package org.jrebirth.forge.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.util.Packages;
import org.jrebirth.forge.AbstractJRebirthPluginTest;
import org.jrebirth.forge.utils.PluginUtils.CreationType;
import org.junit.Test;

/**
 * Test cases for color-add command.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 * 
 */
public class ColorAddTest extends AbstractJRebirthPluginTest {

    @Test
    public void testResourceIsNotExisitInStartup() {
        DirectoryResource directory = null;
        final DirectoryResource sourceFolder = project.getFacet(JavaSourceFacet.class).getSourceFolder();

        directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + CreationType.RESOURCE.getPackageName() + "."));
        assertFalse(directory.isDirectory());
    }

    @Test
    public void testForExistanceOfResourcePackage() throws Exception {
        executeColorAddWindowBorderWebCCCCCC();
        assertTrue(isResourcePackageExists(topLevelPackage + CreationType.RESOURCE.getPackageName() + "."));
    }

    @Test
    public void testForExistanceOfColorClass() throws Exception {
        executeColorAddWindowBorderWebCCCCCC();
        DirectoryResource directory = null;
        directory = getJavaSourceDirResource().getChildDirectory(Packages.toFileSyntax(topLevelPackage + CreationType.RESOURCE.getPackageName() + "."));
        System.out.println(projectName + "Colors.java");
        assertTrue(directory.getChild(projectName + "Colors.java").exists());
    }

    @Test
    public void testForExistanceOfTheVariableCreated() throws Exception {
        executeColorAddWindowBorderWebCCCCCC();
        System.out.println(topLevelPackage + CreationType.RESOURCE.getPackageName() + "."+ projectName + "Colors.java");
        final JavaInterface jInterface = parseJavaInterface(topLevelPackage + CreationType.RESOURCE.getPackageName() + ".", projectName + "Colors.java");
        assertTrue(jInterface.hasField("WINDOW_BORDER"));
    }

    /* *********************************** *************************************** */

    private void executeColorAddWindowBorderWebCCCCCC() throws Exception {
        getShell().execute("jrebirth color-add --name windowBorder --value CCCCCC --colorType web");
    }

}
