
package servidorkickstarter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class Servidor{

    private static GestaoProjs gestao;
    
    public static void main(String args[])
    {
        Socket csock;
        ServerSocket ssock = null;
        FileInputStream saveFile;
        ObjectInputStream restore;
        Object obj;
        HashMap<String,Utilizador> utilizadores;
        Set<String> nomesUtilizadores;
        Thread thread;
                
        try {
            ssock = new ServerSocket(4000); /*apeteceu-me meter porta 4000*/
        } catch (IOException ex) {
            System.out.println("Erro ao ligar o servidor (ver se porta 4000 se encontra em uso)");
            System.exit(0);
        }
        
        //Carrega estado do servidor
        try {
            saveFile = new FileInputStream("saveFile.sav");
            restore = new ObjectInputStream(saveFile);
            obj = restore.readObject();
            gestao = (GestaoProjs) obj;
            
            utilizadores = gestao.getUtilizadores().getHashUtilizadores();
            nomesUtilizadores = utilizadores.keySet();
            for(String nomesAux : nomesUtilizadores){
                utilizadores.get(nomesAux).setLogin(false);
            }
            
            gestao.condit = new HashMap();
            gestao.locks = new HashMap();   
            } 
            catch (IOException | ClassNotFoundException ex) {
                gestao = new GestaoProjs();
            }
        
        //Thread que guarda os dados ou desliga o servidor
        thread = new Thread(new Runnable() {
                         @Override
                         public void run(){
                             String aux;
                             Scanner scan = new Scanner(System.in);
                             System.out.println("Para guardar o estado do servidor imprima GUARDAR e para desligar DESLIGAR.");
                             do{
                                 aux = scan.nextLine();
                                 if(aux.equals("GUARDAR")){
                                     try {
                                         FileOutputStream saveFile = new FileOutputStream("saveFile.sav");
                                         try {
                                             ObjectOutputStream save = new ObjectOutputStream(saveFile);
                                             synchronized(gestao){
                                                save.writeObject(gestao);
                                                save.flush();
                                                save.close();
                                                System.out.println("\nGravação concluida com sucesso");
                                             }
                                         } catch (IOException ex) {
                                             System.out.println("Erro ao gravar!");
                                         }
                                         
                                     } catch (FileNotFoundException ex) {
                                         System.out.println("Erro ao gravar!");
                                     }
                                 }
                             }
                             while(!aux.equals("DESLIGAR"));
                             
                             try {
                                FileOutputStream saveFile = new FileOutputStream("saveFile.sav");
                                try {
                                    ObjectOutputStream save = new ObjectOutputStream(saveFile);
                                    synchronized(gestao){
                                       save.writeObject(gestao);
                                       save.flush();
                                       save.close();
                                       System.out.println("\nGravação concluida com sucesso");
                                    }
                                } catch (IOException ex) {
                                    System.out.println("Erro ao gravar!");
                                }

                              } catch (FileNotFoundException ex) {
                                  System.out.println("Erro ao gravar!");
                                     }
                             
                             System.exit(0);
                         }
                         });
        
        thread.start();
        
        System.out.println("<<Server Listening>>");
        while(true)
        {
            try {
                csock = ssock.accept();
                System.out.println("<<Client Connected>>");
                new ClienteHandler(csock, gestao).start();
                } catch (IOException ex) {
            }
        } 
    }

}
