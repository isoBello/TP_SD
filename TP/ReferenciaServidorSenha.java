import java.rmi.*;

public class ReferenciaServidorSenha{
    public static void main(String[] argv){
        try{
            System.out.println("Referenciando servidor...");
            Naming.rebind("Servidor01", new ServerSenha());
        }catch(Exception e){
            System.out.println("Ocorreu um problema no servidor!\n" + e.toString());
        }
    }
}