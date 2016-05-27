import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Vlados Papandos on 26.05.2016.
 */
public class KnuthAnalyzer {
    public ArrayList<Integer> lexTab;
    public ArrayList<TSymbol> ConstTab;
    public ArrayList<TSymbol> IdentTab;
    public ArrayList<TSymbol> KeyTab;
    public ArrayList<AttrSymbol> AttrTab;

    public ArrayList<Element> declarationTable;
    public ArrayList<Element> variableTable;
    public ArrayList<Element> identifiersTable;
    public ArrayList<Element> attributesTable;
    public ArrayList<Element> rangesTable;

    public DocumentBuilderFactory docFactory;
    public DocumentBuilder docBuilder;
    public Document doc;
    public int index;
    public int lexCode;
    public int row;
    public String content;
    public boolean endState;
    public RuleNode[] rulesTable;
    public Stack<Integer> stackBack;
    public boolean goingBack;
    public String tempDigit;
    public int temp;

    public KnuthAnalyzer(ArrayList<Integer> lexTab, ArrayList<TSymbol> constTab, ArrayList<TSymbol> identTab, ArrayList<TSymbol> keyTab, ArrayList<AttrSymbol> attrTab) throws ParserConfigurationException {
        this.lexTab = lexTab;
        ConstTab = constTab;
        IdentTab = identTab;
        KeyTab = keyTab;
        AttrTab = attrTab;
        temp = 0;
        index = 0;
        lexCode = 0;
        row = 1;
        content = "";
        endState = false;
        goingBack = false;
        stackBack = new Stack<>();
        tempDigit = "";
        declarationTable = new ArrayList<>();
        variableTable = new ArrayList<>();
        identifiersTable = new ArrayList<>();
        attributesTable = new ArrayList<>();
        rangesTable = new ArrayList<>();

        rulesTable = new RuleNode[44];

        rulesTable[0] = new RuleNode("signal-program", "program", "T", "F");

        rulesTable[1] = new RuleNode("program", "401", "2", "F");
        rulesTable[2] = new RuleNode("", "procedure-identifier", "3", "F");
        rulesTable[3] = new RuleNode("", "59", "4", "F");
        rulesTable[4] = new RuleNode("", "block", "5", "F");
        rulesTable[5] = new RuleNode("", "46", "END", "F");

        rulesTable[6] = new RuleNode("block", "declarations", "7", "F");
        rulesTable[7] = new RuleNode("", "402", "8", "F");
        rulesTable[8] = new RuleNode("", "statements-list", "9", "F");
        rulesTable[9] = new RuleNode("", "403", "T", "F");

        rulesTable[10] = new RuleNode("declarations", "variable-declarations", "11", "F");

        rulesTable[11] = new RuleNode("variable-declarations", "404", "12", "13");
        rulesTable[12] = new RuleNode("", "declarations-list1", "14", "F");
        rulesTable[13] = new RuleNode("", "empty", "T", "F");

        rulesTable[14] = new RuleNode("declarations-list", "declaration", "17", "16");
        rulesTable[15] = new RuleNode("", "declarations-list", "14", "F");
        rulesTable[16] = new RuleNode("", "empty", "T", "F");

        rulesTable[17] = new RuleNode("declaration", "variable-identifier1", "18", "F");
        rulesTable[18] = new RuleNode("", "identifiers-list1", "19", "F");
        rulesTable[19] = new RuleNode("", "58", "20", "F");
        rulesTable[20] = new RuleNode("", "attribute", "21", "F");
        rulesTable[21] = new RuleNode("", "attributes-list1", "22", "F");
        rulesTable[22] = new RuleNode("", "59", "T", "F");

        rulesTable[23] = new RuleNode("identifiers-list", "44", "24", "26");
        rulesTable[24] = new RuleNode("", "variable-identifier", "25", "F");
        rulesTable[25] = new RuleNode("", "identifiers-list", "23", "F");
        rulesTable[26] = new RuleNode("", "empty", "T", "F");

        rulesTable[27] = new RuleNode("attributes-list", "attribute", "28", "33");
        rulesTable[28] = new RuleNode("", "attributes-list", "27", "F");
        rulesTable[29] = new RuleNode("", "empty", "T", "F");

        rulesTable[30] = new RuleNode("attribute", "91", "31", "F");
        rulesTable[31] = new RuleNode("", "range1", "32", "F");
        rulesTable[32] = new RuleNode("", "ranges-list1", "33", "F");
        rulesTable[33] = new RuleNode("", "93", "T", "F");

        rulesTable[34] = new RuleNode("ranges-list", "44", "35", "37");
        rulesTable[35] = new RuleNode("", "range", "36", "F");
        rulesTable[36] = new RuleNode("", "ranges-list", "34", "F");
        rulesTable[37] = new RuleNode("", "empty", "T", "F");

        rulesTable[38] = new RuleNode("range", "unsigned-integer", "39", "F");
        rulesTable[39] = new RuleNode("", "411", "40", "F");
        rulesTable[40] = new RuleNode("", "unsigned-integer", "T", "F");

        rulesTable[41] = new RuleNode("statements-list", "empty", "T", "F");

        rulesTable[42] = new RuleNode("variable-identifier", "identifier", "T", "F");

        rulesTable[43] = new RuleNode("procedure-identifier", "identifier", "T", "F");
    }

