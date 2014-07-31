package pkgnew.books.checker;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import documentation.manager.DocEntry;
import documentation.manager.RedundantInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import pkgnew.books.checker.filters.Extensions;
import pkgnew.books.checker.filters.Filter;
import pkgnew.books.checker.filters.Tamano;


import java.net.UnknownHostException;
import java.util.Set;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public final class NewBooksChecker {

    Connection con;
  // connect to the local database server
        MongoClient mongoClient;
        DB db;
         DBCollection signs;
        
        public NewBooksChecker() throws UnknownHostException{
             mongoClient = new MongoClient();
             db = mongoClient.getDB("docsManager");
             signs = db.getCollection("signs");
        }

    public void saveCRC(File file) throws SQLException, IOException {

        System.out.println("Salvando: " + file.getAbsolutePath());

        Tuple2<String, String> tup = obtenerFirma(file);

        BasicDBObjectBuilder b = BasicDBObjectBuilder.start("name", file.getName());
        b.add("parent", file.getParent().replaceAll("\\\\", "/").toLowerCase());
        b.add("md5", tup.getA());
        b.add("sha", tup.getB());
        b.add("len", file.length());
        b.add("time", java.lang.System.currentTimeMillis());
        
        signs.insert(b.get());
        
//        try (PreparedStatement ps = (PreparedStatement) con.prepareStatement("INSERT INTO signs VALUES (?, ?, ?, ?, ?, ?)")) {
//
//            ps.setString(1, file.getParent().replaceAll("\\\\", "/"));
//
//            ps.setString(2, file.getName());
//
//            ps.setString(3, tup.getA());
//
//            ps.setString(4, tup.getB());
//
//            ps.setLong(5, file.length());
//
//            ps.setLong(6, java.lang.System.currentTimeMillis());
//
//            ps.execute();
//
//        }

    }

    private static Tuple2<String, String> obtenerFirma(File file) {

        Tuple2<String, String> res = new Tuple2<>();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5"); // Inicializa MD5
            MessageDigest messageDigest2 = MessageDigest.getInstance("SHA"); // Inicializa SHA-1

            try {
                try (InputStream archivo = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int fin_archivo = -1;
                    int caracter;
                    //                    messageDigest2.update(buffer, 0, caracter);
                    caracter = archivo.read(buffer);

                    while (caracter != fin_archivo) {
                        //                    c+= 1;
                        //                    System.out.println("Analisis del Mb número: " + c + " del fichero " + file.getName());
                        messageDigest.update(buffer); // Pasa texto claro a la función resumen
                        messageDigest2.update(buffer);
                        //                    messageDigest.update(buffer, 0, caracter); // Pasa texto claro a la función resumen
                        //                    messageDigest2.update(buffer, 0, caracter);
                        caracter = archivo.read(buffer);
                    }
                }
                byte[] resumen = messageDigest.digest(); // Genera el resumen MD5
                byte[] resumen2 = messageDigest2.digest(); // Genera el resumen SHA-1

                //Pasar los resumenes a hexadecimal

                String s = "";
                for (int i = 0; i < resumen.length; i++) {
                    s += Integer.toHexString((resumen[i] >> 4) & 0xf);
                    s += Integer.toHexString(resumen[i] & 0xf);
                }

                //System.out.println("Resumen MD5: " + s);
                res.setA(s);

                String m = "";
                for (int i = 0; i < resumen2.length; i++) {
                    m += Integer.toHexString((resumen2[i] >> 4) & 0xf);
                    m += Integer.toHexString(resumen2[i] & 0xf);
                }

                res.setB(m);
                //System.out.println("Resumen SHA-1: " + m);

            } catch (java.io.FileNotFoundException fnfe) {
            } catch (java.io.IOException ioe) {
            }

        } catch (java.security.NoSuchAlgorithmException nsae) {
        }

        return res;
    }

    boolean isValid(File f) {

        Filter[] fs = new Filter[]{new Tamano(), new Extensions()};

        for (Filter f1 : fs) {
            f1.setFile(f);
        }
        boolean flag = true;
        int i = 0;
        while (i < fs.length && flag) {
            if (!fs[i].pass()) {
                flag = false;
            }
            i++;
        }
        return flag;
    }

    public void walk(File f) throws Exception {
        if (f.isFile()) {
            if (isValid(f)) {
                saveCRC(f);
            }
        } else {
            for (File f2 : f.listFiles()) {
                walk(f2);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        NewBooksChecker nbc = new NewBooksChecker();
//        NewBooksChecker nbc = new NewBooksChecker("jdbc:postgresql://localhost:5432/LibraryDB", "jalbert", "aa");
//        nbc.walk(new File("d:/Documentation/Mathematics/"));
//        nbc.walk(new File("E:/Albert/Mis documentos/Teaching/"));
//        nbc.walk(new File("E:/Albert/Mis documentos/Compendiums (Encyclopedias et al)/"));
//        nbc.walk(new File("E:/Documentation/"));
//        nbc.walk(new File("e:/Documentation/Version Control/"));

//        ArrayList<String> ch = nbc.checkRepited(1, 100);
//        
//        for (String s : ch) {
//            System.out.println(s);
//        }
        ArrayList<DocEntry> ch = nbc.checkFileNameExistence("Probability and Statistics for Computer Science");

        for (DocEntry s : ch) {
            System.out.println(s);
        }

        System.out.println("Encontrados: " + ch.size() + " casos.");


    }

    public RedundantInfo redundantInfo() throws SQLException {

        RedundantInfo result = new RedundantInfo();

        ArrayList<String> temp = new ArrayList<>();
        final String sql = "SELECT *"
                + " FROM con_repeticiones;";

        try (PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql)) {

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {

                while (rs.next()) {
                    temp.add(rs.getString("md5"));
                }

                result.frepetidos = 0;

                for (String md5 : temp) {

                    final String sql1 = "SELECT *"
                            + " FROM signs"
                            + " WHERE signs.md5 = ?;";

                    try (PreparedStatement ps1 = (PreparedStatement) con.prepareStatement(sql1)) {

                        ps1.setString(1, md5);

                        ps1.execute();

                        try (ResultSet rs1 = ps1.getResultSet()) {
                            rs1.next();
                            while (rs1.next()) {
                                result.frepetidos++;
                                result.mbRedundantes += rs1.getLong("size");
                            }

                        }
                    }

                }
            }
        }

        result.mbRedundantes = result.mbRedundantes / (1024 * 1024);

        return result;

    }

    public void purgRep() throws SQLException {

        final String sql = "SELECT *"
                + " FROM con_repeticiones;";

        try (PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql)) {

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {

                while (rs.next()) {

                    final String sql1 = "SELECT *"
                            + " FROM signs as s"
                            + " WHERE s.md5 = ?"
                            + " ORDER by s.chmili ASC;";

                    try (PreparedStatement ps1 = (PreparedStatement) con.prepareStatement(sql1)) {

                        ps1.setString(1, rs.getString("md5"));

                        ps1.execute();

                        try (ResultSet rs1 = ps1.getResultSet()) {
                            rs1.next();
                            while (rs1.next()) {
                                String fpath = rs1.getString("file_path") + "/" + rs1.getString("file_name");

                                System.out.println("Eliminando: " + fpath);

                                new File(fpath).delete();

                            }

                        }
                    }

                }
            }
        }

    }

    public ArrayList<DocEntry> checkRepited(int from, int to) throws SQLException {

        ArrayList<String> temp = new ArrayList<>();
        ArrayList<DocEntry> res = new ArrayList<>();

        final String sql = "SELECT *"
                + " FROM con_repeticiones;";

        try (PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql)) {

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {

                int i = 1;

                while (i < from && rs.next()) {
                    i++;
                }
                while (rs.next() && i <= to) {
                    temp.add(rs.getString("md5"));
                    i++;
                }

                for (String md5 : temp) {
                    final String sql1 = "SELECT *"
                            + " FROM signs"
                            + " WHERE signs.md5 = ?;";
                    try (PreparedStatement ps1 = (PreparedStatement) con.prepareStatement(sql1)) {
                        ps1.setString(1, md5);
                        ps1.execute();

                        try (ResultSet rs1 = ps1.getResultSet()) {
                            while (rs1.next()) {
                                res.add(new DocEntry(rs1.getString("file_name"), rs1.getString("file_path"), rs1.getLong("size")));
                            }
                            res.add(new DocEntry("**********************************************************************************************************************************************************************************************", "", 0L));
                        }
                    }

                }
            }
        }
        return res;
    }

    public ArrayList<String> checkCRC(File file) throws SQLException, IOException {
        ArrayList<String> res = new ArrayList<>();
        Tuple2<String, String> tup = obtenerFirma(file);
        final String sql = "SELECT *"
                + " FROM signs AS s"
                + " WHERE s.md5 = ?"
                + " OR s.sha = ?;";
        try (PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql)) {
            ps.setString(1, tup.getA());
            ps.setString(2, tup.getB());
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    res.add(rs.getString("file_name"));
                }
            }
        }
        return res;
    }

    public ArrayList<DocEntry> checkFileNameExistence(String substring) throws SQLException, IOException {
        
        ArrayList<DocEntry> res = new ArrayList<>();
        final String sql = "SELECT *"
                + " FROM signs"
                + " WHERE lower(signs.file_name) LIKE '%" + substring.toLowerCase() + "%';";

        try (PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql)) {
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    res.add(new DocEntry(rs.getString("file_name"), rs.getString("file_path"), rs.getLong("size")));
                }
            }
        }
        return res;
    }
    
    public ArrayList<DocEntry> checkPathNameExistence(String substring) throws SQLException, IOException {
        
        ArrayList<DocEntry> res = new ArrayList<>();
        
        final String sql = "SELECT *"
                + " FROM signs"
                + " WHERE lower(signs.file_path) LIKE '%" + substring.toLowerCase() + "%';";

        try (PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql)) {
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    res.add(new DocEntry(rs.getString("file_name"), rs.getString("file_path"), rs.getLong("size")));
                }
            }
        }
        return res;
    }

    public void cleanDB() throws SQLException {

        final String sql = "DELETE "
                + " FROM signs;";

        try (PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql)) {
            ps.execute();
        }

    }
}
