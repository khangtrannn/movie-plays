package org.acme;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.acme.models.Movie;
import org.acme.models.PlayedMovie;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
 
@ApplicationScoped
public class MovieKafkaGenerator { 
    private static final Logger LOG = Logger.getLogger(MovieKafkaGenerator.class);
    private List<Movie> movies = List.of(
       new Movie(1, "The Hobbit", "Peter Jackson", "Fantasy"),
       new Movie(2, "Star Trek: First Contact", "Jonathan Frakes", "Space"),
       new Movie(3, "Encanto", "Jared Bush", "Animation"),
       new Movie(4, "Cruella", "Craig Gillespie", "Crime Comedy"),
       new Movie(5, "Sing 2", "Garth Jennings", "Jukebox Musical Comedy")
   );

    private Random random = new Random();

    @Inject
    Logger logger;

   @Outgoing("play-time-movies")
    public Multi<Record<String, PlayedMovie>> generate() {
    return Multi.createFrom().ticks().every(Duration.ofMillis(2000))   
            .onOverflow().drop()
            .map(tick -> {
                Movie movie = movies.get(random.nextInt(movies.size()));
                int time = random.nextInt(300);
                LOG.infof("movie %s played for %d minutes", movie.name, time);

                // Region as key
                return Record.of("eu", new PlayedMovie(movie.id, time));
    });
}
 
   // Populates movies into Kafka topic
   @Outgoing("movies")                                         
   public Multi<Record<Integer, Movie>> movies() {
       return Multi.createFrom().items(movies.stream()
               .map(m -> Record.of(m.id, m))
       );
   }
}
