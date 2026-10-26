package twitter;

import java.time.Instant;
import java.util.*;

/** Reproducible offline demonstration of the Lab 07 social network toolbox. */
public class Main {
    public static void main(String[] args) {
        List<Tweet> tweets = Arrays.asList(
                new Tweet(1, "Alice", "Learning with @Bob and @Carol", Instant.EPOCH),
                new Tweet(2, "Bob", "Thanks @Carol", Instant.EPOCH),
                new Tweet(3, "Dave", "Hello @Carol", Instant.EPOCH),
                new Tweet(4, "ALICE", "Again @BOB", Instant.EPOCH));
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        System.out.println("Lab 07 - Social Network Analysis");
        System.out.println("Input: 4 offline sample tweets");
        System.out.println("Following graph (author -> mentioned users):");
        for (String author : new TreeSet<>(graph.keySet())) {
            System.out.println("  " + author + " -> " + new TreeSet<>(graph.get(author)));
        }
        System.out.println("Top 10 influencers (or all if fewer than 10):");
        List<String> users = SocialNetwork.influencers(graph);
        for (int i = 0; i < Math.min(10, users.size()); i++) {
            String user = users.get(i);
            long count = graph.values().stream().filter(set -> set.contains(user)).count();
            System.out.printf("%d. %s - %d follower(s)%n", i + 1, user, count);
        }
    }
}
