package servidorSenha;
import java.rmi.*;

public interface InterfaceSenha extends Remote{
    public boolean verificaSenha(String pass, String root, String passRoot) throws RemoteException;
    public boolean addUser(String user, String pass, String root, String passRoot) throws RemoteException, ClassNotFoundException;
    public boolean removeUser(String user, String pass, String root, String passRoot) throws RemoteException;
}