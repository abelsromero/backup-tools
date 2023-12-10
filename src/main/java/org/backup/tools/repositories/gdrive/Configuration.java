package org.backup.tools.repositories.gdrive;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;

public record Configuration(Application application, Options options) {

    private static final String CONFIG_PATH = "~/.config/jbackups/config.yaml";

    public static Configuration load(String path) {
        final InputStream inputStream = readFile(path);

        final Yaml yaml = new Yaml();
        final Map<String, Object> obj = yaml.load(inputStream);

        final Map<String, Object> application = (Map<String, Object>) obj.get("application");
        String name = (String) application.get("name");
        List<String> scopes = (List<String>) application.get("scopes");
        String credentialsPath = (String) application.get("credentials-path");

        final Map<String, Object> options = (Map<String, Object>) obj.get("options");
        String tokensPath = (String) options.get("tokens-path");

        return new Configuration(
            new Application(name, scopes, getCredentials(credentialsPath)),
            new Options(tokensPath)
        );
    }

    public static Configuration load() {
        return load(CONFIG_PATH);
    }

    private static InputStream readFile(String path) {
        if (path.startsWith("~/")) {
            return inputStream(fileFromHome(path.substring(2)));
        } else if (path.startsWith("classpath:")) {
            return inputStreamFromClasspath(path.substring(10));
        } else {
            return inputStream(new File(path));
        }
    }

    private static InputStream inputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static File fileFromHome(String path) {
        return new File(System.getProperty("user.home"), path);
    }

    private static InputStream inputStreamFromClasspath(String path) {
        return Configuration.class.getResourceAsStream(path);
    }

    private static Reader getCredentials(String credentialsPath) {
        return new InputStreamReader(readFile(credentialsPath));
    }
}
