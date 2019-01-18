package com.asn1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase que ejecuta un proceso que constantemente está revisando si existen 
 * CDRs Asn1 sin decodificar en un directorio previamente configurado 
 * @see DaemonConfiguration
 * @author Efrain Blanco
 * @author Jhon Fernandez
 * @version 1.0
 */
public class ASN1Daemon implements DaemonConfiguration{
    private ExecutorService executor;    
    private static String configPath;
    private static String asn1ExeConfigDir;
    private static Properties configurations;
    
    static{
        try {
            configPath = System.getProperty(PROJECT_PATH);
            configurations = Util.getPropertiesConfigFile(configPath + File.separator + CONFIG_PROPERTIES_FILENAME);        
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
    
    /**
     * Ejecuta el programa
     * @param args -
     */
    public static void main(String[] args) {
         try {
             ASN1Daemon daemon = new ASN1Daemon();
             daemon.run();
         } catch (Exception ex) {
             ex.printStackTrace();
         }
    }
    
    /**
     * Configura un ThreadPool(Grupo de hilos) con un numero de hilos previamente 
     * configurados en <code>MAX_PROCESS_PROPERTY</code> y ejecuta proceso de 
     * revisión de directorio principal de CDRs cada <code>WAIT_TIME_PROPERTY</code> milisegundos
     * @see DaemonConfiguration#MAX_PROCESS_PROPERTY
     * @see DaemonConfiguration#ASN1_MAIN_FOLDER_PROPERTY
     * @see #process(java.nio.file.Path) 
     * @throws IOException  si ocurre un error leyendo el directorio principal de CDRs
     * @throws InterruptedException si ocurre un error en la espera de la proxima ejecución
     */
    private void run() throws IOException, InterruptedException{
        int maxProcess = Integer.valueOf(configurations.getProperty(MAX_PROCESS_PROPERTY) );
        int waitTime = Integer.valueOf( configurations.getProperty(WAIT_TIME_PROPERTY) );
        executor = Executors.newFixedThreadPool(maxProcess);
        Path mainFolder = Paths.get(configurations.getProperty(ASN1_MAIN_FOLDER_PROPERTY));

        while (true) {
            Files.list(mainFolder).forEach(schemaPath -> process(schemaPath));
            Thread.sleep(waitTime);
        }
    }
    
    /**
     * Lee las carpetas <code>CDR_INPUT_FOLDER</code> y <code>CDR_IN_PROCESS_FOLDER</code>, 
     * valida su disponibilidad y ejecuta el decodificador correspondiente
     * @see ProcessThread
     * @see Util#getPropertiesConfigFile(java.lang.String) 
     * @see Util#getPath(java.nio.file.Path, java.lang.String) 
     * @see Util#moveFolder(java.nio.file.Path, java.nio.file.Path) 
     * @see DaemonConfiguration#CDR_EXECUTABLE_FOLDER
     * @see DaemonConfiguration#EXE_PROPERTIES_FILENAME
     * @see DaemonConfiguration#PACKAGE_NAME_PROPERTY
     * @see DaemonConfiguration#CDR_INPUT_FOLDER
     * @see DaemonConfiguration#CDR_IN_PROCESS_FOLDER
     * @param schemaPath    directorio de esquema (air,ccn,swp...)
     */
    private void process(Path schemaPath) {
        try {
            asn1ExeConfigDir = schemaPath.toString() + File.separator + CDR_EXECUTABLE_FOLDER + File.separator + EXE_PROPERTIES_FILENAME;
            Properties exeConfiguratios = Util.getPropertiesConfigFile(asn1ExeConfigDir);
            String packageName = exeConfiguratios.getProperty(PACKAGE_NAME_PROPERTY);
            Path inPath  = Util.getPath(schemaPath, CDR_INPUT_FOLDER);
            Path prcPath = Util.getPath(schemaPath, CDR_IN_PROCESS_FOLDER);
            boolean proceed = Files.list(inPath).toArray().length > 0 && Files.list(prcPath).toArray().length == 0;
            
            if( proceed ){
                Util.moveFolder(inPath, prcPath);
                Runnable worker = new ProcessThread(schemaPath, packageName);                
                executor.execute(worker);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
