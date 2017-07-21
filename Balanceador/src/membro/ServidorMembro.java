/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package membro;

import java.net.Socket;

/**
 *
 * @author Sostenes
 */
public class ServidorMembro {

    private String endereco;
    private Integer porta;
    private Integer quantidadeClientes;
    private boolean status;
    private int id;
    
    public ServidorMembro(String endereco, Integer porta, int id) {
        this.quantidadeClientes = 0;
        this.porta = porta;
        this.endereco = endereco;
        status = false;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public  void adicionar(){
        this.quantidadeClientes += 1;
    }
    
    public  void remover(){
        if(this.quantidadeClientes > 0){
            this.quantidadeClientes -= 1;
        }
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getPorta() {
        return porta;
    }

    public void setPorta(Integer porta) {
        this.porta = porta;
    }

    public Integer getQuantidadeClientes() {
        return quantidadeClientes;
    }

    public void setQuantidadeClientes(Integer quantidadeClientes) {
        this.quantidadeClientes = quantidadeClientes;
    }

}
