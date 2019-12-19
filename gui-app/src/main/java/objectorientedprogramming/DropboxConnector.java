package objectorientedprogramming;

import com.dropbox.core.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.*;
import java.util.Optional;
import java.nio.file.Files;

/**
 * Class creates a connection between the app and Dropbox.
 *
 * @author Laura Kanerva.
 */
public class DropboxConnector {
    private String accessToken;
    private File dbxTempFolder;
    private DbxClientV2 client;
    private DbxWebAuth webAuth;

    /**
     * Class constructor, initializes temporary folder for files to be uploaded to Dropbox.
     */
    public DropboxConnector() {
        try {
            dbxTempFolder = Files.createTempDirectory("shoppingListTemp").toFile();
            System.out.println(dbxTempFolder.getAbsolutePath());
            dbxTempFolder.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Authorizes user credentials.
     *
     * @return a URL where users get their authorization keys
     */
    public String authorizeUser() {
        String appKey = "9mm1b8t6rj1ptm0";
        String appSecret = "7xickax6ooeefi8";
        DbxAppInfo appInfo = new DbxAppInfo(appKey, appSecret);
        DbxRequestConfig requestConfig = new DbxRequestConfig("Shopping-list");
        webAuth = new DbxWebAuth(requestConfig, appInfo);
        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
            .withNoRedirect()
            .build();
        return webAuth.authorize(webAuthRequest);
    }

    /**
     * Lets user to log in and continue.
     *
     * @param authCode authorization code
     */
    public void logIn(Optional<String> authCode) {
        String code = authCode.get();
        if (code == null) {
            return;
        }
        code = code.trim();

        DbxAuthFinish authFinish;
        try {
            authFinish = webAuth.finishFromCode(code);
        } catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.authorize: " + ex.getMessage());
            System.exit(1); return;
        }

        System.out.println("Authorization complete.");
        System.out.println("- User ID: " + authFinish.getUserId());
        System.out.println("- Account ID: " + authFinish.getAccountId());
        System.out.println("- Access Token: " + authFinish.getAccessToken());

        accessToken = authFinish.getAccessToken();
        createClient();
    }

    /**
     * Connects to Dropbox and creates a Dropbox client.
     */
    private void createClient() {
        DbxRequestConfig config = new DbxRequestConfig("Shopping-list");
        client = new DbxClientV2(config, accessToken);
    }

    /**
     * Creates a temporary file for JSONObjects and uploads the file to Dropbox.
     *
     * @param file the filename
     * @param jsons the JSONObjects to be uploaded
     */
    public void uploadFile(Optional<String> file, JsonObject[] jsons) {
        String fileName = file.get();
        File tempFile = new File(dbxTempFolder, fileName);
        tempFile.deleteOnExit();

        try {
            JsonUtil jutil = new JsonUtil();
            jutil.writeToJson(tempFile, jsons);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (InputStream in = new FileInputStream(tempFile)) {
            FileMetadata metadata = client.files().uploadBuilder("/" + fileName)
                .uploadAndFinish(in);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }
}