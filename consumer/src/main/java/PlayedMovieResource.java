import org.eclipse.microprofile.reactive.messaging.Channel;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import models.PlayedMovie;

@Path("/movies")
public class PlayedMovieResource {
  @Channel("played-movies")
  Multi<PlayedMovie> playMovies;

  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public Multi<PlayedMovie> stream() {
    return playMovies;
  }
}
