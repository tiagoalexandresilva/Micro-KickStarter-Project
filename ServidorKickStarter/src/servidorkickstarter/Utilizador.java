

package servidorkickstarter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class Utilizador implements Serializable
{
    private String nome;
    private String password;
    private Set<Integer> projs; //projs que investiu
    private int investido;
    private boolean login;
    private boolean esperaFim; //se esta a espera para ser financiado
    private int idproj;
    
    
    public Utilizador(String nome, String password){
        this.nome = nome;
        this.password = password;
        this.projs = new HashSet();
        this.investido = 0;
        this.login = false;
        this.esperaFim = false;
    }
    
    public boolean getLogin(){
        return this.login;
    }
    public int getIdProj(){
        return idproj;
    }
    public boolean getEsperaFim(){
        return esperaFim;
    }
    synchronized public void setEsperaFim(boolean aux, int i){
        this.esperaFim=aux;
        this.idproj=i;
    }   
    synchronized public void setLogin(boolean login){
        this.login = login;
    }
    public String getNome(){
        return this.nome;
    }
    public String getPassword(){
        return this.password;
    }
    public int getInvestido(){
        return this.investido;
    }
    public Set<Integer> getProjs(){
        return this.projs;
    }
    synchronized public void actualizaInvestido(int investimento){
        this.investido += investimento;
    }
    
    @Override
    public String toString(){
        return nome+ " " +password;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        
        Utilizador ut = (Utilizador) obj;
        return this.nome.equals(ut.getNome()) && this.password.equals(ut.getPassword());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.nome);
        hash = 67 * hash + Objects.hashCode(this.password);
        return hash;
    }
}
