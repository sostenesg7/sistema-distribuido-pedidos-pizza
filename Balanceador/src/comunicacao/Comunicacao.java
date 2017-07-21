/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import comunicacao.DadosConexaoMembro;
import membro.ServidorMembro;
import seguranca.CriptDescriptDados;

/**
 *
 * @author Sostenes
 */
public class Comunicacao {

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

    public boolean checarMembro(Socket membro){
        System.out.println("Enviando check");
        if(enviarMensagem("{check}", membro)){
            if(receberMensagem(membro).contains("{ok}")){
                return true;
            }
//return true;
        }
        return false;
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

    /*public void ouvirMembro(ServidorMembro membro){
        Scanner entrada = null;
        String mensagem = null;
        try {
            entrada = new Scanner(membro.getSocket().getInputStream());
            while (entrada.hasNextLine()) {
                mensagem = CriptDescriptDados.descriptografar(entrada.nextLine());
                System.out.println("mensagem recebida" + mensagem);
                if(mensagem.contains("{add}")){
                    membro.adicionar();
                    System.out.println("Membro " + membro.getEndereco() +
                                        " :" + membro.getPorta() + 
                                        "possui " + membro.getQuantidadeClientes() + 
                                        " membros");
                }else if(mensagem.contains("{rem}")){
                    membro.remover();
                }else if(mensagem.contains("{ok}")){
                    membro.setStatus(true);
                }else{
                    membro.setStatus(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/
    
    public String receberMensagem(Socket membro) {
        String mensagem = "";
        Scanner entrada = null;
        try {
            entrada = new Scanner(membro.getInputStream());
            
            //System.out.println(entrada.hasNextLine());
            
            boolean novaMsg = false;
            
            while(true){
                while (entrada.hasNextLine()) {
                    mensagem = entrada.nextLine();
                    novaMsg = true;
                    break;
                    //if(!mensagem.equals("")) break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
                if(novaMsg == true) break;
            }
        } catch (Exception ex) {
            System.out.println("Cliente desconectado");
        }
        
        return CriptDescriptDados.descriptografar(mensagem);
    }
}
