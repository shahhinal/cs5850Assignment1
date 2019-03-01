package cs5850.Assignments.Assignment1;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.mockito.Mockito.mock;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

import java.util.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.google.api.services.drive.Drive;
import static org.hamcrest.CoreMatchers.*;

public class WatchDirectoryServiceTest {
	private  WatchService watchService;
    private Drive mockedDrive;
    private GoogleDriveFileOperation gdFileOp;
    private WatchKey key;
    
    
	@Before
	public void setup() throws Exception{
		mockedDrive = mock(Drive.class);
		gdFileOp = new GoogleDriveFileOperation(mockedDrive);
		watchService = FileSystems.getDefault().newWatchService();
	}
    
    public void createFile(String fileName) throws IOException
    {	
    	 java.io.File file = new java.io.File(fileName);
         if (file.createNewFile()) { 
            System.out.println("File is created!");
         } else {
            System.out.println("File already exists.");
         } 
         FileWriter writer= new FileWriter(file);
         writer.write("Test data");
         writer.close();
    }
    
    public void deleteFile(String fileName) throws IOException
    {
    	java.io.File file = new java.io.File(fileName); 
        if(file.delete()) 
        { 
            System.out.println("File deleted successfully"); 
        } 
        else
        { 
            System.out.println("Failed to delete the file"); 
        } 
    }
    
    public void modifyFile(String fileName,String str) throws IOException
    {
    	        
    	try { 
    		BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true)); 
    		out.write(str); 
    		out.close(); 
    		System.out.println("File modified successfully!");
    		} 
    		catch (IOException e) { 
    		System.out.println("exception occoured" + e); 
    		} 
    }
       
    @Test
    public void testCreateEvents() throws IOException, InterruptedException {
        Path basePath=Paths.get("D:\\DirToWatch");
        key = basePath.register(watchService,ENTRY_CREATE);
        
        createFile("D:\\DirToWatch\\testFile1.txt");
        createFile("D:\\DirToWatch\\testFile2.txt");
        createFile("D:\\DirToWatch\\testFile3.txt");
        
        WatchKey watchKey = watchService.take();
        Assert.assertNotNull(watchKey);
        List<WatchEvent<?>> eventList = watchKey.pollEvents();
        Assert.assertThat(eventList.size(), is(3));
        
        for (WatchEvent<?> event : eventList) {
           	System.out.println(event.kind().toString());
        	Assert.assertThat((event.kind() == StandardWatchEventKinds.ENTRY_CREATE), is(true));
        }
        Path eventPath = (Path) eventList.get(0).context();
        System.out.println(eventPath.toString());
        Assert.assertThat(Files.isSameFile(eventPath, Paths.get("testFile1.txt")), is(true));
        Path watchedPath = (Path) watchKey.watchable();
        Assert.assertThat(Files.isSameFile(watchedPath, basePath), is(true));
    }
    
    @Test
    public void testDeleteEvents() throws IOException, InterruptedException {
        Path basePath=Paths.get("D:\\DirToWatch");
        key = basePath.register(watchService,ENTRY_DELETE);
        
        deleteFile("D:\\DirToWatch\\testFile1.txt");
        WatchKey watchKey = watchService.take();
        Assert.assertNotNull(watchKey);
        List<WatchEvent<?>> eventList = watchKey.pollEvents();
        Assert.assertThat(eventList.size(), is(1));
        
        for (WatchEvent<?> event : eventList) {
        	System.out.println(event.kind().toString());
        	Assert.assertThat((event.kind() == StandardWatchEventKinds.ENTRY_DELETE), is(true));
        }
        Path eventPath = (Path) eventList.get(0).context();
        System.out.println(eventPath.toString());
        Assert.assertThat(Files.isSameFile(eventPath, Paths.get("testFile1.txt")), is(true));
        Path watchedPath = (Path) watchKey.watchable();
        Assert.assertThat(Files.isSameFile(watchedPath, basePath), is(true));
    }

    @Test
    public void testUpdateEvents() throws IOException, InterruptedException {
        Path basePath=Paths.get("D:\\DirToWatch");
        key = basePath.register(watchService,ENTRY_MODIFY);
        modifyFile("D:\\DirToWatch\\testFile3.txt","HELLO");
        WatchKey watchKey = watchService.take();
        Assert.assertNotNull(watchKey);
        List<WatchEvent<?>> eventList = watchKey.pollEvents();
        Assert.assertThat(eventList.size(), is(1));
        
        for (WatchEvent<?> event : eventList) {
        	System.out.println(event.kind().toString());
        	Assert.assertThat((event.kind() == StandardWatchEventKinds.ENTRY_MODIFY), is(true));
        }
        Path eventPath = (Path) eventList.get(0).context();
        System.out.println(eventPath.toString());
        Assert.assertThat(Files.isSameFile(eventPath, Paths.get("testFile3.txt")), is(true));
        Path watchedPath = (Path) watchKey.watchable();
        Assert.assertThat(Files.isSameFile(watchedPath, basePath), is(true));
    }
}
