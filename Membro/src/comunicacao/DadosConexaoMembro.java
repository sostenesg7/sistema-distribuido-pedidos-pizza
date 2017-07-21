/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

import java.net.Socket;

/**
 *
 * @author Sostenes
 */
public class DadosConexaoMembro{
    private Integer porta;
    private String host;
    private int id;
    private int idMembro;

    public DadosConexaoMembro(String host, Integer porta, int id, int idMembro) {
        this.porta = porta;
        this.host = host;
        this.id = id;
        this.idMembro = idMembro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMembro() {
        return idMembro;
    }

    public void setIdMembro(int idMembro) {
        this.idMembro = idMembro;
    }

    
    
    public int getIdServidor() {
        return id;
    }

    public void setIdServidor(int id) {
        this.id = id;
    }
    
    public Integer getPorta() {
        return porta;
    }

    public void setPorta(Integer porta) {
        this.porta = porta;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
