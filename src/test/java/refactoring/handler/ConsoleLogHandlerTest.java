package refactoring.handler;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import refactoring.LogLevel;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConsoleLogHandler.class)
public class ConsoleLogHandlerTest {

	private static Logger logger;

	@BeforeClass
	public static void setup() {
		PowerMockito.mockStatic(Logger.class);
		logger = mock(Logger.class);
		when(Logger.getLogger(anyString())).thenReturn(logger);
	}

	@Test
	public void logWithInvalidArgumentsTest() {
		ConsoleLogHandler consoleLogHandler = new ConsoleLogHandler();

		assertThrows(IllegalArgumentException.class, () -> {
			consoleLogHandler.log(null, null);
		});
	}

	@Test
	public void logTest() {
		String text = "Text";

		ConsoleLogHandler consoleLogHandler = new ConsoleLogHandler();
		verify(logger, never()).log(any(), anyString());
		consoleLogHandler.log(LogLevel.ERROR, text);
		verify(logger, times(1)).log(any(), anyString());
	}

}
