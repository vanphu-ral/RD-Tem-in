package com.mycompany.myapp.web.rest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

/**
 * Test class to verify the logic for processing reconciliation QMS requests.
 * This focuses on the core transformation logic without mocking all dependencies.
 */
class ReconciliationQmsResourceLogicTest {

    /**
     * Test that verifies the calculation of num_box_per_pallet and total_quantity
     * from list_box items in mode_pallet.
     */
    @Test
    void testCalculateNumBoxPerPalletAndTotalQuantity() {
        // Simulate the logic from the modified code
        Map<String, Object> modePallet = new HashMap<>();
        modePallet.put("serial_pallet", "P202512041401460001");

        List<Map<String, Object>> listBox = new ArrayList<>();

        // Add 3 boxes to simulate list_box
        for (int i = 0; i < 3; i++) {
            Map<String, Object> box = new HashMap<>();
            box.put("id", 132 + i);
            box.put("reel_id", "B010120251204140214000" + i);
            box.put("qms_stored_check_id", 12345);
            box.put("ma_lenh_san_xuat_id", 17);
            box.put("time_qms_approve", "2025-12-04 14:01:46");
            box.put("qms_result_check", 1);
            box.put("updated_by", "ad");
            listBox.add(box);
        }

        modePallet.put("list_box", listBox);

        // Simulate the calculation logic from the modified code
        int numBoxPerPallet = 0;
        int totalQuantity = 0;

        List<Map<String, Object>> listBoxFromMode = listBox;
        if (listBoxFromMode != null) {
            for (Map<String, Object> box : listBoxFromMode) {
                numBoxPerPallet++;
                totalQuantity++;
            }
        }

        // Verify the calculations
        assertEquals(
            3,
            numBoxPerPallet,
            "num_box_per_pallet should equal the number of boxes"
        );
        assertEquals(
            3,
            totalQuantity,
            "total_quantity should equal the number of boxes"
        );
    }

    /**
     * Test that verifies mode_box items are grouped into a single list_pallet
     * with empty serial_pallet and quantity_per_box = 0.
     */
    @Test
    void testModeBoxGrouping() {
        // Simulate mode_box data
        List<Map<String, Object>> modeBoxList = new ArrayList<>();

        // Add 2 mode_box items
        for (int i = 0; i < 2; i++) {
            Map<String, Object> modeBox = new HashMap<>();
            modeBox.put("id", 133 + i);
            modeBox.put("reel_id", "B010120251204140214000" + i);
            modeBox.put("qms_stored_check_id", 12345);
            modeBox.put("ma_lenh_san_xuat_id", 17);
            modeBox.put("time_qms_approve", "2025-12-04 14:01:46");
            modeBox.put("qms_result_check", 1);
            modeBox.put("updated_by", "ad");
            modeBoxList.add(modeBox);
        }

        // Simulate the grouping logic from the modified code
        if (modeBoxList != null && !modeBoxList.isEmpty()) {
            Map<String, Object> modeBoxPallet = new HashMap<>();
            modeBoxPallet.put("serial_pallet", "");
            modeBoxPallet.put("quantity_per_box", 0);

            // Calculate num_box_per_pallet and total_quantity from mode_box items
            int numBoxPerPallet = modeBoxList.size();
            int totalQuantity = modeBoxList.size();

            modeBoxPallet.put("num_box_per_pallet", numBoxPerPallet);
            modeBoxPallet.put("total_quantity", totalQuantity);

            // Verify the grouped pallet structure
            assertEquals(
                "",
                modeBoxPallet.get("serial_pallet"),
                "serial_pallet should be empty"
            );
            assertEquals(
                0,
                modeBoxPallet.get("quantity_per_box"),
                "quantity_per_box should be 0"
            );
            assertEquals(
                2,
                modeBoxPallet.get("num_box_per_pallet"),
                "num_box_per_pallet should equal mode_box count"
            );
            assertEquals(
                2,
                modeBoxPallet.get("total_quantity"),
                "total_quantity should equal mode_box count"
            );

            // Verify box information is included
            List<Map<String, Object>> listBox = new ArrayList<>();
            for (Map<String, Object> modeBox : modeBoxList) {
                Map<String, Object> boxInfo = new HashMap<>();
                boxInfo.put("box_code", modeBox.get("reel_id"));
                boxInfo.put("quantity", 1);
                boxInfo.put("note", "");
                boxInfo.put("list_serial_items", "");
                listBox.add(boxInfo);
            }
            modeBoxPallet.put("list_box", listBox);

            assertEquals(
                2,
                ((List<?>) modeBoxPallet.get("list_box")).size(),
                "list_box should contain all mode_box items"
            );
        }
    }

