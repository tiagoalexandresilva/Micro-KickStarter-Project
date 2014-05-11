/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorkickstarter;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;


public class CompareDecr implements Comparator<Map.Entry<String,Integer>>, Serializable{

    @Override
    public int compare(Entry<String,Integer> e1, Entry<String,Integer> e2) {
        if(e1.getValue()>e2.getValue()){
            return -1;
        }
        else if(e1.getValue()<e2.getValue()){
            return 1;
        }
        else{
            return 0;
        }
    }
    
}
