
package clientekickstarter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Cliente {

    private static Menu menuLogin, menuGestaoProjs;
   
    
    public static void main(String[] args)
    {
        carregaMenu();
        
        try {
            mmenuLogin();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        System.out.println("\n<<<<<<<<μ-KickStarter terminado>>>>>>>>");
    }
    
    private static void carregaMenu(){
            String[] smenuLogin={"Login","Novo Registo"};
            String[] smenuGestaoProjs={"Inserir projecto","Oferecer financiamento a um projecto","Listagem de projectos por financiar",
                "Listagem de projectos já financiados","Informação de um projecto"};
   
            menuLogin= new Menu(smenuLogin);
            menuGestaoProjs= new Menu(smenuGestaoProjs);
        }
    
    public static void mmenuLogin() throws IOException
    {
        Socket cs;
        String nick, pass, str1, str2;
        Scanner scan = new Scanner(System.in);
        
        try { 
            cs = new Socket("localhost",4000); /*Crio um socket para um novo cliente*/
            BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            PrintWriter out = new PrintWriter(cs.getOutputStream());
            
            do{
                System.out.println("\n\n<<<<<Bem-Vindo ao μ-KickStarter!>>>>");
                System.out.println("------------------------------------");
                menuLogin.executa();
                switch(menuLogin.getOpcao()){
                    case 1:
                    {
                        System.out.println("\n\n----Iniciar sessão-----");
                        System.out.print("Nickname: ");
                        nick = scan.nextLine();
                        System.out.print("Password: ");
                        pass = scan.nextLine();
                        
                        str1 = "login::"+nick+"::"+pass;
                        out.println(str1);
                        out.flush();
                        
                        str2 = in.readLine();
                        if(str2 == null) break;
                        if(str2.equals("certo")||str2.equals("espera")){
                            if(str2.equals("espera")){
                                System.out.println("\nÀ espera de financiamento para o projecto inserido na sessão anterior.");
                                int financiamento, financiado;
                                str2 = in.readLine();
                                financiamento = Integer.parseInt(str2);
                                str2 = in.readLine();
                                financiado = Integer.parseInt(str2);
                                System.out.println("\n Encontra-se à espera de um total de " + financiamento + ". De momento encontra-se financiado um total de "+ financiado +".\n...À espera de financiamento...");
                                int resto = financiamento - financiado;
                                
                                do{str2 = in.readLine();
                                    resto -= Integer.parseInt(str2);
                                    System.out.println("\nO seu projecto foi financiado no valor de " + Integer.parseInt(str2) + "\n...À espera de financiamento...");
                                }
                                while(resto>0);
                                System.out.println(str2);
                                System.out.println("\nO seu projecto foi financiado com sucesso.");
                                mmenuGestaoProjs(cs,nick);
                            }
                            else mmenuGestaoProjs(cs,nick);
                        }
                        else{
                            if(str2.equals("errado"))
                                System.out.println("\nErro! Login errado!");
                            else{
                                if(str2.equals("emuso"))
                                    System.out.println("\nErro! Sessão em uso.");
                                else
                                    System.out.println("\nUtilizador não registado!");
                            }
                        }
                        break;
                    }    
                    case 2:
                    {
                        System.out.println("\n\n----Novo Registo----");
                        System.out.print("Nickname: ");
                        nick = scan.nextLine();
                        System.out.print("Password: ");
                        pass = scan.nextLine();
                        
                        str1 = "registar::"+nick+"::"+pass;
                        out.println(str1);
                        out.flush();
                        
                        str2 = in.readLine();
                        if(str2 == null) break;
                        if(str2.equals("true"))
                            System.out.println("\nRegisto efectuado com sucesso!");
                        else
                            System.out.println("\nErro! Nickname já em uso!");
                        break;
                    }
                    default:
                        break;
                }
            }while(menuLogin.getOpcao()!=0);
            out.println("0");
            out.flush();
            cs.close();
            
        } catch (IOException ex) {
            System.out.println("\n!!!Servidor Offline!!! ");
        }  
    }
    
    public static void mmenuGestaoProjs(Socket cs, String nick) throws IOException
    {
        String str1, str2, nomeProj, desc, montante, palavra, cod, n;
        String[] tokens;
        Scanner scan = new Scanner(System.in);
        BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        PrintWriter out = new PrintWriter(cs.getOutputStream());
       
        do{
            System.out.println("\n\n<<<<<Gestão de Projectos μ-KickStarter>>>>");
            System.out.println("------------------------------------------");
            menuGestaoProjs.executa();
            switch(menuGestaoProjs.getOpcao()){
                case 1:
                    System.out.println("\n\n----Novo Projecto----");
                    System.out.print("Nome: ");
                    nomeProj = scan.nextLine();
                    System.out.print("Descrição: ");
                    desc = scan.nextLine();
                    System.out.print("Montante necessário para financiamento: ");
                    montante = scan.nextLine();
                    
                    str1 = "InserirProj::"+nomeProj+"::"+desc+"::"+nick+"::"+montante;
                    out.println(str1);
                    out.flush();
                        
                    str2 = in.readLine();
                    if(str2 == null) break;
                    if(Integer.parseInt(str2) < 0)
                        System.out.println("\nErro! Projecto com dados inválidos!"); 
                    else
                        System.out.println("\nProjecto adicionado com sucesso!\nCódigo do projecto: "+str2+"\n...À espera de financiamento...");
                     while(!(str2 = in.readLine()).equals("financiado")){
                        System.out.println("\nO seu projecto foi financiado no valor de " + Integer.parseInt(str2) + "\n...À espera de financiamento...");
                    }
                    System.out.println("\nO seu projecto foi financiado com sucesso.");
                    break;
                case 2:
                    System.out.println("\n\n----Financiar projecto-----");
                    System.out.print("Código: ");
                    cod = scan.nextLine();
                    System.out.print("Montante: ");
                    montante = scan.nextLine();  
                    while(Integer.parseInt(montante)<=0){
                        System.out.println("Erro! Não é possível introduzir montantes negativos ou nulos!");
                        System.out.print("Montante: ");
                        montante = scan.nextLine();
                    }
                    str1 = "OfertaFinanciamento::"+cod+"::"+montante+"::"+nick;
                    out.println(str1);
                    out.flush();
                        
                    str2 = in.readLine();
                    if(str2 == null) break;
                    if(str2.equals("pode"))
                         System.out.println("\nProjecto financiado com sucesso!");
                    else{
                        if(str2.equals("superior"))
                            System.out.println("\nErro! Montante introduzido superior a financiamento requerido.");
                        else{
                            if(str2.equals("financiado"))
                                System.out.println("\nErro! Projecto já se encontra financiado.");
                            else{
                                if(str2.equals("naoexisteproj"))
                                    System.out.println("\nErro! Não existe projecto para o qual quer financiar.");
                                else
                                    System.out.println("\nErro! Montante introduzido excede o financiamento requerido.");
                            }
                       }
                    }       
                    break;
                case 3:
                    System.out.println("\n\n----Procurar projectos por financiar----");
                    System.out.print("Palavra-chave a procurar: ");
                    palavra = scan.nextLine();
                    
                    str1 = "ListProjParaFinanciar::"+palavra;
                    out.println(str1);
                    out.flush();
                        
                    str2 = in.readLine();
                    if(str2 == null) break;
                    if(str2.equals(""))
                        System.out.println("\nNenhum projecto foi encontrado para a palavra-chave introduzida!");
                    else{
                        System.out.println("\n\n----Lista de Projectos por financiar----");
                        tokens = str2.split("::");
                        for(int i = 0; i < tokens.length; i+=2)
                            System.out.println("Código: "+tokens[i]+"   |   Nome: "+tokens[i+1]);
                    }                 
                    break;
                case 4:
                    System.out.println("\n\n----Procurar projectos financiados----");
                    System.out.print("Palavra-chave a procurar: ");
                    palavra = scan.nextLine();

                    str1 = "ListProjFinanciados::"+palavra;
                    out.println(str1);
                    out.flush();

                    str2 = in.readLine();
                    if(str2 == null) break;
                    if(str2.equals(""))
                        System.out.println("\nNenhum projecto foi encontrado para a palavra-chave introduzida!");
                    else{
                        System.out.println("\n\n----Lista de Projectos financiados----");
                        tokens = str2.split("::");
                        for(int i = 0; i < tokens.length; i+=2)
                            System.out.println("Código: "+tokens[i]+"   |   Nome: "+tokens[i+1]);
                    }  
                    break;
                case 5:
                    System.out.println("\n\n----Procurar informação de um projecto----");
                    System.out.print("Código: ");
                    cod = scan.nextLine();
                    System.out.print("N utilizadores que mais contribuiram: ");
                    n = scan.nextLine();

                    str1 = "InfoProj::"+cod+"::"+n;
                    out.println(str1);
                    out.flush();
                    str2 = in.readLine();
                    if(str2 == null) break;
                    if(str2.equals("naoexiste"))
                        System.out.println("\nNenhum projecto foi encontrado para o código introduzido!");
                    else{
                        tokens = str2.split("::");
                        System.out.println("\n\n------------Informação do Projecto "+cod+"------------");
                        System.out.println("Nome: "+tokens[0]+"\nDescrição: "+tokens[1]+"\nFinanciamento já assegurado: "+tokens[2]);
                        System.out.println("\n» Lista dos utilizadores que mais contribuiram:");
                        for(int i = 3; i < tokens.length; i++)
                            System.out.println(tokens[i]);
                    }               
                    break;
                default:
                    break;
            }
        }while(menuGestaoProjs.getOpcao()!=0);
        out.println("logout::"+nick);
        out.flush();
    }
    
}
