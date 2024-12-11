package com.teamb.common.configurations;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class EnvConfig implements InitializingBean, EnvironmentAware {

    private Environment environment;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Get the current working directory
        String currentDir = System.getProperty("user.dir");

        // Create a path to the .env file in the relative directory
        String dotenvPath = Paths.get(currentDir, "API", "backend", ".env").toString();

        // Load the .env file using the relative path
        Dotenv dotenv = Dotenv.configure().directory(dotenvPath).load();

        // Inject the variables into the Spring Environment
        // Set system properties
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        // Alternatively, you can inject them manually into Spring's environment
        // This allows you to use them as @Value in Spring beans
        // You can also set specific properties with the environment
        System.out.println("MONGODB_URI: " + dotenv.get("MONGODB_URI"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
