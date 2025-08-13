package br.com.alura.screenmatch.model;


import br.com.alura.screenmatch.service.ConsultaGemini;
import jakarta.persistence.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.checkerframework.common.value.qual.EnumVal;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

// Indicar para a classe Serie que ela vai ser uma classe no banco
@Entity

// Modificar o nome referência da tabela no banco
@Table(name = "series")
public class Serie {
    // Chave prim[ária
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private Integer totalTemporadas;
    private Double avaliacao;

    @Enumerated(EnumType.STRING)
    // O gênero está vindo do json com várias categorias em uma única string
    // Podemos criar um Enum que é uma lista fixa
    // Já vamos mapear alguns possíveis gêneros e fazer com que o gênero recebido tem que ser algum dos gêneros mapeados

    private Categoria genero;

    private String atores;
    private String poster;
    private String sinopse;

    // Objeto que não vai ser salvo, depois eu penso no relacionamento entre série e episódio
    //@Transient

    // Relacionar série e episódios --> Uma série tem vários epsódios (um pra muitos)

    // Cascade informa como as propriedades secundárias estão sendo salvas
    // Dados os episódios, como eu quero que eles vão ser salvos?
    // CascadeType.ALL --> Todas as vezes que a gente modificar uma série, também vamos conseguir modificar os episódios

    // fetch Informa como nas nossas entidades são carregadas
    // FetchType.EAGER --> traz as nossas entidades mesmo que a gente não pessa
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Episodio> episodios = new ArrayList<>();

    public List<Episodio> getEpisodios() {

        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        // Manipullar a chave estrangeira, pois o relacionamento é bidirecional
        // A dona que tem esses episódios é essa série
        episodios.forEach(e -> e.setSerie(this)) ;

        this.episodios = episodios;
    }

    public Serie() {}


    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();

        // A avaliação vem do json como String, temos que converter para Double
        // Optional é um objeto contêiner que pode ou não conter um valor não nulo
        // Está forçando que o dado presento no Optional seja Double
        // Se o dado for null, então ele vai ser 0.0
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0.0);

        // Ao invés da gente instânciar uma Categoria, como nós temos um método static
        // Então vamos chamar o método direto
        // Vamos pegar a primeira posição antes da vírgula para ser o gênero definitivo
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = ConsultaGemini.obterTraducao(dadosSerie.sinopse()).trim();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "genero = " + genero +
               ", titulo = '" + titulo + '\'' +
               ", totalTemporadas = " + totalTemporadas +
               ", avaliacao = " + avaliacao +
               ", atores = '" + atores + '\'' +
               ", poster = '" + poster + '\'' +
               ", sinopse = '" + sinopse + '\'' +
                ", episodios = '" + episodios + '\'';
    }
}
