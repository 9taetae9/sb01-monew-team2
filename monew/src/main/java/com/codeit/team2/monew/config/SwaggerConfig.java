package com.codeit.team2.monew.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("MoNew API team2")
                .description("팀2 MoNew 문서입니다.")
                .version("v1.0.0"))
            .servers(List.of(new Server()
                .url("http://localhost:8080")
                .description("Local Server")));
    }

    @Bean
    public GroupedOpenApi customerApiGroup() {
        String[] pathsToMatch = {"/api/**"};
        String[] pathsToExclude = {"/api/batch/**", "/api/backup/**"};

        return GroupedOpenApi.builder()
            .group("customer") // Swagger UI 탭 이름
            .pathsToMatch(pathsToMatch) // 이 패키지만 Swagger에 노출
            .pathsToExclude(pathsToExclude)
            .build();
    }

    @Bean
    public GroupedOpenApi adminApiGroup() {
        String[] pathsToMatch = {"/admin/**" ,"/api/batch/**", "/api/backup/**"};

        return GroupedOpenApi.builder()
            .group("admin") // Swagger UI 탭 이름
            .pathsToMatch(pathsToMatch) // 이 패키지만 Swagger에 노출
            .build();
    }
}
