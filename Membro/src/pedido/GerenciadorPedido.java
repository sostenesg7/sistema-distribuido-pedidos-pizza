/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedido;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sostenes
 */
public class GerenciadorPedido {

    private Map<String, Double> tabelaSaboresPrecos;
    private Map<String, Double> tabelaTamanhosPrecos;

    private CaracteristicasPizza caracteristicasPizza;
    private String caracteristicasPizzaJson;

    private static GerenciadorPedido gerPedido;

    public static GerenciadorPedido getInstancia() {
        if (gerPedido == null) {
            return new GerenciadorPedido();
        }
        return gerPedido;
    }

    private GerenciadorPedido() {
        List<String> tamanhos, sabores;
        tamanhos = new ArrayList<>();
        sabores = new ArrayList<>();

        String listaSabores[] = {"queijo", "mussarela", "calabresa", "milho", "frango"};
        double listaValoresSabores[] = {6.0, 7.0, 5.0, 3.0, 13.0};

        String listaTamanhos[] = {"pequeno", "medio", "grande", "extra-grande"};
        double listaValoresTamanhos[] = {10.0, 15.0, 20.0, 25.0};

        sabores.addAll(Arrays.asList(listaSabores));
        tamanhos.addAll(Arrays.asList(listaTamanhos));
        caracteristicasPizza = new CaracteristicasPizza(sabores, tamanhos);

        Gson gson = new Gson();
        caracteristicasPizzaJson = gson.toJson(caracteristicasPizza);

        tabelaSaboresPrecos = new HashMap<>();
        tabelaTamanhosPrecos = new HashMap<>();

        int tamSabores, tamTamanhos;
        tamSabores = listaSabores.length;
        tamTamanhos = listaTamanhos.length;

        for (int i = 0; i < tamSabores; i++) {
            tabelaSaboresPrecos.put(listaSabores[i], listaValoresSabores[i]);
        }

        for (int i = 0; i < tamTamanhos; i++) {
            tabelaTamanhosPrecos.put(listaTamanhos[i], listaValoresTamanhos[i]);
        }

    }

    public String getCaracPizzaJson() {
        return this.caracteristicasPizzaJson;
    }

    public Custo getValorPizza(CaracteristicasPizza caracPizza) {
        Custo valorPizza = new Custo(0);

        int quantidadeSabores = caracPizza.getSabores().size();

        double somaValoresSabores = 0;

        //PEGA O VALORES DOS SABORES DA PIZZA ESCOLHIDO PELO CLIENTE
        for (int i = 0; i < quantidadeSabores; i++) {

            String saborAtual;
            saborAtual = caracPizza.getSabores().get(i);
            
            somaValoresSabores += tabelaSaboresPrecos.get(saborAtual);
        }

        System.out.println("____" + caracPizza.getTamanhos().get(0));

        //PEGA O VALOR DO TAMANHO DA PIZZA ESCOLHIDO PELO CLIENTE
        System.out.println(tabelaTamanhosPrecos);

        String tamanhoPizza;
        tamanhoPizza = caracPizza.getTamanhos().get(0);
        somaValoresSabores += tabelaTamanhosPrecos.get(tamanhoPizza);

        valorPizza.setValor(somaValoresSabores);

        return valorPizza;
    }
}
