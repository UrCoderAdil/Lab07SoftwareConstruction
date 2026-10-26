package twitter;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.junit.Test;

/** Stronger tests for this implementation's minimum mention-only policy. */
public class MySocialNetworkTest {
    @Test public void noMentionsOmitKeys() {
        Tweet tweet = new Tweet(1, "alice", "Hello", Instant.EPOCH);
        assertTrue(SocialNetwork.guessFollowsGraph(Arrays.asList(tweet)).isEmpty());
    }
    @Test public void exactMentionOnlyEdges() {
        List<Tweet> tweets = Arrays.asList(
                new Tweet(1, "Alice", "@Bob @ALICE @bob", Instant.EPOCH),
                new Tweet(2, "Carol", "Hello", Instant.EPOCH));
        Map<String, Set<String>> expected = new HashMap<>();
        expected.put("alice", new HashSet<>(Arrays.asList("bob")));
        assertEquals(expected, SocialNetwork.guessFollowsGraph(tweets));
    }
}
