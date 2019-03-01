package cs5850.Assignments.Assignment1;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GoogleDriveFileOpIntegrtionTest {
	Drive service;
	GoogleDriveFileOperation gdOP;

	@Before
	public void setUp() throws Exception 
	{
		initialGoogleDriveServices();
		gdOP = new GoogleDriveFileOperation(service);
	}

	@After
	public void tearDown() throws Exception 
	{
		gdOP = null;
	}

	public void initialGoogleDriveServices() throws IOException 
	{
		String clientID = "<ClientID>";
		String clientSecret = "<ClientSecret>";
		String redirectPath = "<redirectPath>";
		HttpTransport httpTrans = new NetHttpTransport();
		JsonFactory jsonFac = new JacksonFactory();
		
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTrans, jsonFac, clientID, clientSecret, Arrays.asList(DriveScopes.DRIVE))
			.setAccessType("online")
			.setApprovalPrompt("auto").build();

		String url = flow.newAuthorizationUrl().setRedirectUri(redirectPath).build();
		System.out.println("Enter the authorization code:");
		System.out.println("  " + url);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();
		GoogleTokenResponse res = flow.newTokenRequest(code).setRedirectUri(redirectPath).execute();
		GoogleCredential cred = new GoogleCredential().setFromTokenResponse(res);
		service = new Drive.Builder(httpTrans, jsonFac, cred).build();
	}

	@Test
	public void testGDFileOpIntegTest() throws IOException 
	{
		java.io.File f = new java.io.File("test1.txt");
		f.createNewFile();
		String folderID="<FolderID>";
		gdOP.addExistingFilesOfDir(folderID, f, "");
		Assert.assertNotNull(gdOP.getFileId(f.getName()));
		gdOP.modifyFile(f);
		Assert.assertNotNull(gdOP.getFileId(f.getName()));
		gdOP.deleteFile(f);
		Assert.assertNull(gdOP.getFileId(f.getName()));
	}
}
