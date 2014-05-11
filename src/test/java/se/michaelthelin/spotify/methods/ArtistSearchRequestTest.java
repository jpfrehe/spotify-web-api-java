package se.michaelthelin.spotify.methods;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.michaelthelin.spotify.Api;
import se.michaelthelin.spotify.HttpManager;
import se.michaelthelin.spotify.TestUtil;
import se.michaelthelin.spotify.models.Artist;
import se.michaelthelin.spotify.models.Page;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;

@RunWith(MockitoJUnitRunner.class)
public class ArtistSearchRequestTest {

  @Test
  public void shouldGetArtistsResult_async() throws Exception {
    final Api api = Api.DEFAULT_API;
    final HttpManager mockedHttpManager = TestUtil.MockedHttpManager.returningJson("search-artist.json");
    final ArtistSearchRequest request = api.searchArtists("David Bowie").httpManager(mockedHttpManager).build();

    final CountDownLatch asyncCompleted = new CountDownLatch(1);

    final SettableFuture<Page<Artist>> searchResultFuture = request.getAsync();

    Futures.addCallback(searchResultFuture, new FutureCallback<Page<Artist>>() {
      @Override
      public void onSuccess(Page<Artist> artistSearchResult) {
        List<Artist> artists = artistSearchResult.getItems();
        assertEquals(1, artists.size());

        Artist firstArtist = artists.get(0);
        assertEquals("08td7MxkoHQkXnWAYD8d6Q", firstArtist.getId());

        asyncCompleted.countDown();
      }

      @Override
      public void onFailure(Throwable throwable) {
        fail("Failed to resolve future");
      }
    });

    asyncCompleted.await(1, TimeUnit.SECONDS);
  }

  @Test
  public void shouldGetArtistsResult_sync() throws Exception {
    final Api api = Api.DEFAULT_API;
    final HttpManager mockedHttpManager = TestUtil.MockedHttpManager.returningJson("search-artist.json");
    final ArtistSearchRequest request = api.searchArtists("David Bowie").httpManager(mockedHttpManager).build();

    final Page<Artist> artistSearchResult = request.get();

    final List<Artist> artists = artistSearchResult.getItems();
    assertEquals(1, artists.size());

    final Artist firstArtist = artists.get(0);

    assertEquals("08td7MxkoHQkXnWAYD8d6Q", firstArtist.getId());
  }

}
