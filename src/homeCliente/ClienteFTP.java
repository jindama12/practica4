package homeCliente;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Scanner;

public class ClienteFTP {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        FTPClient cliente = new FTPClient();
        String serverFTP = "192.168.1.136";
        System.out.print("Nombre de usuario: ");
        String usuario = sc.nextLine();

        boolean identificado = false;
        String clave = "";
        if (!usuario.equals("anonymous") && !usuario.equals("ftp")) {
            System.out.print("Contraseña: ");
            clave = sc.nextLine();
            identificado = true;

        }

        try {
            cliente.connect(serverFTP);
            System.out.println(cliente.getReplyString());

            int codigo = cliente.getReplyCode();
            System.out.println("Código:"+codigo);

            if (!FTPReply.isPositiveCompletion(codigo)) {
                cliente.disconnect();
                System.out.println("Conexión rechazada");
                System.exit(0);
            }

            if (!cliente.login(usuario, clave)) {
                cliente.disconnect();
                System.out.println("Datos incorrectos");
                System.exit(0);
            }

            cliente.enterLocalPassiveMode();

            int seleccion;

            do {
                FTPFile[] archivos = cliente.listFiles();

                if (identificado) {
                    menuUsuario();
                } else {
                    menuAnonymous();
                }
                seleccion = sc.nextInt();
                sc.nextLine();

                switch (seleccion) {
                    case 1:
                        if (archivos.length == 0) {
                            System.out.println("El directorio está vacío");
                        } else {
                            System.out.println("Lista de ficheros");

                            for (FTPFile archivo : archivos) {
                                System.out.println("\t" + archivo.getName() + "=>" + archivo.getType());
                            }
                        }
                        break;
                    case 2:
                        System.out.print("¿Qué fichero quieres descargar?: ");
                        String fichero = sc.nextLine();

                        String busqueda = null;

                        boolean existeArchivo = false;
                        for (FTPFile archivo : archivos) {
                            if (archivo.getName().equals(fichero)) {
                                existeArchivo = true;
                                busqueda = archivo.getName();
                            }
                        }

                        if (!existeArchivo) {
                            System.out.println("El archivo indicado no existe");
                        } else {
                            OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(fichero)));
                            cliente.retrieveFile(fichero, os);
                            os.close();
                        }
                        break;
                    case 3:
                        if (identificado) {
                            System.out.print("Dime el nombre del archivo a subir: ");
                            String archivo = sc.nextLine();

                            File f = new File(archivo);

                            if (f.exists()) {
                                FileInputStream fis = new FileInputStream(archivo);
                                cliente.storeFile(archivo, fis);
                                fis.close();
                            } else {
                                System.out.println("El archivo indicado no existe actualmente");
                            }
                        }
                        break;
                }
            } while (seleccion != 0);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void menuUsuario() {
        System.out.println("""
                1 - Mostrar lista de ficheros
                2 - Descargar archivo
                3 - Subir archivo
                0 - Desconectarse
                """);
    }

    public static void menuAnonymous() {
        System.out.println("""
                1 - Mostrar lista de ficheros
                2 - Descargar archivo
                0 - Desconectarse
                """);
    }
}
