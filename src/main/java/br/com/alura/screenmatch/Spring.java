package br.com.alura.screenmatch;

import br.com.alura.screenmatch.principal.PrincipalEstudar;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Spring implements CommandLineRunner {
    public static void main(String[] args) {SpringApplication.run(Spring.class, args);}

    @Override
    public void run(String... args) throws Exception {
        PrincipalEstudar principal = new PrincipalEstudar();
        principal.exibeMenu();
    }
}
