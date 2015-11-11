package lectorentrada;
                                            
public class Configuracion{
    private String basedatos;
    private String usuario;
    private String password;
    private String ip;

    public String getBasedatos (){
        return basedatos;
    }

    public void setBasedatos (String basedatos){
        this.basedatos = basedatos;
    }

    public String getUsuario (){
        return usuario;
    }

    public void setUsuario (String usuario){
        this.usuario = usuario;
    }

    public String getPassword (){
        return password;
    }

    public void setPassword (String password){
        this.password = password;
    }

    public String getIp (){
        return ip;
    }

    public void setIp (String ip){
        this.ip = ip;
    }

    @Override
    public String toString(){
        return "ClassPojo [basedatos = "+basedatos+", usuario = "+usuario+", password = "+password+", ip = "+ip+"]";
    }
}