    public int turnString(String lexCode){
        int intCode = Integer.parseInt(lexCode);

        return intCode;
    }

    public void iteratorLexTab(){
        index++;
        if(index < lexTab.size()){
            lexCode = lexTab.get(index);
        }
    }

    public int findLine(String find){
        int i = 0;
        while (!(rulesTable[i].getLeftPart().equals(find))){
            i++;
        }
        return i;
    }

    public void analyzeKnuth() throws ParserConfigurationException, TransformerException {
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
        lexCode = lexTab.get(index);
        Element signalElement = doc.createElement("signal-program");
        doc.appendChild(signalElement);
        Element programElement = null;
        Element procedureIdentifierElement = null;
        Element blockElement = null;
        Element declarationsElement = null;
        Element variableDeclarationsElement = null;
        Element declarationsList1Element = null;
        Element declarationsListElement = null;
        Element declarationElement = null;
        Element variableIdentifier1Element = null;
        Element identifiersListElement = null;
        Element identifiersList1Element = null;
        Element attributeElement = null;
        Element attributesList1Element = null;
        Element attributesListElement = null;
        Element variableIdentifierElement = null;
        Element rangeElement = null;
        Element unsignedIntegerElement1 = null;
        Element unsignedIntegerElement2 = null;

        while (!endState) {
            if (rulesTable[row].getGoFirst().equals("END")){
                endState = true;
            }
            content = rulesTable[row].getRightPart();
            if (content == "401" && lexCode == 401) {

                programElement = doc.createElement(rulesTable[row].getLeftPart());
                signalElement.appendChild(programElement);
                programElement.appendChild(doc.createTextNode("PROGRAM"));
                row = turnString(rulesTable[row].getGoFirst());
                iteratorLexTab();

            }else if (content == "procedure-identifier"){

                procedureIdentifierElement = doc.createElement(content);
                programElement.appendChild(procedureIdentifierElement);
                Element identifierElement = doc.createElement("identifier");
                procedureIdentifierElement.appendChild(identifierElement);

                String identifierTemp = searchIdentifierById(lexCode);
                Element parentStringElement = null;
                for(int i = 0; i < identifierTemp.length(); i++){
                    //stringTag(identifierElement, i, identifierTemp);
                    temp = (int) identifierTemp.charAt(i);
                    if (temp >= 48 && temp <= 57){
                        if(i == 0) {
                            Element digitElement = doc.createElement("digit");
                            identifierElement.appendChild(digitElement);
                            digitElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
                            parentStringElement = doc.createElement("string");
                            identifierElement.appendChild(parentStringElement);

                        }else {
                            Element digitElement = doc.createElement("digit");
                            parentStringElement.appendChild(digitElement);
                            digitElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
                            Element stringElement = doc.createElement("string");
                            parentStringElement.appendChild(stringElement);
                            parentStringElement = stringElement;

                        }

                    }else if (temp >= 65 && temp <= 90) {
                        if(i == 0) {
                            Element letterElement = doc.createElement("letter");
                            identifierElement.appendChild(letterElement);
                            letterElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
                            parentStringElement = doc.createElement("string");
                            identifierElement.appendChild(parentStringElement);
                        }else {
                            Element letterElement = doc.createElement("letter");
                            parentStringElement.appendChild(letterElement);
                            letterElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
                            Element stringElement = doc.createElement("string");
                            parentStringElement.appendChild(stringElement);
                            parentStringElement = stringElement;
                        }

                    }
                }
                iteratorLexTab();
                row = turnString(rulesTable[row].getGoFirst());

            }else if (content == "59" && lexCode == 59 && rulesTable[row].getGoFirst() == "4"){

                programElement.appendChild(doc.createTextNode(";"));
                iteratorLexTab();
                row = turnString(rulesTable[row].getGoFirst());

            }else if (content == "block"){

                blockElement = doc.createElement(content);
                programElement.appendChild(blockElement);
                stackBack.push(row);
                System.out.println("Push block: " + stackBack.peek());
                row = findLine("block");

            }else if (content == "46" && lexCode == 46){

                programElement.appendChild(doc.createTextNode("."));
                endState = true;

            }else if (content == "declarations"){

                declarationsElement = doc.createElement("declarations");
                blockElement.appendChild(declarationsElement);
                stackBack.push(row);
                System.out.println("Push declaraions: " + stackBack.peek());
                row = findLine("declarations");
            }else if (content == "402" && lexCode == 402) {

                blockElement.appendChild(doc.createTextNode("BEGIN"));
                iteratorLexTab();
                row = turnString(rulesTable[row].getGoFirst());

            }else if (content == "statements-list"){

                Element statementsListElement = doc.createElement("statements-list");
                blockElement.appendChild(statementsListElement);
                Element emptyElement = doc.createElement("empty");
                statementsListElement.appendChild(emptyElement);
                row = turnString(rulesTable[row].getGoFirst());

            } else  if (content == "403" && lexCode == 403) {
                blockElement.appendChild(doc.createTextNode("END"));
                iteratorLexTab();
                System.out.println("Pop 403:" + stackBack.peek());
                row = stackBack.pop() + 1;

            } else if (content == "variable-declarations"){

                variableDeclarationsElement = doc.createElement("variable-declarations");
                declarationsElement.appendChild(variableDeclarationsElement);
                row = turnString(rulesTable[row].getGoFirst());

            } else if (content == "404" && lexCode == 404) {

                variableDeclarationsElement.appendChild(doc.createTextNode("VAR"));
                iteratorLexTab();
                if (lexCode == 402){
                    Element emptyElement = doc.createElement("empty");
                    variableDeclarationsElement.appendChild(emptyElement);
                    System.out.println("Pop 404:" + stackBack.peek());
                    row = stackBack.pop() + 1;
                } else {
                    row = turnString(rulesTable[row].getGoFirst());
                }
            } else if (content == "declarations-list1"){

                declarationTable.add(doc.createElement("declaration-list"));
                variableDeclarationsElement.appendChild(declarationTable.get(declarationTable.size() - 1));
                row = turnString(rulesTable[row].getGoFirst());

            } else if (content == "declaration") {

                declarationElement = doc.createElement(content);
                declarationTable.get(declarationTable.size() - 1).appendChild(declarationElement);

                if (lexCode == 402) {
                    Element emptyElement = doc.createElement("empty");
                    declarationTable.get(declarationTable.size() - 1).appendChild(emptyElement);
                    System.out.println("Current lexCode: " + lexCode);
                    System.out.println("Pop declaration:" + stackBack.peek());
                    row = stackBack.pop() + 1;
                } else {
                    stackBack.push(row);
                    System.out.println("Push declaration: " + stackBack.peek());
                    row = turnString(rulesTable[row].getGoFirst());
                }
            }else if (content == "declarations-list") {

                declarationTable.add(doc.createElement(content));
                declarationTable.get(declarationTable.size() - 2).appendChild(declarationTable.get(declarationTable.size() - 1));
                row = turnString(rulesTable[row].getGoFirst());

            } else if (content == "variable-identifier1") {

                variableIdentifierElement = doc.createElement("variable-identifier");
                declarationElement.appendChild(variableIdentifierElement);
                stackBack.push(row);
                System.out.println("Push variable-identifier1: " + stackBack.peek());
                row = findLine("variable-identifier");

            } else if (content == "identifiers-list1") {

                identifiersTable.add(doc.createElement("identifiers-list"));
                declarationElement.appendChild(identifiersTable.get(identifiersTable.size() - 1));
                if (lexCode == 58) {
                    Element emptyElement = doc.createElement("empty");
                    identifiersTable.get(identifiersTable.size() - 1).appendChild(emptyElement);
                    row = turnString(rulesTable[row].getGoFirst());
                }else {
                    stackBack.push(row);
                    System.out.println("Push identifiers-list1: " + stackBack.peek());
                    row = findLine("identifiers-list");
                }


            } else if (content == "58" && lexCode == 58) {

                declarationElement.appendChild(doc.createTextNode(":"));
                iteratorLexTab();
                row = turnString(rulesTable[row].getGoFirst());
            } else if (content == "attribute" && rulesTable[row].getGoFirst() == "21") {

                attributeElement = doc.createElement(content);
                declarationElement.appendChild(attributeElement);
                stackBack.push(row);
                System.out.println("Push attribute1: " + stackBack.peek());
                row = findLine("attribute");

            } else if (content == "attributes-list1") {

                attributesTable.add(doc.createElement("attributes-list"));
                declarationElement.appendChild(attributesTable.get(attributesTable.size() - 1));
                stackBack.push(row);
                System.out.println("Push attributes-list1: " + stackBack.peek());
                row = findLine("attributes-list");

            } else if (content == "59" && lexCode == 59 && rulesTable[row].getGoFirst()== "T") {
                declarationElement.appendChild(doc.createTextNode(";"));
                iteratorLexTab();
                System.out.println("Pop 59:" + stackBack.peek());
                row = stackBack.pop() + 1;
            } else if (content == "44" && lexCode == 44 && rulesTable[row].getGoFirst() == "24") {

                identifiersTable.get(identifiersTable.size() - 1).appendChild(doc.createTextNode(","));
                iteratorLexTab();
                row = turnString(rulesTable[row].getGoFirst());

            } else if (content == "variable-identifier") {

                variableIdentifierElement = doc.createElement(content);
                identifiersTable.get(identifiersTable.size() - 1).appendChild(variableIdentifierElement);
                stackBack.push(row);
                System.out.println("Push variable-identifier: " + stackBack.peek());
                row = findLine("variable-identifier");

            } else if (content == "identifiers-list") {

                identifiersTable.add(doc.createElement(content));
                declarationElement.appendChild(identifiersTable.get(identifiersTable.size() - 1));

                if(lexCode == 44) {
                    row = turnString(rulesTable[row].getGoFirst());
                } else if (lexCode == 58){
                    Element emptyElement = doc.createElement("empty");
                    identifiersTable.get(identifiersTable.size() - 1).appendChild(emptyElement);
                    System.out.println("Pop identifiers-list:" + stackBack.peek());
                    row = stackBack.pop() + 1;
                }

            } else if (content == "attribute" && rulesTable[row].getGoFirst() == "28") {
                attributeElement = doc.createElement(content);
                attributesTable.get(attributesTable.size() - 1).appendChild(attributeElement);

                if (lexCode == 59) {
                    Element emptyElement = doc.createElement("empty");
                    attributesTable.get(attributesTable.size() - 1).appendChild(emptyElement);
                    System.out.println("Current lexCode: " + lexCode);
                    System.out.println("Pop attribute2:" + stackBack.peek());
                    row = stackBack.pop() + 1;
                } else {
                    stackBack.push(row);
                    System.out.println("Push attribute2: " + stackBack.peek());
                    row = findLine("attribute");
                }

            } else if (content == "attributes-list"){

                attributesTable.add(doc.createElement("attributes-list"));
                attributesTable.get(attributesTable.size() - 2).appendChild(attributesTable.get(attributesTable.size() - 1));
                row = turnString(rulesTable[row].getGoFirst());

            } else if (content == "91") {

                if (lexCode >= 405 && lexCode <= 410) {
                    attributeElement.appendChild(doc.createTextNode(searchKeyById(lexCode)));
                    System.out.println("Here it is " + searchKeyById(lexCode));
                    iteratorLexTab();
                    System.out.println("Pop 91:" + stackBack.peek());
                    row = stackBack.pop() + 1;
                } else if (lexCode == 91) {
                    attributeElement.appendChild(doc.createTextNode("["));
                    iteratorLexTab();
                    row = turnString(rulesTable[row].getGoFirst());
                }

            }else if (content == "range1") {

                rangeElement = doc.createElement("range");
                attributeElement.appendChild(rangeElement);
                stackBack.push(row);
                System.out.println("Current lexcode: " + lexCode);
                System.out.println("Push range1: " + stackBack.peek());
                row = findLine("range");

            }else if (content == "ranges-list1") {

                rangesTable.add(doc.createElement("ranges-list"));
                attributeElement.appendChild(rangesTable.get(rangesTable.size() - 1));

                if(lexCode == 59) {
                    iteratorLexTab();
                    System.out.println("Current lexCode: " + lexCode);
                    System.out.println("Pop ranges-list1: " + stackBack.peek());
                    row = stackBack.pop();
                }else {
                    stackBack.push(row);
                    System.out.println("Current lexcode: " + lexCode);
                    System.out.println("Push ranges-list1: " + stackBack.peek());
                    row = findLine("ranges-list");
                }


            } else if(content == "93" && lexCode == 93) {

                attributeElement.appendChild(doc.createTextNode("]"));
                System.out.println("Pop 93: " + stackBack.peek());
                row = stackBack.pop() + 1;

            }else if (content == "44" && lexCode == 44 && rulesTable[row].getGoFirst() == "35") {
                rangesTable.get(rangesTable.size() - 1).appendChild(doc.createTextNode(","));
                iteratorLexTab();
                row = turnString(rulesTable[row].getGoFirst());

            } else if (content == "range") {

                rangeElement = doc.createElement(content);
                rangesTable.get(rangesTable.size() - 1).appendChild(rangeElement);
                stackBack.push(row);
                System.out.println("Push range: " + stackBack.peek());
                row = findLine("unsigned-integer");

            } else if (content == "ranges-list") {

                rangesTable.add(doc.createElement(content));
                attributeElement.appendChild(rangesTable.get(rangesTable.size() - 1));

                if(lexCode == 44) {
                    row = turnString(rulesTable[row].getGoFirst());
                } else if (lexCode == 93){
                    Element emptyElement = doc.createElement("empty");
                    identifiersTable.get(identifiersTable.size() - 1).appendChild(emptyElement);
                    System.out.println("Pop identifiers-list:" + stackBack.peek());
                    row = stackBack.pop() + 1;
                }
            } else if (content == "unsigned-integer") {
                unsignedIntegerElement1 = doc.createElement("unsigned-integer");
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
                unsignedIntegerElement2 = doc.createElement("unsigned-integer");
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
                row = stackBack.pop() + 1;
            } else if (content == "identifier") {
                Element identifierElement = doc.createElement("identifier");
                if (rulesTable[row].getLeftPart() == "variable-identifier"){
                    variableIdentifierElement.appendChild(identifierElement);
                }


                String identifierTemp = "";

                if (searchIdentifierById(lexCode) != "") {
                    identifierTemp = searchIdentifierById(lexCode);
                } else {
                    Error.output(2);
                }

                //letterTag(identifierElement, i, identifierTemp);
                Element parentStringElement = null;
                for(int i = 0; i < identifierTemp.length(); i++) {
                    //stringTag(identifierElement, i, identifierTemp);
                    temp = (int) identifierTemp.charAt(i);
                    if (temp >= 48 && temp <= 57) {
                        if (i == 0) {
                            Element digitElement = doc.createElement("digit");
                            identifierElement.appendChild(digitElement);
                            digitElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
                            parentStringElement = doc.createElement("string");
                            identifierElement.appendChild(parentStringElement);

                        } else {
                            Element digitElement = doc.createElement("digit");
                            parentStringElement.appendChild(digitElement);
                            digitElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
                            Element stringElement = doc.createElement("string");
                            parentStringElement.appendChild(stringElement);
                            parentStringElement = stringElement;

                        }

                    } else if (temp >= 65 && temp <= 90) {
                        if (i == 0) {
                            Element letterElement = doc.createElement("letter");
                            identifierElement.appendChild(letterElement);
                            letterElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
                            parentStringElement = doc.createElement("string");
                            identifierElement.appendChild(parentStringElement);
                        } else {
                            Element letterElement = doc.createElement("letter");
                            parentStringElement.appendChild(letterElement);
                            letterElement.appendChild(doc.createTextNode(Character.toString(identifierTemp.charAt(i))));
                            Element stringElement = doc.createElement("string");
                            parentStringElement.appendChild(stringElement);
                            parentStringElement = stringElement;
                        }
                    }
                }

                iteratorLexTab();
                System.out.println("Pop identifier:" + stackBack.peek());
                row = stackBack.pop() + 1;
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("result.xml"));
        transformer.transform(source, result);
        System.out.println("Nothing happened");
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

    public String searchKeyById(int lexCodeTemp){
        String temp = "";

        for (TSymbol key: KeyTab) {
            if (key.getCode() == lexCodeTemp) {
                temp = key.getName();
                break;
            }
        }

        return temp;
    }
}
