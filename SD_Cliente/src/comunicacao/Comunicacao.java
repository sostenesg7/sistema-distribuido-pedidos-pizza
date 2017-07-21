/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

import cliente.pedido.CaracteristicasPizza;
import cliente.pedido.Custo;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import comunicacao.DadosConexaoServidor;
import seguranca.CriptDescriptDados;

/**
 *
 * @author Sostenes
 */
public class Comunicacao {

    public CaracteristicasPizza getCaracteristicasPizzaJson(String jsonCaracteristicas) {
        Gson gson = new Gson();
        CaracteristicasPizza caracteristicasPizza;
        caracteristicasPizza = gson.fromJson(jsonCaracteristicas, CaracteristicasPizza.class);
        return caracteristicasPizza;
    }

    public String caracteristicasPizzaParaJson(CaracteristicasPizza carac) {
        Gson gson = new Gson();
        String caracPizzaJson;
        caracPizzaJson = gson.toJson(carac);
        return caracPizzaJson;
    }

    public Custo classeCustoporJson(String custo) {
        Gson gson = new Gson();
        Custo custoJson;
        custoJson = gson.fromJson(custo, Custo.class);
        return custoJson;
    }

    public String dadosPedidoParaJson(DadosPedido dadosPedido) {
        String json;
        Gson gson;
        gson = new Gson();
        json = gson.toJson(dadosPedido, DadosPedido.class);
        return json;
    }

    public DadosPedido jsonParaDadosPedido(String json) {
        Gson gson;
        gson = new Gson();
        return gson.fromJson(json, DadosPedido.class);
    }

    public String servidorParaJson(DadosConexaoServidor dadosConexaoServidor) {
        String json;
        Gson gson;
        gson = new Gson();
        json = gson.toJson(dadosConexaoServidor, DadosConexaoServidor.class);
        return json;
    }

    public DadosConexaoServidor jsonParaServidor(String json) {
        Gson gson;
        gson = new Gson();
        return gson.fromJson(json, DadosConexaoServidor.class);
    }

    public boolean enviarMensagem(String mensagem, Socket cliente) {
        PrintStream out;
        boolean retorno = false;
        try {
            out = new PrintStream(cliente.getOutputStream());
            out.println(CriptDescriptDados.criptografar(mensagem));
            retorno = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }

    public String receberMensagem(Socket membro) {
        String mensagem = null;
        Scanner entrada;

        try {
            entrada = new Scanner(membro.getInputStream());
            boolean novaMsg = false;
            while (true) {
                Socket s = new Socket(membro.getInetAddress().getHostAddress(), membro.getPort());
                s.close();
//System.out.println("testou o balanceador");
                while (entrada.hasNextLine()) {
                    mensagem = entrada.nextLine();
                    novaMsg = true;
                    break;
                    //if(!mensagem.equals("")) break;
                }

               if (novaMsg == true) {
                 break;
               }
               
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
               
            }
            mensagem = CriptDescriptDados.descriptografar(mensagem);

        } catch (Exception ex) {
            System.out.println("Cliente desconectado");
        }

        return mensagem;
    }

}
