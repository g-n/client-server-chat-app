import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for single-line request logging with timestamping
 */
public class LogFormat extends Formatter {
    final static DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public String format(LogRecord record) {
        return "[" + dtFormat.format((new Date())) + "] " + record.getMessage() + "\r\n";
    }
}