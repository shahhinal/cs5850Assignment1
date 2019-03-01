package cs5850.Assignments.Assignment1;

import java.io.IOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;


public class GoogleDriveService {
	
	private Drive googleDriveClient;
	private GoogleDriveService() {
		try {
			initGoogleDriveServices();
		} catch (IOException e) {
			System.out.println("Please check Internet connection and Google credentials.");
			e.printStackTrace();
		}
	}

	
	public void initGoogleDriveServices() throws IOException {
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		
	}

	public Drive getGoogleDriveClient() {
		return googleDriveClient;
	}
}
