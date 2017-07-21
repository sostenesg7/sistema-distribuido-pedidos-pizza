/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import seguranca.CriptDescriptDados;
import comunicacao.DadosConexaoServidor;

/**
 *
 * @author Matheus
 */
public class Cliente {

    private String host;
    private int porta;
    private Socket clienteSocket;
    private int idCliente;

    public Cliente(String host, int porta) {
        this.host = host;
        this.porta = porta;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public Socket getClienteSocket() {
        return clienteSocket;
    }

    public void setClienteSocket(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public DadosConexaoServidor dadosConServPorJson(String dados) {
        Gson gson = new Gson();
        DadosConexaoServidor dadosConServidor;
        dadosConServidor = gson.fromJson(dados, DadosConexaoServidor.class);
        return dadosConServidor;
    }

    public void executa(){
        try {
            Socket cliente = new Socket(this.host, this.porta);
            System.out.println("Cliente conectado ao servidor " + this.porta);
            Receptor r = new Receptor(cliente);
            new Thread(r).start();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        // dispara cliente
        
        try {
            String enderecoBalanceador;
            int portaBalanceador;
            enderecoBalanceador = args[0];
            portaBalanceador = Integer.parseInt(args[1]);
            new Cliente(enderecoBalanceador, portaBalanceador).executa();
        } catch (Exception e) {
        }
    }
}
