import java.util.*;

public class TSymbol
{
    public TSymbol(){}

    public TSymbol(String name, int code) {
        Name = name;
        Code = code;
    }

    private String Name;
    public final String getName()
    {
        return Name;
    }
    public final void setName(String value)
    {
        Name = value;
    }
    private int Code;
    public final int getCode()
    {
        return Code;
    }
    public final void setCode(int value)
    {
        Code = value;
    }
}
