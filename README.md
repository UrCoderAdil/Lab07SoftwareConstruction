# Lab 07 Test First Programming II

SE-314 Software Construction, Fall 2026. Muhammad Adil Umer, CMS 501991, BESE15-B.

Problem 3 of MIT 6.005 PS1: infer a following graph from @-mentions and rank all graph users by follower count. The Eclipse project contains 16 specification tests and 2 tests for the chosen mention-only policy.

## Run in Eclipse

1. File > Import > General > Existing Projects into Workspace.
2. Select this project folder, then Finish. Use an installed Java 8 or newer JDK (the captured run uses Java 21).
3. Right-click the project > Run As > JUnit Test: 18 tests should pass.
4. Open src/twitter/Main.java > Run As > Java Application.

Eclipse resolves its JUnit 4 library container. The downloadable ZIP also includes JUnit 4.13.2 and Hamcrest 3.0 jars for command-line use. On GitHub, Maven resolves JUnit from pom.xml: run `mvn test`, then `mvn exec:java -Dexec.mainClass=twitter.Main`.

## Specification choices

Usernames are case insensitive and may contain letters, digits, underscore and hyphen, following the assignment's Tweet specification. Self edges are removed; repeated mentions by one author count once. This implementation omits authors with no outgoing edges. The general specification allows empty sets instead, so specification tests accept either representation.

influencers includes every distinct key and value user, even users with zero followers. This follows SocialNetwork.java's method contract. The handout's suggestion that one isolated user produces no influencers conflicts with that contract. Equal counts may appear in any order; this implementation uses alphabetical order for reproducible output.

Main uses four fixed offline tweets, not a live Twitter feed. Its ranking is carol 3, bob 1, alice 0, dave 0. The project isolates Lab 07 Problem 3; Extract and Filter exercises are outside its scope. Tweet.java is retained unchanged from the MIT starter, with its copyright notice.

## Verification

The red Eclipse run had 16 errors because both methods threw UnsupportedOperationException. The same specification suite passed 16/16 after implementation. The complete project passed 18/18 with zero errors and zero failures. Screenshots and full source listings are included in the accompanying Word report.

Assignment: https://ocw.mit.edu/ans7870/6/6.005/s16/psets/ps1/
