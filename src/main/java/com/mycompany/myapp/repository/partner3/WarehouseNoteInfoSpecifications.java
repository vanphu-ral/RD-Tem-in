package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.WarehouseNoteInfo;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for WarehouseNoteInfo entity queries.
 */
public class WarehouseNoteInfoSpecifications {

    public static Specification<WarehouseNoteInfo> hasSapCode(String sapCode) {
        return (root, query, criteriaBuilder) -> {
            if (sapCode == null || sapCode.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("sapCode"), sapCode);
        };
    }

    public static Specification<WarehouseNoteInfo> hasSapName(String sapName) {
        return (root, query, criteriaBuilder) -> {
            if (sapName == null || sapName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                root.get("sapName"),
                "%" + sapName + "%"
            );
        };
    }

    public static Specification<WarehouseNoteInfo> hasWorkOrderCode(
        String workOrderCode
    ) {
        return (root, query, criteriaBuilder) -> {
            if (workOrderCode == null || workOrderCode.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                root.get("workOrderCode"),
                "%" + workOrderCode + "%"
            );
        };
    }

    public static Specification<WarehouseNoteInfo> hasVersion(String version) {
        return (root, query, criteriaBuilder) -> {
            if (version == null || version.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("version"), version);
        };
    }

    public static Specification<WarehouseNoteInfo> hasStorageCode(
        String storageCode
    ) {
        return (root, query, criteriaBuilder) -> {
            if (storageCode == null || storageCode.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("storageCode"), storageCode);
        };
    }

    public static Specification<WarehouseNoteInfo> hasCreateBy(
        String createBy
    ) {
        return (root, query, criteriaBuilder) -> {
            if (createBy == null || createBy.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("createBy"), createBy);
        };
    }

    public static Specification<WarehouseNoteInfo> hasEntryTime(
        Instant entryTime
    ) {
        return (root, query, criteriaBuilder) -> {
            if (entryTime == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("entryTime"), entryTime);
        };
    }

    public static Specification<WarehouseNoteInfo> hasTrangThai(
        String trangThai
    ) {
        return (root, query, criteriaBuilder) -> {
            if (trangThai == null || trangThai.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("trangThai"), trangThai);
        };
    }

    public static Specification<WarehouseNoteInfo> hasComment(String comment) {
        return (root, query, criteriaBuilder) -> {
            if (comment == null || comment.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                root.get("comment"),
                "%" + comment + "%"
            );
        };
    }

    public static Specification<WarehouseNoteInfo> hasTimeUpdate(
        Instant timeUpdate
    ) {
        return (root, query, criteriaBuilder) -> {
            if (timeUpdate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("timeUpdate"), timeUpdate);
        };
    }

    public static Specification<WarehouseNoteInfo> hasGroupName(
        String groupName
    ) {
        return (root, query, criteriaBuilder) -> {
            if (groupName == null || groupName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("groupName"), groupName);
        };
    }

    public static Specification<WarehouseNoteInfo> hasComment2(
        String comment2
    ) {
        return (root, query, criteriaBuilder) -> {
            if (comment2 == null || comment2.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                root.get("comment2"),
                "%" + comment2 + "%"
            );
        };
    }

    public static Specification<WarehouseNoteInfo> hasApproverBy(
        String approverBy
    ) {
        return (root, query, criteriaBuilder) -> {
            if (approverBy == null || approverBy.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("approverBy"), approverBy);
        };
    }

    public static Specification<WarehouseNoteInfo> hasBranch(String branch) {
        return (root, query, criteriaBuilder) -> {
            if (branch == null || branch.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("branch"), branch);
        };
    }

    public static Specification<WarehouseNoteInfo> hasProductType(
        String productType
    ) {
        return (root, query, criteriaBuilder) -> {
            if (productType == null || productType.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("productType"), productType);
        };
    }

    public static Specification<WarehouseNoteInfo> hasLotNumber(
        String lotNumber
    ) {
        return (root, query, criteriaBuilder) -> {
            if (lotNumber == null || lotNumber.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("lotNumber"), lotNumber);
        };
    }

    public static Specification<WarehouseNoteInfo> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isNull(root.get("deletedBy"));
    }
}
