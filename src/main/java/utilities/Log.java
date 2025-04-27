package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;

public class Log {

    public static Logger logger = LogManager.getLogger(Log.class);

    static {
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // Force touch an empty log file with current timestamp
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());
            File logFile = new File(logDir, "app_" + timestamp + ".log");
            if (!logFile.exists()) {
                logFile.createNewFile(); // <- this creates an empty file even if no logs
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message) {
        logger.error(message);
    }
    public static void warn(String message) {
    	logger.warn(message);
    }
}



