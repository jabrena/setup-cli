package info.jab.cli.behaviours;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class MavenPlugins implements Behaviour1 {

    @Override
    public void execute(String parameter) {
        if ("flatten".equals(parameter)) {

            String pomPath = "pom.xml";
            // Assuming this path is relative to the execution directory of the CLI
            String flattenPluginXmlPath = "maven-plugins/maven-plugins-flatten.xml";

            writePlugin(pomPath, flattenPluginXmlPath);
        } else {
            System.out.println("Execution parameter is not 'flatten'. No changes made to pom.xml.");
        }
    }

    private void writePlugin(String pomPath, String flattenPluginXmlPath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // Apply security settings to DocumentBuilderFactory to prevent XXE and other vulnerabilities
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true); // General security feature
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            DocumentBuilder db = dbf.newDocumentBuilder();

            // 1. Read and parse maven-plugins-flatten.xml from classpath
            Document flattenDoc;
            try (InputStream flattenPluginStream = getClass().getClassLoader().getResourceAsStream(flattenPluginXmlPath)) {
                if (flattenPluginStream == null) {
                    System.err.println("Error: Flatten plugin XML file not found in classpath: " + flattenPluginXmlPath);
                    return;
                }
                flattenDoc = db.parse(flattenPluginStream); // Can throw SAXException, IOException
            } catch (IOException e) { // Catches IOException from stream operations or parse
                System.err.println("Error reading or parsing flatten plugin XML from classpath: " + e.getMessage());
                return;
            }
            Node pluginNodeToImport = flattenDoc.getDocumentElement(); // This is the <plugin> node

            // 2. Read and parse pom.xml
            File pomFile = new File(pomPath);
            if (!pomFile.exists() || !pomFile.isFile()) {
                System.err.println("Error: pom.xml file not found or is not a file: " + pomFile.getAbsolutePath());
                return;
            }
            Document pomDoc = db.parse(pomFile);
            // Preserve the <?xml version="1.0" encoding="UTF-8"?> declaration if we re-write the file
            pomDoc.setXmlStandalone(true);

            // 3. Locate or create the <build><plugins> section in pom.xml
            Element projectElement = pomDoc.getDocumentElement(); // Should be <project>

            NodeList buildNodes = projectElement.getElementsByTagName("build");
            Element buildElement;
            if (buildNodes.getLength() > 0) {
                buildElement = (Element) buildNodes.item(0); // Assuming one <build> element
            } else {
                buildElement = pomDoc.createElement("build");
                projectElement.appendChild(buildElement);
            }

            NodeList pluginsNodes = buildElement.getElementsByTagName("plugins");
            Element pluginsElement;
            if (pluginsNodes.getLength() > 0) {
                pluginsElement = (Element) pluginsNodes.item(0);
            } else {
                pluginsElement = pomDoc.createElement("plugins");
                buildElement.appendChild(pluginsElement);
            }

            // Clean ALL whitespace-only text nodes from within pluginsElement
            NodeList childrenOfPlugins = pluginsElement.getChildNodes();
            for (int k = childrenOfPlugins.getLength() - 1; k >= 0; k--) {
                Node currentChild = childrenOfPlugins.item(k);
                if (currentChild.getNodeType() == Node.TEXT_NODE && currentChild.getNodeValue().trim().isEmpty()) {
                    pluginsElement.removeChild(currentChild);
                }
            }
            // The previous cleanup was only for the last child. This is more comprehensive for pluginsElement.

            // 4. Import the plugin node and append it to <plugins>
            Node importedPluginNode = pomDoc.importNode(pluginNodeToImport, true);

            // Add a newline text node BEFORE the plugin
            pluginsElement.appendChild(pomDoc.createTextNode("\n            ")); // Assuming 3 levels of indent (12 spaces)

            pluginsElement.appendChild(importedPluginNode);

            // Add a newline text node AFTER the plugin
            pluginsElement.appendChild(pomDoc.createTextNode("\n        ")); // Assuming 2 levels of indent (8 spaces) for the next element

            // 5. Write the modified pom.xml back to disk
            TransformerFactory tf = TransformerFactory.newInstance();
            // Apply security settings to TransformerFactory
            tf.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // For older JAXP, explicit attribute setting might be needed for stricter control
            try {
                tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
                tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            } catch (IllegalArgumentException e) {
                // Attribute not supported, FEATURE_SECURE_PROCESSING should handle it.
                System.out.println("Note: ACCESS_EXTERNAL_DTD/STYLESHEET attributes not supported by this TransformerFactory.");
            }

            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            // Standard Maven indentation is 4 spaces. Some parsers use {http://xml.apache.org/xslt}indent-amount
            // transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); // Keep <?xml ... ?>
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");


            DOMSource source = new DOMSource(pomDoc);
            StreamResult result = new StreamResult(new File(pomPath));
            transformer.transform(source, result);

            System.out.println("Flatten plugin has been programmatically added to pom.xml's build/plugins section.");

        } catch (Exception e) {
            // A more sophisticated error handling/logging should be used in a real app
            System.err.println("Error processing XML for flatten plugin: " + e.getMessage());
        }
    }
}
