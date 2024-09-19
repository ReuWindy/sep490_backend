package com.fpt.sep490.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "swagger")
public class SwaggerConfiguration {
    private String appName;
    private String appDescription;
    private String appVersion;
    private String appLicense;
    private String appLicenseUrl;
    private String appContact;
    private String appContactUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        final Info info = getApiInformation();
        final Components components = new Components();
        final String schemeName = "bearerAuth";
        components.addSecuritySchemes(schemeName, new SecurityScheme().name(schemeName).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"));
        final OpenAPI openAPI = new OpenAPI();
            openAPI.setComponents(components);
            openAPI.setInfo(info);
            openAPI.addSecurityItem(new SecurityRequirement().addList(schemeName));
        return openAPI;
    }

    private Info getApiInformation() {
        final License license = new License();
            license.setName(appLicense);
            license.setUrl(appLicenseUrl);

        final Contact contact = new Contact();
            contact.setName(appContact);
            contact.setUrl(appContactUrl);
            contact.setEmail(appContactUrl);

        final Info info = new Info();
            info.setLicense(license);
            info.setContact(contact);
            info.setVersion(appVersion);
            info.setDescription(appDescription);

        return info;
    }
}
