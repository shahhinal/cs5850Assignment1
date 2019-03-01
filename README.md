<h3>CPP CS5850:Programming Assignment1</h3>
This project is about building prototype of a Dropbox app using cloud based Google Drive service and implement test cases.<br/><br/>

To run the project:
1. Login to https://console.cloud.google.com/apis/ to enable API and and configure to get Google API Credential information. <br/>
2. Replace Google API Credential information in GoogleDriveService.java file. <br/>
3. Execute following commands:<br/>
   ```javac App.java```<br/>
   ```java App <watchFolderName>```<br/>
  Here, watchFolderName is the name of directory to watch<br/><br/> 

For Testing:<br/>
Unit Testing: ```mvn test```
Integration Test: ```mvn integration-test``` <br/>
PlugIns:<br/>
For Cobertura, Checkstyle, Findbugs, CircleCI plugins report, execute command: ```mvn site```
