
import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceArquivos extends Remote{
	public boolean criaPasta(String nome, String user) throws RemoteException, IOException;
	public boolean entraPasta(String caminho, String user) throws RemoteException, IOException;
	public String listaArquivos() throws RemoteException, IOException;
	public boolean put(String nome, byte[] arquivo) throws RemoteException, IOException;
	public File get(String caminho, String arquivo) throws RemoteException, IOException;
	public boolean criaPasta(String user)throws RemoteException, IOException;
	public boolean removePasta(String user)throws RemoteException, IOException;
}