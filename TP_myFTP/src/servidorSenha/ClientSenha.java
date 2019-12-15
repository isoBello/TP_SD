package servidorSenha;
import java.rmi.*;
import java.sql.SQLException;

public class ClientSenha{
    public ClientSenha(){
        System.out.println("Referenciando cliente...");
        try{
            msi = (InterfaceSenha) Naming.lookup("rmi://127.0.0.1/Servidor01");
        }catch(Exception e){
            System.out.println("A referência falhou.\n" + e);
            System.out.println("Certifique-se que o Servidor de Registros e a Aplicação Servidora estão funcionando.");
            System.exit(0);
        }
    }

    public boolean verificaSenha(String pass, String root, String passRoot) throws RemoteException{
        return msi.verificaSenha(pass, root, passRoot);
    }
    public boolean addUser(String user, String pass, String root, String passRoot) throws RemoteException, ClassNotFoundException{
        return msi.addUser(user, pass, root, passRoot);
    }
    public boolean removeUser(String user, String pass, String root, String passRoot) throws RemoteException{
        return msi.removeUser(user, pass, root, passRoot);
    }
    public static void main(String[] args){
        ClientSenha client = new ClientSenha();
        try{
            //System.out.println("Deu: " + client.addUser(args[0], args[1], args[2], args[3]));
        }catch (Exception e){
            System.out.println("Exceção durante as chamadas remotas");
            e.printStackTrace();
        }
    }
    private InterfaceSenha msi;
}