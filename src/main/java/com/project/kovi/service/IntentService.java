package com.project.kovi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kovi.entity.stats.CovidStats;
import com.project.kovi.entity.news.NewsList;
import com.project.kovi.entity.Query;
import com.pusher.rest.Pusher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class IntentService {

    @Value("${rapid.api.key}")
    private String apiKey;

    @Value("${api.news.host}")
    private String newsApiHost;

    @Value("${api.covid.host}")
    private String covidApiHost;

    @Autowired
    private FirestoreService fireService;

    @Autowired
    private Pusher pusher;

    private Map<String, Locale> countries = new HashMap<>();

    public IntentService(){
        super();
        Locale locale;
        for(String countryCode : Locale.getISOCountries()){
            locale = new Locale("", countryCode);
            countries.put(locale.getDisplayCountry(), locale);
        }
        countries.put("USA", countries.get("United States"));
        countries.put("The United States", countries.get("United States"));
        countries.put("the United States", countries.get("United States"));
        countries.put("U.S.A", countries.get("United States"));

        countries.put("UK", countries.get("United Kingdom"));
        countries.put("The United Kingdom", countries.get("United Kingdom"));
        countries.put("the United Kingdom", countries.get("United Kingdom"));
    }

    @Async
    public CompletableFuture<NewsList> getNews(String queryId, String q, String country, String sessionId){
//        System.out.println("IntentService::getNews");
        OkHttpClient client = new OkHttpClient();

        String keyword = null;

        try{
            keyword = URLEncoder.encode(q, StandardCharsets.UTF_8.toString());
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException("Invalid Search Parameter for news");
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://newscatcher.p.rapidapi.com/v1/search").newBuilder();
        urlBuilder.addQueryParameter("q", keyword)
                .addQueryParameter("lang", "en")
                .addQueryParameter("sort_by", "relevancy")
                .addQueryParameter("page", "1")
                .addQueryParameter("media", "True");

        String cc = countries.get(country).getCountry();
        if(country != null && !country.isBlank())
            urlBuilder.addQueryParameter("country", cc);
        else
            urlBuilder.addQueryParameter("country", "IN");

        String url = urlBuilder.build().toString();
//        System.out.println("[getNews] q = " + q);
//        System.out.println("[getNews] cc = " + cc);
//        System.out.println("[getNews] country = " + country);
//        System.out.println("[getNews] News url = " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapidapi-host", newsApiHost)
                .build();
        Response response = null;
        NewsList news = null;
        try {
            response = client.newCall(request).execute();

            if(!response.isSuccessful())
                throw new RuntimeException("Failed to fetch news");

            ObjectMapper objectMapper = new ObjectMapper();
            news = objectMapper.readValue(response.body().byteStream(), NewsList.class);

            // Persist
            Query query = fireService.getQueryById(queryId);

            // Set The News on QueryResponse -> ResponseResources
            query.getResponse().setFetchingResources(false);
            query.getResponse().setHasResources(true);

            query.getResponse().getResources().setHasNews(true);
            query.getResponse().getResources().setNews(news);

            query = fireService.updateQuery(queryId, Collections.singletonMap("response", query.getResponse()));

            pusher.trigger(sessionId+"-DATA", "ON_DATA", query);

        } catch (IOException e) {
            System.out.println("Failed to Fetch News");
            e.printStackTrace();
        }catch(NullPointerException exc){
            System.out.println("exc = " + exc);
            exc.printStackTrace();
        }catch(Exception e){
            System.out.println("Something went wrong -> " + e.getMessage());
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(news);
    }

    @Async
    public CompletableFuture<List<CovidStats>> getStats(String queryId, String country, String sessionId){
        List<CovidStats> covidStats = null;
//        System.out.println("IntentService::getStats");
//        System.out.println("[getStats] country = " + country);

        String url = null;
        if(country.equalsIgnoreCase("World")){
            url = "https://vaccovid-coronavirus-vaccine-and-treatment-tracker.p.rapidapi.com/" +
                    "api/npm-covid-data/world";
        }else{
            String iso3 = countries.get(country).getISO3Country();
            System.out.println("[getStats] iso3 = " + iso3);
            url = String.format("https://vaccovid-coronavirus-vaccine-and-treatment-tracker.p.rapidapi.com/api/" +
                    "npm-covid-data/country-report-iso-based/%s/%s", country, iso3);
        }

//        System.out.println("[getStats] Location -> " + country + " Url -> " + url);

        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url(url)
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapiadpi-host", covidApiHost)
                .build();

        try{
            Response res = client.newCall(req).execute();
            if(!res.isSuccessful())
                throw new RuntimeException("Failed to Load Statistics");
            ObjectMapper mapper = new ObjectMapper();
            covidStats = mapper.readValue(res.body().byteStream(),
                    mapper.getTypeFactory().constructCollectionType(List.class, CovidStats.class));

            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("response.fetchingResources", false);
            updateMap.put("response.hasResources", true);
            updateMap.put("response.resources.hasStats", true);
            updateMap.put("response.resources.stats", covidStats.get(0));

            Query query = fireService.updateQuery(queryId, updateMap);

            pusher.trigger(sessionId+"-DATA", "ON_DATA", query);

        }catch(IOException e){
            System.out.println("Problem loading statistics" + e.getMessage());
            e.printStackTrace();
        }catch(Exception e){
            System.out.println("Error fetching statistics " + e.getMessage());
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(covidStats);
    }

}
