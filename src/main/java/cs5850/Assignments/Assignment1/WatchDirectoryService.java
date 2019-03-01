package cs5850.Assignments.Assignment1;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

public class WatchDirectoryService {
	private final WatchService watchService;
    private final Map<WatchKey,Path> keys;
    private boolean trace = false;
    private GoogleDriveFileOperation driveOp;
    private String directoryName;
    private String folderID;
    private String lastDirectoryName;
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    public WatchDirectoryService(Path dir, GoogleDriveFileOperation driveOp) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.driveOp = driveOp;
        this.keys = new HashMap<WatchKey,Path>();
        this.directoryName=dir.toString(); 
        registerDirectory(dir);
        this.trace = true;
        lastDirectoryName = directoryName.substring(directoryName.lastIndexOf("/")+1);	
    }

    public void addDirectoryToDrive()
    {
    	try
    	{
    	
    	folderID=driveOp.addFolder(lastDirectoryName);
    	}
    	catch(IOException e)
    	{
    		System.out.println("Failed to get FolderID");
        	e.printStackTrace();
    	}
    }
   
    public void getFilesFromDir(String directoryName)
    {
    	try
    	{
    	File directory = new File(directoryName);
    	File[] fileList = directory.listFiles();

    	for (File f : fileList) {
    		if (f.isFile()) {
    			driveOp.addExistingFilesOfDir(folderID,f,"OnLoad");
    		} 
    	 }
    	}
    	catch(IOException e)
    	{
    		System.out.println("Error while adding existing files to drive");
        	e.printStackTrace();
    	}
    }
   
    @SuppressWarnings("rawtypes")
	public void processEvents() {
    	boolean isExists=driveOp.fileExists(lastDirectoryName);
    	if (isExists==false)
    	{
    		addDirectoryToDrive();
    	}
    	else
    	{
    		folderID= driveOp.getFileId(lastDirectoryName);
    	}
    	getFilesFromDir(directoryName);
  	   	File f;
        for (;;) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                if (kind == OVERFLOW) {
                    continue;
                }
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                System.out.format("%s: %s\n", event.kind().name(), child);
                try {
	                if (event.kind() == ENTRY_CREATE) {
	                	f=child.toFile();
	                	if (!f.isDirectory())
	                	{
	                		driveOp.addExistingFilesOfDir(folderID,f,"");
	                	}
	                	
	                } else if (event.kind() == ENTRY_MODIFY) {
	                	driveOp.modifyFile(child.toFile());
	                } else if (event.kind() == ENTRY_DELETE) {
	                	f=child.toFile();
	                	if (!f.isDirectory())
	                	{
	                		driveOp.deleteFile(f);
	                	}
	                }
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
}
