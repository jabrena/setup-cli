package info.jab.cli.io;

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
import java.util.Objects;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

public class UpdateMavenPom {

    private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        return dbf.newDocumentBuilder();
    }

    private Document loadPomDocument(String pomPath, DocumentBuilder db) throws org.xml.sax.SAXException, IOException {
        File pomFile = new File(pomPath);
        if (!pomFile.exists() || !pomFile.isFile()) {
            System.err.println("Error: pom.xml file not found or is not a file: " + pomFile.getAbsolutePath());
            throw new IOException("pom.xml file not found or is not a file: " + pomFile.getAbsolutePath());
        }
        Document pomDoc = db.parse(pomFile);
        pomDoc.setXmlStandalone(true);
        return pomDoc;
    }

    private Transformer createTransformer() throws TransformerConfigurationException {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
        try {
            tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException e) {
            System.out.println("Note: ACCESS_EXTERNAL_DTD/STYLESHEET attributes not supported by this TransformerFactory.");
        }
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        return transformer;
    }

    private void writePomDocument(Document pomDoc, String pomPath, Transformer transformer) throws TransformerException {
        DOMSource source = new DOMSource(pomDoc);
        StreamResult result = new StreamResult(new File(pomPath));
        transformer.transform(source, result);
    }

    public void writePluginInBuildSection(String pomPath, String pluginXmlPath) {
        try {
            DocumentBuilder db = createDocumentBuilder();

            // 1. Read and parse maven-plugins-xxx.xml from classpath
            Document flattenDoc;
            try (InputStream flattenPluginStream = getClass().getClassLoader().getResourceAsStream(pluginXmlPath)) {
                if (Objects.isNull(flattenPluginStream)) {
                    System.err.println("Error: Flatten plugin XML file not found in classpath: " + pluginXmlPath);
                    return;
                }
                flattenDoc = db.parse(flattenPluginStream);
            } catch (IOException | org.xml.sax.SAXException e) {
                System.err.println("Error reading or parsing flatten plugin XML from classpath: " + e.getMessage());
                return;
            }
            Node pluginNodeToImport = flattenDoc.getDocumentElement();

            // 2. Read and parse pom.xml
            Document pomDoc = loadPomDocument(pomPath, db);

            // 3. Locate or create the <build><plugins> section in pom.xml
            Element projectElement = pomDoc.getDocumentElement();

            NodeList buildNodes = projectElement.getElementsByTagName("build");
            Element buildElement;
            if (buildNodes.getLength() > 0) {
                buildElement = (Element) buildNodes.item(0);
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

            // 4. Import the plugin node and append it to <plugins>
            Node importedPluginNode = pomDoc.importNode(pluginNodeToImport, true);

            // Add a newline text node BEFORE the plugin
            pluginsElement.appendChild(pomDoc.createTextNode("\n")); // Assuming 3 levels of indent (12 spaces)
            pluginsElement.appendChild(importedPluginNode);
            // Add a newline text node AFTER the plugin
            pluginsElement.appendChild(pomDoc.createTextNode("\n")); // Assuming 2 levels of indent (8 spaces) for the next element

            // 5. Write the modified pom.xml back to disk
            Transformer transformer = createTransformer();
            writePomDocument(pomDoc, pomPath, transformer);

        } catch (Exception e) {
            System.err.println("Error processing XML for flatten plugin: " + e.getMessage());
        }
    }

    public void writeProperties(String pomPath, String propertiesXmlPath) {
        try {
            DocumentBuilder db = createDocumentBuilder();

            // 1. Read and parse properties XML from classpath
            Document propertiesDoc;
            try (InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream(propertiesXmlPath)) {
                if (Objects.isNull(propertiesStream)) {
                    System.err.println("Error: Properties XML file not found in classpath: " + propertiesXmlPath);
                    return;
                }
                propertiesDoc = db.parse(propertiesStream);
            } catch (IOException | org.xml.sax.SAXException e) {
                System.err.println("Error reading or parsing properties XML from classpath: " + e.getMessage());
                return;
            }
            Element propertiesRootToImport = propertiesDoc.getDocumentElement();

            // 2. Read and parse pom.xml
            Document pomDoc = loadPomDocument(pomPath, db);

            // 3. Locate or create the <properties> section in pom.xml
            Element projectElement = pomDoc.getDocumentElement();

            NodeList propertiesNodes = projectElement.getElementsByTagName("properties");
            Element propertiesElement;
            if (propertiesNodes.getLength() > 0) {
                propertiesElement = (Element) propertiesNodes.item(0);
            } else {
                propertiesElement = pomDoc.createElement("properties");
                Node insertBeforeNode = null;
                NodeList projectChildren = projectElement.getChildNodes();
                for (int i = 0; i < projectChildren.getLength(); i++) {
                    Node child = projectChildren.item(i);
                    if (child.getNodeName().equals("description") ||
                        child.getNodeName().equals("dependencyManagement") ||
                        child.getNodeName().equals("dependencies") ||
                        child.getNodeName().equals("build")) {
                        insertBeforeNode = child;
                        break;
                    }
                }
                if (Objects.nonNull(insertBeforeNode)) {
                    projectElement.insertBefore(pomDoc.createTextNode("\n    "), insertBeforeNode);
                    projectElement.insertBefore(propertiesElement, insertBeforeNode);
                    projectElement.insertBefore(pomDoc.createTextNode("\n    "), insertBeforeNode);
                } else {
                    projectElement.appendChild(pomDoc.createTextNode("\n    "));
                    projectElement.appendChild(propertiesElement);
                }
            }

            // MODIFIED SECTION: Logic to update existing property or add the new one from propertiesRootToImport
            String propertyNameToProcess = propertiesRootToImport.getTagName();
            String propertyValueToProcess = propertiesRootToImport.getTextContent();
            boolean propertyUpdatedOrAdded = false;

            NodeList existingProperties = propertiesElement.getChildNodes();
            for (int i = 0; i < existingProperties.getLength(); i++) {
                Node node = existingProperties.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element existingPropertyElement = (Element) node;
                    if (existingPropertyElement.getTagName().equals(propertyNameToProcess)) {
                        existingPropertyElement.setTextContent(propertyValueToProcess);
                        propertyUpdatedOrAdded = true;
                        break;
                    }
                }
            }

            if (!propertyUpdatedOrAdded) {
                Node importedPropertyNode = pomDoc.importNode(propertiesRootToImport, true);

                // Add a newline text node for spacing before the new property, if propertiesElement is not empty
                // and does not already end with a spacing node.
                if (propertiesElement.hasChildNodes()) {
                    Node lastChild = propertiesElement.getLastChild();
                    if (!(lastChild != null && lastChild.getNodeType() == Node.TEXT_NODE && lastChild.getNodeValue().trim().isEmpty())) {
                         propertiesElement.appendChild(pomDoc.createTextNode("\n        "));
                    } else if (lastChild != null && lastChild.getNodeType() == Node.TEXT_NODE && !lastChild.getNodeValue().endsWith("        ")) {
                        if (propertiesElement.getTextContent().trim().isEmpty()) {
                            propertiesElement.setTextContent("");
                            propertiesElement.appendChild(pomDoc.createTextNode("\n        "));
                        } else {
                             propertiesElement.appendChild(pomDoc.createTextNode("\n        "));
                        }
                    }
                } else {
                     propertiesElement.appendChild(pomDoc.createTextNode("\n        "));
                }
                propertiesElement.appendChild(importedPropertyNode);
                System.out.println("Property '" + propertyNameToProcess + "' added to pom.xml.");
                propertyUpdatedOrAdded = true;
            }
            // END OF MODIFIED SECTION

            // Ensure correct formatting for the closing </properties> tag
            Node lastChild = propertiesElement.getLastChild();
            if (lastChild != null && lastChild.getNodeType() == Node.ELEMENT_NODE) {
                 propertiesElement.appendChild(pomDoc.createTextNode("\n    "));
            } else if (lastChild != null && lastChild.getNodeType() == Node.TEXT_NODE) {
                if (!lastChild.getNodeValue().endsWith("\n    ")) {
                    String trimmedValue = lastChild.getNodeValue().trim();
                    if (trimmedValue.isEmpty()) {
                        propertiesElement.removeChild(lastChild);
                    } else {
                        lastChild.setNodeValue(trimmedValue);
                    }
                    propertiesElement.appendChild(pomDoc.createTextNode("\n    "));
                }
            } else if (!propertiesElement.hasChildNodes()){
                 propertiesElement.appendChild(pomDoc.createTextNode("\n    "));
            }

            // 5. Write the modified pom.xml back to disk
            Transformer transformer = createTransformer();
            writePomDocument(pomDoc, pomPath, transformer);

        } catch (Exception e) {
            System.err.println("Error processing XML for adding properties: " + e.getMessage());
        }
    }

}
