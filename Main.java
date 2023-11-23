import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static List<String> words;                  // List that will contain the 5 names of the oil rigs
    private static List<Character> availableVowels;     // List containing the Vowels that are still available
    private static List<Character> availableConsonants; // List containing the Consonants that are still available
    private static List<String> wordsPool;              // List containing all the words to choose from
    private static Letter l;    // object of letter, used to retrieve vowels and consonants

    public static void main(String[] args) throws FileNotFoundException {
        // record the starting time
        long startTime = System.currentTimeMillis( );

        // initialise the available letters from l
        l = new Letter();
        availableVowels = new ArrayList<>();
        availableVowels.addAll(l.getVowels());
        availableConsonants = new ArrayList<>();
        availableConsonants.addAll(l.getConsonant());

        // add the first 2 words through the addWord method, that also deletes all the used letters
        // from the available ones
        words = new ArrayList<>();
        addWord("fjord");
        addWord("vibex");

        // create a Scanner to read the words, the file is in the path in the parameter
        Scanner scanner = new Scanner(new File("resources/helin.txt"));
        // reads and also checks all the words, the valid ones are added to wordsPool
        wordsPool = readWords(scanner);

        // find the remaining 3 words and add them to wordsPool
        findWord();

        // set the starting date to sunday 26 at 8pm and the formatter to print the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm");
        LocalDateTime startingDate = LocalDateTime.of(2023, 11, 26, 20, 0);

        // We set the initial duration to 7 (3 for the first flight + 4 for the travel between each rig)
        int duration = 7;
        // We add to duration the result of the calcDur function applied to all 5 words
        for (int i = 0; i < words.size(); i++) {
            duration += calcDur(words.get(i));
        }

        // We finally add the ascii code of the last letter - 96 (a = 1, b = 2, etc...)
        if (availableVowels.isEmpty()) {
            duration += (((int) availableConsonants.get(0)) - 96);
        } else {
            duration += (((int) availableVowels.get(0)) - 96);
        }

        // Print the results
        System.out.println("The names of the 5 rigs are:");
        for (int i = 0; i < words.size(); i++) {
            System.out.print(words.get(i));
            if (i < words.size() - 1) System.out.print(", ");
            else System.out.println();
        }
        System.out.println("The trip takes " + duration + " hours.");
        System.out.println("It starts on " + startingDate.format(formatter));
        System.out.println("And finishes on " + startingDate.plusHours(duration).format(formatter));

        // record the ending time
        long endTime = System.currentTimeMillis( );
        System.out.println("Process finished in " + (endTime - startTime) + "ms");

    }

    /**
     * check if all the letters in the word are still available to use
     * also check if the number of vowels is EXACTLY 1
     * this is because we have 3 available vowels (a,u,y) and only 3 words
     * they all must contain at least a value, therefore we need
     * to ensure that no word has more than 1, so that every word can have 1
     *
     * @param word  the word to check
     * @return  true, if the word is valid, false otherwise
     */
    public static boolean checkWord(String word) {
        char[] letters = word.toCharArray();
        // if the first or last letter is a vowel, return false
        if (l.getVowels().contains(letters[0]) || l.getVowels().contains(letters[4])) {
            return false;
        }

        int vowelCounter = 0;
        /* create a copy of the pool of consonants, we remove the letters as soon as we find them
         * to prevent duplicate letters inside the same word, we don't need to do the same for vowels
         * because we already stated that if a word contains more than a vowel it's automatically not valid
         * although this might not be true in all cases, but it is in this one */
        List<Character> ac = new ArrayList<>(List.copyOf(availableConsonants));
        for (char c:letters) {
            // if it's an available vowel increment the counter and go to the next letter
            if (availableVowels.contains(c)) {
                vowelCounter++;
                continue;
            }
            // if it's an available consonant remove it from the pool ac
            if (ac.contains(c)) {
                ac.remove((Character) c);
            // if it's not an available letter, return false
            } else {
                return false;
            }
        }
        // if only one vowel has been seen, return true
        return vowelCounter == 1;
    }

    /**
     * adds a new word to the collection and removes all of its
     * letters from the available letters
     * @param word  the word to insert
     */
    public static void addWord(String word) {
        char[] letters = word.toCharArray();
        for (char c:letters) {
            if (availableVowels.contains(c)) {
                availableVowels.remove((Character) c);
            } else {
                availableConsonants.remove((Character) c);
            }
        }
        words.add(word);
    }

    /**
     * read all words from the file (using the passed Scanner)
     * and if they are valid, they are added to the list of available words
     * we are also mapping the position of the first words starting with
     * each letter
     * @param input Scanner to read from
     * @return  An ArrayList of read words
     */
    public static List<String> readWords(Scanner input) {
        List<String> result = new ArrayList<>();
        while(input.hasNextLine()) {
            String word = input.nextLine().trim();
            if (checkWord(word)) result.add(word);
        }
        return result;
    }

    /**
     * Main routine to research the 3 words
     * we start with words containing y and z, since there are fewer
     */
    public static void findWord() {
        List<String> aWords = setWordList('a');     // words containing a
        List<String> uWords = setWordList('u');     // words containing u
        List<String> yWords = setWordList('y');     // words containing y
        List<String> zWords = setWordList('z');     // words containing z

        String[] chosenWords = new String[3];
        for (String y:yWords) {
            for (String z:zWords) {
                char[] cz = z.toCharArray();
                for (char c:cz) {
                    if (c == 'u') {
                        int found = searchFor(y, z, aWords);
                        if (found != -1) {
                            addWord(y);
                            addWord(z);
                            addWord(aWords.get(found));
                            return;
                        }
                        break;
                    } else if (c == 'a') {
                        int found = searchFor(y, z, uWords);
                        if (found != -1) {
                            addWord(y);
                            addWord(z);
                            addWord(uWords.get(found));
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * calculates the duration of the job on a rig based on
     * the conditions defined in the challenge
     * @param word  the name of the oil rig
     * @return      the amount of hours required to work on it
     */
    private static int calcDur(String word) {
        if (l.getConsonant().contains(word.charAt(1))) {
            if (l.getConsonant().contains(word.charAt(2))) {
                return 5;
            } else {
                return 15;
            }
        } else if (l.getConsonant().contains(word.charAt(2))) {
            if (l.getConsonant().contains(word.charAt(3))) {
                return 20;
            } else {
                return 10;
            }
        }
        return 30;
    }

    /**
     * searches in the List a word that doesn't have any letter in common
     * with the other 2 words a and b, also checks if a and b themselves
     * are only composed of unique letters, at this step we are not checking
     * anymore for duplicate letters inside a word because we already filtered
     * out all the words with this property
     * @param a     first word
     * @param b     second word
     * @param words list of words to search for the third word
     * @return      the index of the word in words that passes the test, if no word is found -1
     */
    private static int searchFor(String a, String b, List<String> words) {
        List<Character> remaining = new ArrayList<>();
        remaining.addAll(availableVowels);
        remaining.addAll(availableConsonants);
        char[] ca = a.toCharArray();
        for (char c:ca) {
            if (!remaining.contains(c)) return -1;
            else remaining.remove((Character) c);
        }
        char[] cb = b.toCharArray();
        for (char c:cb) {
            if (!remaining.contains(c)) return -1;
            else remaining.remove((Character) c);
        }
        for (String word:words) {
            boolean wrong = false;
            char[] cw = word.toCharArray();
            for (char c:cw) {
                if (!remaining.contains(c)) {
                    wrong = true;
                    break;
                }
            }
            if (!wrong) return words.indexOf(word);
        }
        return -1;
    }

    /**
     * Creates List as a list of all words in wordPool
     * that contain the passed letter
     * @param letter    letter to check
     * @return      the List of words
     */
    public static List<String> setWordList(char letter) {
        List<String> result = new ArrayList<>();
        for (String word:wordsPool) {
            char[] letters = word.toCharArray();
            for (char c:letters) {
                if (c == letter) result.add(word);
            }
        }
        return result;
    }

}
