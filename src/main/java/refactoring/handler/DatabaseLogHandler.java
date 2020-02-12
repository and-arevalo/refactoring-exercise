package refactoring.handler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import refactoring.LogLevel;

public class DatabaseLogHandler implements LogHandler {

	private static final Logger LOGGER = Logger.getLogger(DatabaseLogHandler.class.getName());

	private static final String INVALID_ARGUMENT_ERROR = "Error or Warning or Message must be specified";
	private static final String INVALID_CONFIGURATION_ERROR = "Invalid configuration";

	private static final String JDBC_PARAM = "jdbc";
	private static final String USER_PARAM = "user";
	private static final String PASSWORD_PARAM = "password";

	private static final String INSERT_STATEMENT = "INSERT INTO LOG_VALUES(LEVEL, MESSAGE) VALUES (?, ?)";

	private Map<String, String> dbParams;

	public DatabaseLogHandler(Map<String, String> dbParams) throws SecurityException, IOException {
		this.dbParams = dbParams;
	}

	@Override
	public void log(LogLevel level, String text) {
		if (this.dbParams == null //
				|| !dbParams.containsKey(USER_PARAM) //
				|| !dbParams.containsKey(PASSWORD_PARAM) //
				|| !dbParams.containsKey(JDBC_PARAM)) {
			throw new IllegalStateException(INVALID_CONFIGURATION_ERROR);
		}
		if (level == null) {
			throw new IllegalArgumentException(INVALID_ARGUMENT_ERROR);
		}

		Properties properties = new Properties();
		properties.put(USER_PARAM, dbParams.get(USER_PARAM));
		properties.put(PASSWORD_PARAM, dbParams.get(PASSWORD_PARAM));

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = DriverManager.getConnection(dbParams.get(JDBC_PARAM), properties);

			ps = connection.prepareStatement(INSERT_STATEMENT);
			ps.setInt(1, level.getLevel());
			ps.setString(2, text);
			ps.executeUpdate();

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}

}
