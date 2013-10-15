package sockit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MessageUtils {

    // fake ascii motherfucking lord of god
    public static final byte LONG_TYPE = 108;
    public static final byte DOUBLE_TYPE = 100;
    public static final byte INT_TYPE = 105;
    public static final byte FLOAT_TYPE = 102;
    public static final byte BOOL_TYPE = 63;
    public static final byte LIST_TYPE = 84;
    public static final byte STRING_TYPE = 115;
    public static final byte DICT_TYPE = 68;
    public static final byte HETERO_TYPE = 120;

    /**
     * Throws an exception if the type is not yet supported
     * @param type The type to test.
     * @throws IOException If the type is not supported yet.
     */
    public static void isTypeExist(byte type) throws IOException{
        if(type != LONG_TYPE &&
                type != DOUBLE_TYPE &&
                type != INT_TYPE &&
                type != FLOAT_TYPE &&
                type != BOOL_TYPE &&
                type != LIST_TYPE &&
                type != STRING_TYPE &&
                type != DICT_TYPE &&
                type != HETERO_TYPE)
            throw new IOException("This type (" + type + ") is not yet supported.");
    }

    /**
     *
     * @param o
     * @return
     * @throws IOException
     */
    public static byte getTypeOfClass(Object o) throws IOException{
        if(o.getClass().equals(Long.class))
            return LONG_TYPE;
        else if(o instanceof Double)
            return DOUBLE_TYPE;
        else if(o instanceof Integer)
            return INT_TYPE;
        else if(o instanceof Float)
            return FLOAT_TYPE;
        else if(o instanceof Boolean)
            return BOOL_TYPE;
        else if(o instanceof List)
            return LIST_TYPE;
        else if(o instanceof String)
            return STRING_TYPE;
        else if(o instanceof Map)
            return DICT_TYPE;
        else
            throw new IOException("This type (" + o.getClass().getName() + ") is not supported yet.");
    }
}
