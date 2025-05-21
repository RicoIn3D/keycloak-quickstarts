package org.keycloak.nemdkv.authenticator.credential;

import org.keycloak.models.OrganizationModel;

import java.util.List;

public class OrganizationMapper {

    public static OrganizationRepresentation toRepresentation(OrganizationModel model) {
        if (model == null) {
            return null;
        }

        return new OrganizationRepresentation(
                model.getId(),
                model.getName(),
                model.getAlias(),
                (List<String>) model.getDomains(), // returns List<String>
                model.getAttributes() // returns Map<String, List<String>>
        );
    }
}