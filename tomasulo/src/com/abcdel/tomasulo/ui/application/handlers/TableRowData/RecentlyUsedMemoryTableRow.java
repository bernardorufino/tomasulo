package com.abcdel.tomasulo.ui.application.handlers.TableRowData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecentlyUsedMemoryTableRow {

    private StringProperty mAddress;
    private StringProperty mValue;

    public RecentlyUsedMemoryTableRow(String address, String value) {
        mAddress = new SimpleStringProperty(address);
        mValue = new SimpleStringProperty(value);
    }

    public StringProperty mAddressProperty() {
        return mAddress;
    }

    public StringProperty mValueProperty() {
        return mValue;
    }

    public static class Builder {

        private Map<Integer, Integer> mRecentlyUsedMemory;

        public Builder from(Map<Integer, Integer> recentlyUsedMemory) {
            mRecentlyUsedMemory = recentlyUsedMemory;
            return this;
        }

        public List<RecentlyUsedMemoryTableRow> buildAll() {
            List<RecentlyUsedMemoryTableRow> recentlyUsedMemoryRows = new ArrayList<>();
            if (mRecentlyUsedMemory != null) {
                for (Map.Entry entry : mRecentlyUsedMemory.entrySet()) {
                    String address = String.valueOf(entry.getKey());
                    String value = String.valueOf(entry.getValue());
                    recentlyUsedMemoryRows.add(new RecentlyUsedMemoryTableRow(address, value));
                }
            }
            return recentlyUsedMemoryRows;
        }
    }
}
