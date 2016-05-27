/**
 * Created by Vlados Papandos on 26.05.2016.
 */
public class RuleNode {
    private String leftPart;
    private String rightPart;
    private String goTo;
    private String isFalse;

    public RuleNode(String leftPart, String rightPart, String goTo, String isFalse) {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        this.goTo = goTo;
        this.isFalse = isFalse;
    }

    public String getLeftPart() {
        return leftPart;
    }

    public String getRightPart() {
        return rightPart;
    }

    public String getGoFirst() {
        return goTo;
    }

    public String getGoSecond() {
        return isFalse;
    }
}
