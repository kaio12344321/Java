package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    // Buscar pelo nome da série (titulo -> nome do atributo da classe Serie)
    // Contain serve para ele tentar achar um trecho da string
    // Ignorar a diferença entre letras maiúsculas e minúsculas
    // O retorno vai ser um Optional, pq pode ser que ele não encontre a série (null)
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    // Buscar Série pelo nome do ator --> atores
    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, double avaliacao);

    // Buscar Top 5 série pelas avaliações
    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    // Buscar por Categoria
    // Na classe Serie, te um atributo do tipo Categoria --> genero (Enum)
    List<Serie> findByGenero(Categoria categoria);

    // buscar no banco todas as séries que contenham um número máximo de temporadas e uma avaliação mínima.
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporada, double avaliacao);

    // Consulta normal do banco de dados
    //@Query(value = "select * from series where series.total_temporadas <= 5 and series.avaliacao >= 7.5", nativeQuery = true)

    // Consulta usando JPQL
    @Query("select s from Serie s where s.totalTemporadas <= :totalTemporada and s.avaliacao >= :avaliacao")

    // O mesmo resultado do método de cima, mas a query do banco de dados
    List<Serie> seriesPorTemporadaEAvaliacao(int totalTemporada, double avaliacao);

    // Buscar episódios por trecho
    @Query("""
    SELECT e
    FROM Serie s
    JOIN s.episodios e
    WHERE LOWER(e.titulo) LIKE LOWER(CONCAT('%', :trechoEpisodio, '%'))
    ORDER BY e.temporada ASC, e.numeroEpisodio ASC
    """)
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);


    @Query("""
    SELECT e
    FROM Serie s
    JOIN s.episodios e
    WHERE s = :serie
    ORDER BY e.avaliacao DESC
    LIMIT 5
    """)
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query("""
    SELECT e
    FROM Serie s
    JOIN s.episodios e
    WHERE s = :serie AND
          YEAR(e.dataLancamento) >= :anoLancamento
    
    """)
    List<Episodio> EpisodiosPorSerieEAno(Serie serie, int anoLancamento);




}
