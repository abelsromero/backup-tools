package org.backup.tools.repositories.gdrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GDriveFactory {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final GDrive getConnection(Application app, Options options) throws IOException {
        final Credential credentials = getCredentials(app, options, HTTP_TRANSPORT);
        final Drive driveClient = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
            .setApplicationName(app.name())
            .build();

        return new GDrive(driveClient.files());
    }

    /**
     * Learn:
     * - OAuth is the best options so far:
     * -- API_KEY does not hold principal, hence cannot validate permissions. Used only for public resources https://cloud.google.com/docs/authentication/api-keys
     * -- Service Account:
     * --- Hard to restrict to only GDrive. Though to handle GC resources.
     * --- Requires adding the service-account as co-owner on folder for backups. Pro: see what was created by tool; Con: extra permission mgmt.
     */
    private static Credential getCredentials(Application app, Options options, HttpTransport httpTransport) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, app.credentials());

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, app.scopes())
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(options.tokensPath())))
            .setAccessType("offline")
            .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
