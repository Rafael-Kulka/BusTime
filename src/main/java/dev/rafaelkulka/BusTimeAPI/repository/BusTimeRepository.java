package dev.rafaelkulka.BusTimeAPI.repository;

import dev.rafaelkulka.BusTimeAPI.Settings;
import dev.rafaelkulka.BusTimeAPI.models.BusTimes.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.rafaelkulka.BusTimeAPI.models.Linha;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class BusTimeRepository {
    private static String downloadHTML(String url) throws IOException {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        StringBuilder html = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                html.append(inputLine);
            }
        }

        return html.toString();
    }

    private static Linhas getBusTimeFromHTML(String html, String Name){
        Linhas result = new Linhas();

        int horarioTerminal = 0;
        int observacaoTerminal = 1;
        int horarioBairro = 2;
        int observacaoBairro = 3;

        String[] types = {"dias-uteis", "sabados", "domingos-feriados"};
        for(String categoriaDeDias : types){
            Document documento = Jsoup.parse(html, "UTF-8");

            Element elemento = documento.getElementById(categoriaDeDias);

            if (elemento != null) {
                Horario horario = new Horario();
                HoraTerminal horaTerminal = new HoraTerminal();
                HoraBairro horaBairro = new HoraBairro();

                Elements elementos = elemento.getElementsByTag("p");
                horario.type = categoriaDeDias;

                int index = 0;
                for(Element el : elementos){
                    if(index == horarioTerminal){
                        horaTerminal.hora = el.text().split(" ");
                    }
                    else if (index == observacaoTerminal){
                        horaTerminal.observacoes = el.html().replace("\n", "").split("<br>");
                    }
                    else if (index == horarioBairro){
                        horaBairro.hora = el.text().split(" ");
                    }else if (index == observacaoBairro){
                        horaBairro.observacoes = el.html().replace("\n", "").split("<br>");
                    }

                    index++;
                }
                horario.horaTerminal = horaTerminal;
                horario.horaBairro = horaBairro;

                result.horarios.add(horario);
                result.name = Name;
            } else {
                System.out.println("Elemento com ID '" + categoriaDeDias + "' não encontrado.");
            }
        }

        return result;
    }

    private static List<Linha> getListOfAvailableLines(String html) {
        List<Linha> linhas = new ArrayList<>();

        Pattern pattern = Pattern.compile("<option value='(.*?)'[^>]*>(.*?)</option>");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String path = matcher.group(1).trim();
            String name = matcher.group(2).trim();

            Linha linha = new Linha(path, name);
            linhas.add(linha);
        }

        return linhas;
    }

    public static BusTime getBusTimes(){
        BusTime busTime = new BusTime();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File arquivoJson = new File(Settings.pathFile);

            if (arquivoJson.exists()) {
                long ultimaModificacaoEmMillis = arquivoJson.lastModified();
                Date ultimaModificacaoDate = new Date(ultimaModificacaoEmMillis);

                Date dataAtual = new Date();

                long diferencaEmDias = TimeUnit.MILLISECONDS.toDays(dataAtual.getTime() - ultimaModificacaoDate.getTime());

                if (diferencaEmDias < 5) {
                    return objectMapper.readValue(arquivoJson, BusTime.class);
                } else {
                    try {
                        String url = "https://www.transpiedade.com.br";
                        String htmlContent = downloadHTML(url);

                        String html = htmlContent;
                        List<Linha> linhas = getListOfAvailableLines(html);
                        int qtd = 0;
                        for (Linha linha : linhas) {
                            String urlLine = "https://www.transpiedade.com.br/linhas/" + linha.getPath();
                            String htmlContent2 = downloadHTML(urlLine);
                            Linhas linhaBus = getBusTimeFromHTML(htmlContent2, linha.getName());

                            busTime.linhas.add(linhaBus);

                            qtd++;
                        }

                        try {
                            objectMapper.writeValue(new File(Settings.pathFile), busTime);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        return busTime;

    }
}
