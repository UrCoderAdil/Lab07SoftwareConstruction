package twitter;

import java.util.*;
import java.util.regex.*;

/**
 * Infers following from mentions and ranks users by distinct follower count.
 * Usernames are case insensitive. A graph maps each author to followed users.
 * A user cannot follow themselves; empty adjacency sets may be omitted.
 */
public class SocialNetwork {
    // Tweet usernames include letters, digits, underscore and hyphen.
    // The left boundary excludes email addresses and embedded usernames.
    private static final Pattern MENTION = Pattern.compile(
            "(?<![A-Za-z0-9_-])@([A-Za-z0-9_-]+)(?![A-Za-z0-9_-])");

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * @param tweets evidence, not modified by this method
     * @return a social network in which Ernie follows Bert iff there is evidence
     * in the tweets. An @-mention is required evidence; other evidence is
     * permitted by the assignment. Every returned user is an author or mention.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        throw new UnsupportedOperationException("RED phase: not implemented");
    }

    /**
     * Find users ordered by descending number of followers.
     * @param followsGraph a social network as defined by the class, not modified
     * @return all distinct usernames in the graph, including zero-follower users,
     * in descending follower-count order. Ties may appear in any order.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        throw new UnsupportedOperationException("RED phase: not implemented");
    }
}
