/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package membro;

import java.util.Comparator;
import membro.ServidorMembro;

public class MembroComparador implements Comparator<ServidorMembro> {

    @Override
    public int compare(ServidorMembro m1, ServidorMembro m2) {
        return m1.getQuantidadeClientes().compareTo(m2.getQuantidadeClientes());
    }
}
