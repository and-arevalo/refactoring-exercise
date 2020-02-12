package refactoring.handler;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import refactoring.LogLevel;

public class ConsoleLogHandler implements LogHandler {

	private static final Logger LOGGER = Logger.getLogger(ConsoleLogHandler.class.getName());
	static {
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(new ConsoleHandler());
	}

	private static final String INVALID_ARGUMENT_ERROR = "Error or Warning or Message must be specified";

	@Override
	public void log(LogLevel level, String text) {
		if (level == null) {
			throw new IllegalArgumentException(INVALID_ARGUMENT_ERROR);
		}

		LOGGER.log(Level.INFO, text);
	}

}
