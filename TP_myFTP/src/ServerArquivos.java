
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;

@SuppressWarnings("serial")
public class ServerArquivos extends UnicastRemoteObject implements InterfaceArquivos{
	private String caminho = "home/";
	
	public ServerArquivos() throws RemoteException{
        System.out.println("Servidor de arquivos iniciando...");
    }
	
	public boolean criaPasta(String user) throws IOException{
		this.caminho = caminho + user;
        File pasta = new File(caminho);  
        if (pasta.mkdir()) {   
            return true;
        } 
        else { 
            return false;
        } 
	}
	
	public boolean criaPasta(String nome, String user) throws IOException {
		this.caminho = caminho + user + nome;
        File pasta = new File(caminho); 
        if (pasta.mkdir()) {   
            return true;
        } 
        else { 
            return false;
        } 
	}
	
	public boolean entraPasta(String nome, String user) throws IOException{
		this.caminho = caminho + user + nome;
		File pasta = new File(caminho); 
		this.caminho = pasta.getPath();
		return true;
	}
	
	public boolean listaArquivos(String opt) throws IOException{
		if(caminho != opt) {
			this.caminho = opt;
		}
		
		File pasta = new File(caminho);
		String[]entries = pasta.list();
		
		if(entries.length > 0) {
			for(String s: entries){
			    System.out.println(s);
			    return true;
			}	
		}
		return false;
	}
	
	public boolean put(String opt, String arquivo) throws IOException{
		if(caminho != opt) {
			this.caminho = opt + arquivo;
		}
		
		File f = new File(caminho);
		f.getParentFile().mkdirs(); 
		f.createNewFile();
		return true;
	}
	
	public File get(String opt, String arquivo) throws IOException{
		if(caminho != opt) {
			this.caminho = opt;
		}
		
		File pasta = new File(caminho);
		File[] entries = pasta.listFiles();
		String[] entrys = pasta.list();
		
		if(entries.length > 0) {
			for(File s: entries){
			    for(String r: entrys) {
			    	if(r.equals(arquivo)) {
			    		if(r.equals(s.getName()))
			    			return s;
			    	}
			    }
			}	
		}
		return null;
	}
	
	public boolean removePasta(String nome, String user) throws IOException{
		this.caminho = caminho + user + nome;
        File pasta = new File(caminho); 
        
		String[]entries = pasta.list();
		
		if(entries.length > 0) {
			for(String s: entries){
			    File currentFile = new File(pasta.getPath(),s);
			    currentFile.delete();
			}	
		}
		
		pasta.delete();
		return true;
	}	
	
	public boolean removePasta(String user) throws IOException{
		this.caminho = caminho + user;
        File pasta = new File(caminho); 
        
		String[]entries = pasta.list();
		
		if(entries.length > 0) {
			for(String s: entries){
			    File currentFile = new File(pasta.getPath(),s);
			    currentFile.delete();
			}	
		}
		
		pasta.delete();
		return true;
	}
}
