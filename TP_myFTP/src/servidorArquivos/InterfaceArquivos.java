package servidorArquivos;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceArquivos extends Remote{
    public boolean criaPasta(String nome, String user) throws RemoteException, IOException;
    public boolean entraPasta(String caminho, String args[]) throws RemoteException, IOException;
    public boolean removePasta(String caminho, String args[]) throws RemoteException, IOException;
   /* public boolean listaArquivos(String caminho, String args[]) throws RemoteException, IOException;
    public boolean put(String caminho, String arquivo, String args[]) throws RemoteException, IOException;
    public boolean get(String caminho, String arquivo, String args[]) throws RemoteException, IOException;*/
}