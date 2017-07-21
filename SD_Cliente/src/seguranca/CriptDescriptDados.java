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

    public static String criptografar(String dados) {

        char dadosChar[], dadosCript[];
        dadosChar = dados.toCharArray();

        dadosCript = new char[dadosChar.length + 32];

        int tamanhoDados = dadosChar.length;

        StringBuilder b = new StringBuilder();

        for (int i = 0; i < tamanhoDados; i++) {
            //dadosCript[i] = (char) (dadosChar[i]+1);
            b.append((char) (dadosChar[i] + 1));
        }

        return b.toString();
    }

    public static String descriptografar(String dados) {

        char dadosChar[], dadosDescript[];
        dadosChar = dados.toCharArray();

        //dadosDescript = new char[dadosChar.length+32];
        StringBuilder b = new StringBuilder();

        int tamanhoDados = dadosChar.length;

        for (int i = 0; i < tamanhoDados; i++) {
            //dadosDescript[i] = (char) (dadosChar[i]-1);
            b.append((char) (dadosChar[i] - 1));
        }

        return b.toString();
    }

}
