package refactoring;

public enum LogLevel {

	// MESSAGE < WARNING < ERROR
	MESSAGE(1), WARNING(2), ERROR(3);

	private final int level;

	private LogLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

}
