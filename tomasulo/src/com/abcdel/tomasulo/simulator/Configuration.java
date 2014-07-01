package com.abcdel.tomasulo.simulator;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Configuration {

    public static Configuration fromFile(File file) throws FileMalformedException {
        try {
            List<String> configs = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            return new Configuration(configs);
        } catch (Exception e) {
            FileMalformedException error = new FileMalformedException();
            error.initCause(e);
            throw error;
        }
    }

    public static Configuration defaults() {
        return new Configuration();
    }

    private static final int L1_SIZE = 64 * 1024 / 4;
    private static final int L1_BLOCKS_PER_SET = 2;
    private static final int L2_SIZE = 1 * 1024 * 1024 / 4;
    private static final int L2_BLOCKS_PER_SET = 16;

    public Map<FunctionalUnit.Type, FunctionalUnitSpecs> functionalUnits = Maps.newHashMap(
            new ImmutableMap.Builder<FunctionalUnit.Type, FunctionalUnitSpecs>()
                    .put(FunctionalUnit.Type.ADD, new FunctionalUnitSpecs(4, 4))
                    .put(FunctionalUnit.Type.MULT, new FunctionalUnitSpecs(2, 2))
                    .put(FunctionalUnit.Type.LOAD, new FunctionalUnitSpecs(2, 2))
                    .put(FunctionalUnit.Type.BRANCH, new FunctionalUnitSpecs(1, 1))
                    .build());
    public CacheSpecs l1 = new CacheSpecs(L1_SIZE, L1_BLOCKS_PER_SET);
    public CacheSpecs l2 = new CacheSpecs(L2_SIZE, L2_BLOCKS_PER_SET);
    public int constantMemoryAccessTime = 4;
    public boolean useCache = true;
    public int registers = 32;
    public int track = 10;
    public int interval = 10;

    private Configuration() {
        /* Defaults */
    }

    private Configuration(List<String> configs) {
        useCache = false;
        List<String> fuTypes = getFuTypesAsStrings();
        for (String config : configs) {
            config = config.replaceFirst("#.*$", "").trim();
            if (config.isEmpty()) continue;
            config = config.toUpperCase();
            Scanner scanner = new Scanner(config).useDelimiter("\\s+");
            String id = scanner.next();
            if (fuTypes.contains(id)) {
                FunctionalUnit.Type fuType = FunctionalUnit.Type.valueOf(id);
                int fus = scanner.nextInt();
                int rss = scanner.nextInt();
                functionalUnits.put(fuType, new FunctionalUnitSpecs(fus, rss));
            } else {
                switch (id) {
                    case "L1":
                        l1.size = scanner.nextInt();
                        l1.blocksPerSet = scanner.nextInt();
                        break;
                    case "L2":
                        l2.size = scanner.nextInt();
                        l2.blocksPerSet = scanner.nextInt();
                        break;
                    case "MEMORY":
                        constantMemoryAccessTime = scanner.nextInt();
                        break;
                    case "USE":
                        if (scanner.next().equals("CACHE")) {
                            useCache = true;
                        }
                        break;
                    case "REGISTERS":
                        registers = scanner.nextInt();
                        break;
                    case "TRACK":
                        track = scanner.nextInt();
                        break;
                    case "INTERVAL":
                        interval = scanner.nextInt();
                        break;
                }
            }
        }
    }

    private static List<String> getFuTypesAsStrings() {
        List<FunctionalUnit.Type> fuTypes = Arrays.asList(FunctionalUnit.Type.values());
        return Lists.transform(fuTypes, Functions.toStringFunction());
    }


    public static class FunctionalUnitSpecs {
        public int functionalUnits;
        public int reserveStations;

        public FunctionalUnitSpecs(int functionalUnits, int reserveStations) {
            this.functionalUnits = functionalUnits;
            this.reserveStations = reserveStations;
        }
    }

    public static class CacheSpecs {
        public int size;
        public int blocksPerSet;

        public CacheSpecs(int size, int blocksPerSet) {
            this.size = size;
            this.blocksPerSet = blocksPerSet;
        }
    }


    public static class FileMalformedException extends Exception {
        /* Empty */
    }
}
