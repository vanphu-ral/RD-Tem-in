package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

public class WarehouseTransferSummaryDTO implements Serializable {

    private Long owtqDocEntry;
    private long numberOfRecord;
    private long totalQuantity;

    public WarehouseTransferSummaryDTO() {}

    public WarehouseTransferSummaryDTO(
        Long owtqDocEntry,
        long numberOfRecord,
        long totalQuantity
    ) {
        this.owtqDocEntry = owtqDocEntry;
        this.numberOfRecord = numberOfRecord;
        this.totalQuantity = totalQuantity;
    }

    public Long getOwtqDocEntry() {
        return owtqDocEntry;
    }

    public void setOwtqDocEntry(Long owtqDocEntry) {
        this.owtqDocEntry = owtqDocEntry;
    }

    public long getNumberOfRecord() {
        return numberOfRecord;
    }

    public void setNumberOfRecord(long numberOfRecord) {
        this.numberOfRecord = numberOfRecord;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseTransferSummaryDTO that = (WarehouseTransferSummaryDTO) o;
        return (
            numberOfRecord == that.numberOfRecord &&
            totalQuantity == that.totalQuantity &&
            Objects.equals(owtqDocEntry, that.owtqDocEntry)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(owtqDocEntry, numberOfRecord, totalQuantity);
    }

    @Override
    public String toString() {
        return (
            "WarehouseTransferSummaryDTO{" +
            "owtqDocEntry=" +
            owtqDocEntry +
            ", numberOfRecord=" +
            numberOfRecord +
            ", totalQuantity=" +
            totalQuantity +
            '}'
        );
    }
}
