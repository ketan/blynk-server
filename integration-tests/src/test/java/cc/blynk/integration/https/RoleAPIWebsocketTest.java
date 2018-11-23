package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.api.http.dashboard.dto.OrganizationDTO;
import cc.blynk.server.api.http.dashboard.dto.RoleDTO;
import cc.blynk.server.core.model.web.Organization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RoleAPIWebsocketTest extends APIBaseTest {

    @Test
    public void createRole() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);

        RoleDTO roleDTO = new RoleDTO(-1, "My New Role", 0, 0);
        client.createRole(admin.orgId, roleDTO);
        roleDTO = client.parseRoleDTO(1);
        assertNotNull(roleDTO);
        assertEquals(4, roleDTO.id);
        assertEquals("My New Role", roleDTO.name);
        assertEquals(0, roleDTO.permissions1);
        assertEquals(0, roleDTO.permissions2);
    }

    @Test
    public void updateRole() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);

        RoleDTO roleDTO = new RoleDTO(3, "My Updated Role", 0, 0);
        client.updateRole(admin.orgId, roleDTO);
        roleDTO = client.parseRoleDTO(1);
        assertNotNull(roleDTO);
        assertEquals(3, roleDTO.id);
        assertEquals("My Updated Role", roleDTO.name);
        assertEquals(0, roleDTO.permissions1);
        assertEquals(0, roleDTO.permissions2);
    }

    @Test
    public void deleteRole() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);

        client.deleteRole(admin.orgId, 3);
        client.verifyResult(ok(1));
    }

    @Test
    public void getRoles() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);

        client.getRoles(admin.orgId);
        RoleDTO[] roleDTOs = client.parseRoleDTOs(1);
        assertNotNull(roleDTOs);
        assertEquals(0, roleDTOs[0].id);
        assertEquals(1, roleDTOs[1].id);
        assertEquals(2, roleDTOs[2].id);
        assertEquals(3, roleDTOs[3].id);
    }

    @Test
    public void getRole() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);

        client.getRole(admin.orgId, 1);
        RoleDTO roleDTOs = client.parseRoleDTO(1);
        assertNotNull(roleDTOs);
        assertEquals(1, roleDTOs.id);
        assertEquals("Admin", roleDTOs.name);
    }

    @Test
    public void checkOnlyDefaultRolesAreCreated() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);

        int orgId = admin.orgId;
        RoleDTO roleDTO = new RoleDTO(3, "My Updated Role", 0, 0);
        client.updateRole(orgId, roleDTO);
        roleDTO = client.parseRoleDTO(1);
        assertNotNull(roleDTO);
        assertEquals(3, roleDTO.id);
        assertEquals("My Updated Role", roleDTO.name);
        assertEquals(0, roleDTO.permissions1);
        assertEquals(0, roleDTO.permissions2);

        Organization subOrg = new Organization("SubOrg", "Europe/Kiev", "/static/logo.png", true, orgId);
        client.createOrganization(orgId, subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(orgId, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);
        assertEquals(3, subOrgDTO.roles[2].id);
        assertEquals("User", subOrgDTO.roles[2].name);
    }
}
