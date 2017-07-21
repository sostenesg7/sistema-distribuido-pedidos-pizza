/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

import cliente.pedido.CaracteristicasPizza;

/**
 *
 * @author Sostenes
 */
public class DadosPedido {
    private CaracteristicasPizza caracteristicasPizza;
    private int idCliente;
    private int idServidor;

    public DadosPedido(CaracteristicasPizza caracteristicasPizza, int idCliente, int idServidor) {
        this.caracteristicasPizza = caracteristicasPizza;
        this.idCliente = idCliente;
        this.idServidor = idServidor;
    }

    public CaracteristicasPizza getCaracteristicasPizza() {
        return caracteristicasPizza;
    }

    public void setCaracteristicasPizza(CaracteristicasPizza caracteristicasPizza) {
        this.caracteristicasPizza = caracteristicasPizza;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(int idServidor) {
        this.idServidor = idServidor;
    }
    
    
    
}
