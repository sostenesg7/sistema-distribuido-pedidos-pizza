/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import comunicacao.Comunicacao;
import comunicacao.DadosConexaoMembro;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pedido.Pedido;
import seguranca.CriptDescriptDados;

/**
 *
 * @author Matheus
 */
public class Membro {

    private int porta;
    private int portaBalanceador;
    private String host;
    private String hostBalanceador;
    private List<Socket> listaClientes;
    private static boolean status;
    private Socket servidorBalanceador;
    private int id;
    private List<Membro> membros;
    private List<Pedido> pedidos;
    int numClientes;

    public Membro(String host, int porta, int id) {
        this.porta = porta;
        this.host = host;
        this.id = id;
        this.pedidos = new ArrayList<>();
        numClientes = 0;
    }

    public Membro(String enderecoMembro, int portaMembro, String enderecoBalanc, int portaBalanc) {
        this.host = enderecoMembro;
        this.porta = portaMembro;
        this.hostBalanceador = enderecoBalanc;
        this.portaBalanceador = portaBalanc;
        this.listaClientes = new ArrayList<>();
        this.status = false;
        membros = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        numClientes = 0;
    }

    public int getNumClientes() {
        return numClientes;
    }

    public void setNumClientes(int numClientes) {
        this.numClientes = numClientes;
    }

    public List<Membro> getMembros() {
        return membros;
    }

    public void setMembros(List<Membro> membros) {
        this.membros = membros;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public int getPortaBalanceador() {
        return portaBalanceador;
    }

    public void setPortaBalanceador(int portaBalanceador) {
        this.portaBalanceador = portaBalanceador;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostBalanceador() {
        return hostBalanceador;
    }

    public void setHostBalanceador(String hostBalanceador) {
        this.hostBalanceador = hostBalanceador;
    }

    public List<Socket> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(List<Socket> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public Socket getServidorBalanceador() {
        return servidorBalanceador;
    }

    public void setServidorBalanceador(Socket servidorBalanceador) {
        this.servidorBalanceador = servidorBalanceador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void notificarBalanceador(String msg) {
        Comunicacao comunicacao;
        comunicacao = new Comunicacao();
        comunicacao.enviarMensagem(msg, servidorBalanceador);
    }

    public static boolean isStatus() {
        return status;
    }

    public static void setStatus(boolean status) {
        Membro.status = status;
    }

    public int getPorta() {
        return this.porta;
    }

    public int getQuandidadeClientes() {
        return this.listaClientes.size();
    }

    public void executa() {
        Comunicacao comunicacao;
        comunicacao = new Comunicacao();
        DadosConexaoMembro dadosConexaoServidor;
        int numeroClientes = 0;
        try {
            ServerSocket servidor = new ServerSocket(this.porta);
            servidorBalanceador = new Socket(hostBalanceador, portaBalanceador);

            if (servidorBalanceador.isConnected()) {
                status = true;
                System.out.println("Conectado com o balanceador!");
                dadosConexaoServidor = new DadosConexaoMembro(this.host, this.porta, this.id, numeroClientes);
                numeroClientes++;
                comunicacao.enviarMensagem("{membro}" + comunicacao.servidorParaJson(dadosConexaoServidor), servidorBalanceador);

                String mensagem;

                mensagem = comunicacao.receberMensagem(servidorBalanceador);
                if (mensagem.contains("{id}:")) {//TAG MENSAGEM
                    mensagem = mensagem.substring("{id}:".length());
                    System.out.println(mensagem);
                    this.id = Integer.parseInt(mensagem);

                    try {
                        servidorBalanceador.close();
                    } catch (Exception e) {
                    }

                    //MEMBRO ESPERANDO CLIENTE
                    while (true) {
                        // aceita um cliente
                        System.out.println("Membro aberto na porta: " + this.porta);
                        
                        System.out.println(">>>>>>>>>>>Aguardando conexoes");
                        Socket cliente = servidor.accept();
                        Membro membro = this;

                        Thread cli = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Nova conexão: " + cliente.getLocalPort() + " "
                                        + cliente.getInetAddress().getHostAddress());

                                String msg;
                                msg = comunicacao.receberMensagem(cliente);

                                if (msg.contains("{cliente}")) {
                                    System.out.println("cliente conectado");
                                    TrataCliente tc = new TrataCliente(cliente, membro);
                                    new Thread(tc).start();
                                } else if (msg.contains("{check}")) {
                                    //Quem testou a comunicacao aqui foi o balanceador
                                    System.out.println("Balanceador testando");
                                    try {
                                        cliente.close();
                                    } catch (IOException ex) {
                                    }
                                } else if (msg.contains("{add_membro}")) {
                                    DadosConexaoMembro dadosConexaoMembro;

                                    dadosConexaoMembro = comunicacao.jsonParaServidor(msg.substring("{add_membro}".length()));
                                    Membro m = new Membro(dadosConexaoMembro.getHost(),
                                            dadosConexaoMembro.getPorta(),
                                            dadosConexaoMembro.getIdServidor()
                                    );

                                    membros.add(m);

                                    System.out.println("novo membro adicionado");
                                    System.out.println("endereco: " + m.getHost() + ", porta:" + m.getPorta() + ", id:" + m.getId());
                                    try {
                                        cliente.close();
                                    } catch (IOException ex) {
                                    }
                                } else if (msg.contains("{add_pedido_cliente}")) {
                                    System.out.println(msg);
                                    Pedido pedido;
                                    pedido = comunicacao.jsonParaPedido(msg.substring("{add_pedido_cliente}".length()));

                                    for (Membro m : membros) {
                                        if (m.getId() == pedido.getCliente().getIdServidor()) {
                                            System.out.println("Adicionando pedidos do membro " + m.getId());
                                            m.pedidos.add(pedido);
                                            break;
                                        }
                                    }
                                    try {
                                        cliente.close();
                                    } catch (IOException ex) {
                                    }
                                }
                            }
                        });
                        cli.start();

                        try {
                            Thread.sleep(10);
                        } catch (Exception ex) {

                        }

                    }
                }//else if(mensagem.contains("{fim}:")){
                //   System.out.println(mensagem);
                //}
            }

        } catch (IOException ex) {
            System.out.println("PORTA JA UTILIZADA, TENTANDO OUTRA");
        }

    }

    /*
    public void enviarMensagem(String msg, ) {
     // envia msg para todo mundo
     for (PrintStream cliente : this.clientes) {
       cliente.println(msg);
     }
   }*/
    public static void main(String[] args) throws IOException {
        int portaBal, portaMem = 0;
        String enderecoBal, enderecoMem;
        Membro m;
        
        try {
            System.out.println("Parametros: enderecoMembro enderecoBalanceador portaBalanceador");
            
            enderecoMem = args[0];
            enderecoBal = args[1];
            portaBal = Integer.parseInt(args[2]);
            
            for (int i = 0; i < 8; i++) {

                if (status == false) {
                    portaMem = 12351;
                    portaMem += new Random().nextInt(8);
                    m = new Membro(enderecoMem, portaMem, enderecoBal, portaBal);
                    System.out.println("Tentando abrir servidor na porta " + portaMem);
                    m.executa();
                    
                } else {
                    
                    break;
                }
            }
        
        } catch (Exception e) {
        }
        
    }
}
