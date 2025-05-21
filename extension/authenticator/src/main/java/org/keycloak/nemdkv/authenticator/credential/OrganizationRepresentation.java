package org.keycloak.nemdkv.authenticator.credential;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationRepresentation {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("domains")
    private List<String> domains;

    @JsonProperty("attributes")
    private Map<String, List<String>> attributes;

    // Constructors
    public OrganizationRepresentation() {}

    public OrganizationRepresentation(String id, String name, String alias,
                                      List<String> domains, Map<String, List<String>> attributes) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.domains = domains;
        this.attributes = attributes;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }
}
