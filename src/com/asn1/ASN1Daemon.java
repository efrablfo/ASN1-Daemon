package com.asn1;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Efrain.Blanco
 */
public class ASN1Daemon implements DaemonConfiguration{
    private ExecutorService executor;    
    private static String configPath;
    private static String asn1ExeConfigDir;
    private static Properties configurations;
    
    static{
        try {
            configPath = System.getProperty(PROJECT_PATH);
            configurations = getPropertiesConfigFile();        
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
         try {
             ASN1Daemon daemon = new ASN1Daemon();
             daemon.run();
         } catch (Exception ex) {
             ex.printStackTrace();
         }
    }
    
    private void run() throws Exception {
        int maxProcess = Integer.valueOf(configurations.getProperty(MAX_PROCESS_PROPERTY) );
        executor = Executors.newFixedThreadPool(maxProcess);
        Path mainFolder = Paths.get(configurations.getProperty(ASN1_MAIN_FOLDER_PROPERTY));

        while (true) {
            Files.list(mainFolder).forEach(schemaPath -> process(schemaPath));
            Thread.sleep(1000);
        }
    }
    
    private void process(Path schemaPath) {
        try {
            asn1ExeConfigDir = schemaPath.toString() + File.separator + CDR_EXECUTABLE_FOLDER + File.separator + EXE_PROPERTIES_FILENAME;
            Properties exeConfiguratios = Util.getPropertiesExeConfigFile(asn1ExeConfigDir);
            String packageName = exeConfiguratios.getProperty(PACKAGE_NAME_PROPERTY_NAME);
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
    
    private static Properties getPropertiesConfigFile() throws Exception{
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream(configPath + File.separator + CONFIG_PROPERTIES_FILENAME);
        prop.load(input);
        return prop;
    }
}
