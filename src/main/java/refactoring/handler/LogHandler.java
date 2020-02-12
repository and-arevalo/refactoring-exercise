package refactoring.handler;

import refactoring.LogLevel;

public interface LogHandler {

	void log(LogLevel level, String text);

}
