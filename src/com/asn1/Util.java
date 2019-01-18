package com.asn1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * Clase utilitaria para el manejo de directorios y archivos de configuración
 * @see java.nio.file
 * @author Efrain Blanco
 * @author Jhon Fernandez
 * @version 1.0
 */
public class Util {
    
    /**
     * Obtiene objeto <code>java.nio.file.Path</code> partiendo de un 
     * directorio base y el nombre del folder o archivo a buscar
     * @param basePath  directorio base
     * @param forlderName   nombre de folder o archivo a buscar
     * @return objeto <code>java.nio.file.Path</code> (ubicación del folder o archivo)
     * @throws IOException  si hay algun error con el directorio base
     */
    protected static Path getPath(Path basePath, String forlderName) throws IOException {
        return Files.list(basePath).filter(folder -> {
            return folder.toString().endsWith(forlderName);
        }).findFirst().get();
    }
    
    /**
     * Mueve el contenido de un folder a un directorio especifico
     * @see #move(java.nio.file.Path, java.nio.file.Path) 
     * @param src   path origen
     * @param dst   path destino 
     * @throws IOException  si ocurre un error moviendo los archivos
     */
    protected static void moveFolder(Path src, Path dst) throws IOException {
        Files.list(src).forEach(pathFile -> {
            try {
                Files.walk(pathFile).forEach(source -> move(source, dst.resolve(src.relativize(source))));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    /**
     * Mueve un arhivo a un directorio especifico
     * @param src   path origen
     * @param dst   path destino 
     */
    protected static void move(Path src, Path dst) {
        try {
            Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * Retorna objeto con la información de archivo de configuración
     * @param configFileDir directorio del archivo de configuración
     * @return Objeto con la informacion del archivo de configuración
     * @throws FileNotFoundException    si no encuentra arhivo de configuración
     * @throws IOException  si no fue posible cargar el archivo de configuración  
     */
    protected static Properties getPropertiesConfigFile(String configFileDir) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream(configFileDir);
        prop.load(input);
        return prop;
    }
}
