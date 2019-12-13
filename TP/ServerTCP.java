import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ServerTCP {
    public static void main(String[] args) throws IOException{
        int port = Integer.valueOf(args[0]);

        ServerSocket socket = new ServerSocket(port);

        while(true){
            Socket s = null;

            try{
            s = socket.accept();
            System.out.println("Novo cliente conectado!");
            DataInputStream input = new DataInputStream(s.getInputStream());
            DataOutputStream output = new DataOutputStream(s.getOutputStream());

            System.out.println("Atribuindo nova thread para o cliente.");

            Thread thread = new ClientHandler(s, input, output);
            thread.start();
            }catch(Exception e){
                if(socket != null && socket.isClosed()){
                    try{
                        socket.close();
                    }catch(IOException io){
                        io.printStackTrace();
                    }
                }
                s.close();
                System.out.println("Excceção: " + e.toString());
            }
        }   
    }
}

class ClientHandler extends Thread{
    final DataInputStream input;
    final DataOutputStream output;
    final Socket socket;
    Console cons = System.console();
    ArrayList<String> users = new ArrayList<String>();
    ArrayList<String> passwords = new ArrayList<String>();
    String pass;
    String keyPass = "vkrkq9VVjxuRt5vZ";

    public ClientHandler(Socket socket, DataInputStream input, DataOutputStream output){
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run(){
        String received;
        String returns;

        while(true){
            try{
                output.writeUTF("O que você quer? [Login | root]:\n" + "Digite Exit para terminar a conexão.");
                received = input.readUTF();

                if(received.equals("Exit")){
                    System.out.println("Cliente " + this.socket + " enviou exit...");
                    System.out.println("Encerrando a conexão.");
                    this.socket.close();
                    System.out.println("Conexão encerrada!");
                    break;
                }
                
                switch(received){
                    case "Login":
                        output.writeUTF("Digite o usuário: \n" + "Digite Exit para terminar a conexão.");
                        received = input.readUTF();
                        users.add(received);
                        returns = received;
                        output.writeUTF("Bem vindo, " + returns);
                        output.writeUTF("Digite sua senha: \n" + "Digite Exit para terminar a conexão.");
                        received = input.readUTF();
                        pass = criptografaString(received);
                        passwords.add(pass);
                        break;
                    case "root":
                        output.writeUTF("Iniciando conexão segura..." + "Digite Exit para terminar a conexão.");
                        output.writeUTF("Digite a senha do root: \n");
                        received = input.readUTF();
                        pass = criptografaString(received);
                        break;
                    default:
                        output.writeUTF("Entrada inválida.");
                        break;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        try{
            this.input.close();
            this.output.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private String criptografaString(String pass){
        SecretKey key = new SecretKeySpec(keyPass.getBytes(), "AES");
        String fs = new String("false");

        try {
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.ENCRYPT_MODE, key);	 	 	 	 
            byte[] msg = cipher.doFinal(pass.getBytes());	 	 	 
            return msg.toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return fs;
    }

    private String descriptografaString(String pass){
        SecretKey key = new SecretKeySpec(keyPass.getBytes(), "AES");
        String fs = new String("false");

        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] msg = cipher.doFinal(pass.getBytes());
            String senha = new String(msg);
            return senha;
        }catch(Exception e){
            e.printStackTrace();
        }
        return fs;
    }

    public ArrayList<String> getUsers(){
        return users;
    }

    public ArrayList<String> getPass(){
        return passwords;
    }
}

class ClientSenha{
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

    public void retornaChamada(String chamada, ClientSenha client, String args[]){
        if(chamada.equals("verificaSenha")){
            try{
                client.verificaSenha(args[1], args[2], args[3]);
            }catch(Exception e){
                System.out.println("Exceção durante as chamadas remotas: " + e);
            }
        }else if(chamada.equals("addUser")){
            try{
                client.addUser(args[1], args[2], args[3], args[4]);
            }catch(Exception e){
                System.out.println("Exceção durante as chamadas remotas: " + e);
            }
        }else if(chamada.equals("removeUser")){
            try{
                client.removeUser(args[1], args[2], args[3], args[4]);
            }catch(Exception e){
                System.out.println("Exceção durante as chamadas remotas: " + e);
            }
        }else{
            System.out.println("Erro durante a chamada. Verifique o método.");
        }
    }

    public boolean verificaSenha(String pass, String root, String passRoot) throws RemoteException{
        return msi.verificaSenha(pass, root, passRoot);
    }
    public boolean addUser(String user, String pass, String root, String passRoot) throws RemoteException{
        return msi.addUser(user, pass, root, passRoot);
    }
    public boolean removeUser(String user, String pass, String root, String passRoot) throws RemoteException{
        return msi.removeUser(user, pass, root, passRoot);
    }
    public static void main(String[] argv){
        ClientSenha client = new ClientSenha();
        try{
            client.retornaChamada(argv[0], client, argv);
        }catch (Exception e){
            System.out.println("Exceção durante as chamadas remotas: " + e);
        }
    }
    private InterfaceSenha msi;
}