package servidorArquivos;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;

public class ServerArquivos extends UnicastRemoteObject implements InterfaceArquivos{
	private String caminho = "~Documents/home/";
	
	public ServerArquivos() throws RemoteException{
        System.out.println("Servidor de arquivos iniciando...");
    }
	
	public boolean criaPasta(String nome, String user) throws IOException {
		this.caminho = caminho + user;
        File pasta = new File(caminho); 
        System.out.println(caminho);
        if (pasta.mkdir()) {   
            return true;
        } 
        else { 
            return false;
        } 
        
		/*String[] args = new String[] {"/bin/bash", "-c", "mkdir", caminho, nome};
		Process proc = new ProcessBuilder(args).start();
		proc.getInputStream();
		return true;*/
	}
	
	public boolean entraPasta(String nome, String argv[]) throws IOException{
		this.caminho = caminho + argv[1];
		String[] args = new String[] {"/bin/bash", "-c", "cd", caminho, nome};
		Process proc = new ProcessBuilder(args).start();
		proc.getInputStream();
		return true;
	}
	
	public boolean removePasta(String nome, String argv[]) throws IOException{
		this.caminho = caminho + argv[1];
		String[] args = new String[] {"/bin/bash", "-c", "cd", caminho, nome};
		Process proc = new ProcessBuilder(args).start();
		proc.getInputStream();
		return true;		
	}	
}
