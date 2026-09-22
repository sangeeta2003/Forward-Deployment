package coderarmy.in.netflix_recommend;

import coderarmy.in.netflix_recommend.model.Movie;
import coderarmy.in.netflix_recommend.model.MovieData;
import coderarmy.in.netflix_recommend.model.MovieMatch;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import jakarta.annotation.PostConstruct;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class MovieService {
    private final EmbeddingModel embeddingModel;
    private final JsonMapper jsonMapper;
    private final List<Movie> movieEmbeddings = new ArrayList<>();
    public MovieService(EmbeddingModel embeddingModel, JsonMapper jsonMapper){
        this.embeddingModel = embeddingModel;
        this.jsonMapper = jsonMapper;
    }
    @PostConstruct
    public void initializeMovies() throws IOException{
        ClassPathResource resource = new ClassPathResource("movies.json");
        InputStream inputStream = resource.getInputStream();
                List<MovieData> movieDataList = jsonMapper.readValue(
                        inputStream, new TypeReference<>() {});
                for(MovieData movieData:movieDataList){
                    float[] embedding = embeddingModel.embed(
                            movieData.getDescription()
                    );
                    Movie movie = new Movie(
                            movieData.getTitle(),
                            movieData.getDescription(),
                            embedding
                    );
                    movieEmbeddings.add(movie);
                }
                inputStream.close();
        System.out.println(
                movieEmbeddings.size() + " movies loaded with embeddings."
        );
        for(Movie movie : movieEmbeddings){
            System.out.println(Arrays.toString(movie.getEmbeddings()));
        }

    }
    public List<MovieMatch> search(String query){
        float[] userQueryEmbedding = embeddingModel.embed(query);
        List<MovieMatch> matches = new ArrayList<>();
        for(Movie movie : movieEmbeddings){
            double similarity = cosineSimilarity(userQueryEmbedding, movie.getEmbeddings());
            MovieMatch match = new MovieMatch(movie.getTitle(), movie.getDescription(), similarity);
            matches.add(match);
        }
        sortBySimilarity(matches);
        return topMatches(matches,3);
    }
    public List<MovieMatch> similarMovies(String title){
        Movie selectedMovie = findMovie(title);
        List<MovieMatch> matches = new ArrayList<>();
        for(Movie movie: movieEmbeddings){
            if(movie.getTitle().equalsIgnoreCase(title)){
                continue;
            }
            double similarity = cosineSimilarity(
                    selectedMovie.getEmbeddings(),
                    movie.getEmbeddings());
            MovieMatch match = new MovieMatch(
                    movie.getTitle(),
                    movie.getDescription(),
                    similarity
            );
            matches.add(match);

        }
        sortBySimilarity(matches);
        return topMatches(matches,3);
    }
    private Movie findMovie(String title){
        for(Movie movie: movieEmbeddings){
            if(movie.getTitle().equalsIgnoreCase(title)){
                return movie;
            }
        }
        throw new IllegalArgumentException(
                "Movie not found" + title
        );
    }
    private double cosineSimilarity(float[] a , float[] b){
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for(int i = 0 ; i < a.length;i++){
            dotProduct += (a[i] * b[i]);
            normA += (a[i] * a[i]);
            normB += (b[i] * b[i]);
        }
        if(normA == 0 || normB == 0){
            return 0.0;
        }
        return dotProduct/(Math.sqrt(normA) * Math.sqrt(normB));
    }
    private void sortBySimilarity(List<MovieMatch> matches){
        Collections.sort(matches, (first,second)->Double.compare(second.getMatch(),first.getMatch()));
    }
private List<MovieMatch> topMatches(
        List<MovieMatch> matches, int limit){
        List<MovieMatch> topMatches = new ArrayList<>();
        int numberOfMatches = Math.min(limit, matches.size());
        for(int i = 0 ;i < numberOfMatches;i++){
            topMatches.add(matches.get(i));
        }
return topMatches;
}

}
