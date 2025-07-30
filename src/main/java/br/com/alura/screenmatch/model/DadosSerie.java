package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/* Diferença entre @JsonAlias e @JsonProperty
 * O @JsonAlias somente lê o json com o apelido e escreve com o nome original do atributo, e também é possível passar um
 * vetor de palavras para ele tentar achar o valor
 *
 * O @JsonProperty serve tanto no processo de "Serialização", quanto "Deserialização",ou seja,
 * lê e escreve o json usando o apelido
 */

// @JsonIgnoreProperties ignora os dados implicitos
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao,
                         @JsonAlias("Genre") String genero,
                         @JsonAlias("Actors") String atores,
                         @JsonAlias("Poster") String poster,
                         @JsonAlias("Plot") String sinopse) {
}


