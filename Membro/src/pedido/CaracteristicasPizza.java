/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedido;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Sostenes
 */
public class CaracteristicasPizza implements Serializable {

    private List<String> sabores;
    private List<String> tamanhos;

    public CaracteristicasPizza(List<String> sabores, List<String> tamanhos) {
        this.sabores = sabores;
        this.tamanhos = tamanhos;
    }

    public List<String> getSabores() {
        return sabores;
    }

    public void setSabores(List<String> sabores) {
        this.sabores = sabores;
    }

    public List<String> getTamanhos() {
        return tamanhos;
    }

    public void setTamanhos(List<String> tamanhos) {
        this.tamanhos = tamanhos;
    }

}
