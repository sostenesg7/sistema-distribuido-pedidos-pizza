package servidor;

import membro.MembroComparador;
import comunicacao.Comunicacao;
import comunicacao.DadosConexaoMembro;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import membro.ServidorMembro;
import seguranca.Criptografia;

public class ServidorBalanceador {

    private final static int MSG_ADD = 0;
    private final static int MSG_REM = 1;
    private final static int MSG_OK = 2;
    private final static int MSG_CLIENTE = 3;
    private final static int MSG_MEMBRO = 4;
    private List<ServidorMembro> membros;
    private int porta;
    private int numMembros;

    public ServidorBalanceador(int porta) {
        membros = new ArrayList<>();
        this.porta = porta;
        numMembros = 0;
    }

    private ServidorMembro encontrarMembro(int id){
        ServidorMembro retorno = null;
        for(ServidorMembro m: membros){
            if(m.getId() == id){
                retorno = m;
            }
        }
        return retorno;
    }
    
    public ServidorMembro balancear() {
        ServidorMembro servidorMenorCarga = null;
        Comunicacao comunicacao;
        comunicacao = new Comunicacao();
        int status = 0;

        //TESTA SE OS MEMBROS ESTÃO ATIVOS, 2 TESTES PARA CADA MEMBRO, ANTES DE VERIFICAR QUAL DELES TEM MENOR CARGA
        //CASO NÃO ESTEJA RESPONDENDO A CONEXAO, ELE É REMOVIDO
        
        ServidorMembro membro;
        
        for (int in=0; in< membros.size(); in++) {
            //status = false;
            
            membro = membros.get(in);
            
            System.out.println(">>>>>>>>>>>>>>>>>...checando Membro " + membro.getPorta());
            Socket membroTesteConexao = null;
            for (int i = 0; i < 3; i++) {
                try {
                    membroTesteConexao = new Socket(membro.getEndereco(), membro.getPorta());
                    comunicacao.enviarMensagem("{check}", membroTesteConexao);
                    status = 1;
                    break;
                } catch (Exception ex) {
                    status = 0;
                }finally{
                    try {
                        membroTesteConexao.close();
                    } catch (Exception ex) {
                    }
                }
            }
            
            if (status == 0) {
                membros.remove(membro);
                System.out.println("Membro" + membro.getEndereco() + ":" + membro.getPorta() + " removido");
            } else {
                System.out.println("Membro" + membro.getEndereco() + ":" + membro.getPorta() + " está online");
            }
        }
        try {
            servidorMenorCarga = Collections.min(membros, new MembroComparador());
        } catch (Exception ex) {
            System.out.println("Nao ha nenhum membro conectado");
        }
        return servidorMenorCarga;
    }

