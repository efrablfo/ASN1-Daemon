package com.asn1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Clase Thread que ejecuta el decodificador de un esquema(<code>schemaPath</code>) determinado
 * @see DaemonConfiguration
 * @author Efrain Blanco
 * @author Jhon Fernandez
 * @version 1.0
 */
public class ProcessThread implements Runnable, DaemonConfiguration {

    private Path schemaPath;
    private String packageName;

    /**
     * Inicializa Thread con directorio de esquema y el nombre del paquete de 
     * las clases ASN1 generadas por el compilador jASN1 
     * @param schemaPath    directorio de esquema
     * @param packageName   nombre de paquete
     */
    public ProcessThread(Path schemaPath, String packageName) {
        this.schemaPath = schemaPath;
        this.packageName = packageName;
    }

    /**
     * Inicia ejecución
     */
    @Override
    public void run() {
        try {
            executeJar();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Lee archivo .asn y obtiene el nombre del OutputRecord
     * @see DaemonConfiguration#EXPORTS_ASN_STMT
     * @param asnPath   directorio de archivo .asn
     * @return  nombre de OutputRecord
     * @throws IOException  si ocurre un error en la lectura del archivo .asn
     */
    private String getAsnOutputRecord(Path asnPath) throws IOException{
        List<String> asn1FileLines = Files.readAllLines(asnPath);
        String outputRecordClass = asn1FileLines.stream().filter(line -> line.
                toUpperCase().startsWith(EXPORTS_ASN_STMT) ).map( line -> line.split(" ")[1] ).findFirst().get();
        outputRecordClass = outputRecordClass.replaceAll(";", "");
        return outputRecordClass;
    }

    /**
     * Ejecuta jar del decodificador del esquema
     * @see #getAsnOutputRecord(java.nio.file.Path) 
     * @see Util#getPath(java.nio.file.Path, java.lang.String) 
     * @see DaemonConfiguration#CDR_EXECUTABLE_FOLDER
     * @see DaemonConfiguration#CDR_ASN_FOLDER
     * @see DaemonConfiguration#JAR_EXTENSION
     * @see DaemonConfiguration#ASN_EXTENSION
     * @see DaemonConfiguration#CMD_JAR_STMT
     * @throws IOException  si ocurre un error en la lectura de los 
     *                      archivos <code>JAR_EXTENSION, ASN_EXTENSION</code>
     */
    private void executeJar() throws IOException {
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
