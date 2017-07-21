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
public class Pizzas implements Serializable {

    private List<String> sabores;
    private char tamanho;
    private String saborBorda;
    private double valor;

    public Pizzas(List<String> sabores, char tamanho, String saborBorda, double valor) {
        this.sabores = sabores;
        this.tamanho = tamanho;
        this.saborBorda = saborBorda;
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public List<String> getSabores() {
        return sabores;
    }

    public void setSabores(List<String> sabores) {
        this.sabores = sabores;
    }

    public char getTamanho() {
        return tamanho;
    }

    public void setTamanho(char tamanho) {
        this.tamanho = tamanho;
    }

    public String getSaborBorda() {
        return saborBorda;
    }

    public void setSaborBorda(String saborBorda) {
        this.saborBorda = saborBorda;
    }

}
