package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=7372d4a7";
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;


        while (opcao != 0) {
            var menu = """
                    
                    -------------------------------------------------------------
                    1 - Buscar Séries
                    2 - Buscar Episódios
                    3 - Listar Séries Buscadas
                    
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

        // Realizar uma busca a partir do nome
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo()
                        .toLowerCase()
                        .contains(nomeSerie
                                .toLowerCase()))
                .findFirst();

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
        //series = new ArrayList<>();

        // Pegar do repositório (Do banco de dados) todas as séries cadastradas
        series = repositorio.findAll();

        // Salvar série na lista
//        series = dadosSeries.stream()
//                .map(d -> new Serie(d))
//                .collect(Collectors.toList());

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);


    }
}
