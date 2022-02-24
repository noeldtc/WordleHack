import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Main {

    private static  String[] dicc;
    private static Map<Character,Void> descartes;

    public static void main(String[] args) throws FileNotFoundException {
        InputStream filearray = Main.class.getResourceAsStream("array.bin");
        if(filearray==null) throw new FileNotFoundException("array.bin not found.");
        System.out.println("Wordle ES: Pon las letras que lleves ya y te daremos la lista con el posible resultado en ella. " +
                "Para representar el valor actual se debe poner: Palabras descartadas -> letra descartada.letras..., siguiente ;Casillas verdes -> letra, Casillas amarillas -> letra mayuscula y " +
                "Casilla vacia -> '-'. Ejemplo: escribe: lT-io; para la solucion: litio.");
        Scanner sc = new Scanner(filearray);;
        String st = "";
        while (sc.hasNextLine()) st+= sc.nextLine();
        sc.close();

        if(st.length()>0) System.out.println("FILE READ CORRECTLY");
        else {
            System.out.println("FILE READ UNSUCCESFULLY");
            return;
        }

        dicc = st.split(",");

        Main main = new Main();
        main.readHashPopularWords();

        Scanner input = new Scanner(System.in);
        while(true) {
            descartes = new HashMap<>();
            String pattern = input.nextLine();
            if(pattern.contains(",")){
                String[] data = pattern.split(",");
                String[] letras_descartadas = data[0].split("/");
                for(String letra: letras_descartadas) descartes.put(letra.toLowerCase(Locale.ROOT).charAt(0),null);
                pattern = data[1];
            }
            if(pattern.length()==5) {
                List<Word> res = main.getResults(pattern);

                Collections.sort(res, new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return (int)o1.perc - (int)o2.perc;
                    }
                });
                for(Word word: res){
                    System.out.println(word.word + " -> "+ (word.perc) +"%");
                }


                System.out.println("\n-----\nSe han encontrado "+ res.size() + " resultados. \nProbabilidad de acertar: " + (1. * 100/res.size())+ " %");
                System.out.println("\n\n");
            }else System.out.println("Error: Length has to be 5. \n\n");
        }

    }


    public class Word{
        private String word;
        private double perc;

        public Word(String word, double perc) {
            this.word = word;
            this.perc = perc;
        }
    }

    private Map<String,Integer> popularWords;
    private void readHashPopularWords() throws FileNotFoundException {
        popularWords = new HashMap<>();
        InputStream filearray = Main.class.getResourceAsStream("word_popularity.csv");
        if(filearray ==null) throw new FileNotFoundException("word_popularity.csv not found");
        Scanner sc = new Scanner(filearray);
        sc.nextLine();
        sc.nextLine();
        int count = 0;
        while (sc.hasNextLine()) {
            String word = sc.nextLine().split(",")[0];
            if(word.length()==5)popularWords.put(word,count++);
        };
        sc.close();

    }


    private double getPoints(String word){
        double res = 0;
        char[] letters = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','ñ','o','p','q','r','s','t','u','v','w','x','y','z'};
        double[] perc = new double[]{12.53,1.42,4.68,5.86,13.68,0.69,1.01,0.7,6.25,0.44,0.02,4.97,3.15,6.71,0.31,8.68,2.51,0.88,6.87,7.98,4.63,3.93,0.9,0.01,0.22,0.9,0.52};
        //Popularity with letters
        Map<Character,Void> charsProcessed = new HashMap<>();
        for(int i =0; i < word.length();i++){
            char letter = word.charAt(i);
            for(int j =0; j < letters.length;j++){
                if(letters[j] == letter && !charsProcessed.containsKey(letter)){
                    res+=perc[j];
                    charsProcessed.put(letter,null);
                }
            }
        }
        //Popularity of whole words
        double popularity = 0;
        if(popularWords.containsKey(word)){
            popularity = (popularWords.size()-popularWords.get(word))/popularWords.size();
        }

        res = (res+popularity)/2;

        return res;
    }


    private List<Word> getResults(String pattern){
        List<Word> res = new LinkedList<>();

        for(String word : dicc){
            if(confirm(word,pattern)){
                res.add(new Word(word,getPoints(word)));
            }
        }

        return res;
    }

    private boolean confirm(String word,String pattern){
        boolean res=false;

        for (int i =0; i < word.length();i++){
            if(descartes.containsKey(word.toLowerCase(Locale.ROOT).charAt(i))) return false;
        }

        for(int i =0; i < pattern.length();i++){
            if(pattern.charAt(i) == word.charAt(i) || pattern.charAt(i) == '-'){
                res = true;
            }else if(Character.isUpperCase(pattern.charAt(i))){
                char minuscula=Character.toLowerCase(pattern.charAt(i));
                boolean found = false;
                for(int j =0; j < word.length();j++){
                    if(i==j && minuscula == word.charAt(j)){
                        return false;
                    } if(i!=j && minuscula == word.charAt(j)){
                        found=true;
                    }
                }
                if(!found) return false;
            }else{
                return false;
            }
        }
        return  res;
    }

    private static boolean checkString(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < str.length();i++) {
            ch = str.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if(numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }


}
