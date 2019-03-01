package cs5850.Assignments.Assignment1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveFileOperation {
	
		public Drive service;

		public GoogleDriveFileOperation(Drive service) {
			this.service = service;
		}

				
		public String addFolder(String dirName) throws IOException {
			
			File fileMetadata = new File();
		    fileMetadata.setName(dirName);
		    fileMetadata.setMimeType("application/vnd.google-apps.folder");

		    File file = service.files().create(fileMetadata)
		        .setFields("id")
		        .execute();
		    String folderID=file.getId();
		    return folderID;
		}
		
		
		public void addExistingFilesOfDir(String folderId,java.io.File localfile,String action) throws IOException
		{
			
			String fileId = getFileId(localfile.getName());
			if (fileId == null) {
						
			File fileMetadata = new File();
			fileMetadata.setViewersCanCopyContent(true);
			fileMetadata.setName(localfile.getName());
			fileMetadata.setParents(Collections.singletonList(folderId));
			FileContent mediaContent = new FileContent("*/*", localfile);
			service.files().create(fileMetadata,mediaContent)
			    .setFields("id, parents")
			    .execute();
			}
			else if(action=="OnLoad")
			{
				modifyFile(localfile);
			}
		}
		
		public void modifyFile(java.io.File localFile) throws IOException {
			String fileId = getFileId(localFile.getName());
			if (fileId!=null)
			{
				File body = new File();
				body.setName(localFile.getName());
				FileContent mediaContent = new FileContent("*/*", localFile);
				service.files().update(fileId, body, mediaContent).execute();
			}
		}

		public void deleteFile(java.io.File localFile) throws IOException {
			String fileId = getFileId(localFile.getName());
			if (fileId == null) {
				throw new FileNotFoundException();
			} else {
				service.files().delete(fileId).execute();
			}
		}

		public String getFileId(String fileName) {
			try {
				List request = service.files().list();
				FileList files = request.execute();
				for(File file : files.getFiles()) {
					System.out.println(file.getName());
					if (file.getName().equals(fileName)) {
						return file.getId();
					}
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			return null;
		}
		
		
		public boolean fileExists(String fileName) {
			try {
				List request = service.files().list();
				FileList files = request.execute();
				
				for(File file : files.getFiles()) {
					if (file.getName().equals(fileName)) {
						return true;
					}
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			return false;
		}
}
