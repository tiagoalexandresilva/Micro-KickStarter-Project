/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorkickstarter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;



public class GestaoProjs implements Serializable
{
    private Projectos projs;
    private Utilizadores uts;
    public HashMap<String,Condition> condit;
    public HashMap<String,Lock> locks;
    
    public GestaoProjs(){
        this.projs = new Projectos();
        this.uts = new Utilizadores();
        this.condit = new HashMap();
        this.locks = new HashMap();
    }
    
    public Utilizadores getUtilizadores()
    {
        return this.uts;
    }
    
    public void addConditLock(String nomeUt, Condition condicao, Lock l){
        synchronized(condit){
            condit.put(nomeUt, condicao);
        }
        synchronized(locks){
            locks.put(nomeUt, l);
        }
    }
    
    public void acordaCliente(String nomeUt){
        locks.get(nomeUt).lock();
        condit.get(nomeUt).signal();
        locks.get(nomeUt).unlock();
    }
    public void sleepCliente(String nomeUt) throws InterruptedException{
        locks.get(nomeUt).lock();
        condit.get(nomeUt).awaitNanos(1000000000);
        locks.get(nomeUt).unlock();
    }
    
    public Projectos getProjectos()
    {
        return this.projs;
    }
}
