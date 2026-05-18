package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Wrapper DTO for pallet print ND request that includes paper size configuration.
 * Used specifically for pallet_A4_ND.jasper template.
 */
public class PalletPrintNdRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @NotEmpty
    private List<PalletNdDTO> pallets;

    // Default to A4 if not provided
    private String paperSize = "A4";

    // Getters and Setters

    public List<PalletNdDTO> getPallets() {
        return pallets;
    }

    public void setPallets(List<PalletNdDTO> pallets) {
        this.pallets = pallets;
    }

    public String getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }
}
