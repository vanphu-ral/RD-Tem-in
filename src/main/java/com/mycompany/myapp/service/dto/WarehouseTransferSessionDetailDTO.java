package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class WarehouseTransferSessionDetailDTO implements Serializable {

    private WarehouseTransferSessionDTO session;
    private List<OwtqWarehouseTransferSessionDTO> owtqSessions;
    private List<WarehouseTransferDetailDTO> details;
    private List<ScannedPalletDTO> scannedPallets;

    public WarehouseTransferSessionDTO getSession() {
        return session;
    }

    public void setSession(WarehouseTransferSessionDTO session) {
        this.session = session;
    }

    public List<OwtqWarehouseTransferSessionDTO> getOwtqSessions() {
        return owtqSessions;
    }

    public void setOwtqSessions(
        List<OwtqWarehouseTransferSessionDTO> owtqSessions
    ) {
        this.owtqSessions = owtqSessions;
    }

    public List<WarehouseTransferDetailDTO> getDetails() {
        return details;
    }

    public List<ScannedPalletDTO> getScannedPallets() {
        return scannedPallets;
    }

    // public List<ScannedPalletDTO> getScannedPallets() {
    //     return scannedPallets;
    // }

    public void setDetails(List<WarehouseTransferDetailDTO> details) {
        this.details = details;
    }

    public void setScannedPallets(List<ScannedPalletDTO> scannedPallets) {
        this.scannedPallets = scannedPallets;
    }

    // public void setScannedPallets(List<ScannedPalletDTO> scannedPallets) {
    //     this.scannedPallets = scannedPallets;
    // }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseTransferSessionDetailDTO that =
            (WarehouseTransferSessionDetailDTO) o;
        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session);
    }
}
