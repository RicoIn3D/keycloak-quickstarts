package org.keycloak.nemdkv.authenticator.credential;

import org.keycloak.models.OrganizationModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrganizationMapper {

    public static OrganizationRepresentation toRepresentation(OrganizationModel model) {
        if (model == null) {
            return null;
        }

        return new OrganizationRepresentation(
                model.getId(),
                model.getName(),
                model.getAlias(),
                Collections.emptyList(), // Ignoring domains - returns List<String> empty
                model.getAttributes() // returns Map<String, List<String>>
        );
    }
    public static OrganizationWrapper toWrappedRepresentation(OrganizationModel model) {
        if (model == null) {
            return null;
        }

        Map<String, OrganizationWrapper.SimpleOrganization> map =
                Map.of(model.getAlias(), new OrganizationWrapper.SimpleOrganization(model.getId()));

        return new OrganizationWrapper(map);
    }
    public static Map<String, Map<String, Object>> toAttributeFormat(OrganizationModel model) {
        if (model == null) {
            return Map.of();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", model.getId());

        // Add all attributes (which is Map<String, List<String>>)
        if (model.getAttributes() != null) {
            result.putAll(model.getAttributes());
        }

        return Map.of(model.getAlias(), result);
    }
}