import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * @author Tej
 *
 */
public class NaiveBayesTagging {
	private Map<String, Integer> uniTagMap, biTagMap, wordTag;
	private File training;

	/**
	 * Initializes training file and hashmaps
	 * 
	 * @param f Input file
	 * @throws IOException
	 */
	public NaiveBayesTagging(File f) throws IOException {
		this.training = f;
		uniTagMap = new HashMap<>();
		biTagMap = new LinkedHashMap<>();
		wordTag = new HashMap<>();
		initialize();
	}

	/**
	 * Parses the file and initializes the hash map
	 * 
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		Scanner fileReader = new Scanner(this.training);
		fileReader.useDelimiter("\\s+");
		String prevTag = null;
		String curTag = null;
		if (fileReader.hasNext()) {
			String word_tag = fileReader.next();
			String[] wordTagg = word_tag.split("_");
			this.uniTagMap.put(wordTagg[1], uniTagMap.getOrDefault(wordTagg[1], 0) + 1);
			this.wordTag.put(word_tag, this.wordTag.getOrDefault(word_tag, 0) + 1);
			curTag = wordTagg[1];
		}

		while (fileReader.hasNext()) {
			prevTag = curTag;
			String word_tag = fileReader.next();
			String[] wordTagg = word_tag.split("_");
			if (wordTagg.length > 1) {
				curTag = wordTagg[1];
				this.uniTagMap.put(wordTagg[1], uniTagMap.getOrDefault(wordTagg[1], 0) + 1);
				this.wordTag.put(word_tag, this.wordTag.getOrDefault(word_tag, 0) + 1);
				this.biTagMap.put(prevTag + " " + curTag, this.biTagMap.getOrDefault(prevTag + " " + curTag, 0) + 1);
			}
		}
		fileReader.close();
		generateProbabilityFiles();
	}

	/**
	 * Calculated the bigram tag probabilities and probability of word
	 * 
	 * @throws IOException
	 */
	private void generateProbabilityFiles() throws IOException {
		FileWriter bigramTag = new FileWriter("BigramTagProbability.txt");
		PrintWriter pbt = new PrintWriter(bigramTag);
		for (Map.Entry<String, Integer> s : this.biTagMap.entrySet()) {
			pbt.println(s.getKey() + " " + s.getValue());
		}
		pbt.close();

		FileWriter wordTag = new FileWriter("WordTag.txt");
		PrintWriter wt = new PrintWriter(wordTag);
		for (Map.Entry<String, Integer> s : this.wordTag.entrySet()) {
			wt.println(s.getKey() + " " + s.getValue());
		}
		wt.close();
	}

	/**
	 * Computes the appropriate tag given the word and prev tag
	 * 
	 * @param word Word whose tag is to be computed
	 * @param prevTag Previous words tag
	 * @return Naive Bayes classified tag with maximum likelihood
	 */
	public String getTags(String word, String prevTag) {
		String[] tags = { "NN", "VB" };
		double maxProb = 0;
		String result = "";
		for (String tag : tags) {
			double probability = ((double) this.wordTag.getOrDefault(word + "_" + tag, 1)
					/ this.uniTagMap.getOrDefault(tag, 1));
			probability *= ((double) this.biTagMap.getOrDefault(prevTag + " " + tag, 0)
					/ this.uniTagMap.getOrDefault(prevTag, 1));
			if (probability > maxProb) {
				maxProb = probability;
				result = tag;
			}
		}
		return result;
	}

	/**
	 * Driver Function
	 * 
	 * @param cd
	 * @throws IOException
	 */
	public static void main(String[] cd) throws IOException {
        if(cd.length != 1) {
            System.out.println("Please enter file name");
            System.out.println("Try - 'java NaiveBayesTagging POSTaggedTrainingSet.txt'");
            System.exit(1);
        }
		NaiveBayesTagging nbt = new NaiveBayesTagging(new File(cd[0]));
        Scanner sc = new Scanner(System.in);
        System.out.println("Press 1 to enter : word for which tags is to be found and previous words tag");
        System.out.println("Press anything except 1 to terminate");
        whileloop: while(sc.hasNext()) {    
            String c = sc.nextLine();
            switch(c) {
                case "1":
                String word = sc.nextLine();
                String prevTag = sc.nextLine();
                System.out.println("Most Probable Tag is " + nbt.getTags(word, prevTag));
                break;

                default:
                break whileloop;
            }
            System.out.println();
            System.out.println("Press 1 to enter : word for which tags is to be found and previous words tag");
            System.out.println("Press anything except 1 to terminate");
        }
        sc.close();
	}
}
