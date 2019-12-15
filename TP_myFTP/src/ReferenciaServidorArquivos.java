
import java.rmi.Naming;

public class ReferenciaServidorArquivos{
    public static void main(String[] argv){
        try{
            System.out.println("Referenciando servidor...");
            Naming.rebind("Servidor02", new ServerArquivos());
        }catch(Exception e){
            System.out.println("Ocorreu um problema no servidor!\n" + e.toString());
        }
    }
}