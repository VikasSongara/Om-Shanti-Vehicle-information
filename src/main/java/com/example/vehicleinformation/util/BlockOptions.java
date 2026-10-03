package com.example.vehicleinformation.util;

import java.util.List;

public final class BlockOptions {

    public static final List<String> VALUES = List.of("A", "B", "C", "D", "E", "F", "G");

    public static final String PATTERN = "^[A-G]$";

    private BlockOptions() {
    }

    public static boolean isValid(String blockNo) {
        return blockNo != null && VALUES.contains(blockNo.trim().toUpperCase());
    }
}
