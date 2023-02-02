/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConectPG {

    //--> Para salvar o mantener viva la conexion
    Connection con;
    String cadenaConexion = "jdbc:postgresql://localhost:5432/mvc2023";
    // ---> Se puede configurar para tener usuarios con menos privilegios, como que no puede borrar la base o tablas
    String pgUsuario = "postgres";
    String pgPassword = "1234";

    public ConectPG() {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConectPG.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            con = DriverManager.getConnection(cadenaConexion, pgUsuario, pgPassword);
        } catch (SQLException ex) {
            Logger.getLogger(ConectPG.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ResultSet consulta(String sql) {
        try {
            //--> Crear setencia
            Statement st = con.createStatement();
            return st.executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(ConectPG.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // --> Puede ser un SQLException o un boolean
    public boolean accion(String sql) {
        try {
            //Puede ser un INSERT-UPDATE-DELETE
            Statement st = con.createStatement();
            st.execute(sql);
            st.close();//Cierrar la conexion.
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ConectPG.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public Connection getCon() {
        return con;
    }
}
