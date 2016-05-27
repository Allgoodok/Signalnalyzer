import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by Vlados Papandos on 17.05.2016.
 */
public class Example {
    public DocumentBuilderFactory docFactory;
    public DocumentBuilder docBuilder;
    public Document doc;

    Example() throws ParserConfigurationException {
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
    }

    public void Elementa () throws TransformerException {
        Element signalElement = doc.createElement("signal-program");
        Element programElement = doc.createElement("program");
        signalElement.appendChild(programElement);
        programElement.appendChild(doc.createTextNode("PROGRAM"));
    }
    public static void main(String[] args) {
        DocumentBuilderFactory docFactory;
        DocumentBuilder docBuilder;
        Document doc;
        try {
            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("signal-program");
            doc.appendChild(rootElement);
            Element programElement = doc.createElement("program");
            rootElement.appendChild(programElement);
            programElement.appendChild(doc.createTextNode("PROGRAM"));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
