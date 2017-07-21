/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import com.google.gson.Gson;
import comunicacao.Comunicacao;
import comunicacao.DadosConexaoMembro;
import comunicacao.DadosPedido;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pedido.CaracteristicasPizza;
import pedido.Custo;
import pedido.GerenciadorPedido;
import pedido.Pedido;
import seguranca.CriptDescriptDados;

public class TrataCliente implements Runnable {

    private Socket cliente;
    private Membro servidor;

    public TrataCliente(Socket cliente, Membro servidor) {
        this.cliente = cliente;
        this.servidor = servidor;
    }

    public void run() {
        Comunicacao comunicacao;
        comunicacao = new Comunicacao();

        try {
            //RETORNA OS SABORES PARA O CLIENTE
            GerenciadorPedido gerPedido;
            CaracteristicasPizza caracteristicasPizzaCliente;
            gerPedido = GerenciadorPedido.getInstancia();
            String mensagem;
            String tagOpcoes = "{opcoes}:";
            String tagPedido = "{pedido}:";
            String tagFim = "{fim}:";
            
            boolean balanceadorNotificado = false;
            
            comunicacao.enviarMensagem("{id}:" + servidor.getNumClientes(), cliente);
            
            while(true){
                
                System.out.println("Esperando cliente");
                mensagem = comunicacao.receberMensagem(cliente);
                
                System.out.println("Mensagem cliente" + mensagem);
                
                if(!balanceadorNotificado){
                    Socket balanceador;
                    System.out.println("Balancador nao noti");
                    
                    try {
                        balanceador = new Socket(servidor.getHostBalanceador(), servidor.getPortaBalanceador());
                        comunicacao.enviarMensagem("{add}:" + servidor.getId(), balanceador);
                        //comunicacao.enviarMensagem("{id}:"+this.servidor.getNumClientes(), cliente);
                        this.servidor.setNumClientes(this.servidor.getNumClientes()+1);
                        balanceadorNotificado = true;
                        balanceador.close();
                    } catch (IOException ex) {
                    }
                }
                
                if(mensagem.contains(tagOpcoes)){
                    //System.out.println("É 'opcoes'");
                    comunicacao.enviarMensagem("{pizzas}:" + gerPedido.getCaracPizzaJson(), cliente);
                    
                    System.out.println("Enviou" + mensagem);
                    
                }else if(mensagem.contains(tagPedido)){
                    
                    mensagem = mensagem.substring(tagPedido.length());
                    DadosPedido dp = comunicacao.jsonParaDadosPedido(mensagem);
                    
                    //se i id do cliente for diferente do membro atual, o srevidor que estava atendendo ele CAIU!!!!!!
                    if(servidor.getId() != dp.getIdServidor()){
                        Membro membroCaido = servidor.getMembros().get(dp.getIdServidor());
                        Pedido pedidoCliente = membroCaido.getPedidos().get(dp.getIdCliente());
                        Custo c = new Custo(pedidoCliente.getValor());
                        String custoPizzaClienteJson;
                        custoPizzaClienteJson = comunicacao.classeCustoParaJson(c);
                        comunicacao.enviarMensagem("{custo}:" + custoPizzaClienteJson, cliente);
                         //Trata os dados do membro caido e envia para o cliente
                    }
                    
                    //id que está no cliente é o meu 
                    else{
                        
                        //System.out.println("Diferente");
                        
                        caracteristicasPizzaCliente = dp.getCaracteristicasPizza();
                        DadosConexaoMembro d = new DadosConexaoMembro(servidor.getHost(), servidor.getPorta(), servidor.getId(), dp.getIdCliente());
                        Pedido p = new Pedido(caracteristicasPizzaCliente, d);
                        Custo custoPizzaCliente;
                        custoPizzaCliente = gerPedido.getValorPizza(caracteristicasPizzaCliente);
                        p.setValor(custoPizzaCliente.getValor());

                        //Enviar pedido para todos os membros
                        //System.out.println("memmbtos" + servidor.getMembros().size());
                        
                        for(Membro m: servidor.getMembros()){
                            try {
                                //Envia o novo pedido para todos os membros
                                Socket membroSocket = new Socket(m.getHost(), m.getPorta());
                                String msg;
                                msg = comunicacao.pedidoParaJson(p);
                                comunicacao.enviarMensagem("{add_pedido_cliente}" + msg, membroSocket);
                                membroSocket.close();
                            } catch (Exception e) {
                                System.out.println("Replicacao com o membro :" + m.getId() +" nao eh possivel, pois o mesmo está offline" );
                                //e.printStackTrace();
                            }
                        }
                        
                        System.out.println("Enviados");
                        
                        String custoPizzaClienteJson;
                        custoPizzaClienteJson = comunicacao.classeCustoParaJson(custoPizzaCliente);
                        
                        try {
                            
                            //AGUARDANDO CANCELAMENTO DO MEMBRO
                            Thread.sleep(10000);
                            
                        } catch (Exception e) {
                        }
                        
                        comunicacao.enviarMensagem("{custo}:" + custoPizzaClienteJson, cliente);
                        
                        //comunicacao.enviarMensagem("{custo}"+ custoPizzaClienteJson, cliente);
                        System.out.println("Cliente " + cliente.getPort() + " Escolheu:");
                        System.out.println("Tamanho: " + caracteristicasPizzaCliente.getTamanhos().get(0));
                        System.out.print("Sabores: ");

                        for (String sabor : caracteristicasPizzaCliente.getSabores()) {
                            System.out.print(sabor + ", ");
                        }
                    }
                }else if (mensagem.contains(tagFim)) {
                   
                    Socket balanceador;
                    //System.out.println(servidor.getHostBalanceador() +" --- "+ servidor.getPortaBalanceador());
                    balanceador = new Socket(servidor.getHostBalanceador(), servidor.getPortaBalanceador());
                    comunicacao.enviarMensagem("{rem}:" + servidor.getId(), balanceador);
                    balanceador.close();
                    //servidor.notificarBalanceador("{rem}:" + servidor.getId());
                    cliente.close();
                    System.out.println("Cliente encerrado");
                    break;
                }
            }  
        } catch (Exception ex) {
                //ex.printStackTrace();
        }
    }
}
