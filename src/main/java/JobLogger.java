
// It is a bad practice to use the default package.

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class violates the principle of single responsibility,
 * because it is responsible for logging to files, database,
 * and console, and is responsible for determining which logger
 * and logging level to use.
 * 
 * This class violates the Dependency Investment Principle because
 * it depends on implementations, and not on abstractions. 
 */
public class JobLogger {
	// It is cumbersome to add another destination, i.e., another kind of database
	// or a web service.
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logToDatabase;
	// It is cumbersome to add another logging level, i.e, DEBUG.
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	// This variable is not used. It is a Java smell.
	private boolean initialized;
	// References to generic type Map<K,V> should be parameterized.
	private static Map dbParams;
	// Loggers should be static (to have only one instance per class, and also, to
	// avoid attempts to serialize loggers) and final (no need to change the logger
	// over the runtime).
	private static Logger logger;

	// Each time an object is instantiated, the properties used by the other objects
	// are also changed.
	public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
			boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
		// The name of the logger should be the same as the class.
		// The logger should be initialized only once in the runtime.
		logger = Logger.getLogger("MyLog");
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;
		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		dbParams = dbParamsMap;
	}

	// Using implementations in static methods makes unit testing and mock creation
	// difficult. It also violates the principle of the Open/Closed Principle.
	// It is a bad practice to use "throws Exception".
	public static void LogMessage(String messageText, boolean message, boolean warning, boolean error)
			throws Exception {
		// If messageText is null, it will throw an NullPointerException
		messageText.trim();
		if (messageText == null || messageText.length() == 0) {
			return;
		}
		if (!logToConsole && !logToFile && !logToDatabase) {
			// A custom subclass of Exception should be used, i.e., llegalStateException. In
			// this way, more information about the type of error is provided.
			throw new Exception("Invalid configuration");
		}
		if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
			// A custom subclass of Exception should be used, i.e.,
			// IllegalArgumentException. In this way, more information about the type of
			// error is provided.
			throw new Exception("Error or Warning or Message must be specified");
		}

		// This implementation is highly coupled with JDBC connections.
		Connection connection = null;
		Properties connectionProps = new Properties();

		// There are magic strings. It is a common Java smell.
		// If dbParams is null, it will throw an NullPointerException.
		connectionProps.put("user", dbParams.get("userName"));
		connectionProps.put("password", dbParams.get("password"));

		// If an SQLException is thrown, it is not handled.
		// It does not work for all databases, i.e., jdbc:oracle:thin:@myhost:1521:orcl
		connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
				+ ":" + dbParams.get("portNumber") + "/", connectionProps);

		int t = 0;
		if (message && logMessage) {
			t = 1;
		}

		if (error && logError) {
			t = 2;
		}

		if (warning && logWarning) {
			t = 3;
		}

		// If an SQLException is thrown, it is not handled.
		Statement stmt = connection.createStatement();

		String l = null;
		// If an IOException or SecurityException are thrown, it is not handled.
		File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}

		FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");
		ConsoleHandler ch = new ConsoleHandler();

		// It is more efficient to use a StringBuilder than concatenate strings.
		// The value l is not used.
		if (error && logError) {
			l = l + "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
		}

		if (warning && logWarning) {
			// This line is similar to line 126.
			l = l + "warning " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
		}

		if (message && logMessage) {
			// This line is similar to line 126.
			l = l + "message " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
		}

		if (logToFile) {
			// Each time a message is logged, the logger will have a new handler.
			logger.addHandler(fh);
			// If a message is logged to file and to console, the logger will log the
			// message twice.
			logger.log(Level.INFO, messageText);
		}

		if (logToConsole) {
			// Each time a message is logged, the logger has a new handler.
			logger.addHandler(ch);
			logger.log(Level.INFO, messageText);
		}

		if (logToDatabase) {
			// The following sql statement does not work.
			// The following insert is vulnerable to SQL injection attacks.
			stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(t) + ")");
		}

		// The connection is never closed, and therefore, there are resource leaks.
	}
}