package br.com.alura.screenmatch.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.rmi.ServerException;

public class ConsultaGemini {
    private static final String CHAVE = System.getenv("GEMINI_APIKEY");
    public static String obterTraducao(String texto) {
        try {
            // The client gets the API key from the environment variable `GOOGLE_API_KEY`.
            Client client = Client.builder().apiKey(CHAVE).build();
            ;

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.0-flash",
                            "Apenas traduza, sem me dizer nada alem disso, o seguinte trecho: " + texto,
                            null);

            return response.text();
        }finally {
            return texto;
        }



    }
}
