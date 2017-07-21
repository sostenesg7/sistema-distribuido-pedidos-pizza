/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import cliente.pedido.CaracteristicasPizza;
import cliente.pedido.Custo;
import comunicacao.Comunicacao;
import comunicacao.DadosConexaoServidor;
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
import seguranca.CriptDescriptDados;

class Receptor implements Runnable {

    private Socket servidor;
    private Socket balanceador;
    private String hostBalanceador;
    private int portaBalanceador;
    private int idCliente;
    private int idServidor;
    
    public Receptor(Socket servidor) {
        this.balanceador = servidor;
        this.portaBalanceador = servidor.getPort();
        this.hostBalanceador = servidor.getInetAddress().getHostAddress();
        System.out.println("Conectando a host " + hostBalanceador + " porta " + portaBalanceador);
    }

    public Socket getServidor() {
        return servidor;
    }

    public void setServidor(Socket servidor) {
        this.servidor = servidor;
    }

    public Socket getBalanceador() {
        return balanceador;
    }

    public void setBalanceador(Socket balanceador) {
        this.balanceador = balanceador;
    }

    public String getHostBalanceador() {
        return hostBalanceador;
    }

    public void setHostBalanceador(String hostBalanceador) {
        this.hostBalanceador = hostBalanceador;
    }

    public int getPortaBalanceador() {
        return portaBalanceador;
    }

    public void setPortaBalanceador(int portaBalanceador) {
        this.portaBalanceador = portaBalanceador;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
    
    public boolean receberDadosServidorMembro() {
        Comunicacao comunicacao;
        DadosConexaoServidor conexaoServidor;
        Socket membro;
        String mensagem;
        String tagMembro;
        String tagCliente;
        String tagSemMembro;

        tagCliente = "{cliente}";
        tagMembro = "{membro}";
        tagSemMembro = "{sem_membro}";
        comunicacao = new Comunicacao();

        boolean conectado = false;

        if (balanceador == null) {
            try {
                balanceador = new Socket(hostBalanceador, portaBalanceador);
            } catch (IOException ex) {
                return false;
            }
        }

        while (conectado == false) {
            comunicacao.enviarMensagem(tagCliente, this.balanceador);
            
            System.out.println("Aguardando balanceador");
            
            mensagem = comunicacao.receberMensagem(balanceador);

            
            try {
                if (mensagem.contains(tagMembro)) {
                    conexaoServidor = comunicacao.jsonParaServidor(mensagem.substring(tagMembro.length()));
                    this.idServidor = conexaoServidor.getId();
                    
                    membro = new Socket(conexaoServidor.getHost(), conexaoServidor.getPorta());
                    comunicacao.enviarMensagem("{cliente}", membro);
                    
                    System.out.println("Aguardando membro");
                    mensagem = comunicacao.receberMensagem(membro);
                    System.out.println("");
                    this.idCliente = Integer.parseInt(mensagem.substring("{id}:".length()));
                    
                    System.out.println("Meu id : " + idCliente);
                    
                    this.servidor = membro;
                    System.out.println("Cliente redireconado para: " + conexaoServidor.getHost() + ":" + conexaoServidor.getPorta());
                    this.balanceador.close();
                    this.balanceador = null;
                    conectado = true;

                } else if (mensagem.contains(tagSemMembro)) {
                    System.out.println("Sem membros para conectar");
                }
            } catch (Exception e) {
            }

        }
        return conectado;
    }

    public void run() {
        // recebe msgs do servidor e imprime na tela

        Comunicacao comunicacao;
        comunicacao = new Comunicacao();

        try {
            if (!receberDadosServidorMembro()) {
                return;
            }

            CaracteristicasPizza caracteristicasPizzaServidor;

            boolean estadoConexao = true;
            String tagPizzas = "{pizzas}:";

            String mensagem;

            System.out.println(servidor.getInetAddress().getHostAddress() + " " + servidor.getPort());

            //forca o recebimento da mensagem
            //Caso o servidor tenha caido, insiste em receber um novo membro
            while (true) {
                comunicacao.enviarMensagem("{opcoes}:", servidor);
                mensagem = comunicacao.receberMensagem(servidor);
                if (mensagem == null) {
                    if (!receberDadosServidorMembro()) {
                        return;
                    }
                } else {
                    break;
                }
            }
            
            if (mensagem.contains(tagPizzas)) {
                caracteristicasPizzaServidor = comunicacao.getCaracteristicasPizzaJson(mensagem.substring(tagPizzas.length()));
                CaracteristicasPizza caracPizzaCliente;
                List<String> sabores;
                sabores = new ArrayList<>();
                System.out.println("Escolha os Sabores");
                int indiceSabor = 0, escolhaSabor;
                for (String sabor : caracteristicasPizzaServidor.getSabores()) {
                    System.out.println(indiceSabor + "- " + sabor);
                    indiceSabor++;
                }
                System.out.println(indiceSabor + "- Finalizar selecao de sabores");

                int opcao, opcaoSair;
                opcaoSair = indiceSabor;
                List<String> saboresCliente;
                saboresCliente = new ArrayList<>();

                Scanner teclado = new Scanner(System.in);

                while (teclado.hasNextInt()) {
                    opcao = teclado.nextInt();
                    if (opcao == opcaoSair) {
                        break;
                    }
                    //Busca o sabor escolhido pelo cliente na lista de sabores recebidos do servidor
                    saboresCliente.add(caracteristicasPizzaServidor.getSabores().get(opcao));
                }

                System.out.println("\nTamanhos");
                int indiceTamanho = 0, escolhaTamanho;
                for (String tamanho : caracteristicasPizzaServidor.getTamanhos()) {
                    System.out.println(indiceTamanho + "- " + tamanho);
                    indiceTamanho++;
                }

                escolhaTamanho = teclado.nextInt();

                List<String> tamanhoEscolhido;
                tamanhoEscolhido = new ArrayList<>();
                tamanhoEscolhido.add(caracteristicasPizzaServidor.getTamanhos().get(escolhaTamanho));
                caracPizzaCliente = new CaracteristicasPizza(saboresCliente, tamanhoEscolhido);

              //  String pizzaParaJson;
                //pizzaParaJson = comunicacao.caracteristicasPizzaParaJson(caracPizzaCliente);
                //Envia os dados da pizza para o servidor
                String tagCusto = "{custo}:";
                
                //Se nao conseguir a resposta do servidor, na hora de fazer o pedido, pede outro membro
                DadosPedido dp = new DadosPedido(caracPizzaCliente, idCliente, idServidor);
                String json = comunicacao.dadosPedidoParaJson(dp);
                
                while (true) {
                    
                    comunicacao.enviarMensagem("{pedido}:" + json, servidor);
                    
                    System.out.println("Aguardando resposta do membro");
                    
                    mensagem = comunicacao.receberMensagem(servidor);
                    
                    System.out.println("Mensagem passou");
                    
                    if (mensagem == null) {
                        boolean statusConexao = receberDadosServidorMembro();
                        if (statusConexao == false) {
                            return;
                        }
                    } else {
                        break;
                    }
                    
                }

                System.out.println("mag " + mensagem);
                
                Custo custoPizza = comunicacao.classeCustoporJson(mensagem.substring(tagCusto.length()));
                System.out.println("Pedido realizado com suceso!");
                System.out.println("Valor da pizza R$ " + custoPizza.getValor());
                comunicacao.enviarMensagem("{fim}:", servidor);

                servidor.close();
                teclado.close();
                System.out.println("saiu");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //}
}
