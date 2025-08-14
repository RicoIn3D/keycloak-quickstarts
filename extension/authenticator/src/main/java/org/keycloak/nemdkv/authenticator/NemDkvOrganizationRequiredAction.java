/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.nemdkv.authenticator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.CredentialRegistrator;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.OrganizationModel;
import org.keycloak.models.UserModel;
import org.keycloak.nemdkv.authenticator.credential.OrganizationMapper;
import org.keycloak.nemdkv.authenticator.credential.SecretQuestionCredentialModel;
import org.keycloak.organization.OrganizationProvider;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NemDkvOrganizationRequiredAction implements RequiredActionProvider, CredentialRegistrator {
    public static final String PROVIDER_ID = "nemkdv_organization_config";
    public static final String ORGANIZATION_ATTRIBUTE = "organization_id";
    public static final String ORGANIZATION_ACTIVE_ATTRIBUTE = "active_organization";

    public static final String ORGANIZATION_DTO_ATTRIBUTE = "active_organization_dto";
    @Override
    public void evaluateTriggers(RequiredActionContext context) {

    }

    @Override
    public String getCredentialType(KeycloakSession session, AuthenticationSessionModel AuthenticationSession) {
        return SecretQuestionCredentialModel.TYPE;
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {

        OrganizationProvider orgProvider = context.getSession().getProvider(OrganizationProvider.class);
        UserModel user = context.getUser();
        List<OrganizationModel> userOrganizations  = orgProvider.getByMember(user).collect(Collectors.toList());

//TODO: RICO tHis could be the prod version:
    /*
    // Handle different cases based on organization count
        if (userOrganizations.isEmpty()) {
            // No organizations - just continue the flow
            user.setSingleAttribute(ORGANIZATION_DTO_ATTRIBUTE, "");
            context.success();
            return;
        } else if (userOrganizations.size() == 1) {
            // Only one organization - set it as active and continue
            //OrganizationModel singleOrg = userOrganizations.get(0);
            //setActiveOrganization(context, singleOrg);
            user.setSingleAttribute(ORGANIZATION_DTO_ATTRIBUTE, "");
            context.success();
            return;
        }*/
        // Handle different cases based on organization count
        if (userOrganizations.isEmpty()) {
            // No organizations - just continue the flow
            user.removeAttribute(ORGANIZATION_DTO_ATTRIBUTE);
            //user.setSingleAttribute(ORGANIZATION_DTO_ATTRIBUTE, "");
            context.success();
            return;
        } else if (userOrganizations.size() == 1) {
            // Only one organization - set it as active and continue
            user.removeAttribute(ORGANIZATION_DTO_ATTRIBUTE);
            //user.setSingleAttribute(ORGANIZATION_DTO_ATTRIBUTE, "{}");
            context.success();
            return;
        } else {
            // Multiple organizations - challenge user to select one
            Response challenge = context.form()
                    .setAttribute("organizations", userOrganizations)
                    .createForm("organization-selection.ftl");
            context.challenge(challenge);
        }
    }
    private void setActiveOrganization(RequiredActionContext context, OrganizationModel organization) {

        context.getUser().setSingleAttribute(ORGANIZATION_ATTRIBUTE, organization.getId());
        context.getUser().setSingleAttribute(ORGANIZATION_ACTIVE_ATTRIBUTE, organization.getAlias());

        Map<String, Map<String, Object>> dto = OrganizationMapper.toAttributeFormat(organization);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(dto);
            context.getUser().setSingleAttribute(ORGANIZATION_DTO_ATTRIBUTE, json);

        } catch (JsonProcessingException e) {
            System.out.println("NemDKV - Json error " + organization.getName());
            throw new RuntimeException(e);
        }

    }
    @Override
    public void processAction(RequiredActionContext context) {

        String active_org = (context.getHttpRequest().getDecodedFormParameters().getFirst("organization_id"));

        UserModel user = context.getUser();
        OrganizationProvider orgProvider = context.getSession().getProvider(OrganizationProvider.class);
        List<OrganizationModel> orgs = orgProvider.getByMember(user).toList();
        // Find the name of the active organization
        Optional<OrganizationModel> singleOrg = orgs.stream()
                .filter(org -> org.getId().equals(active_org)).findFirst();

        if (singleOrg.isPresent())
        {
            String orgName = singleOrg.get().getAlias();
            System.out.println("NemDKV - Active organization name: " + orgName);
            setActiveOrganization(context, singleOrg.get());

        }

       context.success();
    }

    @Override
    public void close() {

    }
}
