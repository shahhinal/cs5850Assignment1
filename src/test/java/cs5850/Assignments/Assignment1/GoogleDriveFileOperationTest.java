package cs5850.Assignments.Assignment1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


public class GoogleDriveFileOperationTest {
	private Drive mockedDriveObj;
	private GoogleDriveFileOperation  gdFileOp;
	private ByteArrayOutputStream byteOS;

	@Before
	public void setup() {
		mockedDriveObj = mock(Drive.class);
		gdFileOp = new GoogleDriveFileOperation(mockedDriveObj);
		byteOS = new ByteArrayOutputStream();
		System.setOut(new PrintStream(byteOS));
	}

	@Test
	public void testGoogleDrivegdFileOp() {
		assertNotNull(gdFileOp.service);
	}
		
	@Test
	public void testaddFolder() throws IOException {
		String directoryName="D:\\DirToWatch";
		
		File fileMetadata = new File();
		fileMetadata.setName(directoryName);
		fileMetadata.setMimeType("application/vnd.google-apps.folder");
		
		File file = new File();
		file.setId("testID");
		
		Files files = mock(Files.class);
		Files.Create insert = mock(Files.Create.class);
		when(mockedDriveObj.files()).thenReturn(files);
		when(files.create(fileMetadata)).thenReturn(insert);
		when(insert.setFields("id")).thenReturn(insert);
		when(insert.execute()).thenReturn(file);
				
		gdFileOp.addFolder(directoryName);

		verify(insert).execute();
		assertEquals("Folder ID: testID", byteOS.toString().trim());
	}
	
	@Test
	public void testAddExistingFilesOfDir() throws IOException {
		String folderId="123";
		java.io.File localFile = new java.io.File("D:\\DirToWatch\\sample.txt");
		File file = new File();
		file.setName(localFile.getName());
		file.setId("sample.txt");

		FileList filelist = new FileList();
		java.util.List<File> list = new java.util.ArrayList<File>();
		list.add(file);
		filelist.setFiles(list);

		Files files = mock(Files.class);
		List request = mock(List.class);
		Files.Create insert = mock(Files.Create.class);
		when(mockedDriveObj.files()).thenReturn(files);
		when(files.list()).thenReturn(request);
		when(request.execute()).thenReturn(filelist);
		when(files.create(Mockito.any(File.class), Mockito.any(FileContent.class))).thenReturn(insert);
		when(insert.execute()).thenReturn(file);

		gdFileOp.addExistingFilesOfDir(folderId	,localFile,"");

		verify(request).execute();
		assertEquals("sample.txt", byteOS.toString().trim());
	}
	
	@Test
	public void testGetFileId() throws IOException {
		File file = new File();
		file.setId("testID");
		file.setName("testTitle");

		FileList filelist = new FileList();
		java.util.List<File> list = new java.util.ArrayList<File>();
		list.add(file);
		filelist.setFiles(list);

		Files files = mock(Files.class);
		List request = mock(List.class);

		when(mockedDriveObj.files()).thenReturn(files);
		when(files.list()).thenReturn(request);
		when(request.execute()).thenReturn(filelist);

		assertEquals("testID", gdFileOp.getFileId("testTitle"));

	}

	@Test
	public void testModifyFile() throws IOException {
		java.io.File localFile = new java.io.File("D://test.txt");
		File file = new File();
		file.setId("test");
		file.setName("test.txt");

		FileList filelist = new FileList();
		java.util.List<File> list = new java.util.ArrayList<File>();
		list.add(file);
		filelist.setFiles(list);

		Files files = mock(Files.class);
		Files.Update update = mock(Files.Update.class);
		List request = mock(List.class);
		when(localFile.getName()).thenReturn("test.txt");
		when(mockedDriveObj.files()).thenReturn(files);
		when(files.list()).thenReturn(request);
		when(request.execute()).thenReturn(filelist);
		when(files.update(Mockito.any(String.class), Mockito.any(File.class), Mockito.any(FileContent.class)))
				.thenReturn(update);
		when(update.execute()).thenReturn(file);

		gdFileOp.modifyFile(localFile);

		verify(update).execute();
		assertEquals("test.txt", byteOS.toString().trim());
	}

	
	@Test(expected = NullPointerException.class)
	public void testModifyFileWithNullId() throws IOException {
		java.io.File localFile = new java.io.File("D://test.txt");
		File file = new File();
		file.setId("test");
		file.setName("test.txt");

		FileList filelist = new FileList();
		java.util.List<File> list = new java.util.ArrayList<File>();
		list.add(file);
		filelist.setFiles(list);

		Files files = mock(Files.class);
		Files.Update update = mock(Files.Update.class);
		List request = mock(List.class);
		when(mockedDriveObj.files()).thenReturn(files);
		when(files.list()).thenReturn(request);
		when(request.execute()).thenReturn(filelist);
		when(files.update(Mockito.any(String.class), Mockito.any(File.class), Mockito.any(FileContent.class)))
				.thenReturn(update);
		gdFileOp.modifyFile(localFile);
		verify(update).execute();
	}

	
}
