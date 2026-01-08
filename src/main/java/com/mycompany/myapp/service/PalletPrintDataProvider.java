package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.PrintPalletDTO;
import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * Data provider for JasperReports pallet printing templates.
 * Generates table data for both regular and Bán thành phẩm product types.
 */
public class PalletPrintDataProvider implements JRDataSource {

    private final List<RowData> data;
    private int currentIndex = -1;

    public PalletPrintDataProvider(PrintPalletDTO pallet, boolean isBtpLayout) {
        this.data = new ArrayList<>();

        if (isBtpLayout) {
            generateBtpData(pallet);
        } else {
            generateRegularData(pallet);
        }
    }

    private void generateBtpData(PrintPalletDTO pallet) {
        // Row 1: Tên sản phẩm
        addRow("Tên sản phẩm", pallet.getTenSanPham(), 1);

        // Row 2: Serial pallet | Mã Sp
        addRow("Serial pallet", pallet.getSerialPallet(), 1);
        addRow(
            "Mã Sp",
            pallet.getMaSAP() != null
                ? pallet.getMaSAP()
                : pallet.getProductCode(),
            1
        );

        // Row 3: Version | Khách hàng
        addRow(
            "Version",
            pallet.getVersion() != null ? pallet.getVersion() : "N/A",
            1
        );
        addRow("Khách hàng", pallet.getKhachHang(), 1);

        // Row 4: QDSX | Kế hoạch SX
        addRow("QDSX", pallet.getSoQdsx(), 1);
        addRow(
            "Kế hoạch SX",
            pallet.getWoId() != null
                ? pallet.getWoId()
                : pallet.getSerialPallet(),
            1
        );

        // Row 5: Ngành | Tổ
        addRow(
            "Ngành",
            pallet.getNganh() != null ? pallet.getNganh() : pallet.getLed2(),
            1
        );
        addRow(
            "Tổ",
            pallet.getTo() != null ? pallet.getTo() : pallet.getLpl2(),
            1
        );

        // Row 6: MFG Date (ngày sx) | ERP WO
        addRow("MFG Date<br />(ngày sx)", pallet.getNgaySanXuat(), 1);
        addRow(
            "ERP WO",
            pallet.getMaLenhSanXuat() != null
                ? pallet.getMaLenhSanXuat()
                : "N/A",
            1
        );

        // Row 7: Thứ tự pallet | Sl sp/pallet
        addRow(
            "Thứ tự pallet",
            pallet.getThuTuGiaPallet() != null
                ? pallet.getThuTuGiaPallet().toString()
                : "",
            1
        );
        addRow(
            "Sl sp/pallet",
            pallet.getTotalProductsOnPallet() != null
                ? pallet.getTotalProductsOnPallet().toString()
                : "",
            1
        );

        // Row 8: Sl thùng/pallet | Sl Sp/thùng
        addRow("Sl thùng/pallet", pallet.getSoLuongBaoNgoaiThungGiaPallet(), 1);
        addRow(
            "Sl Sp/thùng",
            pallet.getSoLuongCaiDatPallet() != null
                ? pallet.getSoLuongCaiDatPallet().toString()
                : "",
            1
        );

        // Row 9: Người KT | KQKT
        addRow("Người KT", pallet.getNguoiKiemTra(), 1);
        addRow("KQKT", pallet.getKetQuaKiemTra(), 1);

        // Row 10: Ghi chú (span 2 columns)
        addRow("Ghi chú", pallet.getNote(), 2);
    }

    private void generateRegularData(PrintPalletDTO pallet) {
        // Row 1: Khách hàng
        addRow("Khách hàng", pallet.getKhachHang(), 1);

        // Row 2: Serial pallet
        addRow("Serial pallet", pallet.getSerialPallet(), 1);

        // Row 3: Tên sản phẩm
        addRow("Tên sản phẩm", pallet.getTenSanPham(), 1);

        // Row 4: PO Number
        addRow("PO Number", pallet.getPoNumber(), 1);

        // Row 5: Item No/SKU | Ngành
        addRow("Item No/SKU", pallet.getItemNoSku(), 1);
        addRow("Ngành", pallet.getLed2(), 1);

        // Row 6: Số QDSX | Tổ
        addRow("Số QDSX", pallet.getSoQdsx(), 1);
        addRow("Tổ", pallet.getTo(), 1);

        // Row 7: Ngày sản xuất | Date Code
        addRow("Ngày sản xuất", pallet.getNgaySanXuat(), 1);
        addRow("Date Code", pallet.getDateCode(), 1);

        // Row 8: Số lượng (sản phẩm/pallet) | SL sản phẩm/Thùng
        addRow(
            "Số lượng<br />(sản phẩm/pallet)",
            pallet.getSoLuongCaiDatPallet() != null
                ? pallet.getSoLuongCaiDatPallet().toString()
                : "",
            1
        );
        addRow(
            "SL SP/Thùng",
            pallet.getSlThung() != null ? pallet.getSlThung().toString() : "",
            1
        );

        // Row 9: Số lượng (thùng/pallet) | Thứ tự giá pallet
        addRow(
            "Số lượng<br />(thùng/pallet)",
            pallet.getSoLuongBaoNgoaiThungGiaPallet(),
            1
        );
        addRow(
            "Thứ tự giá",
            pallet.getThuTuGiaPallet() != null
                ? pallet.getThuTuGiaPallet().toString()
                : "",
            1
        );

        // Row 10: Người kiểm tra | Kết quả kiểm tra
        addRow("Người kiểm tra", pallet.getNguoiKiemTra(), 1);
        addRow("KQ kiểm tra", pallet.getKetQuaKiemTra(), 1);

        // Row 11: Ghi chú (span 2 columns)
        addRow("Ghi chú", pallet.getNote(), 2);
    }

    private void addRow(String label, String value, int colspan) {
        data.add(new RowData(label, value, colspan));
    }

    @Override
    public boolean next() throws JRException {
        currentIndex++;
        return currentIndex < data.size();
    }

    @Override
    public Object getFieldValue(JRField field) throws JRException {
        if (currentIndex < 0 || currentIndex >= data.size()) {
            return null;
        }

        RowData rowData = data.get(currentIndex);

        switch (field.getName()) {
            case "label":
                return rowData.getLabel();
            case "value":
                return rowData.getValue();
            case "colspan":
                return rowData.getColspan();
            default:
                return null;
        }
    }

    private static class RowData {

        private final String label;
        private final String value;
        private final int colspan;

        public RowData(String label, String value, int colspan) {
            this.label = label;
            this.value = value != null ? value : "";
            this.colspan = colspan;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public int getColspan() {
            return colspan;
        }
    }
}
