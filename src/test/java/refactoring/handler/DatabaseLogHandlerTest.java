package refactoring.handler;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import refactoring.LogLevel;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DatabaseLogHandler.class)
public class DatabaseLogHandlerTest {

	@Test
	public void logWithInvalidConfigurationTest() throws SecurityException, IOException {
		assertThrows(IllegalStateException.class, () -> {
			DatabaseLogHandler databaseLogHandler = new DatabaseLogHandler(null);
			databaseLogHandler.log(null, null);
		});

		Map<String, String> properties = new HashMap<String, String>();
		assertThrows(IllegalStateException.class, () -> {
			DatabaseLogHandler databaseLogHandler = new DatabaseLogHandler(properties);
			databaseLogHandler.log(null, null);
		});

		properties.put("user", "123");
		assertThrows(IllegalStateException.class, () -> {
			DatabaseLogHandler databaseLogHandler = new DatabaseLogHandler(properties);
			databaseLogHandler.log(null, null);
		});

		properties.put("password", "123");
		assertThrows(IllegalStateException.class, () -> {
			DatabaseLogHandler databaseLogHandler = new DatabaseLogHandler(properties);
			databaseLogHandler.log(null, null);
		});

		properties.put("jdbc", "123");
		properties.remove("password");
		assertThrows(IllegalStateException.class, () -> {
			DatabaseLogHandler databaseLogHandler = new DatabaseLogHandler(properties);
			databaseLogHandler.log(null, null);
		});
	}

	@Test
	public void logWithInvalidArgumentsTest() throws SecurityException, IOException {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("user", "123");
		properties.put("password", "123");
		properties.put("jdbc", "123");

		DatabaseLogHandler databaseLogHandler = new DatabaseLogHandler(properties);
		assertThrows(IllegalArgumentException.class, () -> {
			databaseLogHandler.log(null, null);
		});
	}

	@Test
	public void logTest() throws Exception {
		Map<String, String> dbParams = new HashMap<String, String>();
		dbParams.put("user", "123");
		dbParams.put("password", "123");
		dbParams.put("jdbc", "jdbc:oracle:thin:@myhost:1521:orcl");

		Properties properties = new Properties();
		properties.put("user", "123");
		properties.put("password", "123");

		LogLevel level = LogLevel.ERROR;
		String text = "Text";

		PowerMockito.mockStatic(DriverManager.class);
		Connection connection = mock(Connection.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		when(DriverManager.getConnection(eq(dbParams.get("jdbc")), eq(properties))).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

		DatabaseLogHandler databaseLogHandler = new DatabaseLogHandler(dbParams);
		databaseLogHandler.log(level, text);

		verify(connection, times(1)).prepareStatement(eq("INSERT INTO LOG_VALUES(LEVEL, MESSAGE) VALUES (?, ?)"));
		verify(preparedStatement, times(1)).setInt(eq(1), eq(level.getLevel()));
		verify(preparedStatement, times(1)).setString(eq(2), same(text));
		verify(preparedStatement, times(1)).executeUpdate();
		verify(preparedStatement, times(1)).close();
		verify(connection, times(1)).close();
	}

	@Test
	public void detachTest() {
		// Nothing to test
	}

}
