package twitter;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.junit.Test;

/**
 * Partition strategy
 * guessFollowsGraph: 0/1/many tweets, 0/1/many mentions, one/many authors,
 * repeated mentions, mixed case, self mentions, boundaries, input preservation.
 * Required mention edges are checked as subsets. Extra legal evidence is allowed;
 * casing, empty-key representation and iteration order are not prescribed.
 * influencers: empty/nonempty graphs, zero/one/many followers, unequal/tied counts,
 * users occurring as keys/values/both, case variations, and input preservation.
 * Rankings must contain every distinct graph user and have nonincreasing counts.
 */
public class SocialNetworkTest {
    private static Tweet tweet(long id, String author, String text) {
        return new Tweet(id, author, text, Instant.parse("2026-10-23T04:00:00Z"));
    }
    private static Set<String> set(String... users) {
        return new HashSet<>(Arrays.asList(users));
    }
    private static Map<String, Set<String>> normalized(Map<String, Set<String>> graph) {
        Map<String, Set<String>> result = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            String user = entry.getKey().toLowerCase(Locale.ROOT);
            assertFalse("Duplicate author ignoring case", result.containsKey(user));
            Set<String> targets = new HashSet<>();
            for (String target : entry.getValue()) {
                String lower = target.toLowerCase(Locale.ROOT);
                assertNotEquals("No self following", user, lower);
                assertTrue("No duplicate target ignoring case", targets.add(lower));
            }
            result.put(user, targets);
        }
        return result;
    }
    private static void edge(Map<String, Set<String>> graph, String from, String to) {
        assertTrue("Missing required edge " + from + " -> " + to,
                normalized(graph).getOrDefault(from, Collections.emptySet()).contains(to));
    }
    private static void onlyUsers(Map<String, Set<String>> graph, String... users) {
        Set<String> allowed = set(users);
        for (Map.Entry<String, Set<String>> entry : normalized(graph).entrySet()) {
            assertTrue(allowed.contains(entry.getKey()));
            assertTrue(allowed.containsAll(entry.getValue()));
        }
    }
    private static void ranking(Map<String, Set<String>> graph) {
        Map<String, Integer> counts = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : normalized(graph).entrySet()) {
            counts.putIfAbsent(entry.getKey(), 0);
            for (String target : entry.getValue()) counts.merge(target, 1, Integer::sum);
        }
        List<String> actual = SocialNetwork.influencers(graph);
        Set<String> seen = new HashSet<>();
        int previous = Integer.MAX_VALUE;
        for (String user : actual) {
            String lower = user.toLowerCase(Locale.ROOT);
            assertTrue("Unexpected or repeated user", counts.containsKey(lower) && seen.add(lower));
            assertTrue("Counts must not increase", counts.get(lower) <= previous);
            previous = counts.get(lower);
        }
        assertEquals("All graph users must be ranked", counts.keySet(), seen);
    }

    @Test public void emptyTweets() {
        assertTrue(SocialNetwork.guessFollowsGraph(Collections.emptyList()).isEmpty());
    }
    @Test public void tweetsWithoutMentions() {
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet(1, "alice", "Hello world")));
        onlyUsers(graph, "alice"); // An omitted key and an empty adjacency set are both legal.
    }
    @Test public void singleMention() {
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet(1, "ernie", "Hello @bert")));
        edge(graph, "ernie", "bert"); onlyUsers(graph, "ernie", "bert");
    }
    @Test public void multipleMentions() {
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet(1, "alice", "@bob and @carol")));
        edge(graph, "alice", "bob"); edge(graph, "alice", "carol");
        onlyUsers(graph, "alice", "bob", "carol");
    }
    @Test public void multipleTweetsFromOneUser() {
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(Arrays.asList(
                tweet(1, "alice", "@bob @bob"), tweet(2, "alice", "@carol @bob")));
        edge(graph, "alice", "bob"); edge(graph, "alice", "carol");
        onlyUsers(graph, "alice", "bob", "carol");
    }
    @Test public void mixedCaseAuthorsAndMentions() {
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(Arrays.asList(
                tweet(1, "ALIce", "@BOB"), tweet(2, "alice", "@bob @CAROL")));
        edge(graph, "alice", "bob"); edge(graph, "alice", "carol");
        onlyUsers(graph, "alice", "bob", "carol");
    }
    @Test public void selfMentionsDoNotCreateSelfEdges() {
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet(1, "alice", "@ALICE @bob")));
        edge(graph, "alice", "bob"); onlyUsers(graph, "alice", "bob");
    }
    @Test public void mentionBoundariesAndUsernameCharacters() {
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(Arrays.asList(
                tweet(1, "alice", "mail x@invalid.edu; (@user_1), @two-name!")));
        edge(graph, "alice", "user_1"); edge(graph, "alice", "two-name");
        onlyUsers(graph, "alice", "user_1", "two-name");
    }
    @Test public void multipleAuthorsAndTweetsNotModified() {
        List<Tweet> tweets = new ArrayList<>(Arrays.asList(
                tweet(1, "alice", "@bob"), tweet(2, "carol", "@bob")));
        List<Tweet> before = new ArrayList<>(tweets);
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        edge(graph, "alice", "bob"); edge(graph, "carol", "bob");
        assertEquals(before, tweets);
    }
    @Test public void emptyGraphForInfluencers() {
        assertTrue(SocialNetwork.influencers(Collections.emptyMap()).isEmpty());
    }
    @Test public void singleUserWithoutFollowers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alone", set());
        ranking(graph); // The method spec includes zero-follower users.
    }
    @Test public void singleInfluencer() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", set("bob")); ranking(graph);
        assertEquals("bob", SocialNetwork.influencers(graph).get(0).toLowerCase(Locale.ROOT));
    }
    @Test public void multipleInfluencers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", set("bob", "carol"));
        graph.put("bob", set("carol")); graph.put("dave", set("carol"));
        ranking(graph);
    }
    @Test public void tiedInfluence() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", set("bob")); graph.put("bob", set("alice"));
        ranking(graph); // Either tied order is legal.
    }
    @Test public void caseInsensitiveFollowerCounts() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("ALICE", set("BOB")); graph.put("carol", set("bob"));
        graph.put("BoB", set("CAROL")); ranking(graph);
    }
    @Test public void graphNotModified() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", set("bob"));
        Map<String, Set<String>> before = new HashMap<>();
        before.put("alice", set("bob"));
        SocialNetwork.influencers(graph); assertEquals(before, graph);
    }
}
