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

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.*;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.models.*;
import org.keycloak.organization.OrganizationProvider;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NemDkvOrganizationSecretQuestionAuthenticator implements Authenticator, CredentialValidator<NemDkvOrganizationCredentialProvider> {
    public static final String ORGANIZATION_ATTRIBUTE = "organization_id";
    public static final String ORGANIZATION_ACTIVE_ATTRIBUTE = "active_organization";

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        OrganizationProvider orgProvider = context.getSession().getProvider(OrganizationProvider.class);
        UserModel user = context.getUser();
        List<OrganizationModel> orgs = orgProvider.getByMember(user).collect(Collectors.toList());

        System.out.println("NemDKV Org choosing");

        Response challenge = context.form()
            .setAttribute("organizations", orgs)
            .createForm("organization-selection.ftl");
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        boolean validated = validateAnswer(context);
        if (!validated) {
            Response challenge =  context.form()
                    .setError("badSecret")
                    .createForm("organization-selection.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }
        context.success();
    }


    protected boolean validateAnswer(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        String active_org = formData.getFirst("organization_id");
        System.out.println("NemDKV Validate answer when cookie false "+ active_org);

        OrganizationProvider orgProvider = context.getSession().getProvider(OrganizationProvider.class);
        List<OrganizationModel> orgs = orgProvider.getByMember(context.getUser()).toList();

        String orgName = orgs.stream()
                .filter(org -> org.getId().equals(active_org))
                .map(OrganizationModel::getName)  // Assuming there's a getName() method
                .findFirst()
                .orElse("Unknown Organization");


        if (active_org != null && !active_org.isBlank()) {
            context.getUser().setSingleAttribute(ORGANIZATION_ATTRIBUTE, active_org);
            context.getUser().setSingleAttribute(ORGANIZATION_ACTIVE_ATTRIBUTE, orgName);
            System.out.println("NemDkv -TO OlD stored active org to user attribute");
            return true;
        }

        return false;

    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return getCredentialProvider(session).isConfiguredFor(realm, user, getType(session));
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        user.addRequiredAction(NemDkvOrganizationRequiredAction.PROVIDER_ID);
    }

    public List<RequiredActionFactory> getRequiredActions(KeycloakSession session) {
        return Collections.singletonList((NemDkvOrganizationRequiredActionFactory)session.getKeycloakSessionFactory().getProviderFactory(RequiredActionProvider.class, NemDkvOrganizationRequiredAction.PROVIDER_ID));
    }

    @Override
    public void close() {

    }

    @Override
    public NemDkvOrganizationCredentialProvider getCredentialProvider(KeycloakSession session) {
        return (NemDkvOrganizationCredentialProvider)session.getProvider(CredentialProvider.class, NemDkvOrganizationCredentialProviderFactory.PROVIDER_ID);
    }
}
