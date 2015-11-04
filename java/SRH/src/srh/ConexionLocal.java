package srh;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Francisco
 */
public class ConexionLocal {
    	
    /*Conexión a DB*/
    @SuppressWarnings("finally")
    public static Connection createConnection() throws Exception {
    	        Connection conexion=null;
    	     
    	        try{
    	            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	            String url = "jdbc:sqlserver://10.106.18.43;databaseName=TCADBGNS;user=sa;password=tinkblucon2;";
    	            conexion= DriverManager.getConnection(url);
    	            //System.out.println("Conexión con SQL Server Exitosa");
                    //JOptionPane.showMessageDialog(new JFrame(), "Conexion SQL Exitosa");
    	        }
    	        catch(ClassNotFoundException ex){
    	            System.out.println("Error1 en la Conexión con la BD "+ex.getMessage());
    	            conexion=null;
    	        }
    	        catch(SQLException ex){
    	        	System.out.println("Error1 en la Conexión con la BD "+ex.getMessage());
    	            conexion=null;
    	        }
    	        catch(Exception ex){
    	        	System.out.println("Error1 en la Conexión con la BD "+ex.getMessage());
    	            conexion=null;
    	        }
    	        finally{   
    	            return conexion;
    	        }
    	 }
}
