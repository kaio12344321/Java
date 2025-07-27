package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);

    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=7372d4a7";

    public void exibeMenu(){
        // Pegar a serie digitada pelo usuário
        System.out.println("Digite o nome da série para a busca: ");
        var nomeSerie = leitura.nextLine();

        // Chamadar o metodo  obterDados() e atualizar o endereco com a busca
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

        // Chamadar o metodo  obterDados()
        var dados = conversor.obterDados(json, DadosSerie.class);

        System.out.println(dados);

		// Criar uma lista de DadosTemporada
		List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1; i <= dados.totalTemporadas(); i++){
			// Atualizar o link para percorrer todas as temporadas
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&Season=" + i + API_KEY);

			// Instanciar a classe DadosTemporada()
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

		    // Adicionar dados a lista
			temporadas.add(dadosTemporada);

		}

	    System.out.println("\nDados de Todas as Temporadas");
	    temporadas.forEach(System.out::println);

        System.out.println("\n\n");

//        for (int i = 0; i < dados.totalTemporadas(); i++){
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        /* Lambda
        * São funções onde vamos ter o parâmetro e toda vez que vamos iterar com uma coleção, nós chamamos esse
        * parâmetro para fazer algo
        */
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

    }
}
