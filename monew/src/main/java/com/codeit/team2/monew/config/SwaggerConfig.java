package com.codeit.team2.monew.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
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
}
