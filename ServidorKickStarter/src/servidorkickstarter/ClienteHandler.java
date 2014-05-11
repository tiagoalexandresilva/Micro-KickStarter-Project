/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servidorkickstarter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClienteHandler extends Thread {

    private Socket csocket;
    private GestaoProjs gestao;
    private Lock l;
    private int i = 0;
    private BufferedReader buff;
    
    public ClienteHandler(Socket csocket, GestaoProjs gestao) throws IOException
    {
        this.csocket = csocket;
        this.gestao = gestao;
    }
    
    @Override
    public void run()
    {
        String str, log, pal, investir, inf;
        String nome = "desconhecido";
        int proj;
        boolean reg;
        String[] tokens;
        this.l = new ReentrantLock();
        this.i=0;
        
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
            PrintWriter out = new PrintWriter(csocket.getOutputStream());
            buff = in;
            while(((str = in.readLine()) != null) && (!str.equals("0")))
            {
                tokens = str.split("::");
                switch(tokens[0]){
                    case "registar":
                    {
                        reg = this.gestao.getUtilizadores().registaUtilizador(tokens[1], tokens[2]);
                        out.println(reg);
                        out.flush();
                        
                        break;
                    }
                    case "login":
                    {
                        log = this.gestao.getUtilizadores().login(tokens[1], tokens[2]);
                        if(log.equals("certo") || log.equals("espera")){
                            Condition cond = l.newCondition();
                            gestao.addConditLock(tokens[1], cond, l);
                            nome = tokens[1];
                        }
                        out.println(log);
                        out.flush();
                      
                        if(log.equals("espera")){
                            in.mark(10000);
                                int projaux = gestao.getUtilizadores().getIdProjUser(tokens[1]);
                                int finan = gestao.getProjectos().getFinanciamentoProj(projaux);
                                out.println(Integer.toString(finan));
                                out.flush();
                                finan = gestao.getProjectos().getFinanciadoProj(projaux);
                                out.println(Integer.toString(finan-finan));
                                out.flush();
                                while(!gestao.getProjectos().podeInvestir(projaux, 0).equals("financiado") && i!=-1){
                                    gestao.sleepCliente(tokens[1]);

                                    Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run(){
                                        try{
                                            i = buff.read();
                                        }
                                        catch (IOException e){
                                            i=-1;
                                        }
                                    }
                                    });
                                    thread.start();
                                    thread.join(10);
                                    int finanAux = gestao.getProjectos().getFinanciadoProj(projaux);
                                    if(finan<finanAux){
                                        out.println(Integer.toString(finanAux-finan));
                                        out.flush();
                                        finan=finanAux;
                                    }
                                }
                            
                            gestao.getUtilizadores().setEsperaFimUser(tokens[1], false, -1);
                            if(i==-1){
                                gestao.getUtilizadores().logout(tokens[1]);
                                break;
                            }
                            in.reset();
                        }
                        break;
                    }
                    case "logout":
                    {
                        this.gestao.getUtilizadores().logout(tokens[1]);
                        break;
                    }
                    case "InserirProj":
                    {
                        proj = this.gestao.getProjectos().addProj(tokens[1], tokens[2], tokens[3], Integer.parseInt(tokens[4]));    
                        out.println(proj);
                        out.flush();
                        gestao.getUtilizadores().setEsperaFimUser(tokens[3], true, proj);
                        in.mark(10000);
                        int finan = 0;
                        while(!gestao.getProjectos().podeInvestir(proj, 0).equals("financiado") && i!=-1){
                            gestao.sleepCliente(tokens[3]);
                            
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run(){
                                    try{
                                        i= buff.read();
                                    }
                                    catch (IOException e){
                                        i=-1;
                                    }
                                }
                                });
                            thread.start();
                            thread.join(10);
                            int finanAux = gestao.getProjectos().getFinanciadoProj(proj);
                            if(finan<finanAux){
                                out.println(Integer.toString(finanAux-finan));
                                out.flush();
                                finan=finanAux;
                            }
                        }
                        if(i==-1){
                            gestao.getUtilizadores().logout(tokens[3]);
                            break;
                        }
                        gestao.getUtilizadores().setEsperaFimUser(tokens[3], false, -1);
                        out.println("financiado");
                        out.flush();
                        in.reset();
                        break;
                    }
                    case "OfertaFinanciamento":
                    {
                        investir = this.gestao.getProjectos().realizaInvestimento(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), tokens[3]);
                            
                        if(investir.equals("pode")){
                            this.gestao.getUtilizadores().fezInvestimento(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), tokens[3]);
                            gestao.acordaCliente(gestao.getProjectos().getNomeUtProj(Integer.parseInt(tokens[1])));
                        } 
                        out.println(investir);
                        out.flush();
                        break;
                    }
                    case "ListProjParaFinanciar":
                    {           
                        pal = this.gestao.getProjectos().listProjParaFinanciar(tokens[1]);
                        out.println(pal);
                        out.flush();
                        
                        break;
                    }
                    case "ListProjFinanciados":
                    {
                        pal = this.gestao.getProjectos().listProjFinanciados(tokens[1]);
                        out.println(pal);
                        out.flush();
                        break;
                    }
                    case "InfoProj":
                    {
                        inf = this.gestao.getProjectos().infoProj(Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]));
                        out.println(inf);
                        out.flush();
                        break;
                    }
                    default:
                        break;
                }
            }
            out.close();
        } catch (InterruptedException ex) { 
            Logger.getLogger(ClienteHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException e) {
            if(!nome.equals("desconhecido"))
                gestao.getUtilizadores().logout(nome); 
        }
        catch(NullPointerException e){
            if(!nome.equals("desconhecido"))
                gestao.getUtilizadores().logout(nome);  
        }
    }              
             
}
