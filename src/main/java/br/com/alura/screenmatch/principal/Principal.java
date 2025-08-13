package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import org.checkerframework.checker.units.qual.C;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=7372d4a7";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private Optional<Serie> serieBusca;

    // Declarar a lista fora do método listarSeriesBuscadas(), para usar como atributo na classe principal
    private List<Serie> series = new ArrayList<>();

    private SerieRepository repositorio;
    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;


        while (opcao != 0) {
            var menu = """
                    
                    -------------------------------------------------------------
                    1  - Buscar Séries
                    2  - Buscar Episódios
                    3  - Listar Séries Buscadas
                    4  - Buscar Série Por Título
                    5  - Buscar Séries Por Ator
                    6  - Buscar Top 5 Séries
                    7  - Buscar Séries Por Categoria
                    8  - Filtrar Séries
                    9  - Buscar Episódio Por Trecho
                    10 - Top Episódios Por Série
                    11 - Buscar Episódios A Partir De Uma Data
                    
                    0 - Sair
                    -------------------------------------------------------------
                    Escolha uma opção:""";

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;

                case 2:
                    buscarEpisodioPorSerie();
                    break;

                case 3:
                    listarSeriesBuscadas();
                    break;

                case 4:
                    buscarSeriePorTitulo();
                    break;

                case 5:
                    buscarSeriesPorAtor();
                    break;

                case 6:
                    buscarTop5Series();
                    break;

                case 7:
                    buscarSeriesPorCategoria();
                    break;

                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;

                case 9:
                    buscarEpisodioPorTrecho();
                    break;

                case 10:
                    topEpisodiosPorSerie();
                    break;

                case 11:
                    buscarEpisodiosAPartirDeUmaData();
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção Inválida");
            }
        }
    }




    private DadosSerie getDadosSerie() {
        // Pegar a serie digitada pelo usuário
        System.out.println("Digite o nome da série para a busca: ");
        var nomeSerie = leitura.nextLine();

        // Chamadar o metodo  obterDados() e atualizar o endereco com a busca
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

        // Chamadar o metodo  obterDados()
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);

        return dados;
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);

        // Toda vez que for buscar a série, vai ser adicionado na lista
        //dadosSeries.add(dados);

        // Salvar a série
        repositorio.save(serie);
        System.out.println(dados);
    }

    public void buscarEpisodioPorSerie() {
//        DadosSerie dadosSerie = getDadosSerie();
//
//        // Criar uma lista de DadosTemporada
//        List<DadosTemporada> temporadas = new ArrayList<>();

        // Listar as séries salvas no banco
        listarSeriesBuscadas();

        System.out.println("\nEscolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        // Realizar uma busca a partir do nome --> Mais custoso
//        Optional<Serie> serie = series.stream()
//                .filter(s -> s.getTitulo()
//                        .toLowerCase()
//                        .contains(nomeSerie
//                                .toLowerCase()))
//                .findFirst();

        // Consulta diretamento no banco de dados
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            // Variavel do tipo série e não mais Optional
            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                // Atualizar o link para percorrer todas as temporadas
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&Season=" + i + API_KEY);

                // Instanciar a classe DadosTemporada()
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

                // Adicionar dados a lista
                temporadas.add(dadosTemporada);

            }

            System.out.println("\nDados de Todas as Temporadas");
            temporadas.forEach(System.out::println);

            // Criar Lista de episódios
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios()
                            .stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());

            // Setar a série
            serieEncontrada.setEpisodios(episodios);

            // Salvar no banco de dados
            repositorio.save(serieEncontrada);
        }
        else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas() {
        // Imprimir os dados da lista dadosSeries
        // dadosSeries.forEach(System.out::println);

        // Criar uma lista de séries
        //List<Series> series = new ArrayList<>();

        // Salvar série na lista
//        series = dadosSeries.stream()
//                .map(d -> new Serie(d))
//                .collect(Collectors.toList());


        // Pegar do repositório (Do banco de dados) todas as séries cadastradas
        series = repositorio.findAll();

        // Imprimir a própria série ordenando pela categoria
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()){
            System.out.println("Dados da série: " + serieBusca.get());
        } else{
            System.out.println("Série não encontrada!");
        }

    }

    private void buscarSeriesPorAtor() {
        System.out.println("Qual o nome para a busca? ");
        var nomeAtor = leitura.nextLine();

        System.out.println("Avaliações a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        System.out.println("Séries em que " + nomeAtor + " trabalhou");

        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliação " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();

        serieTop.forEach(s -> System.out.println(s.getTitulo() + " avaliação " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar séries de que categoria/gênero? ");
        var nomeGenero = leitura.nextLine();

        // O que o usuário digitou, representa qual item do Enum?
        Categoria categoria = Categoria.fromPortugues(nomeGenero);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria " + nomeGenero);

        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        // Derived Queries
//        System.out.println("Filtrar séries até quantas temporadas? ");
//        var totalTemporadas = leitura.nextInt();
//        leitura.nextLine();
//        System.out.println("Com avaliação a partir de que valor? ");
//        var avaliacao = leitura.nextDouble();
//        leitura.nextLine();
//        List<Serie> filtroSeries = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(totalTemporadas, avaliacao);
//        System.out.println("*** Séries filtradas ***");
//        filtroSeries.forEach(s ->
//                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));

        // JPQA
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));

    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual o nome do episódio para a busca? ");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);

        System.out.println("Episódios Encontrados: ");
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));


    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();

            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s - Avaliação %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosAPartirDeUmaData() {
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();

            System.out.println("Digite o ano limite de lançamento: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.EpisodiosPorSerieEAno(serie, anoLancamento);

            episodiosAno.forEach(System.out::println);

        }
    }
}