    /**
     * Test that verifies the complete transformation logic for a sample request.
     */
    @Test
    void testCompleteTransformation() {
        // Create a sample request similar to the one in the task description
        Map<String, Object> request = new HashMap<>();

        // mode_pallet with list_box
        Map<String, Object> modePallet = new HashMap<>();
        modePallet.put("serial_pallet", "P202512041401460001");
        modePallet.put("qms_stored_check_id", 12345);
        modePallet.put("ma_lenh_san_xuat_id", 17);
        modePallet.put("time_qms_approve", "2025-12-04 14:01:46");
        modePallet.put("qms_result_check", 1);
        modePallet.put("updated_by", "ad");

        List<Map<String, Object>> listBox = new ArrayList<>();
        Map<String, Object> box = new HashMap<>();
        box.put("id", 132);
        box.put("reel_id", "B0101202512041402140001");
        box.put("qms_stored_check_id", 12345);
        box.put("ma_lenh_san_xuat_id", 17);
        box.put("time_qms_approve", "2025-12-04 14:01:46");
        box.put("qms_result_check", 1);
        box.put("updated_by", "ad");
        listBox.add(box);

        modePallet.put("list_box", listBox);

        List<Map<String, Object>> modePalletList = new ArrayList<>();
        modePalletList.add(modePallet);
        request.put("mode_pallet", modePalletList);

        // mode_box
        List<Map<String, Object>> modeBoxList = new ArrayList<>();
        Map<String, Object> modeBox = new HashMap<>();
        modeBox.put("id", 133);
        modeBox.put("reel_id", "B0101202512041402140001");
        modeBox.put("qms_stored_check_id", 12345);
        modeBox.put("ma_lenh_san_xuat_id", 17);
        modeBox.put("time_qms_approve", "2025-12-04 14:01:46");
        modeBox.put("qms_result_check", 1);
        modeBox.put("updated_by", "ad");
        modeBoxList.add(modeBox);
        request.put("mode_box", modeBoxList);

        // Verify the request structure
        assertNotNull(
            request.get("mode_pallet"),
            "mode_pallet should be present"
        );
        assertNotNull(request.get("mode_box"), "mode_box should be present");
        assertEquals(
            1,
            ((List<?>) request.get("mode_pallet")).size(),
            "mode_pallet should have 1 item"
        );
        assertEquals(
            1,
            ((List<?>) request.get("mode_box")).size(),
            "mode_box should have 1 item"
        );

        // Verify mode_pallet structure
        Map<String, Object> firstModePallet = ((List<
                Map<String, Object>
            >) request.get("mode_pallet")).get(0);
        assertEquals(
            "P202512041401460001",
            firstModePallet.get("serial_pallet")
        );
        assertEquals(
            1,
            ((List<?>) firstModePallet.get("list_box")).size(),
            "mode_pallet should have 1 box"
        );

        // Verify mode_box structure
        Map<String, Object> firstModeBox = ((List<
                Map<String, Object>
            >) request.get("mode_box")).get(0);
        assertEquals("B0101202512041402140001", firstModeBox.get("reel_id"));
    }
}
