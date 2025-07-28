import java.util.*;

public class RecommendationEngine {

    static class Item {
        String title;
        Set<String> keywords;

        Item(String title, String... keywords) {
            this.title = title;
            this.keywords = new HashSet<>(Arrays.asList(keywords));
        }

        @Override
        public String toString() {
            return title;
        }
    }

    // Cosine similarity based on Jaccard index for simplicity
    static double similarity(Item a, Item b) {
        Set<String> intersection = new HashSet<>(a.keywords);
        intersection.retainAll(b.keywords);

        Set<String> union = new HashSet<>(a.keywords);
        union.addAll(b.keywords);

        return (double) intersection.size() / union.size();
    }

    public static List<Item> recommend(Item input, List<Item> items, int topN) {
        Map<Item, Double> scores = new HashMap<>();

        for (Item item : items) {
            if (!item.equals(input)) {
                double score = similarity(input, item);
                scores.put(item, score);
            }
        }

        return scores.entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static void main(String[] args) {
        List<Item> itemList = List.of(
            new Item("Inception", "sci-fi", "dream", "action"),
            new Item("Interstellar", "space", "sci-fi", "drama"),
            new Item("The Matrix", "action", "sci-fi", "virtual"),
            new Item("The Notebook", "romance", "drama"),
            new Item("Avengers", "action", "superhero", "sci-fi")
        );

        Item input = new Item("Your Selection", "action", "sci-fi");

        System.out.println("Recommendations for: " + input.keywords);
        List<Item> recommendations = recommend(input, itemList, 3);
        for (Item item : recommendations) {
            System.out.println(" - " + item);
        }
    }
}
