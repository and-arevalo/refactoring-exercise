package refactoring.handler;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import refactoring.LogLevel;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileLogHandler.class)
public class FileLogHandlerTest {

	private static final String FILENAME = "file.log";

	@Test
	public void testConstructor() throws SecurityException, IOException {
		PowerMockito.mockStatic(Logger.class);

		Logger logger = mock(Logger.class);
		when(Logger.getLogger(FILENAME)).thenReturn(logger);

		new FileLogHandler(FILENAME);
		verify(logger, times(1)).addHandler(any(FileHandler.class));
	}

	@Test
	public void logWithInvalidConfigurationTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			new FileLogHandler(null);
		});
	}

	@Test
	public void logWithInvalidArgumentsTest() throws SecurityException, IOException {
		assertThrows(IllegalArgumentException.class, () -> {
			FileLogHandler fileLogHandler = new FileLogHandler(FILENAME);
			fileLogHandler.log(null, null);
		});
	}

	@Test
	public void logTest() throws SecurityException, IOException {
		PowerMockito.mockStatic(Logger.class);

		Logger logger = mock(Logger.class);
		when(Logger.getLogger(FILENAME)).thenReturn(logger);

		String text = "Text";

		FileLogHandler fileLogHandler = new FileLogHandler(FILENAME);
		verify(logger, never()).log(any(), anyString());
		fileLogHandler.log(LogLevel.ERROR, text);
		verify(logger, times(1)).log(any(), same(text));
	}

}