    public void executar() throws IOException {
        ServerSocket servidor;
        servidor = new ServerSocket(this.porta);
        while (true) {
            System.out.println("Aguardando conexoes");
            Socket cliente = servidor.accept();
            /* System.out.println("Nova conexão{porta:" + cliente.getLocalPort() +  ", host:"+  
                cliente.getInetAddress().getHostAddress() + "}"
            );*/

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String mensagem;
                    Comunicacao comunicacao;
                    String json;
                    DadosConexaoMembro dadosConexaoMembro;

                    comunicacao = new Comunicacao();
                    mensagem = comunicacao.receberMensagem(cliente);

                    int checkMsg, idMembro;
                    int[] resultado;
                    resultado = checarMsg(mensagem);
                    checkMsg = resultado[0];
                    idMembro = resultado[1];

                    switch (checkMsg) {
                        case MSG_MEMBRO: {
                            System.out.println("MEMBRO CONECTADO");
                            json = mensagem.substring("{membro}".length());//Corta a mensagem em duas partes, tag e json
                            ServidorMembro membro;
                            dadosConexaoMembro = comunicacao.jsonParaServidor(json);
                            membro = new ServidorMembro(dadosConexaoMembro.getHost(), dadosConexaoMembro.getPorta(), numMembros);
                            
                            System.out.println("Novo membro : host:  " + membro.getEndereco() + " porta: " + membro.getPorta());
                            
                            //System.out.println(membro.getPorta() + " " + membro.getEndereco());
                            //if(comunicacao.checarMembro(cliente)){
                            membros.add(membro);
                            numMembros++;
                            System.out.println("Membro adicionado");
                            
                            comunicacao.enviarMensagem("{id}:" + membro.getId(), cliente);
                            
                            for(ServidorMembro m: membros){
                                try {
                                    if(m.getId() != membro.getId()){
                                        //Envia o novo membro para todos os membros
                                        Socket membroSocket = new Socket(m.getEndereco(), m.getPorta());
                                        comunicacao.enviarMensagem("{add_membro}" + 
                                                    comunicacao.dadosConexMembroParaJson(new DadosConexaoMembro(membro.getEndereco(), membro.getPorta(), membro.getId()))
                                                    , membroSocket);
                                        membroSocket.close();
                                        
                                        membroSocket = new Socket(membro.getEndereco(), membro.getPorta());
                                        //Envia os membros para o novo membro
                                        comunicacao.enviarMensagem("{add_membro}" + 
                                                    comunicacao.dadosConexMembroParaJson(new DadosConexaoMembro(m.getEndereco(), m.getPorta(), m.getId()))
                                                    , membroSocket);
                                        membroSocket.close();
                                    }
                                } catch (Exception e) {
                                }
                            }
                            break;
                        }
                        case MSG_CLIENTE:
                            //O CLIENTE ESTÁ SOLICITANDO COMUNICACAO COM OS MEMBROS AQUI!

                            System.out.println("CLIENTE CONECTADO");
                            ServidorMembro servidorMenorCarga;
                            servidorMenorCarga = balancear();
                            if (servidorMenorCarga != null) {

                                dadosConexaoMembro = new DadosConexaoMembro(
                                        servidorMenorCarga.getEndereco(),
                                        servidorMenorCarga.getPorta(), 
                                        servidorMenorCarga.getId()
                                );

                                mensagem = comunicacao.dadosConexMembroParaJson(dadosConexaoMembro);

                                System.out.println("Srevidor menor carga " + servidorMenorCarga.getEndereco() + servidorMenorCarga.getPorta());
                                comunicacao.enviarMensagem("{membro}" + mensagem, cliente);

                                System.out.println("Redirecionando cliente "
                                        + cliente.getPort()
                                        + "para o servidor "
                                        + servidorMenorCarga.getEndereco() + " "
                                        + servidorMenorCarga.getPorta()
                                        + ",Que possui " + servidorMenorCarga.getQuantidadeClientes() + "Clientes"
                                );

                                try {
                                    cliente.close();
                                } catch (IOException ex) {
                                }
                            } else {
                                comunicacao.enviarMensagem("{sem_membros}", cliente);
                            }
                            break;
                        case MSG_ADD:
                            encontrarMembro(idMembro).adicionar();
                            break;
                        case MSG_REM:
                            encontrarMembro(idMembro).remover();
                            break;
                        case MSG_OK:
                            encontrarMembro(idMembro).setStatus(true);
                            break;
                        default:
                            encontrarMembro(idMembro).setStatus(false);
                            break;
                    }

                    try {
                        cliente.close();
                    } catch (IOException ex) {
                        
                    }
                }
            }).start();
        }
    }

    public static int[] checarMsg(String mensagem) {
        String tagMembro, tagCliente, tagAdd, tagRem, tagOk;
        tagMembro = "{membro}";
        tagCliente = "{cliente}";
        tagAdd = "{add}:";
        tagRem = "{rem}:";
        tagOk = "{ok}:";
        int[] retorno = new int[2];

        //System.out.println("Checando msg " + mensagem);

        if (mensagem.contains(tagMembro)) {
            retorno[0] = MSG_MEMBRO;
            //retorno[1] = Integer.parseInt(mensagem.substring(0, tagMembro.length()));
        } else if (mensagem.contains(tagCliente)) {
            retorno[0] = MSG_CLIENTE;
            //retorno[1] = Integer.parseInt(mensagem.substring(0, tagCliente.length()));
        } else if (mensagem.contains(tagAdd)) {
            retorno[0] = MSG_ADD;
            retorno[1] = Integer.parseInt(mensagem.substring(tagAdd.length()));
        } else if (mensagem.contains(tagRem)) {
            retorno[0] = MSG_REM;
            retorno[1] = Integer.parseInt(mensagem.substring(tagRem.length()));
        } else if (mensagem.contains(tagOk)) {
            retorno[0] = MSG_OK;
            retorno[1] = Integer.parseInt(mensagem.substring(tagOk.length()));
        }
        return retorno;
    }

    public static void main(String[] args) throws IOException {
        // inicia o servidor
        
        int portaBalanceador;
        
        try {
            portaBalanceador = Integer.parseInt(args[0]);
            new ServidorBalanceador(portaBalanceador).executar();
        } catch (Exception e) {
        }
        
        
    }
}
