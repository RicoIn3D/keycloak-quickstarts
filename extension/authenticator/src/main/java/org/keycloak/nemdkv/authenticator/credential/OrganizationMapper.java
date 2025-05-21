package org.keycloak.nemdkv.authenticator.credential;

import org.keycloak.models.OrganizationModel;
import org.keycloak.models.OrganizationDomainModel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrganizationMapper {

    public static OrganizationRepresentation toRepresentation(OrganizationModel model) {
        if (model == null) {
            return null;
        }
        List<String> domains = model.getDomains() != null
                ? model.getDomains()
                .map(OrganizationDomainModel::getName).collect(Collectors.toList())
                : null;

        return new OrganizationRepresentation(
                model.getId(),
                model.getName(),
                model.getAlias(),
                domains, // returns List<String>
                model.getAttributes() // returns Map<String, List<String>>
        );
    }
}