/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servidorkickstarter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class Projecto implements Serializable
{
    private String nome;
    private String descricao;
    private int codigo;
    private String nomeUt;
    private int financiamento;
    private int financiado;
    private TreeMap<String,Integer> fiadores;
    
    public Projecto( String nome, String desc, String nomeUt, int cod, int financiamento){
        this.nome = nome;
        this.descricao = desc;
        this.codigo = cod;
        this.nomeUt = nomeUt;
        this.financiamento = financiamento;
        this.financiado = 0;
        this.fiadores = new TreeMap<String,Integer>();
    }
    
    public String getNome(){
        return this.nome;
    }
    
    public String getDescricao(){
        return this.descricao;
    }
    
    public int getCodigo(){
        return this.codigo;
    }
    
    public String podeInvestir(int investimento){
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
    
    public String getNomeUt(){
        return this.nomeUt;
    }
    
    public int getFinanciamento(){
        return this.financiamento;
    }
    
    public int getFinanciado(){
        return this.financiado;
    }
    
    public TreeMap<String,Integer> getFiadores(){
        return this.fiadores;
    }
    
    synchronized public void actualizaFinanciado(int investimento){
        this.financiado += investimento;
    }
    
    public TreeMap<String,Integer> getCopiaFiadores(){
        TreeMap<String,Integer> copia = new TreeMap();
        for(String key : this.fiadores.keySet()){
            int value = this.fiadores.get(key);
            copia.put(key,value);
        }
        return copia;
    }
    
    public String fiadoresQueMaisContribuiram(TreeMap<String,Integer> fia, int n){
	String res = new String();
        int i=0;
	Set<Map.Entry<String,Integer>> lista1 = fia.entrySet();
	List<Map.Entry<String,Integer>> lista = new ArrayList<Map.Entry<String,Integer>>();
	lista.addAll(lista1);
	Collections.sort(lista,new CompareDecr());
        for(Map.Entry<String,Integer> entry  : lista)
        {
            if(n == 0)
                res += "• Utilizador: "+entry.getKey()+"  ‣‣‣  Montante financiado: "+entry.getValue()+"::";
            else{
                if(i < n && i < fia.size()){
                    res += "• Utilizador: "+entry.getKey()+"  ‣‣‣  Montante financiado: "+entry.getValue()+"::";
                    i++;
                }
                else{
                    break;
                }
            }
        }
        return res;
    }
}
