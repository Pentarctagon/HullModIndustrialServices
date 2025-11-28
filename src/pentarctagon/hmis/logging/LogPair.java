package pentarctagon.hmis.logging;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LogPair
{
    private final String key;
    private final ThreadLocal<String> value = new ThreadLocal<>();

    public LogPair(String key)
    {
        this.key = key;
        value.set("");
    }

    public String key()
    {
        return key;
    }

    public String val()
    {
        String v = value.get();
        value.remove();
        return v;
    }

    public LogPair v(Object... values)
    {
        if(values != null && values.length > 0)
        {
            value.set(Arrays.stream(values).map(value -> value != null ? value.toString() : "NULL").collect(Collectors.joining()));
        }
        return this;
    }
}
