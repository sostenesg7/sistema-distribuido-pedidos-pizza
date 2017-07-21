/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedido;

import comunicacao.DadosConexaoMembro;
import java.util.List;

/**
 *
 * @author Sostenes
 */
public class Pedido {
    private CaracteristicasPizza caracteristicasPizza;
    private DadosConexaoMembro cliente;
    private double valor;

    public Pedido(CaracteristicasPizza caracteristicasPizza, DadosConexaoMembro cliente) {
        this.caracteristicasPizza = caracteristicasPizza;
        this.cliente = cliente;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    
    
    
    public CaracteristicasPizza getCaracteristicasPizza() {
        return caracteristicasPizza;
    }

    public void setCaracteristicasPizza(CaracteristicasPizza caracteristicasPizza) {
        this.caracteristicasPizza = caracteristicasPizza;
    }

    public DadosConexaoMembro getCliente() {
        return cliente;
    }

    public void setCliente(DadosConexaoMembro cliente) {
        this.cliente = cliente;
    }
    
    
    
}
