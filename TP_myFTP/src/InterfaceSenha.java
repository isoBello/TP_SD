
import java.io.IOException;
import java.rmi.*;

public interface InterfaceSenha extends Remote{
    public boolean verificaSenha(String user, String pass, String root, String passRoot) throws RemoteException, ClassNotFoundException;
    public boolean addUser(String user, String pass, String root, String passRoot) throws RemoteException, ClassNotFoundException, IOException;
    public boolean removeUser(String user, String pass, String root, String passRoot) throws RemoteException, ClassNotFoundException, IOException;
}