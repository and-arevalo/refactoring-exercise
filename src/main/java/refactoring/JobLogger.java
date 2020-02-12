package refactoring;

import java.util.Set;

import refactoring.handler.LogHandler;

public interface JobLogger {

	LogLevel getLevel();

	void setLevel(LogLevel level);

	void addHandler(LogHandler handler);

	void removeHandler(LogHandler handler);

	Set<LogHandler> getHandlers();

	void log(LogLevel level, String text);

}
