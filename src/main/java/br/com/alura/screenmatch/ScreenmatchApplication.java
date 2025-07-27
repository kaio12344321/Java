package br.com.alura.screenmatch;


import br.com.alura.screenmatch.principal.Principal;
import br.com.alura.screenmatch.service.ConsumoAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication extends ConsumoAPI implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
		// Instância da classe principal para deixar o main mais limpo
		Principal principal = new Principal();
		principal.exibeMenu();
	}
}
