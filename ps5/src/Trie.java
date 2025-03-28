import java.util.ArrayList;

public class Trie {
    TrieNode root;
    // Wildcards
    final char WILDCARD = '.';

    private class TrieNode {
        // TODO: Create your TrieNode class here.
        int[] presentChars = new int[62];
        TrieNode[] children = new TrieNode[63];
    }

    public Trie() {
        // TODO: Initialise a trie class here.
        root = new TrieNode();
    }

    //Converts characters in string to TrieNode indexes
    private int charToIndex(char c) {
        if (c >= 'A' && c <= 'Z') return c - 'A';         // 0-25
        if (c >= 'a' && c <= 'z') return c - 'a' + 26;    // 26-51
        if (c >= '0' && c <= '9') return c - '0' + 52;    // 52-61
        return -1;
    }

    /**
     * Inserts string s into the Trie.
     *
     * @param s string to insert into the Trie
     */
    void insert(String s) {
        // TODO
        TrieNode current = root;
        for (int i = 0; i < s.length(); i++) {
            int index = charToIndex(s.charAt(i));
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current.presentChars[index]++; // Track occurrences
            current = current.children[index];
        }
        current.children[62] = new TrieNode();
    }

    /**
     * Checks whether string s exists inside the Trie or not.
     *
     * @param s string to check for
     * @return whether string s is inside the Trie
     */
    boolean contains(String s) {
        // TODO
        TrieNode current = root;
        for (int i = 0; i < s.length(); i++) {
            int index = charToIndex(s.charAt(i));
            if (current.children[index] == null) {
                return false;
            }
            current = current.children[index];
        }
        if (current.children[62] == null) {
            return false;
        }
        return true;
    }

    /**
     * Searches for strings with prefix matching the specified pattern sorted by lexicographical order. This inserts the
     * results into the specified ArrayList. Only returns at most the first limit results.
     *
     * @param s       pattern to match prefixes with
     * @param results array to add the results into
     * @param limit   max number of strings to add into results
     */
    void prefixSearch(String s, ArrayList<String> results, int limit) {
        helper(root, s, 0, "", results, limit);
    }

    private void helper(TrieNode node, String s, int index, String currentWord, ArrayList<String> results, int limit) {
        if (node == null || results.size() >= limit) {
            return;
        }
        //Only search after processing full RegEx input
        if (index == s.length()) {
            dfs(node, currentWord, results, limit);
            return;
        }
        char c = s.charAt(index);
        if (c == '.') {
            for (int j = 0; j < 62; j++) {
                if (node.children[j] != null) {
                    helper(node.children[j], s, index + 1, currentWord + indexToChar(j), results, limit);
                }
            }
        } else {
            int charIndex = charToIndex(c);
            helper(node.children[charIndex], s, index + 1, currentWord + c, results, limit);
        }
    }

    private void dfs(TrieNode node, String s, ArrayList<String> results, int limit) {
        if (results.size() >= limit) {
            return;
        }
        if (node.children[62] != null) { // Correct word completion check
            results.add(s);
        }
        for (int i = 0; i < 62; i++) {
            if (node.children[i] != null) {
                dfs(node.children[i], s + indexToChar(i), results, limit);
            }
        }
    }
    private char indexToChar(int index) {
        if (index >= 0 && index < 26) return (char) ('A' + index);
        if (index >= 26 && index < 52) return (char) ('a' + (index - 26));
        if (index >= 52 && index < 62) return (char) ('0' + (index - 52));
        return '?';
    }


    // Simplifies function call by initializing an empty array to store the results.
    // PLEASE DO NOT CHANGE the implementation for this function as it will be used
    // to run the test cases.
    String[] prefixSearch(String s, int limit) {
        ArrayList<String> results = new ArrayList<String>();
        prefixSearch(s, results, limit);
        return results.toArray(new String[0]);
    }


    public static void main(String[] args) {
        Trie t = new Trie();
        t.insert("peter");
        t.insert("piper");
        t.insert("picked");
        t.insert("a");
        t.insert("peck");
        t.insert("of");
        t.insert("pickled");
        t.insert("peppers");
        t.insert("pepppito");
        t.insert("pepi");
        t.insert("pik");

        String[] result1 = t.prefixSearch("pe", 10);
        String[] result2 = t.prefixSearch("p.p", 5);
        // result1 should be:
        for (String word : result2) {
            System.out.println(word);
        }
        // ["peck", "pepi", "peppers", "pepppito", "peter"]
        // result2 should contain the same elements with result1 but may be ordered arbitrarily
    }
}
