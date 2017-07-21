package comunicacao;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import pedido.CaracteristicasPizza;
import pedido.Custo;
import pedido.Pedido;
import seguranca.CriptDescriptDados;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Sostenes
 */
public class Comunicacao {



    public boolean enviarMensagem(String mensagem, Socket cliente) {
        PrintStream out;
        boolean retorno = false;
        try {
            out = new PrintStream(cliente.getOutputStream());
            out.println(CriptDescriptDados.criptografar(mensagem));
            //out.println(CriptDescriptDados.criptografar(mensagem));
            retorno = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return retorno;
    }

    public void responderBalanceador(Socket balanceador){
        Scanner entrada = null;
        String mensagem;
        try {
            entrada = new Scanner(balanceador.getInputStream());
            while (entrada.hasNext()) {
                mensagem = CriptDescriptDados.descriptografar(entrada.next());
                if(mensagem.equals("{check}")){
                    enviarMensagem("{ok}", balanceador);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
    
    public String receberMensagem(Socket membro) {
        String mensagem = null;
        Scanner entrada = null;
        int tempoMaximo = 30000; //60000 Deve aguardar por no maximo 1 minuto
        int tempoAtual = 0;
        try {
            entrada = new Scanner(membro.getInputStream());
            
            //System.out.println(entrada.hasNextLine());
            
            boolean novaMsg = false;
            
            while(true){
                //System.out.println("Cheacndo novas msgs");
                
                if(tempoAtual >= tempoMaximo){
                    System.out.println("Tempo esgotado");
                    mensagem = "{fim}:{tempo_esgotado}";
                    break;
                }
                
                while (entrada.hasNextLine()) {
                    mensagem = entrada.nextLine();
                    //if(!mensagem.equals("")) break;
                    novaMsg = true;
                    mensagem = CriptDescriptDados.descriptografar(mensagem);
                    break;
                }
                
                //Tem um loop dentro do outro, logo, vai consumir muito processamento
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
                
                tempoAtual += 100;
                
               if(novaMsg == true) break;
            }
        } catch (Exception ex) {
            System.out.println("Cliente desconectado");
        }
        
        System.out.println("O retorno eh: "  + mensagem);
        
        return mensagem;
    }

        
    public String servidorParaJson(DadosConexaoMembro dadosConexaoServidor) {
        String json;
        Gson gson;
        gson = new Gson();
        json = gson.toJson(dadosConexaoServidor, DadosConexaoMembro.class);
        return json;
    }
    
    public DadosConexaoMembro jsonParaServidor(String json) {
        Gson gson;
        gson = new Gson();
        return gson.fromJson(json, DadosConexaoMembro.class);
    }
    
    public String dadosConexMembroParaJson(DadosConexaoMembro dados) {
        Gson g = new Gson();
        String dadosJson;
        dadosJson = g.toJson(dados, DadosConexaoMembro.class);
        return dadosJson;
    }

    public Pedido jsonParaPedido(String json) {
        Gson gson;
        gson = new Gson();
        return gson.fromJson(json, Pedido.class);
    }
    
    public String pedidoParaJson(Pedido dados) {
        Gson g = new Gson();
        String dadosJson;
        dadosJson = g.toJson(dados, Pedido.class);
        return dadosJson;
    }
    
    public CaracteristicasPizza caracteristicasPizzaPorJson(String jsonCaracteristicas) {
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

    public String classeCustoParaJson(Custo custo) {
        Gson gson = new Gson();
        String custoJson;
        custoJson = gson.toJson(custo);
        return custoJson;
    }
}
