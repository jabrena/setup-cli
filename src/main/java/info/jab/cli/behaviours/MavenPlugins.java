package info.jab.cli.behaviours;

import org.jspecify.annotations.NullMarked;

import info.jab.cli.io.UpdateMavenPom;

@NullMarked
public class MavenPlugins implements Behaviour1 {

    private final UpdateMavenPom updateMavenPom;

    public MavenPlugins() {
        this.updateMavenPom = new UpdateMavenPom();
    }

    // For testing purposes
    public MavenPlugins(UpdateMavenPom updateMavenPom) {
        this.updateMavenPom = updateMavenPom;
    }

    @Override
    public void execute(String parameter) {
        if ("flatten".equals(parameter)) {
            String pomPath = "pom.xml";
            String flattenPluginXmlPath = "maven-plugins/maven-plugins-flatten.xml";
            String propertiesXmlPath = "maven-plugins/maven-plugins-flatten-version.xml";

            updateMavenPom.writePluginInBuildSection(pomPath, flattenPluginXmlPath);
            updateMavenPom.writeProperties(pomPath, propertiesXmlPath);
            System.out.println("Flatten plugin has been programmatically added to pom.xml");
        } else {
            System.out.println("Execution parameter is not 'flatten' or 'add-properties'. No changes made to pom.xml.");
        }
    }

}
