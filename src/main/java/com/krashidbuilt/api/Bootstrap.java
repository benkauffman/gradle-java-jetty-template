package com.krashidbuilt.api;

import io.swagger.jaxrs.config.SwaggerContextService;
import io.swagger.models.ExternalDocs;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


/**
 * Created by Ben Kauffman on 1/15/2017.
 */
public class Bootstrap extends HttpServlet {

    private static Logger logger = LogManager.getLogger();

    @Override
    public void init(ServletConfig config) throws ServletException {

        logger.info("BOOTSTRAP CLASS INITIALIZED");


        Info info = new Info()
                .title("KrashidBuilt - Template API")
                .description("This is a template gradle java API written by KrashidBuilt.  "
                        + "\n##To authenticate against and interact with the API through this swagger doc:  "
                        + "\n1. Navigate to the [authentication](#!/authentication/login) section and "
                        + "use the login endpoint to generate an access token.  "
                        + "\n2. Copy the access token to your clip board.  "
                        + "\n3. Click \"Authorize\" in the documentation header.  "
                        + "\n4. Type in \"Bearer \" as the value and then paste in the access token after it.  "
                        + "\n5. Click authorize to embed the token in the Authorization header of each request.  ");


        Swagger swagger = new Swagger().info(info);
        swagger.externalDocs(new ExternalDocs("Find out more about KrashidBuilt", "https://krashidbuilt.com"));

        ApiKeyAuthDefinition apiKeyAuthDefinition = new ApiKeyAuthDefinition("Authorization", In.HEADER);
        swagger.securityDefinition("Authorization", apiKeyAuthDefinition);


        new SwaggerContextService().withServletConfig(config).updateSwagger(swagger);
    }
}
