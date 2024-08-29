import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLGenerator {

    private Document document;

    public static void main(String[] args) {
        try {
            XMLGenerator generator = new XMLGenerator();
            generator.readFilesAndGenerateXML();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFilesAndGenerateXML() throws IOException, ParserConfigurationException, TransformerException {
        // List of directories to scan
        String[] directories = {
            "adapters", 
            "selectors", 
            "policies", 
            "datastores"
        };

        // Initialize the XML document
        prepareXMLStructure();

        for (String dir : directories) {
            File folder = new File(dir);
            
            // Ensure the folder exists
            if (folder.exists() && folder.isDirectory()) {
                File[] listOfFiles = folder.listFiles();

                for (File file : listOfFiles) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        // Process each JSON file and add it to the XML structure
                        setXMLData(file.getPath());
                    }
                }
            } else {
                System.out.println("Directory does not exist or is not a directory: " + dir);
            }
        }

        // Save the XML document to a file
        saveXMLDocument();
    }

    private void prepareXMLStructure() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        document = docBuilder.newDocument();

        // Root element of the XML
        Element rootElement = document.createElement("files");
        document.appendChild(rootElement);
    }

    private void setXMLData(String filePath) {
        // Get the root element
        Element rootElement = document.getDocumentElement();

        // Create a new file element
        Element fileElement = document.createElement("file");
        fileElement.setAttribute("path", filePath);

        // Add the file element to the root element
        rootElement.appendChild(fileElement);
    }

    private void saveXMLDocument() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File("output.xml"));

        transformer.transform(source, result);

        System.out.println("XML file saved as output.xml");
    }
}
