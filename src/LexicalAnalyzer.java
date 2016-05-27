import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.*;

public class LexicalAnalyzer {
    String text;
    String textname;
    FileInputStream fileInput;
    private ArrayList<Integer> LexTab = new ArrayList<Integer>();
    private ArrayList<TSymbol> ConstTab = new ArrayList<TSymbol>();
    private ArrayList<TSymbol> IdentTab = new ArrayList<TSymbol>();
    private ArrayList<TSymbol> KeyTab = new ArrayList<TSymbol>(Arrays.asList(new TSymbol[]
            {
                    new TSymbol("PROGRAM", 401),
                    new TSymbol("BEGIN", 402),
                    new TSymbol("END", 403),
                    new TSymbol("VAR", 404),
                    new TSymbol("SIGNAL", 405),
                    new TSymbol("COMPLEX", 406),
                    new TSymbol("INTEGER", 407),
                    new TSymbol("FLOAT", 408),
                    new TSymbol("BLOCKFLOAT", 409),
                    new TSymbol("EXT",410),
                    new TSymbol("..", 411)
            }));
    private ArrayList<AttrSymbol> AttrTab = new ArrayList<AttrSymbol>(Arrays.asList(new AttrSymbol[]
            {
                    new AttrSymbol(9, 0),
                    new AttrSymbol(13, 0),
                    new AttrSymbol(32, 0),
                    new AttrSymbol(33, 4),
                    new AttrSymbol(34, 4),
                    new AttrSymbol(35, 4),
                    new AttrSymbol(36, 4),
                    new AttrSymbol(37, 4),
                    new AttrSymbol(38, 4),
                    new AttrSymbol(39, 4),
                    new AttrSymbol(40, 3),
                    new AttrSymbol(41, 3),
                    new AttrSymbol(42, 3),
                    new AttrSymbol(43, 4),
                    new AttrSymbol(44, 4),
                    new AttrSymbol(45, 4),
                    new AttrSymbol(46, 5),
                    new AttrSymbol(47, 4),
                    new AttrSymbol(48, 1),
                    new AttrSymbol(49, 1),
                    new AttrSymbol(50, 1),
                    new AttrSymbol(51, 1),
                    new AttrSymbol(52, 1),
                    new AttrSymbol(53, 1),
                    new AttrSymbol(54, 1),
                    new AttrSymbol(55, 1),
                    new AttrSymbol(56, 1),
                    new AttrSymbol(57, 1),
                    new AttrSymbol(58, 4),
                    new AttrSymbol(59, 4),
                    new AttrSymbol(60, 4),
                    new AttrSymbol(61, 4),
                    new AttrSymbol(62, 4),
                    new AttrSymbol(63, 4),
                    new AttrSymbol(64, 4),
                    new AttrSymbol(65, 2),
                    new AttrSymbol(66, 2),
                    new AttrSymbol(67, 2),
                    new AttrSymbol(68, 2),
                    new AttrSymbol(69, 2),
                    new AttrSymbol(70, 2),
                    new AttrSymbol(71, 2),
                    new AttrSymbol(72, 2),
                    new AttrSymbol(73, 2),
                    new AttrSymbol(74, 2),
                    new AttrSymbol(75, 2),
                    new AttrSymbol(76, 2),
                    new AttrSymbol(77, 2),
                    new AttrSymbol(78, 2),
                    new AttrSymbol(79, 2),
                    new AttrSymbol(80, 2),
                    new AttrSymbol(81, 2),
                    new AttrSymbol(82, 2),
                    new AttrSymbol(83, 2),
                    new AttrSymbol(84, 2),
                    new AttrSymbol(85, 2),
                    new AttrSymbol(86, 2),
                    new AttrSymbol(87, 2),
                    new AttrSymbol(88, 2),
                    new AttrSymbol(89, 2),
                    new AttrSymbol(90, 2),
                    new AttrSymbol(91, 4),
                    new AttrSymbol(92, 4),
                    new AttrSymbol(93, 4),
                    new AttrSymbol(94, 4),
                    new AttrSymbol(95, 4),
                    new AttrSymbol(96, 4),
                    new AttrSymbol(97, 2),
                    new AttrSymbol(98, 2),
                    new AttrSymbol(99, 2),
                    new AttrSymbol(100, 2),
                    new AttrSymbol(101, 2),
                    new AttrSymbol(102, 2),
                    new AttrSymbol(103, 2),
                    new AttrSymbol(104, 2),
                    new AttrSymbol(105, 2),
                    new AttrSymbol(106, 2),
                    new AttrSymbol(107, 2),
                    new AttrSymbol(108, 2),
                    new AttrSymbol(109, 2),
                    new AttrSymbol(110, 2),
                    new AttrSymbol(111, 2),
                    new AttrSymbol(112, 2),
                    new AttrSymbol(113, 2),
                    new AttrSymbol(114, 2),
                    new AttrSymbol(115, 2),
                    new AttrSymbol(116, 2),
                    new AttrSymbol(117, 2),
                    new AttrSymbol(118, 2),
                    new AttrSymbol(119, 2),
                    new AttrSymbol(120, 2),
                    new AttrSymbol(121, 2),
                    new AttrSymbol(122, 2),
                    new AttrSymbol(123, 4),
                    new AttrSymbol(124, 4),
                    new AttrSymbol(125, 4),
                    new AttrSymbol(126, 4)
            }));

