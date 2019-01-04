package adventofcode2018;

public class StopWatch {

    private static final long MILLIS_PER_SECOND = 1000;
    private static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    private static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    private static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    private long startTime;
    private long stopTime;

    public StopWatch() {
        startTime = System.currentTimeMillis();
        stopTime = 0;
    }

    public String stop() {
        stopTime = System.currentTimeMillis();
        return DDHHMMSSsss(stopTime-startTime);
    }

    @Override
    public String toString() {
        if (stopTime == 0) {
            return DDHHMMSSsss(System.currentTimeMillis() - startTime);
        } else {
            return DDHHMMSSsss(stopTime - startTime);
        }
    }

    private String DDHHMMSSsss(long milliseconds) {
        long days = milliseconds / MILLIS_PER_DAY;
        milliseconds = milliseconds - (days * MILLIS_PER_DAY);
        long hours = milliseconds / MILLIS_PER_HOUR;
        milliseconds = milliseconds - (hours * MILLIS_PER_HOUR);
        long minutes = milliseconds / MILLIS_PER_MINUTE;
        milliseconds = milliseconds - (minutes * MILLIS_PER_MINUTE);
        long seconds = milliseconds / MILLIS_PER_SECOND;
        milliseconds = milliseconds - (seconds * MILLIS_PER_SECOND);

        return String.format("%02d:%02d:%02d:%02d.%03d", days, hours, minutes,
                seconds, milliseconds);
    }

    public void reset() {
        startTime = System.currentTimeMillis();
        stopTime = 0;
    }
}
