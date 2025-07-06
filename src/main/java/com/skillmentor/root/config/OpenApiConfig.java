package com.skillmentor.root.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SkillMentor API Documentation",
                version = "1.0",
                description = "SkillMentor is a platform designed to connect learners with mentors for skill development and knowledge sharing.",
                contact = @Contact(name = "Developer", email = "dulniduthennakoon@gmail.com"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        )
)
public class OpenApiConfig {}