    public LexicalAnalyzer(String textname){
        this.textname = textname;
    }

    public AttrSymbol Geti(int index) throws IOException {
        char c = '0';
        int r = 0;
        AttrSymbol Result = new AttrSymbol();

        for(int i = 0; i <= index; i++) {
            r = fileInput.read();
            if (i == index) {
                c = (char) r;
            }
        }
        Result.setValue(c);

        for (AttrSymbol theKey : AttrTab) {
            if (theKey.getValue() == Result.getValue()) {
                Result.setAttr(theKey.getAttr());
            }
        }
        return Result;
    }

    public int KeyTabSearch(String constword){
        int res = 0;
        for (TSymbol theKey : KeyTab) {
            if (theKey.getName().equals(constword)) {
                res = theKey.getCode();
                break;
            }
        }
        return res;
    }

    public AttrSymbol Gets(int index) {
        AttrSymbol Result = new AttrSymbol();
        Result.setValue((short) text.charAt((index)));

        for (AttrSymbol theKey : AttrTab) {
            if (theKey.getValue() == Result.getValue()) {
                Result.setAttr(theKey.getAttr());
            }
        }
        return Result;
    }


    public int IdnTabSearch(String constword){
        int res = 0;
        for (TSymbol theKey : IdentTab) {
            if (theKey.getName().equals(constword)) {
                res = theKey.getCode();
                break;
            }
        }
        return res;
    }

    public int ConstTabSearch(String constword){
        int res = 0;
        for (TSymbol theKey : ConstTab)
        {
            if (theKey.getName().equals(constword))
            {
                res = theKey.getCode();
                break;
            }
        }
        return res;
    }

    public void ConstTabForm(String word) {
        if (ConstTab.isEmpty())
        {
            TSymbol tempVar = new TSymbol();
            tempVar.setName(word);
            tempVar.setCode(501);
            ConstTab.add(tempVar);
        }
        else
        {
            TSymbol tempVar2 = new TSymbol();
            tempVar2.setName(word);
            tempVar2.setCode(ConstTab.get(ConstTab.size() - 1).getCode() + 1);
            ConstTab.add(tempVar2);
        }
    }

