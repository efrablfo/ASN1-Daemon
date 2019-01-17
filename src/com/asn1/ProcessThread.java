package com.asn1;

import static com.asn1.DaemonConfiguration.CDR_EXECUTABLE_FOLDER;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Efrain.Blanco
 */
public class ProcessThread implements Runnable, DaemonConfiguration {

    private Path schemaPath;
    private String packageName;

    public ProcessThread(Path schemaPath, String packageName) {
        this.schemaPath = schemaPath;
        this.packageName = packageName;
    }

    @Override
    public void run() {
        try {
            executeJar();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private String getAsnOutputRecord(Path asnPath) throws IOException{
        List<String> asn1FileLines = Files.readAllLines(asnPath);
        String outputRecordClass = asn1FileLines.stream().filter(line -> line.
                toUpperCase().startsWith(EXPORTS_ASN_STMT) ).map( line -> line.split(" ")[1] ).findFirst().get();
        outputRecordClass = outputRecordClass.replaceAll(";", "");
        return outputRecordClass;
    }

    private Process executeJar() throws IOException {
        Path exePath = Paths.get(schemaPath.toString() + File.separator + CDR_EXECUTABLE_FOLDER);
        Path asnPath = Paths.get(schemaPath.toString() + File.separator + CDR_ASN_FOLDER);
        Path jarFilePath = Util.getPath(exePath, JAR_EXTENSION);
        Path asnFilePath = Util.getPath(asnPath, ASN_EXTENSION);
        String command = CMD_JAR_STMT + "\"" + jarFilePath.toString() + "\" " + 
                getAsnOutputRecord(asnFilePath) + " \"" + schemaPath.toString() + "\" " + packageName;
        Process process = Runtime.getRuntime().exec(command);

        System.out.println("***JAR: " + jarFilePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("JAR input stream: " + line);
        }
        return process;
    }

    public Path getSchemaPath() {
        return schemaPath;
    }

    public void setSchemaPath(Path schemaPath) {
        this.schemaPath = schemaPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
