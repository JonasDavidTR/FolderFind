package folderfind;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Find {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("URL: ");
        String userInput = scanner.nextLine();
        System.out.println("Você escolheu: " + userInput);

        String address = userInput;

        try {
            URL pageLocation = new URL(address);
            Scanner in = new Scanner(pageLocation.openStream());

            while (in.hasNextLine()) {
                String line = in.nextLine();

                if (line.contains("href=\"https://") || line.contains("href=\"https://")) {
                    int from = line.indexOf("href=\"") + 6;
                    int to = line.indexOf("\"", from);
                    String url = line.substring(from, to);
                    System.out.println(url);
                }
            }

            in.close();
        } catch (MalformedURLException e) {
            System.err.println("URL inválida: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro ao abrir a conexão: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}