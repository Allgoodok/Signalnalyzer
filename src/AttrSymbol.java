import java.util.*;

    public class AttrSymbol
    {
        public AttrSymbol() {
        }

        public AttrSymbol(int value, int attr) {
            Value = value;
            Attr = attr;
        }

        public int Value;
        public final int getValue()
        {
            return Value;
        }
        public final void setValue(int value)
        {
            Value = value;
        }
        public int Attr;
        public final int getAttr()
        {
            return Attr;
        }
        public final void setAttr(int value)
        {
            Attr = value;
        }
    }

