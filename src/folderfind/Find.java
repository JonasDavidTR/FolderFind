package folderfind;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Find {

    public static void main(String[] args) {

    	Scanner scanner = new Scanner(System.in);

        System.out.print("URL (https://exemplo.com): ");
        String userInput = scanner.nextLine().trim();
        
        // Verifica se a Url foi digitada corretamente
        if(urlExists(userInput)) {
        	System.out.println("Ok!");
	    }else {
	        System.out.println("URL Inválida ou inexistente!!");
	    }


        try {
        	Set<String> foundUrls = extractUrlsFromUrl(userInput);
            Set<String> commonFolders = readCommonFolders("domainStandard.txt");

            
            // Verificação do arquivo .txt
            System.out.println("Pastas comuns de sites:");
            for (String folder : commonFolders) {
                String urlWithFolder = formatUrl(userInput, folder);

                if (urlExists(urlWithFolder)) {
                    System.out.println("URL: " + urlWithFolder);
                }
            }

            // Verificação das URLs encontradas
            System.out.println("Urls encontradas:");
            for (String url : foundUrls) {
                if (urlExists(url)) {
                    System.out.println("Found URL: " + url);
                } else {
                    System.out.println(url + " ---- inacessível ----");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }

        scanner.close();
    }

    // Verificação de URL
    public static boolean urlExists(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            System.out.println("Erro ao verificar a URL: " + e.getMessage());
            return false;
        }
    }

    // Leitura do arquivo
    public static Set<String> readCommonFolders(String filename) throws IOException {
        Set<String> folders = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                folders.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return folders;
    }

    // Extração de URLs a partir do conteúdo da URL
    private static Set<String> extractUrlsFromUrl(String urlString) {
        Set<String> urls = new HashSet<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            String baseUrl = url.getProtocol() + "://" + url.getHost();
            urls = extractUrls(content.toString(), baseUrl);
        } catch (IOException e) {
            System.err.println("Erro ao extrair URLs da página: " + e.getMessage());
        }
        return urls;
    }

    // Extração de URLs do conteúdo HTML
    private static Set<String> extractUrls(String html, String baseUrl) {
        Set<String> urls = new HashSet<>();
        Pattern pattern = Pattern.compile("href=\"(.*?)\"", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String url = matcher.group(1);

            // Formatação da URL se necessário
            if (!url.startsWith("http")) {
                if (url.startsWith("/")) {
                    url = baseUrl + url;
                } else {
                    url = baseUrl + "/" + url;
                }
            }

            urls.add(url);
        }

        return urls;
    }

    // Formatação da URL com a pasta comum
    private static String formatUrl(String baseUrl, String folder) {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        return baseUrl + folder;
    }
}
