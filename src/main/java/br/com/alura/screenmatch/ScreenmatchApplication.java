package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class ScreenmatchApplication extends ConsumoAPI implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
		// Url da API
		var url = "https://www.omdbapi.com/?t=gilmore+girls&apikey=7372d4a7";

		// Instanciar a classe ConsumoAPI
		var consumoApi = new ConsumoAPI();

		// Chamadar o metodo  obterDados()
		var json = consumoApi.obterDados(url);

		System.out.println("\n");
		System.out.println(json);

		// Instanciar a classe ConverteDados()
		var conversor = new ConverteDados();

		// Chamadar o metodo  obterDados()
		var dados = conversor.obterDados(json, DadosSerie.class);

		System.out.println("\n");
		System.out.println(dados);
	}
}
