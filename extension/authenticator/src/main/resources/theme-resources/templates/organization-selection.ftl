<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "header">
        ${msg("loginTitleHtml",realm.name)}
    <#elseif section = "form">
        <form id="kc-totp-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">

            <div class="${properties.kcFormGroupClass!}">
                <#if organizations?? && organizations?size gt 0>
                    <#assign sortedOrganizations = organizations?sort_by("name")>
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="organization" class="${properties.kcLabelClass!}">Select your organization</label>
                    </div>
                    <div class="${properties.kcInputWrapperClass!}">
                        <select id="organization" name="organization_id" class="${properties.kcInputClass!}">
                            <#list sortedOrganizations as org>
                                <option value="${org.id}" <#if org?index == 0>selected</#if>>${org.name}</option>
                            </#list>
                        </select>
                    </div>
                <#else>
                    <div class="form-note">
                        <small>No organizations available.</small>
                    </div>
                </#if>
            </div>
            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <div class="${properties.kcFormButtonsWrapperClass!}">
                        <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                               name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                    </div>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
