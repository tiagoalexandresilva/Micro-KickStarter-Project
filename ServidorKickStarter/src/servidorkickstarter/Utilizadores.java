
package servidorkickstarter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;


public class Utilizadores implements Serializable
{
    private HashMap<String,Utilizador> utilizadores;

    public Utilizadores(){
        this.utilizadores = new HashMap();
    }
    
    public boolean registaUtilizador(String nome, String password){
        Utilizador ut = new Utilizador(nome, password);
        synchronized(utilizadores){
            if(this.utilizadores.get(nome)==null){
                this.utilizadores.put(nome, ut);
                return true;
            }
            else{
                return false;
            }
        }
    }
    public boolean esperaFimUser(String nome){
        return utilizadores.get(nome).getEsperaFim();
    }
    public int getIdProjUser(String nome){
        return utilizadores.get(nome).getIdProj();
    }
    public void setEsperaFimUser(String nome, boolean aux, int i){
            utilizadores.get(nome).setEsperaFim(aux,i);
    }
    
    public String login(String nome, String password){
        synchronized(utilizadores){
            Utilizador ut = this.utilizadores.get(nome);
            if(ut == null)
                return "naoexiste";
            synchronized(ut){
                if( (ut.getNome().equals(nome)) && (!ut.getPassword().equals(password)))
                    return "errado";
                else{
                    if(ut.getLogin() == true)
                        return "emuso";
                    else{
                        ut.setLogin(true);
                        if(ut.getEsperaFim())
                            return "espera";
                        else
                            return "certo";
                    }
                }
            }
        }
    }
    
    public HashMap<String,Utilizador> getHashUtilizadores(){
        return this.utilizadores;
    }
    
    public void logout(String nome){
        Utilizador ut = utilizadores.get(nome);
        this.utilizadores.get(nome).setLogin(false);
    }
    
    public void fezInvestimento(int codigo, int investimento, String nomeUt){
        Utilizador ut = this.utilizadores.get(nomeUt);
        ut.actualizaInvestido(investimento);
        Set a = ut.getProjs();
        synchronized(a){
            a.add(codigo);
        }
    }
}
