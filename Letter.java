import java.util.ArrayList;
import java.util.List;

public class Letter {

    private List<Character> consonant;
    private List<Character> vowels;

    public Letter() {
        this.vowels = new ArrayList<>();
        vowels.add('a');
        vowels.add('e');
        vowels.add('i');
        vowels.add('o');
        vowels.add('u');
        vowels.add('y');
        this.consonant = new ArrayList<>();
        for (int i = 97; i < 123; i++) {
            if (vowels.contains((char) i)) continue;
            this.consonant.add((char) i);
        }
    }

    public List<Character> getConsonant() {
        return consonant;
    }

    public List<Character> getVowels() {
        return vowels;
    }
}
