package com.marula.cache;

import com.marula.cache.memory.Memory;
import com.marula.cache.memory.TwoLevelCachedMemory;
import com.marula.cache.utils.HistoryUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* TODO: Remove from dirties when evicted from CacheUnit */
public class TestSuite {

    private static final int L1_SIZE = 64 * 1024;
    private static final int L1_BLOCKS_PER_SET = 2;
    private static final int L2_SIZE = 1 * 1024 * 1024;
    private static final int L2_BLOCKS_PER_SET = 16;
    //private static final String TESTS_FOLDER = "src" + File.separator + "tests";
    private static final String TESTS_FOLDER = "tests";
    private static final boolean VERBOSE = false;
    private static final boolean ALLOW_DUMPS = false; // If requires too much memory, turn off

    public static void main(String[] args) throws IOException {
        test(0);
//        testSuite();
    }

    private static void test(int n) throws IOException {
        TwoLevelCachedMemory memory = newMemory().setAllowDumps(ALLOW_DUMPS);
        testCase(memory, new File(TESTS_FOLDER + File.separator + n + ".test.txt"), false);
        List<TwoLevelCachedMemory.LogEntry> history = memory.getHistory();
        System.out.println(HistoryUtils.summary(history));
        System.out.println();
//        System.out.println(HistoryUtils.prettyHistory(history, VERBOSE));
    }

    private static void testSuite() throws IOException {
        int total = 0;
        int passed = 0;
        for (Path file : Files.newDirectoryStream(Paths.get(TESTS_FOLDER))) {
            System.out.println();
            if (file.getFileName().toString().matches(".*?\\.test\\.txt$")) {
                Boolean ans = reportTestCase(file.toFile());
                if (ans != null) {
                    total = total + 1;
                    if (ans) passed = passed + 1;
                }
            }
        }
        System.out.println("==== passed: " + passed + " / " + total);
    }

    private static Boolean reportTestCase(File file) throws IOException {
        TwoLevelCachedMemory memory = newMemory();
        return testCase(memory, file, true);
    }

    private static TwoLevelCachedMemory newMemory() {
        return new TwoLevelCachedMemory(L1_SIZE, L1_BLOCKS_PER_SET, L2_SIZE, L2_BLOCKS_PER_SET);
    }

    private static Boolean testCase(TwoLevelCachedMemory memory, File file, boolean report) throws IOException {
        prepare(memory);
        BufferedReader br = new BufferedReader(new FileReader(file));
        int expectedTotalTime = -1;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (expectedTotalTime == -1) {
                Pattern pattern = Pattern.compile("^\\s*;=(.*)$");
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    expectedTotalTime = Integer.parseInt(matcher.group(1).trim());
                    continue;
                }
            }
            if (line.matches("^\\s*;.*$")) continue;
            String[] entry = line.trim().split("\\s+");
            if (entry.length != 2) continue;
            int address = (int) Long.parseLong(entry[0].toUpperCase(), 16);
            char op = entry[1].trim().toUpperCase().charAt(0);
            switch (op) {
                case 'R':
                    memory.read(address);
                    break;
                case 'W':
                    memory.write(address, 0);
                    break;
                default:
                    throw new AssertionError();
            }
        }
        br.close();
        if (report) {
            System.out.print("file " + file.toString() + ": ");
            if (expectedTotalTime == -1) {
                System.out.println("\n-- no expected total time found on file");
                return null;
            } else {
                HistoryUtils.Statistics stats = HistoryUtils.statistics(memory.getHistory());
                int totalTime = stats.totalTime;
                boolean passed = (totalTime == expectedTotalTime);
                System.out.println((passed) ? "PASSED" : "FAILED");
                System.out.println("-- expected total time: " + expectedTotalTime + " clocks");
                System.out.println("--   actual total time: " + totalTime + " clocks");
                return passed;
            }
        } else {
            return null;
        }
    }

    private static void prepare(Memory memory) {
        /* Empty */
    }
}
