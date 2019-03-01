package cs5850.Assignments.Assignment1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GoogleDriveService {
	private static String clientID = "<ClientID>";
	private static String clientSecret = "<ClientSecret>";
	private static String redirectPath = "<redirectPath>";

	private static GoogleDriveService gdObj;
	private Drive gdClient;
	private GoogleDriveService() {
		try {
			initGoogleDriveServices();
		} catch (IOException e) {
			System.out.println("Please check Internet connection");
			e.printStackTrace();
		}
	}

	public static GoogleDriveService get() {
		if (gdObj == null) {
			gdObj = new GoogleDriveService();
		}
		return gdObj;
	}

	public void initGoogleDriveServices() throws IOException {
		HttpTransport httpTrans = new NetHttpTransport();
		JsonFactory jsonFac = new JacksonFactory();

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTrans, jsonFac, clientID, clientSecret, Arrays.asList(DriveScopes.DRIVE))
		.setAccessType("online")
		.setApprovalPrompt("auto").build();

		String url = flow.newAuthorizationUrl().setRedirectUri(redirectPath).build();
		System.out.println("Enter the authorization code:");
		System.out.println("  " + url);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();

		GoogleTokenResponse res = flow.newTokenRequest(code).setRedirectUri(redirectPath).execute();
		GoogleCredential cred = new GoogleCredential().setFromTokenResponse(res);

		gdClient = new Drive.Builder(httpTrans, jsonFac, cred).build();
	}

	public Drive getgdClient() {
		return gdClient;
	}
}
