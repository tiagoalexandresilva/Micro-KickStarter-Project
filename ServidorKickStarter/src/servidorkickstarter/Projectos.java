/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorkickstarter;

import java.io.Serializable;
import java.util.TreeMap;


public class Projectos  implements Serializable
{
    private TreeMap<Integer,Projecto> projectos;
    
    public Projectos(){
        this.projectos = new TreeMap();
    }
    
    public String getNomeUtProj(int id){
        return projectos.get(id).getNomeUt();
    }
    public int getFinanciadoProj(int id){
        return projectos.get(id).getFinanciado();
    }
    public int getFinanciamentoProj(int id){
        return projectos.get(id).getFinanciamento();
    }
    synchronized public int addProj(String nomeProj, String desc, String nomeUt, int montante){
        int id = this.projectos.size()+1;
        
        if(nomeProj != null && desc != null && nomeUt != null){
            this.projectos.put(id, new Projecto(nomeProj, desc, nomeUt, id, montante));
            return id;
        }
        else{
            return -1;
        }
    }
    
    public String podeInvestir(int codigo, int investimento){
        int financiamento, financiado;
        
        if(this.projectos.containsKey(codigo)){
            Projecto pj = this.projectos.get(codigo);
            synchronized(pj){
                financiamento = pj.getFinanciamento();
                financiado = pj.getFinanciado();
            }
            
            if(financiamento - financiado == 0)
                return "financiado";
            else{
                if(investimento > financiamento)
                    return "superior";
                else{
                   if(financiamento - financiado - investimento < 0)                           
                       return "excede";
                   else
                       return "pode";
                }
            }
       }

       return "naoexisteproj";
    }
    
    public String realizaInvestimento(int codigo, int investimento, String nomeUt){
        Projecto proj = this.projectos.get(codigo);
        String aux;
        if(proj==null)
            return "naoexisteproj";
        synchronized(proj){
            aux = proj.podeInvestir(investimento);
            if(aux.equals("pode")){
                if(proj.getFiadores().containsKey(nomeUt)){
                    int valor = proj.getFiadores().get(nomeUt) + investimento;
                    proj.getFiadores().put(nomeUt, valor);
                }else{
                    proj.getFiadores().put(nomeUt,investimento);
                }

                proj.actualizaFinanciado(investimento);
            }
        }
        return aux;
    }

    public String listProjParaFinanciar(String chave){
        int n = this.projectos.size();
        String res = new String();
        
        for(int i=1; i<=n; i++)
        {
            Projecto proj = this.projectos.get(i);
            if( (proj.getDescricao().toLowerCase().contains(chave.toLowerCase())) && (proj.getFinanciamento()-proj.getFinanciado()) > 0){
                res += proj.getCodigo()+"::"+proj.getNome()+"::";
            }
        }
        return res;
    }

    public String listProjFinanciados(String chave){
        String res;
        res = new String();
        int n = this.projectos.size();
        
        for(int i=1; i<=n; i++){
            Projecto proj = this.projectos.get(i);
                if((proj.getDescricao().toLowerCase().contains(chave.toLowerCase())) && ((proj.getFinanciamento()-proj.getFinanciado()) == 0)){
                    res += proj.getCodigo()+"::"+proj.getNome()+"::";
            }
        }
        return res;
    }

    public String infoProj(int cod, int n){
        Projecto proj = this.projectos.get(cod);
        
        if(proj != null){
                return proj.getNome() + "::" + proj.getDescricao() + "::" + proj.getFinanciado() + "::" + proj.fiadoresQueMaisContribuiram(proj.getCopiaFiadores(), n);
                }
        else{
            return "naoexiste";
        }
    } 
    
}
