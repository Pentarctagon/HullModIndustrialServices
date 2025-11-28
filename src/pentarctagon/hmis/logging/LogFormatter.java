package pentarctagon.hmis.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static pentarctagon.hmis.logging.LogPairList.*;

public class LogFormatter
{
    private LogFormatter(){}

    public static String msg(LogPair... logs)
    {
        ArrayList<LogPair> statements = new ArrayList<>(Arrays.asList(logs));
        statements.add(TS.v(getCurrentTimestamp()));

        addCaller(statements);

        return "{"
            +statements.stream().map(s -> "\""+s.key()+"\":\""+s.val()+"\"").collect(Collectors.joining(","))
            +"}";
    }

    private static void addCaller(ArrayList<LogPair> statements)
    {
        Optional<StackWalker.StackFrame> frame = StackWalker.getInstance().walk(s -> s.filter(f -> !f.getClassName().equals("pentarctagon.hmis.logging.LogFormatter")).findFirst());
        if(frame.isPresent())
        {
            StackWalker.StackFrame f = frame.get();
            statements.add(LINE.v(f.getLineNumber()));
            statements.add(METHOD.v(f.getMethodName()));
            statements.add(CLASS.v(f.getClassName()));
        }
        statements.add(THREAD_NAME.v(Thread.currentThread().getName()));
        statements.add(THREAD_ID.v(Thread.currentThread().getId()));
    }

    private static String getCurrentTimestamp()
    {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS"));
    }
}
