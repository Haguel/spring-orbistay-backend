package dev.haguel.orbistay.config;

import dev.haguel.orbistay.dto.response.JwtResponseDTO;
import dev.haguel.orbistay.util.EndPoints;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerDocumentationConfig {

    @Bean
    public OpenApiCustomizer oauth2LoginCustomiser() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths == null) {
                paths = new Paths();
                openApi.setPaths(paths);
            }

            PathItem pathItem = new PathItem();
            Operation getOperation = new Operation()
                    .addTagsItem("OAuth2")
                    .summary("Google oauth2 login")
                    .responses(new ApiResponses().addApiResponse("200",
                            new ApiResponse()
                                    .description("User signed in successfully")
                                    .content(new Content().addMediaType("application/json",
                                            new MediaType().schema(new Schema<JwtResponseDTO>().$ref("#/components/schemas/JwtResponseDTO")))
                                    )
                    ));


            pathItem.setGet(getOperation);
            paths.addPathItem(EndPoints.OAuth2.OAUTH2_GOOGLE_LOGIN, pathItem);
        };
    }
}
