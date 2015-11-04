/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lectorentrada;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Francisco
 */
public class ConexionSQL_Local {
        public static Connection alternativeConnection() throws Exception{
        Connection conexion=null;
        
        try{
    	    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	    String url = "jdbc:sqlserver://localhost;databaseName=TCADBGNS;user=sa;password=tinkblucon2;";
    	    conexion= DriverManager.getConnection(url);
    	    //System.out.println("Conexión con SQL Server Exitosa");
    	}
    	catch(ClassNotFoundException ex){
    	    JOptionPane.showMessageDialog(new JFrame(),"Error en la Conexión con la BD 1 : "+ex.getMessage());
    	    conexion=null;
    	}
    	catch(SQLException ex){
            JOptionPane.showMessageDialog(new JFrame(),"Error en la Conexión con la BD 2: "+ex.getMessage());
            conexion=null;
    	}
    	catch(Exception ex){
    	    JOptionPane.showMessageDialog(new JFrame(),"Error en la Conexión con la BD 3 : "+ex.getMessage());
            conexion=null;
    	}
    	finally{   
    	return conexion;
    	}
        
    }
}
