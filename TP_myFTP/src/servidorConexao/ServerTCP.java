package servidorConexao;

import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import servidorSenha.*;

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
                        returns = received;
                        output.writeUTF("Bem vindo, " + returns);
                        output.writeUTF("Digite sua senha: \n" + "Digite Exit para terminar a conexão.");
                        received = input.readUTF();
                        pass = criptografaString(received);
                        boolean request = conectToServerSenha();
                        
                        if(request == true) {
                        	output.writeUTF("Digite --help para saber os comandos válidos.\n" + "Digite seu comando ou Exit para terminar a conexão.");
                        }
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
    
    public boolean conectToServerSenha() {
    	System.out.println("Referenciando cliente...");
        try{
            msi = (InterfaceSenha) Naming.lookup("rmi://127.0.0.1/Servidor01");
        }catch(Exception e){
            System.out.println("A referência falhou.\n" + e);
            System.out.println("Certifique-se que o Servidor de Registros e a Aplicação Servidora estão funcionando.");
            System.exit(0);
        }
        
        private InterfaceSenha msi;

    }
    
}