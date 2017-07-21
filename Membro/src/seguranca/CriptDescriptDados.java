/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seguranca;

import java.util.Arrays;

/**
 *
 * @author Sostenes
 */
public class CriptDescriptDados {
    
    public static String criptografar(String dados){
        
        char dadosChar[];
        dadosChar = dados.toCharArray();
        
        int tamanhoDados = dadosChar.length;
        
        StringBuilder b = new StringBuilder();
        
        for(int i = 0; i< tamanhoDados; i++){
            b.append((char) (dadosChar[i]+1));
        }
        
        return b.toString();
    }
    
    public static String descriptografar(String dados){
        
        char dadosChar[];
        dadosChar = dados.toCharArray();
        
        StringBuilder b = new StringBuilder();
        
        int tamanhoDados = dadosChar.length;
        
        for(int i = 0; i< tamanhoDados; i++){
            b.append((char) (dadosChar[i]-1));
        }
        
        return b.toString();
    }
}