INSTRUCTIVO

-Configurar propiedades del archivo .../dist/config.properties:
1. max-process = Numero maximo de decodificadores que permitirá el ThreadPool.
2. asn1-folder-cdr = Ruta de folder principal de CDRs. (La misma ruta de <cdr-dir> del aplicativo <AntAsn1>).
3. wait-time = Tiempo de espera en milisegundos para ejecutar el siguiente proceso.

-Copiar CDRs a folder de lectura.
1. Ubicar el folder <asn1-folder-cdr>/<esquema>/in/ y copiar los archivos CDRs que desee decodificar.

-Ejecutar aplicativo:
1. Abrir consola CMD.
2. Entrar al folder que contiene el jar  [ cd .../ASN1Daemon/dist/ ]
3. Ejecutar jar [ java -jar ASN1Daemon.jar ]