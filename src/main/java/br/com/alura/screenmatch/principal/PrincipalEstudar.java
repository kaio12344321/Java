package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PrincipalEstudar {
    private Scanner leitura = new Scanner(System.in);

    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=7372d4a7";

    public void exibeMenu() {
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

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
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

        // Em temporadas, me dê os episodios, em episodios, me dê os títulos
        // forEach --> Para cada
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        // Exemplo
        List<String> nomes = Arrays.asList("Jacque", "Iasmin", "Paulo", "Rodrigo", "Nico");

        // Stream são operações intermediárias --> no exemplo foi ordenado e em seguida foi limitado em 3 valores
        // Tudo oq faz algo com esse stream (forEach) é chamada de operações finais
//        nomes.stream()
//                .sorted()
//                .limit(3)
//                .filter(n -> n.startsWith("N"))
//                .map(String::toUpperCase)
//                .forEach(System.out::println);

        // flatMap é uma lista dentro de uma lista, dentro dessa lista eu vou ter outras listas e eu quero juntar
        List<DadosEpisodio> dadosEpisodios = temporadas.stream().flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        // Top 10 episódios
        System.out.println("\n\nTop 10 - Episódios");

        // Ordenar de forma decrescente
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                // O peek ajuda a interpretar o que está acontecendo em cada etapa do stream
                .peek(e -> System.out.println("Primeiro Filtro(N/A): " + e))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .peek(e -> System.out.println("Segundo Filtro(Ordenar): " + e))
                .limit(10)
                .peek(e -> System.out.println("Terceiro Filtro(Top 10): " + e))
                .map(e -> e.titulo().toUpperCase())
                .peek(e -> System.out.println("Quarto Filtro(Letra Maiúscula): " + e))
                .forEach(System.out::println);

        System.out.println("\n\n");

        // Lista de Episodios de todas as temporadas
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numeroTemporada(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);
        System.out.println("\n\n");

        System.out.println("Média dos Episódios por Temporada");
        // Exibir a média dos episódios por temporada
        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);

        System.out.println("\n\n");

        // Classe que os retorna comandos estatísticos para análise
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor Episódio: " + est.getMax());
        System.out.println("Pior Episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());

        System.out.println("\n\n");

        // Encontrar o episodio
        System.out.println("Digite o trecho do título do episódio: ");
        var trechoTitulo = leitura.nextLine();

        // Variavel retorno da busca do episódio
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().
                        toUpperCase().
                        contains(trechoTitulo.toUpperCase()))
                .findFirst();

        // Verificar se oq foi digitado aparece na lista de episódios
        if (episodioBuscado.isPresent()){
            System.out.println("Episódio encontrado!");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
            System.out.println("Episódio: " + episodioBuscado.get());
        }
        else{
            System.out.println("Episódio não encontrado");
        }

        System.out.println("\n\n");

        //  Pegar a data inicial a partir do ao digitado
        System.out.println("A partir de que ano você deseja ver os episódios?");
        var ano = leitura.nextInt();
        leitura.nextLine();
        LocalDate dataBusca = LocalDate.of(ano,1,1);

        // Formatar a data para o formato brasileiro
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Criar uma lista de datas
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " Episódio: " + e.getTitulo() +
                                " Data de Lançamento: " + e.getDataLancamento().format(formatador)
                ));




    }
}
