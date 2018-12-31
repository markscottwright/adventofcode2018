package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.primitives.Ints;

public class Day4 {

    /**
     * I hold an individual log entry, with time, log entry type and guard id
     * 
     * @author wrightm
     *
     */
    static class Entry {
        static String TIME_PATTERN = "\\[([0-9]+)-([0-9]+)-([0-9]+) ([0-9]+):([0-9]+)\\]";
        static Pattern GUARD_BEGINS_SHIFT = Pattern
                .compile(TIME_PATTERN + ".*Guard #([0-9]+) begins shift");
        static Pattern GUARD_WAKES_UP = Pattern
                .compile(TIME_PATTERN + ".*wakes.up");
        static Pattern GUARD_FALLS_ALSEEP = Pattern
                .compile(TIME_PATTERN + ".*falls.asleep");

        public Entry(String guardId, EntryType type, int year, int month,
                int day, int hour, int minute) {
            this.guardId = guardId;
            this.type = type;
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
        }

        public String toString() {
            return "Entry [guardId=" + guardId + ", type=" + type + ", year="
                    + year + ", month=" + month + ", day=" + day + ", hour="
                    + hour + ", minute=" + minute + "]";
        }

        enum EntryType {
            start_shift, fall_asleep, wakes_up
        }

        final String guardId;
        final EntryType type;
        final int year;
        final int month;
        final int day;
        final int hour;
        final int minute;

        int lowerMinuteInRange() {
            if (hour == 0)
                return minute;
            else
                return 0;
        }

        // entry should be the next entry
        boolean sameDay(Entry e) {
            return year == e.year && month == e.month && day == e.day;
        }

        boolean previousDay(Entry e) {
            return year == e.year && month == e.month && day == e.day - 1;
        }

        public int upperMinuteInRange() {
            if (hour == 0)
                return minute;
            else
                return 60;
        }

        static Entry parseEntry(String line, String guardId)
                throws IOException {
            Matcher m;
            if ((m = GUARD_BEGINS_SHIFT.matcher(line)).matches()) {
                return new Entry(m.group(6), EntryType.start_shift,
                        Integer.parseInt(m.group(1)),
                        Integer.parseInt(m.group(2)),
                        Integer.parseInt(m.group(3)),
                        Integer.parseInt(m.group(4)),
                        Integer.parseInt(m.group(5)));
            } else if ((m = GUARD_FALLS_ALSEEP.matcher(line)).matches()) {
                return new Entry(guardId, EntryType.fall_asleep,
                        Integer.parseInt(m.group(1)),
                        Integer.parseInt(m.group(2)),
                        Integer.parseInt(m.group(3)),
                        Integer.parseInt(m.group(4)),
                        Integer.parseInt(m.group(5)));
            } else if ((m = GUARD_WAKES_UP.matcher(line)).matches()) {
                return new Entry(guardId, EntryType.wakes_up,
                        Integer.parseInt(m.group(1)),
                        Integer.parseInt(m.group(2)),
                        Integer.parseInt(m.group(3)),
                        Integer.parseInt(m.group(4)),
                        Integer.parseInt(m.group(5)));
            } else {
                throw new IOException(
                        "input file format error: (" + line + ")");
            }
        }
    }

    public static void main(String[] args) throws IOException {

        var lineIterator = Files.lines(Paths.get("data", "day4.txt"))
                .iterator();

        HashMap<String, int[]> guardSleepSchedule = buildPerGuardSchedule(
                lineIterator);

        // most asleep guard
        String maxId = "";
        int maxMinutes = 0;
        for (var id : guardSleepSchedule.keySet()) {
            int minutesAsleep = Arrays.stream(guardSleepSchedule.get(id)).sum();
            if (minutesAsleep > maxMinutes) {
                maxMinutes = minutesAsleep;
                maxId = id;
            }
        }

        // which minute was that guard asleep the most
        var mostAsleepGuardSchedule = guardSleepSchedule.get(maxId);
        int mostTimeSpendSleeping = Arrays.stream(mostAsleepGuardSchedule).max()
                .getAsInt();
        int mostSleepyMinute = Ints.indexOf(mostAsleepGuardSchedule,
                mostTimeSpendSleeping);

        System.out.println("guard id = " + maxId + ", sleepiest minute = "
                + mostSleepyMinute);
        System.out.println(maxId + "*" + mostSleepyMinute + "="
                + (Integer.parseInt(maxId) * mostSleepyMinute));
    }

    public static HashMap<String, int[]> buildPerGuardSchedule(
            Iterator<String> lineIterator) throws IOException {
        // go through each line and compare with previous, updating guards'
        // sleep times as you go. Data should be sorted.
        HashMap<String, int[]> guardSleepSchedule = new HashMap<>();
        String guardId = "unknown";
        Entry prevEntry = null;
        while (lineIterator.hasNext()) {
            final Entry entry = Entry.parseEntry(lineIterator.next(), guardId);
            if (prevEntry != null)
                updateSchedule(guardSleepSchedule, prevEntry, entry);
            guardId = entry.guardId;
            prevEntry = entry;
        }

        // handle terminal entry
        if (prevEntry.type == Entry.EntryType.fall_asleep) {
            incrementPerMinuteSleepCount(
                    guardSleepSchedule.get(prevEntry.guardId),
                    prevEntry.lowerMinuteInRange(), 60);
        }
        return guardSleepSchedule;
    }

    private static void updateSchedule(
            HashMap<String, int[]> guardSleepSchedule, Entry prevEntry,
            Entry entry) {

        if (!guardSleepSchedule.containsKey(prevEntry.guardId)) {
            guardSleepSchedule.put(prevEntry.guardId, new int[60]);
        }
        if (!guardSleepSchedule.containsKey(entry.guardId)) {
            guardSleepSchedule.put(entry.guardId, new int[60]);
        }

        if (entry.type == Entry.EntryType.wakes_up) {
            incrementPerMinuteSleepCount(guardSleepSchedule.get(entry.guardId),
                    prevEntry.lowerMinuteInRange(), entry.upperMinuteInRange());
        } else if (entry.type == Entry.EntryType.start_shift
                && prevEntry.type == Entry.EntryType.fall_asleep
                && !prevEntry.sameDay(entry)) {
            incrementPerMinuteSleepCount(guardSleepSchedule.get(entry.guardId),
                    prevEntry.lowerMinuteInRange(), 60);
        }
    }

    private static void incrementPerMinuteSleepCount(
            int[] perMinuteSleepSchedule, int startMinute, int endMinute) {
        for (int i = startMinute; i < endMinute; ++i) {
            perMinuteSleepSchedule[i]++;
        }

    }
}
