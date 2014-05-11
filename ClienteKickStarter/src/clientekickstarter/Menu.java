/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clientekickstarter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Menu {
    private List<String> opcoes;
    private int op;

    public Menu(String[] opcoes) {
        this.opcoes = new ArrayList<>();
        for (String op : opcoes) {
            this.opcoes.add(op);
        }
        this.op = 0;
    }

    public void executa() {
        do {
            showMenu();
            this.op = lerOpcao();
        } while (this.op == -1);
    }

    private void showMenu() {
        for (int i = 0; i < this.opcoes.size(); i++) {
            System.out.print(i + 1);
            System.out.print(" - ");
            System.out.println(this.opcoes.get(i));
        }
        System.out.println("0 - Sair");
    }
    
    private int lerOpcao() {
        int opt; 
        Scanner is = new Scanner(System.in);
        
        System.out.print("Opção: ");
        opt = is.nextInt();
        if (opt<0 || opt>this.opcoes.size()) {
            System.out.println("\n!!!Opção Inválida!!!\n");
            opt = -1;
        }
        return opt;
    }
    
    public int getOpcao() {
        return this.op;
    }
}
