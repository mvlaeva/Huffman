import java.io.*;
import java.util.*;

abstract class HuffmanTree implements Comparable<HuffmanTree> {

	public final int frequency; // the frequency of this tree

	public HuffmanTree(int freq) {
		frequency = freq;
	}

	// compares on the frequency
	public int compareTo(HuffmanTree tree) {
		return frequency - tree.frequency;
	}
}

class HuffmanLeaf extends HuffmanTree {
	public final char value; // the character this leaf represents

	public HuffmanLeaf(int freq, char val) {
		super(freq);
		value = val;
	}
}

class HuffmanNode extends HuffmanTree {
	public final HuffmanTree left, right; // subtrees

	public HuffmanNode(HuffmanTree l, HuffmanTree r) {
		super(l.frequency + r.frequency);
		left = l;
		right = r;
	}
}

public class Huffman implements Runnable {
	static long start1 = System.currentTimeMillis();
	private static HuffmanTree res;
	private int[] frequencies;
	private int current;
	private int max;
	
	
	public Huffman(int[] freqs, int currentThread, int maxThreads) {
		frequencies = freqs;
		current = currentThread;
		max = maxThreads;
	}
	
	public static HuffmanTree GetTree() {
		return res;
	}

	// input is an array of frequencies, indexed by character code
	public void buildTree(int[] charFreqs) {
		PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
		
		// initially, we have a forest of leaves
		// one for each non-empty character
		int startIndex = (current / max) * charFreqs.length;
		int endIndex = ((current + 1) / max) * charFreqs.length;
		
		for (int i = startIndex; i < endIndex; i++)
			if (charFreqs[i] > 0)
				trees.offer(new HuffmanLeaf(charFreqs[i], (char) i));

		// loop until there is only one tree left
		while (trees.size() > 1) {
			// two trees with least frequency

			HuffmanTree a = trees.poll();
			HuffmanTree b = trees.poll();

			// put into new node and re-insert into queue
			trees.offer(new HuffmanNode(a, b));
		}
		updateTree(trees);
	}
	
	private synchronized void updateTree(PriorityQueue<HuffmanTree> pQueue){
		res = pQueue.poll();
	}
	
	public static void print(StringBuffer prefix) {
		printRecursive(res, prefix);
	}
	
	private static void printRecursive(HuffmanTree tree, StringBuffer prefix) {
		if (tree instanceof HuffmanLeaf) {
			HuffmanLeaf leaf = (HuffmanLeaf) tree;

			// print out character, frequency, and code for this leaf (which is
			// just the prefix)
			System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);

		} else if (tree instanceof HuffmanNode) {
			HuffmanNode node = (HuffmanNode) tree;

			// traverse left
			prefix.append('0');
			printRecursive(node.left, prefix);
			prefix.deleteCharAt(prefix.length() - 1);

			// traverse right
			prefix.append('1');
			printRecursive(node.right, prefix);
			prefix.deleteCharAt(prefix.length() - 1);
		}
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String test = "mreji.txt";
		String result = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(test));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    result = sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int threadsCount = 0;

		do {
			System.out.println("Threads: ");
			threadsCount = input.nextInt();
		} while (threadsCount <= 0);

		// we will assume that all our characters will have
		// code less than 256, for simplicity
		int[] charFreqs = new int[result.length()];

		// allows to execute the process and then do smth with the result

		// read each character and record the frequencies
		for (char c : result.toCharArray())
			charFreqs[c]++;
		

		Thread[] threads = new Thread[threadsCount];
		for (int i = 0; i < threadsCount; i++) {

			// print out results
			threads[i] = new Thread(new Huffman(charFreqs, i, threadsCount));
			threads[i].start();
		}
		for (int i = 0; i < threadsCount; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");

		long stop = System.currentTimeMillis();
		long diff = stop - start1;
//		
		print(new StringBuffer());
		
		System.out.println("Total execution time for current run: " + diff + " ms");

	}

	@Override
	public void run() {
		buildTree(frequencies);
	}

}
