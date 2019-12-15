
import java.io.IOException;
import java.rmi.*;

public class ClientArquivos {
	public ClientArquivos(){
        System.out.println("Referenciando cliente...");
        try{
            msi = (InterfaceArquivos) Naming.lookup("rmi://127.0.0.1/Servidor01");
        }catch(Exception e){
            System.out.println("A referência falhou.\n" + e);
            System.out.println("Certifique-se que o Servidor de Registros e a Aplicação Servidora estão funcionando.");
            System.exit(0);
        }
    }
	
	public boolean criaPasta(String nome, String user) throws IOException{
        return msi.criaPasta(nome, user);
    }
	
	public static void main(String[] args){
        ClientArquivos client = new ClientArquivos();
        try{
            System.out.println("Deu: " + client.criaPasta(args[0], args[1]));
        }catch (Exception e){
            System.out.println("Exceção durante as chamadas remotas");
            e.printStackTrace();
        }
    }
	
	private InterfaceArquivos msi;
}
