package cs5850.Assignments.Assignment1;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App 
{
	 public static void main(String[] args) throws IOException {
	        // parse arguments
	        if (args.length < 1) {
	        	 System.out.println("No Directory Path Provided!");
	             System.exit(-1);
	        }
	        String directoryName=args[0]; 	        
	        Path dir = Paths.get(directoryName); 
	 }
}
