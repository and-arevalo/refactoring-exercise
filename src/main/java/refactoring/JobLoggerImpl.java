package refactoring;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import refactoring.handler.LogHandler;

public class JobLoggerImpl implements JobLogger {

	private static final String INVALID_ARGUMENT_ERROR = "Error or Warning or Message must be specified";
	private static final String INVALID_CONFIGURATION_ERROR = "Invalid configuration";

	private LogLevel level;
	private Set<LogHandler> handlers = new HashSet<LogHandler>();

	public JobLoggerImpl() {
		super();
	}

	public JobLoggerImpl(LogLevel level) {
		super();
		this.level = level;
	}

	public JobLoggerImpl(LogLevel level, Set<LogHandler> handlers) {
		super();
		this.level = level;
		this.handlers = handlers;
	}

	@Override
	public LogLevel getLevel() {
		return level;
	}

	@Override
	public void setLevel(LogLevel level) {
		this.level = level;
	}

	@Override
	public void addHandler(LogHandler handler) {
		this.handlers.add(handler);
	}

	@Override
	public void removeHandler(LogHandler handler) {
		this.handlers.remove(handler);
	}

	@Override
	public Set<LogHandler> getHandlers() {
		return Collections.unmodifiableSet(this.handlers);
	}

	@Override
	public void log(LogLevel level, String text) {
		if (this.level == null) {
			throw new IllegalStateException(INVALID_CONFIGURATION_ERROR);
		}
		if (level == null) {
			throw new IllegalArgumentException(INVALID_ARGUMENT_ERROR);
		}

		if (level.getLevel() < this.level.getLevel()) {
			return;
		}

		for (LogHandler handler : this.handlers) {
			handler.log(level, text);
		}
	}

}
