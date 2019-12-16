
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;

@SuppressWarnings("serial")
public class ServerArquivos extends UnicastRemoteObject implements InterfaceArquivos{
	private String caminho = "home/";
	
	public ServerArquivos() throws RemoteException{
		this.caminho = "home/";
		File pasta = new File(caminho); 
		if(!pasta.exists())
			pasta.mkdir();
        System.out.println("Servidor de arquivos iniciando...");
    }
	
	public boolean criaPasta(String nome) throws IOException{
		String path = caminho + "/" + nome;
        File pasta = new File(path);  
        if (pasta.mkdir()) {   
            return true;
        } 
        else { 
            return false;
        } 
	}
	
	public boolean criaPasta(String nome, String user) throws IOException {
		String path = caminho + "/" +  user + "/" + nome;
        File pasta = new File(path); 
        if (pasta.mkdir()) {   
            return true;
        } 
        else { 
            return false;
        } 
	}
	
	public boolean entraPasta(String nome, String user) throws IOException{
		if (nome.equals("..")) {
			caminho = caminho.substring(0, caminho.lastIndexOf("/"));
			System.out.println(caminho);
			return true;
		} else {
			this.caminho = caminho + "/" + nome;
			File pasta = new File(caminho); 
			this.caminho = pasta.getPath();
			System.out.println(caminho);
			return true;
		}
	}
	
	public String listaArquivos() throws IOException{

		File pasta = new File(caminho);
		String[]entries = pasta.list();
		String retorno = "";
		if(entries.length > 0) {
			for(String s: entries){
				retorno += s +"\n";
			}
		}
		return retorno;
	}
	
	public boolean put(String nome, byte[] arquivo) throws IOException{
		File f = new File(caminho + "/" + nome);
		FileOutputStream fw = new FileOutputStream(f);
		fw.write(arquivo);
		f.getParentFile().mkdirs();
		f.createNewFile();
		fw.close();
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
	
	
	public boolean removePasta(String nome) throws IOException{
		String path = caminho + "/" +nome;
		System.out.println("remove: " + path);
        File pasta = new File(path); 
        
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
