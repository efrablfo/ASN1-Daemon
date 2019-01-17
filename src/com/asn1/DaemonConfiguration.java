package com.asn1;

/**
 *
 * @author Efrain.Blanco
 */
interface DaemonConfiguration {
    public static final String CMD_JAR_STMT = "java -jar ";
    public static final String EXPORTS_ASN_STMT = "EXPORTS";
    public static final String CDR_IN_PROCESS_FOLDER = "prc";
    public static final String CDR_INPUT_FOLDER = "in";
    public static final String ASN_EXTENSION = ".asn";
    public static final String JAR_EXTENSION = ".jar";
    public static final String CDR_ASN_FOLDER = "asn";
    public static final String CDR_EXECUTABLE_FOLDER = "exe";
    public static final String CONFIG_PROPERTIES_FILENAME = "config.properties";
    public static final String ASN1_MAIN_FOLDER_PROPERTY = "asn1-folder-cdr";
    public static final String MAX_PROCESS_PROPERTY = "max-process";
    public static final String PROJECT_PATH = "user.dir";
    public static final String EXE_PROPERTIES_FILENAME = "exe.properties";
    public static final String PACKAGE_NAME_PROPERTY_NAME = "package-name";
}
