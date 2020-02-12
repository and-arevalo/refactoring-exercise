package refactoring.handler;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import refactoring.LogLevel;

public class FileLogHandler implements LogHandler {

	private static final String INVALID_CONFIGURATION_ERROR = "Invalid configuration";
	private static final String INVALID_ARGUMENT_ERROR = "Error or Warning or Message must be specified";

	private Logger logger;
	private FileHandler fileHandler;

	public FileLogHandler(String filename) throws SecurityException, IOException {
		if (filename == null || filename.isEmpty()) {
			throw new IllegalArgumentException(INVALID_CONFIGURATION_ERROR);
		}

		this.fileHandler = new FileHandler(filename);
		this.logger = Logger.getLogger(filename);
		this.logger.setUseParentHandlers(false);
		this.logger.addHandler(this.fileHandler);
	}

	@Override
	public void log(LogLevel level, String text) {
		if (level == null) {
			throw new IllegalArgumentException(INVALID_ARGUMENT_ERROR);
		}

		this.logger.log(Level.INFO, text);
	}

}
