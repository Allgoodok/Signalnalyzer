import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SyntaxAnalyzer {
    public ArrayList<Integer> lexTab;
    public ArrayList<TSymbol> ConstTab;
    public ArrayList<TSymbol> IdentTab;
    public ArrayList<TSymbol> KeyTab;
    public ArrayList<AttrSymbol> AttrTab;
    public DocumentBuilderFactory docFactory;
    public DocumentBuilder docBuilder;
    public Document doc;
    public int index;
    public int lexCode;

    public SyntaxAnalyzer(ArrayList<Integer> lexTab, ArrayList<TSymbol> constTab, ArrayList<TSymbol> identTab, ArrayList<TSymbol> keyTab, ArrayList<AttrSymbol> attrTab) throws ParserConfigurationException {
        this.lexTab = lexTab;
        ConstTab = constTab;
        IdentTab = identTab;
        KeyTab = keyTab;
        AttrTab = attrTab;
        index = 0;
        lexCode = 0;
    }


    public void iteratorLexTab(){
        index++;
        if(index <   lexTab.size()){
            lexCode = lexTab.get(index);
        }
    }

    public void initiateSyntaxAnalyzer() throws TransformerException, ParserConfigurationException {
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
        lexCode = lexTab.get(index);
        signalTag();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("result.xml"));
        transformer.transform(source, result);
    }

    public void signalTag() {
        Element signalElement = doc.createElement("signal-program");
        doc.appendChild(signalElement);
        programTag(signalElement);
    }

    public void programTag(Element parent) {
        if(lexCode != 401){
            Error.output(4);
        } else {
            Element programElement = doc.createElement("program");
            parent.appendChild(programElement);
            programElement.appendChild(doc.createTextNode("PROGRAM"));
            iteratorLexTab();
            procedureIdentifierTag(programElement);

            if(lexCode != 59){
                Error.output(5);
            } else {
                programElement.appendChild(doc.createTextNode(";"));
                iteratorLexTab();
                blockTag(programElement);
            }
        }
    }

    public void blockTag(Element parent){
        Element blockElement = doc.createElement("block");
        parent.appendChild(blockElement);
        declarationsTag(blockElement);

        if(lexCode != 402){
            Error.output(6);
        } else {
            blockElement.appendChild(doc.createTextNode("BEGIN"));
            iteratorLexTab();
            statementsListTag(blockElement);
        }
        if (lexCode != 403){
            Error.output(7);
        } else {
            blockElement.appendChild(doc.createTextNode("END"));
            }
        }

    public void declarationsTag(Element parent) {
        Element declarationsElement = doc.createElement("declarations");
        parent.appendChild(declarationsElement);
        variableDeclarationsTag(declarationsElement);
    }

    public void variableDeclarationsTag(Element parent) {
        Element variableDeclarationsElement = doc.createElement("variable-declarations");
        parent.appendChild(variableDeclarationsElement);

        if (lexCode != 404) {
            emptyTag(variableDeclarationsElement);
        } else {
            iteratorLexTab();
            variableDeclarationsElement.appendChild(doc.createTextNode("VAR"));
            declarationsListTag(variableDeclarationsElement);
        }
    }

    public void declarationsListTag(Element parent) {
        Element declarationsListTag = doc.createElement("declarations-list");
        parent.appendChild(declarationsListTag);
        declarationTag(declarationsListTag);

        if (lexCode != 402) {
            while (lexCode != 59) {
                declarationsListTag(parent);
            }
        }
    }

    public void declarationTag(Element parent) {
        Element declarationElement = doc.createElement("declaration");
        parent.appendChild(declarationElement);
        variableIdentifierTag(declarationElement);
        identifiersListTag(declarationElement);

        if(lexCode != 58) {
            Error.output(14);
        } else {
            declarationElement.appendChild(doc.createTextNode(":"));
            iteratorLexTab();
        }

        attributeTag(declarationElement);
        attributesListTag(declarationElement);

        if (lexCode != 59) {
            Error.output(5);
        } else {
            declarationElement.appendChild(doc.createTextNode(";"));
            iteratorLexTab();
        }
    }

    public void identifiersListTag(Element parent) {
        Element identifiersListTag = doc.createElement("identifiers-list");
        parent.appendChild(identifiersListTag);

        if (lexCode == 44){
            identifiersListTag.appendChild(doc.createTextNode(","));
        }

        if(lexCode != 44) {
            emptyTag(identifiersListTag);

            if (lexCode != 58) {
                iteratorLexTab();
            }
        } else {
            if (lexCode == 44) {
                iteratorLexTab();
            }

            variableIdentifierTag(identifiersListTag);


            while(lexCode != 58){
                identifiersListTag(parent);
            }
        }
    }

    public void attributesListTag (Element parent) {
        Element attributesListElement = doc.createElement("attribute-list");
        parent.appendChild(attributesListElement);

        if (lexCode == 59){
            emptyTag(attributesListElement);
        } else {
            attributeTag(attributesListElement);
            while (lexCode != 59) {
                attributesListTag(parent);
            }
            emptyTag(parent);
        }
    }

    public void attributeTag(Element parent){

        if(lexCode == 91) {
            Element attributeElement = doc.createElement("attribute");
            parent.appendChild(attributeElement);
            attributeElement.appendChild(doc.createTextNode("["));
            rangeTag(attributeElement);
            rangesListTag(attributeElement);
        }else if(!(lexCode == 405 || lexCode == 406 || lexCode == 407 || lexCode == 408 || lexCode == 409 || lexCode == 410 || lexCode == 91)){
                Error.output(3);
        } else /*if (lexCode != 91)*/{
            Element attributeElement = doc.createElement("attribute");
            parent.appendChild(attributeElement);
            attributeElement.appendChild(doc.createTextNode(String.valueOf(lexCode)));
        }
        iteratorLexTab();
    }

    public void rangesListTag(Element parent) {
        while (lexCode != 93){
            if (lexCode == 44){
                Element rangesListElement = doc.createElement("ranges-list");
                parent.appendChild(rangesListElement);
                rangesListElement.appendChild(doc.createTextNode(","));
                rangeTag(rangesListElement);
            }
        }
    }

    public void rangeTag(Element parent) {
        String tempDigit = "";
        Element rangeElement = doc.createElement("range");
        parent.appendChild(rangeElement);
        Element unsignedIntegerElement1 = doc.createElement("unsigned-integer");
        rangeElement.appendChild(unsignedIntegerElement1);
        iteratorLexTab();
        if (lexCode >= 501 && lexCode <= 1000){
            tempDigit = searchConstantById(lexCode);
            for (int i = 0; i < tempDigit.length(); i++){
                if(i == 0){
                    Element digitElement = doc.createElement("digit");
                    unsignedIntegerElement1.appendChild(digitElement);
                    digitElement.appendChild(doc.createTextNode(Character.toString(tempDigit.charAt(i))));
                }else{
                    Element digitsStringElement = doc.createElement("digits-string");
                    unsignedIntegerElement1.appendChild(digitsStringElement);
                    Element digitElement = doc.createElement("digit");
                    digitsStringElement.appendChild(digitElement);
                    digitElement.appendChild(doc.createTextNode(Character.toString(tempDigit.charAt(i))));
                }
            }
        }
        iteratorLexTab();
        rangeElement.appendChild(doc.createTextNode(".."));
        iteratorLexTab();
        Element unsignedIntegerElement2 = doc.createElement("unsigned-integer");
        rangeElement.appendChild(unsignedIntegerElement2);
        if (lexCode >= 501 && lexCode <= 1000){
            tempDigit = searchConstantById(lexCode);
            for (int i = 0; i < tempDigit.length(); i++){
                if(i == 0){
                    Element digitElement = doc.createElement("digit");
                    unsignedIntegerElement2.appendChild(digitElement);
                    digitElement.appendChild(doc.createTextNode(Character.toString(tempDigit.charAt(i))));
                }else{
                    Element digitsStringElement = doc.createElement("digits-string");
                    unsignedIntegerElement2.appendChild(digitsStringElement);
                    Element digitElement = doc.createElement("digit");
                    digitsStringElement.appendChild(digitElement);
                    digitElement.appendChild(doc.createTextNode(Character.toString(tempDigit.charAt(i))));
                }
            }
        }
        iteratorLexTab();
    }

    public void statementsListTag(Element parent) {
        Element statementsListElement = doc.createElement("statements-list");
        parent.appendChild(statementsListElement);
        emptyTag(statementsListElement);
    }

    public void emptyTag(Element parent) {
        Element emptyElement = doc.createElement("empty");
        parent.appendChild(emptyElement);
    }

    public void procedureIdentifierTag(Element parent){
        Element procedureIdenifierElement = doc.createElement("procedure-identifier");
        parent.appendChild(procedureIdenifierElement);
        identifierTag(procedureIdenifierElement);
    }

    public void variableIdentifierTag(Element parent){
        Element variableIdenifierElement = doc.createElement("variable-identifier");
        parent.appendChild(variableIdenifierElement);
        identifierTag(variableIdenifierElement);
    }

    public void identifierTag(Element parent) {
        Element identifierElement = doc.createElement("identifier");
        parent.appendChild(identifierElement);

        String identifierTemp = "";
        int temp = 0;

        if (searchIdentifierById(lexCode) != "") {
            identifierTemp = searchIdentifierById(lexCode);
        } else {
            Error.output(2);
        }

        //letterTag(identifierElement, i, identifierTemp);

        for(int i = 0; i < identifierTemp.length(); i++){
            //stringTag(identifierElement, i, identifierTemp);
            temp = (int) identifierTemp.charAt(i);
            if (temp >= 48 && temp <= 57){
                Element stringElement = doc.createElement("string");
                identifierElement.appendChild(stringElement);
                Element digitElement = doc.createElement("digit");
                stringElement.appendChild(digitElement);
                digitElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
            }else if (temp >= 65 && temp <= 90) {
                Element stringElement = doc.createElement("string");
                identifierElement.appendChild(stringElement);
                Element letterElement = doc.createElement("letter");
                stringElement.appendChild(letterElement);
                letterElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
            }
        }



        iteratorLexTab();
    }

    public void stringTag(Element parent, int i, String lexem) {
        boolean error = true;
        Element stringElement = doc.createElement("string");
        parent.appendChild(stringElement);

        if (i > lexem.length() - 1) {
            emptyTag(stringElement);
        } else {
            for (int k = 35; k < 61; k++) {
                if ((int) lexem.charAt(i) == AttrTab.get(k).getValue()) ;
                error = false;
            }
            if (error == false) {
                letterTag(stringElement, i, lexem);
            } else {
                for (int k = 18; k < 28; k++) {
                    if ((int) lexem.charAt(i) == AttrTab.get(k).getValue()) ;
                    error = false;
                }
                if (error == false) {
                    digitTag(stringElement, i, lexem);
                } else {
                    Error.output(1);
                }
                stringTag(stringElement, i, lexem);
            }
        }
    }

    public void letterTag(Element parent, int i, String lexem){

        boolean error = true;

        for(int k = 35; k < 61; k++) {
            if ((int) lexem.charAt(i) == AttrTab.get(k).getValue()) ;
            error = false;
        }

        if (error != true)
        {
            Element letterElement = doc.createElement("letter");
            parent.appendChild(letterElement);
            letterElement.appendChild(doc.createTextNode(Character.toString(lexem.charAt(i))));
            i++;
        }

    }

    public void digitTag(Element parent, int i, String lexem){

        boolean error = true;

        for(int k = 18; k < 28; k++) {
            if ((int) lexem.charAt(i) == AttrTab.get(k).getValue()) ;
            error = false;
        }

        if (error != true)
        {
            Element letterElement = doc.createElement("digit");
            parent.appendChild(letterElement);
            letterElement.appendChild(doc.createTextNode(Character.toString(lexem.charAt(i))));
            i++;
        }

    }

    public String searchIdentifierById(int lexCodeTemp){
        String temp = "";

        for (TSymbol key: IdentTab) {
            if (key.getCode() == lexCodeTemp) {
                temp = key.getName();
                break;
            }
        }

        return temp;
    }

    public String searchConstantById(int lexCodeTemp){
        String temp = "";

        for (TSymbol key: ConstTab) {
            if (key.getCode() == lexCodeTemp) {
                temp = key.getName();
                break;
            }
        }

        return temp;
    }
}