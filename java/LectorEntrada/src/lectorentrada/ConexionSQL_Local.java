package lectorentrada;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Francisco
 */
public class ConexionSQL_Local {
        public static Connection alternativeConnection() throws Exception{
        Connection conexion=null;
                ObjectMapper mapperLectura = new ObjectMapper(); // can reuse, share globally
                ConfigLectura configLectura = mapperLectura.readValue(new File("C:\\Program Files\\LectorHuella\\ConfiguracionLocal.json"), ConfigLectura.class);
        try{
    	    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	    String url = "jdbc:sqlserver:// "+ configLectura.getIp()+" ;databaseName= "+configLectura.getBasedatos()+" ; "
            + "user= "+configLectura.getUsuario()+" ;password = " +configLectura.getPassword()+ ";";
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
