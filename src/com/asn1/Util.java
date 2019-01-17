package com.asn1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 *
 * @author Efrain.Blanco
 */
public class Util {
    
    public static Path getPath(Path path, String forlderName) throws IOException{
        return Files.list(path).filter(folder -> {
            return folder.toString().endsWith(forlderName) ;
                }).findFirst().get();
    }
    
    public static void moveFolder(Path src, Path dest) throws IOException {
        Files.list(src).forEach( pathFile ->{
            try {
                Files.walk( pathFile).forEach(source -> move(source, dest.resolve(src.relativize(source))) );
            } catch (IOException ex) {
               ex.printStackTrace();
            }
        });
    }
    
    public static void move(Path source, Path dest) {
        try {
            Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static Properties getPropertiesExeConfigFile(String asn1ExeConfigDir) throws Exception{
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream(asn1ExeConfigDir);
        prop.load(input);
        return prop;
    }
}
