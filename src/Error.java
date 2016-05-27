/**
 * Created by Vlados Papandos on 11.05.2016.
 */
public class Error {
    public static void output(int errorCode) {

        switch (errorCode) {
            case 1: System.out.println("Illegal symbol");
                break;
            case 2: System.out.println("No such identifier");
                break;
            case 3: System.out.println("No such attribute");
                break;
            case 4: System.out.println("Directive PROGRAM is missing");
                break;
            case 5: System.out.println("; missed");
                break;
            case 6: System.out.println("BEGIN missed");
                break;
            case 7: System.out.println("END missed");
                break;
            case 8: System.out.println("VAR missed");
                break;
            case 9: System.out.println("Nothing between begin and end");
                break;
            case 10: System.out.println(") missed");
                break;
            case 11: System.out.println("( missed");
                break;
            case 12: System.out.println("*) is missed");
                break;
            case 13: System.out.println(", is missed");
                break;
            case 14: System.out.println(": is missed");
                break;
            case 15: System.out.println(". is missed");
                break;
        }
    }
}