    public String getText() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(textname));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            text = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
        return text;
    }

    public void IdnTabForm(String word){
        if (IdentTab.isEmpty()) {
            TSymbol tempVar3 = new TSymbol();
            tempVar3.setName(word);
            tempVar3.setCode(1001);
            IdentTab.add(tempVar3);
        }
        else
        {
            TSymbol tempVar4 = new TSymbol();
            tempVar4.setName(word);
            tempVar4.setCode(IdentTab.get(IdentTab.size() - 1).getCode() + 1);
            IdentTab.add(tempVar4);
        }
    }


    private void Scanner() throws IOException {
        text = getText();
        AttrSymbol symbol = new AttrSymbol();
        int lexCode = 0;
        String buff = "";
        boolean SuppressOutput;

        fileInput = new FileInputStream(textname);
        int r;
        int textlength= 0;
        while ((r = fileInput.read()) != -1) {
           textlength++;
        }




        int i = 0;
        if (text.equals("")) {
            System.out.print("Error");
        }
        do {
            symbol = Gets(i);
            buff = "";
            lexCode = 0;
            SuppressOutput = false;

            switch (symbol.getAttr()) {
                case 0: {
                    while (i != text.length()) {
                        symbol = Gets(i);
                        if (symbol.getAttr() != 0) {
                            break;
                        }
                        i++;
                    }
                    SuppressOutput = true;
                }
                break;
                case 1: {
                    while ((i != text.length()) && (symbol.getAttr() == 1)) {
                        buff += (char) symbol.getValue();
                        i++;
                        if (i != text.length()) {
                            symbol = Gets(i);
                        }
                    }
                    if (ConstTabSearch(buff) != 0) {
                        lexCode = ConstTabSearch(buff);
                    } else {
                        if (ConstTab.size() != 0) {
                            lexCode = ConstTab.get(ConstTab.size() - 1).getCode() + 1;
                        } else {
                            lexCode = 501;
                        }
                        ConstTabForm(buff);
                    }
                }
                break;
                case 2: {
                    while ((i != text.length()) && ((symbol.getAttr() == 2) || (symbol.getAttr() == 1))) {
                        buff += (char) symbol.getValue();
                        i++;
                        if (i != text.length()) {
                            symbol = Gets(i);
                        }
                    }
                    if (KeyTabSearch(buff) != 0) {
                        lexCode = KeyTabSearch(buff);
                    } else {
                        if (IdnTabSearch(buff) != 0) {
                            lexCode = IdnTabSearch(buff);
                        } else {
                            if (IdentTab.size() != 0) {
                                lexCode = IdentTab.get(IdentTab.size() - 1).getCode() + 1;
                            } else {
                                lexCode = 1001;
                            }
                            IdnTabForm(buff);
                        }
                    }
                }
                break;
                case 3: {
                    i++;
                    if (i != text.length()) {
                        symbol = Gets(i);
                    }
                    if (symbol.getValue() == 42) {
                        i++;
                        if (i != text.length()) {
                            symbol = Gets(i);
                        }
                        do {
                            while ((i != text.length()) && (symbol.getValue() != 42)) {
                                i++;
                                if (i != text.length()) {
                                    symbol = Gets(i);
                                }
                            }
                            if (i == text.length()) {
                                Error.output(12);
                            }
                            break;

                        } while (symbol.getValue() == 41);

                        if (i != text.length()) {
                            i++;
                            symbol = Gets(i);

                            if (symbol.getValue() == 41) {
                                SuppressOutput = true;
                            } else {
                                Error.output(12);
                            }
                        }
                        SuppressOutput = true;
                        i++;
                    } else {
                        lexCode = 40;
                        System.out.println("Illegal symbol");
                    }
                }

                break;
                case 4: {

                    lexCode = (short) symbol.getValue();
                    i++;
                }
                break;
                case 5: {
                    i++;
                    if (i != text.length()) {
                        symbol = Gets(i);
                    }
                    if (symbol.getValue() == 46) {
                        buff += (char) symbol.getValue();
                        lexCode = 411;
                        i++;
                    } else {
                        SuppressOutput = false;
                        i--;
                        lexCode = 46;
                        i++;
                    }
                }
                break;
                default:
                    symbol= Gets(i);
                    System.out.print("Illegal symbol");

                    break;
            }
            if (SuppressOutput != true) {
                System.out.print(lexCode + " ");
                LexTab.add(lexCode);
            }

        } while (i != text.length());


        System.out.println("\r\nIdentificators Table:");
        for (TSymbol theKey : IdentTab) {
            System.out.println(theKey.getName() + " " + theKey.getCode());
        }


        System.out.println("Constants Table:");
        for (TSymbol theKey : ConstTab) {
            System.out.println(theKey.getName() + " " + theKey.getCode());
        }
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
//        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("Enter file name: ");
//        String name = bufferRead.readLine();

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("test.txt");
        lexicalAnalyzer.Scanner();
        KnuthAnalyzer syntaxAnalyzer = new KnuthAnalyzer(lexicalAnalyzer.LexTab, lexicalAnalyzer.ConstTab, lexicalAnalyzer.IdentTab, lexicalAnalyzer.KeyTab, lexicalAnalyzer.AttrTab);
        syntaxAnalyzer.analyzeKnuth();
        System.out.println(lexicalAnalyzer.text);
    }
}

