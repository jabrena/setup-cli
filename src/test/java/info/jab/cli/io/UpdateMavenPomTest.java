package info.jab.cli.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class UpdateMavenPomTest {

    private UpdateMavenPom updateMavenPom;

    @TempDir
    @SuppressWarnings("NullAway") // JUnit 5 guarantees initialization
    Path tempDir;

    // It's good practice to load resources from the classpath in a way that's testable
    // and doesn't rely on relative paths that might change.
    // For this example, we assume these files are in src/test/resources/info/jab/cli/io/
    private final String samplePluginXmlPath = "info/jab/cli/io/sample-plugin.xml";
    private final String samplePropertiesXmlPath = "info/jab/cli/io/sample-properties.xml";
    private final String malformedPropertiesXmlPath = "info/jab/cli/io/malformed-properties.xml";

    @BeforeEach
    void setUp() {
        updateMavenPom = new UpdateMavenPom();
    }

    private File createPomFile(String content) throws IOException {
        Path pomPath = tempDir.resolve("pom.xml");
        Files.writeString(pomPath, content);
        return pomPath.toFile();
    }

    private Document parseXml(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // Secure processing
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }

    @Test
    @DisplayName("writePluginInBuildSection: given no build section, should create build, plugins, and add plugin")
    void writePlugin_whenPomHasNoBuildSection_shouldAddPlugin() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writePluginInBuildSection(pomFile.getAbsolutePath(), samplePluginXmlPath);

        // Then
        Document doc = parseXml(pomFile);
        NodeList buildNodes = doc.getElementsByTagName("build");
        assertThat(buildNodes.getLength()).as("Build section count").isEqualTo(1);

        NodeList pluginsNodes = doc.getElementsByTagName("plugins");
        assertThat(pluginsNodes.getLength()).as("Plugins section count").isEqualTo(1);

        NodeList pluginNodes = doc.getElementsByTagName("plugin");
        assertThat(pluginNodes.getLength()).as("Plugin element count").isEqualTo(1);

        NodeList artifactIdNodes = doc.getElementsByTagName("artifactId");
        boolean found = false;
        for (int i = 0; i < artifactIdNodes.getLength(); i++) {
            if ("maven-compiler-plugin".equals(artifactIdNodes.item(i).getTextContent())) {
                found = true;
                break;
            }
        }
        assertThat(found).as("maven-compiler-plugin should be added").isTrue();
    }

    @Test
    @DisplayName("writePluginInBuildSection: given build section without plugins, should create plugins and add plugin")
    void writePlugin_whenPomHasBuildButNoPlugins_shouldAddPlugin() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <build></build>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writePluginInBuildSection(pomFile.getAbsolutePath(), samplePluginXmlPath);

        // Then
        Document doc = parseXml(pomFile);
        NodeList pluginsNodes = doc.getElementsByTagName("plugins");
        assertThat(pluginsNodes.getLength()).as("Plugins section count").isEqualTo(1);

        NodeList pluginNodes = doc.getElementsByTagName("plugin");
        assertThat(pluginNodes.getLength()).as("Plugin element count").isEqualTo(1);

        boolean foundMavenCompilerPlugin = false;
        for (int i = 0; i < pluginNodes.getLength(); i++) {
            org.w3c.dom.Element pluginElement = (org.w3c.dom.Element) pluginNodes.item(i);
            NodeList artifactIdNodes = pluginElement.getElementsByTagName("artifactId");
            if (artifactIdNodes.getLength() > 0 && "maven-compiler-plugin".equals(artifactIdNodes.item(0).getTextContent())) {
                foundMavenCompilerPlugin = true;
                break;
            }
        }
        assertThat(foundMavenCompilerPlugin).as("Should find maven-compiler-plugin").isTrue();
    }

    @Test
    @DisplayName("writePluginInBuildSection: given existing plugins, should add new plugin and preserve existing ones")
    void writePlugin_whenPomHasExistingPlugins_shouldAddNewPlugin() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.existing</groupId>
                                <artifactId>existing-plugin</artifactId>
                                <version>1.0</version>
                            </plugin>
                        </plugins>
                    </build>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writePluginInBuildSection(pomFile.getAbsolutePath(), samplePluginXmlPath);

        // Then
        Document doc = parseXml(pomFile);
        NodeList pluginNodes = doc.getElementsByTagName("plugin");
        assertThat(pluginNodes.getLength()).as("Total plugin count").isEqualTo(2);

        boolean foundNewPlugin = false;
        boolean foundExistingPlugin = false;

        for (int i = 0; i < pluginNodes.getLength(); i++) {
            org.w3c.dom.Element pluginElement = (org.w3c.dom.Element) pluginNodes.item(i);
            NodeList artifactIdNodes = pluginElement.getElementsByTagName("artifactId");
            if (artifactIdNodes.getLength() > 0) {
                String artifactIdText = artifactIdNodes.item(0).getTextContent();
                if ("maven-compiler-plugin".equals(artifactIdText)) {
                    foundNewPlugin = true;
                }
                if ("existing-plugin".equals(artifactIdText)) {
                    foundExistingPlugin = true;
                }
            }
        }
        assertThat(foundNewPlugin).as("New plugin 'maven-compiler-plugin' should be added").isTrue();
        assertThat(foundExistingPlugin).as("Existing plugin 'existing-plugin' should be preserved").isTrue();
    }

    @Test
    @DisplayName("writeProperties: given no properties section, should create properties and add java.version")
    void writeProperties_whenPomHasNoPropertiesSection_shouldAddProperties() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        Document doc = parseXml(pomFile);
        NodeList propertiesNodes = doc.getElementsByTagName("properties");
        assertThat(propertiesNodes.getLength()).as("Properties section count").isEqualTo(1);

        NodeList javaVersionNodes = doc.getElementsByTagName("java.version");
        assertThat(javaVersionNodes.getLength()).as("java.version element count").isEqualTo(1);
        assertThat(javaVersionNodes.item(0).getTextContent()).isEqualTo("17");
    }

    @Test
    @DisplayName("writeProperties: given existing properties, should add new property and preserve existing ones")
    void writeProperties_whenPomHasExistingProperties_shouldAddNewProperty() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <properties>
                        <another.property>value</another.property>
                    </properties>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        Document doc = parseXml(pomFile);
        NodeList javaVersionNodes = doc.getElementsByTagName("java.version");
        assertThat(javaVersionNodes.getLength()).as("java.version element count").isEqualTo(1);
        assertThat(javaVersionNodes.item(0).getTextContent()).isEqualTo("17");

        NodeList anotherPropertyNodes = doc.getElementsByTagName("another.property");
        assertThat(anotherPropertyNodes.getLength()).as("another.property element count").isEqualTo(1);
        assertThat(anotherPropertyNodes.item(0).getTextContent()).isEqualTo("value");
    }

    @Test
    @DisplayName("writeProperties: given existing property, should update its value")
    void writeProperties_whenPropertyExists_shouldUpdateValue() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <properties>
                        <java.version>11</java.version>
                    </properties>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // samplePropertiesXmlPath ("info/jab/cli/io/sample-properties.xml")
        // is assumed to contain <java.version>17</java.version> for this test.
        // If it contains other properties, they will also be added/updated.

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        Document doc = parseXml(pomFile);
        NodeList javaVersionNodes = doc.getElementsByTagName("java.version");
        assertThat(javaVersionNodes.getLength()).as("java.version element count").isEqualTo(1);
        assertThat(javaVersionNodes.item(0).getTextContent()).as("java.version should be updated").isEqualTo("17");
    }

    @Test
    @DisplayName("writePluginInBuildSection: should handle non-existent pom.xml gracefully")
    void writePlugin_whenPomFileDoesNotExist_shouldNotThrowUnhandledException() {
        // Given
        String nonExistentPomPath = tempDir.resolve("non-existent-pom.xml").toString();
        String validPluginXmlPath = samplePluginXmlPath;

        // When & Then: Expect no unhandled exception, error logged to stderr (as per current implementation)
        // The method itself catches Exception and prints to stderr, so we verify it doesn't crash.
        // A more robust test might involve capturing stderr or checking if a file was attempted to be written.
        assertThatNoException().isThrownBy(() ->
            updateMavenPom.writePluginInBuildSection(nonExistentPomPath, validPluginXmlPath)
        );
        // Further verification could involve checking that no file was created at nonExistentPomPath
        // or capturing System.err output if the test framework supports it.
    }

    @Test
    @DisplayName("writePluginInBuildSection: should handle invalid plugin XML resource path gracefully")
    void writePlugin_whenPluginXmlPathIsInvalid_shouldNotThrowUnhandledException() throws IOException {
        // Given
        String initialPomContent = """
                <project><modelVersion>4.0.0</modelVersion></project>
                """;
        File pomFile = createPomFile(initialPomContent);
        String invalidPluginXmlPath = "info/jab/cli/io/non-existent-plugin.xml";

        // When & Then
        assertThatNoException().isThrownBy(() ->
            updateMavenPom.writePluginInBuildSection(pomFile.getAbsolutePath(), invalidPluginXmlPath)
        );
        // Verify pom file is not modified or that an error was logged.
        // For simplicity, we check that the method completes. Current implementation logs to stderr.
    }

    @Test
    @DisplayName("writeProperties: should handle non-existent pom.xml gracefully")
    void writeProperties_whenPomFileDoesNotExist_shouldNotThrowUnhandledException() {
        // Given
        String nonExistentPomPath = tempDir.resolve("non-existent-pom.xml").toString();
        String validPropertiesXmlPath = samplePropertiesXmlPath;

        // When & Then
        assertThatNoException().isThrownBy(() ->
            updateMavenPom.writeProperties(nonExistentPomPath, validPropertiesXmlPath)
        );
    }

    @Test
    @DisplayName("writeProperties: should handle invalid properties XML resource path gracefully")
    void writeProperties_whenPropertiesXmlPathIsInvalid_shouldNotThrowUnhandledException() throws IOException {
        // Given
        String initialPomContent = """
                <project><modelVersion>4.0.0</modelVersion></project>
                """;
        File pomFile = createPomFile(initialPomContent);
        String invalidPropertiesXmlPath = "info/jab/cli/io/non-existent-properties.xml";

        // When & Then
        assertThatNoException().isThrownBy(() ->
            updateMavenPom.writeProperties(pomFile.getAbsolutePath(), invalidPropertiesXmlPath)
        );
    }

    @Test
    @DisplayName("writePluginInBuildSection: should handle pom.xml path being a directory gracefully")
    void writePlugin_whenPomPathIsDirectory_shouldNotThrowUnhandledException() throws IOException {
        // Given
        Path directoryPath = tempDir.resolve("pom-dir");
        Files.createDirectories(directoryPath);
        String validPluginXmlPath = samplePluginXmlPath;

        // When & Then
        assertThatNoException().isThrownBy(() ->
            updateMavenPom.writePluginInBuildSection(directoryPath.toString(), validPluginXmlPath)
        );
        // Check that an error was logged (System.err) as per current implementation of loadPomDocument
    }

    @Test
    @DisplayName("writeProperties: should handle pom.xml path being a directory gracefully")
    void writeProperties_whenPomPathIsDirectory_shouldNotThrowUnhandledException() throws IOException {
        // Given
        Path directoryPath = tempDir.resolve("properties-pom-dir");
        Files.createDirectories(directoryPath);
        String validPropertiesXmlPath = samplePropertiesXmlPath;

        // When & Then
        assertThatNoException().isThrownBy(() ->
            updateMavenPom.writeProperties(directoryPath.toString(), validPropertiesXmlPath)
        );
    }

    @Test
    @DisplayName("writeProperties: given empty <properties/> tag, should add property correctly formatted")
    void writeProperties_whenPropertiesTagIsEmpty_shouldAddPropertyFormatted() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <properties/>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        Document doc = parseXml(pomFile);
        NodeList javaVersionNodes = doc.getElementsByTagName("java.version");
        assertThat(javaVersionNodes.getLength()).as("java.version element count").isEqualTo(1);
        assertThat(javaVersionNodes.item(0).getTextContent()).isEqualTo("17");

        // Check formatting: <properties>
        // <java.version>17</java.version>
        // </properties>
        String pomContent = Files.readString(pomFile.toPath());
        assertThat(pomContent).containsPattern("<properties>\\s*<java.version>17</java.version>\\s*</properties>");
    }

    @Test
    @DisplayName("writeProperties: given <properties> with only whitespace, should add property correctly formatted")
    void writeProperties_whenPropertiesTagHasOnlyWhitespace_shouldAddPropertyFormatted() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <properties>    </properties>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        Document doc = parseXml(pomFile);
        NodeList javaVersionNodes = doc.getElementsByTagName("java.version");
        assertThat(javaVersionNodes.getLength()).as("java.version element count").isEqualTo(1);
        assertThat(javaVersionNodes.item(0).getTextContent()).isEqualTo("17");
        String pomContent = Files.readString(pomFile.toPath());
        assertThat(pomContent).containsPattern("<properties>\\s*<java.version>17</java.version>\\s*</properties>");
    }

    @Test
    @DisplayName("writeProperties: given existing property without newlines, should add new property formatted")
    void writeProperties_whenExistingPropertyHasNoNewlines_shouldAddPropertyFormatted() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <properties><existing.prop>value</existing.prop></properties>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath); // Adds java.version

        // Then
        Document doc = parseXml(pomFile);
        NodeList javaVersionNodes = doc.getElementsByTagName("java.version");
        assertThat(javaVersionNodes.getLength()).as("java.version element count").isEqualTo(1);
        assertThat(javaVersionNodes.item(0).getTextContent()).isEqualTo("17");

        NodeList existingPropNodes = doc.getElementsByTagName("existing.prop");
        assertThat(existingPropNodes.getLength()).as("existing.prop element count").isEqualTo(1);
        assertThat(existingPropNodes.item(0).getTextContent()).isEqualTo("value");

        String pomContent = Files.readString(pomFile.toPath());
        // Check that both properties are there. java.version should be added after existing.prop.
        assertThat(pomContent).containsPattern("<properties>\\s*<existing.prop>value</existing.prop>\\s*<java.version>17</java.version>\\s*</properties>");
    }

    @Test
    @DisplayName("writeProperties: should insert <properties> before <description> if it exists and <properties> is missing")
    void writeProperties_whenDescriptionExists_shouldInsertBeforeDescription() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <name>Test Project</name>
                    <description>Test Description</description>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        Document doc = parseXml(pomFile);
        NodeList propertiesNodes = doc.getElementsByTagName("properties");
        assertThat(propertiesNodes.getLength()).as("Properties section count").isEqualTo(1);
        assertThat(doc.getElementsByTagName("java.version").item(0).getTextContent()).isEqualTo("17");

        String pomContent = Files.readString(pomFile.toPath());
        assertThat(pomContent)
            .containsSubsequence(
                "    <name>Test Project</name>", // Previous element
                "\n", // Newline before <properties>
                "    <properties>",
                "\n        <java.version>17</java.version>",
                "\n    </properties>",
                "\n    <description>Test Description</description>" // Element after <properties>
            );
    }

    @Test
    @DisplayName("writeProperties: should insert <properties> before <dependencyManagement> if it exists and <properties> is missing")
    void writeProperties_whenDependencyManagementExists_shouldInsertBeforeDependencyManagement() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <name>Test Project</name>
                    <dependencyManagement>
                        <dependencies/>
                    </dependencyManagement>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        String pomContent = Files.readString(pomFile.toPath());
        assertThat(pomContent)
            .containsSubsequence(
                "    <name>Test Project</name>",
                "\n",
                "    <properties>",
                "\n        <java.version>17</java.version>",
                "\n    </properties>",
                "\n    <dependencyManagement>"
            );
    }

    @Test
    @DisplayName("writeProperties: should insert <properties> before <dependencies> if it exists and <properties> is missing")
    void writeProperties_whenDependenciesExists_shouldInsertBeforeDependencies() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <name>Test Project</name>
                    <dependencies>
                        <dependency/>
                    </dependencies>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        String pomContent = Files.readString(pomFile.toPath());
        assertThat(pomContent)
            .containsSubsequence(
                "    <name>Test Project</name>",
                "\n",
                "    <properties>",
                "\n        <java.version>17</java.version>",
                "\n    </properties>",
                "\n    <dependencies>"
            );
    }

    @Test
    @DisplayName("writeProperties: should insert <properties> before <build> if it exists and <properties> is missing")
    void writeProperties_whenBuildExists_shouldInsertBeforeBuild() throws Exception {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <name>Test Project</name>
                    <build>
                        <plugins/>
                    </build>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        String pomContent = Files.readString(pomFile.toPath());
        assertThat(pomContent)
            .containsSubsequence(
                "    <name>Test Project</name>",
                "\n",
                "    <properties>",
                "\n        <java.version>17</java.version>",
                "\n    </properties>",
                "\n    <build>"
            );
    }

    @Test
    @DisplayName("writeProperties: should pick the first valid anchor (<description>) if multiple exist")
    void writeProperties_whenMultipleAnchorsExist_shouldInsertBeforeFirstCorrectAnchor() throws Exception {
        // Given: description, then dependencies, then build
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                    <name>Test Project</name>
                    <description>A test description.</description>
                    <dependencies>
                        <dependency/>
                    </dependencies>
                    <build>
                        <plugins/>
                    </build>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);

        // When
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), samplePropertiesXmlPath);

        // Then
        String pomContent = Files.readString(pomFile.toPath());
        assertThat(pomContent)
            .containsSubsequence( // Should be before <description>
                "    <name>Test Project</name>",
                "\n",
                "    <properties>",
                "\n        <java.version>17</java.version>",
                "\n    </properties>",
                "\n    <description>A test description.</description>"
            )
            .doesNotContain("<dependencies>\n    <properties>"); // Ensure it's not before other anchors
    }

    @Test
    @DisplayName("writeProperties: should handle malformed properties XML gracefully and not modify POM")
    void writeProperties_whenPropertiesXmlIsMalformed_shouldNotModifyPomAndLog() throws IOException, ParserConfigurationException, SAXException {
        // Given
        String initialPomContent = """
                <project xmlns="http://maven.apache.org/POM/4.0.0">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-project</artifactId>
                    <version>1.0.0</version>
                </project>
                """;
        File pomFile = createPomFile(initialPomContent);
        String originalPomContent = Files.readString(pomFile.toPath());

        // When
        // The malformedPropertiesXmlPath should point to a resource that is indeed malformed.
        // This test assumes `src/test/resources/info/jab/cli/io/malformed-properties.xml` exists and is malformed.
        updateMavenPom.writeProperties(pomFile.getAbsolutePath(), malformedPropertiesXmlPath);

        // Then
        String finalPomContent = Files.readString(pomFile.toPath());
        assertThat(finalPomContent)
            .as("POM content should not be modified after a malformed properties XML")
            .isEqualTo(originalPomContent);

        // Error is logged to System.err by the SUT, this test confirms no exception and no modification.
        // Verifying System.err output is possible but more complex and often environment-dependent.
    }

    // Consider adding tests for error conditions:
    // - pom.xml not found or not readable/writable (partially covered by above)
    // - plugin/properties XML resource not found in classpath (partially covered by above)
    // - Malformed pom.xml initially
    // - Malformed plugin/properties XML from classpath
    // - Cases where parent nodes (like <project>) are missing (though current code might not handle this robustly)
}
