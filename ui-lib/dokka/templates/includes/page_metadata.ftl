<#-- Overrides Dokka's stock page_metadata.ftl only to give pages meaningful titles. -->
<#macro display>
    <#if pageName == "tabotabo-ui">
    <title>Tabotabo UI · API reference</title>
    <#else>
    <title>${pageName} · Tabotabo UI API</title>
    </#if>
    <@template_cmd name="pathToRoot">
    <link href="${pathToRoot}images/logo-icon.svg" rel="icon" type="image/svg">
    </@template_cmd>
</#macro>
