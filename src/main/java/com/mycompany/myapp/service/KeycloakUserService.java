package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.UserSummary;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.BadRequestException;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserService {

    private final RealmResource realm;

    // Logger để in thông tin và lỗi
    private static final Logger logger = LoggerFactory.getLogger(
        KeycloakUserService.class
    );

    public KeycloakUserService(RealmResource realm) {
        this.realm = realm;
    }

    /**
     * Lấy tất cả users có ít nhất 1 trong các role truyền vào.
     */
    public List<UserSummary> getUsersByRoles(List<String> roleNames) {
        // 'seen' dùng để loại trùng ID
        Set<String> seen = new HashSet<>();
        // 'out' chứa kết quả cuối cùng
        List<UserSummary> out = new ArrayList<>();

        for (String roleName : roleNames) {
            try {
                // Lấy resource của role (realm-level)
                ClientResource clientRes = (ClientResource) realm
                    .clients()
                    .findAll()
                    .stream()
                    .filter(c -> c.getClientId().equals("tem_in"))
                    .findFirst()
                    .orElseThrow(() ->
                        new RuntimeException("Client not found")
                    );

                RoleResource rr = clientRes.roles().get(roleName);
                Set<UserRepresentation> members = rr.getRoleUserMembers();

                // Kiểm tra tồn tại role
                logger.info("Fetching users for role: {}", roleName);

                for (UserRepresentation u : members) {
                    // Nếu ID chưa seen, thêm vào kết quả
                    if (seen.add(u.getId())) {
                        UserSummary dto = new UserSummary();
                        dto.setId(u.getId());
                        dto.setUsername(u.getUsername());
                        dto.setEmail(u.getEmail());
                        out.add(dto);
                    }
                }
            } catch (BadRequestException bre) {
                // Role không tồn tại hoặc request sai
                logger.error(
                    "BadRequest for role '{}': {}",
                    roleName,
                    bre.getResponse().readEntity(String.class)
                );
            } catch (Exception ex) {
                // Bắt mọi lỗi bất ngờ khác
                logger.error("Error fetching users for role " + roleName, ex);
            }
        }

        return out;
    }
}
