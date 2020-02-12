package refactoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import refactoring.handler.LogHandler;

public class JobLoggerImplTest {

	private JobLogger createJobLogger() {
		return new JobLoggerImpl();
	}

	@Test
	public void changeLevelTest() {
		JobLogger jobLogger = createJobLogger();
		assertNull("The level should not be initialized", jobLogger.getLevel());

		jobLogger.setLevel(LogLevel.ERROR);
		assertEquals("The level should be ERROR", LogLevel.ERROR, jobLogger.getLevel());

		jobLogger.setLevel(LogLevel.WARNING);
		assertEquals("The level should be WARNING", LogLevel.WARNING, jobLogger.getLevel());
	}

	@Test
	public void adminHandlersTest() {
		JobLogger jobLogger = createJobLogger();
		assertEquals("There should be no handlers", 0, jobLogger.getHandlers().size());

		LogHandler handler = mock(LogHandler.class);
		jobLogger.addHandler(handler);
		assertEquals("There should be one handler", 1, jobLogger.getHandlers().size());
		assertEquals("The handler was not added", handler, jobLogger.getHandlers().iterator().next());

		LogHandler handler2 = mock(LogHandler.class);
		jobLogger.addHandler(handler2);
		assertEquals("There should be two handlers", 2, jobLogger.getHandlers().size());

		jobLogger.removeHandler(handler2);
		assertEquals("There should be one handler", 1, jobLogger.getHandlers().size());
		assertEquals("The handler was not added", handler, jobLogger.getHandlers().iterator().next());
	}

	@Test
	public void logWithInvalidConfigurationTest() {
		JobLogger jobLogger = createJobLogger();
		assertThrows(IllegalStateException.class, () -> {
			jobLogger.log(null, null);
		});
	}

	@Test
	public void logWithInvalidArgumentsTest() {
		JobLogger jobLogger = createJobLogger();
		jobLogger.setLevel(LogLevel.ERROR);

		assertThrows(IllegalArgumentException.class, () -> {
			jobLogger.log(null, null);
		});
	}

	@Test
	public void logLowerLevelTest() {
		JobLogger jobLogger = createJobLogger();
		jobLogger.setLevel(LogLevel.ERROR);

		LogHandler handler = mock(LogHandler.class);
		jobLogger.addHandler(handler);
		LogHandler handler2 = mock(LogHandler.class);
		jobLogger.addHandler(handler2);

		verify(handler, never()).log(any(), any());
		verify(handler2, never()).log(any(), any());
		jobLogger.log(LogLevel.WARNING, null);
		verify(handler, never()).log(any(), any());
		verify(handler2, never()).log(any(), any());
	}

	@Test
	public void logHigherLevelTest() {
		JobLogger jobLogger = createJobLogger();
		jobLogger.setLevel(LogLevel.WARNING);

		LogHandler handler = mock(LogHandler.class);
		jobLogger.addHandler(handler);
		LogHandler handler2 = mock(LogHandler.class);
		jobLogger.addHandler(handler2);

		String text = "Text";

		verify(handler, never()).log(any(), any());
		verify(handler2, never()).log(any(), any());
		jobLogger.log(LogLevel.WARNING, text);
		verify(handler, times(1)).log(eq(LogLevel.WARNING), same(text));
		verify(handler2, times(1)).log(eq(LogLevel.WARNING), same(text));

		for (LogLevel level : LogLevel.values()) {
			if (level != LogLevel.WARNING) {
				verify(handler, never()).log(eq(level), anyString());
				verify(handler2, never()).log(eq(level), anyString());
			}
		}
	}

}
