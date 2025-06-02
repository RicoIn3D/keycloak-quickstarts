package org.keycloak.nemdkv.authenticator.credential;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class OrganizationWrapper {

    @JsonProperty("organization")
    private Map<String, SimpleOrganization> organization;

    public OrganizationWrapper(Map<String, SimpleOrganization> organization) {
        this.organization = organization;
    }

    public Map<String, SimpleOrganization> getOrganization() {
        return organization;
    }

    public void setOrganization(Map<String, SimpleOrganization> organization) {
        this.organization = organization;
    }

    public static class SimpleOrganization {
        @JsonProperty("id")
        private String id;

        public SimpleOrganization(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

