import static org.junit.Assert.*;

import org.junit.Test;


public class TesterX {

	/** Test case for basic insert condition, no split will occur. */
	@Test
	public void testBasicInsertion() {
		Character alphabet[] = new Character[] { 'a','b','c','d'};
		  String alphabetStrings[] = new String[alphabet.length];
		  for (int i = 0; i < alphabet.length; i++) {
		    alphabetStrings[i] = (alphabet[i]).toString();
		  }
			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			Utils.bulkInsert(tree, alphabet, alphabetStrings);

			String test = Utils.outputTree(tree);
			String correct = 
			    "[(a,a);(b,b);(c,c);(d,d);]$%%";
			assertEquals(correct, test);
	}
	
	
	/** Test case for add and split. */
	@Test
	public void testBasicLeafSplit() {
		Character alphabet[] = new Character[] { 'a','b','c','d','e'};
		  String alphabetStrings[] = new String[alphabet.length];
		  for (int i = 0; i < alphabet.length; i++) {
		    alphabetStrings[i] = (alphabet[i]).toString();
		  }
			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			Utils.bulkInsert(tree, alphabet, alphabetStrings);

			String test = Utils.outputTree(tree);
			String correct = 
				"@c/@%%[(a,a);(b,b);]#[(c,c);(d,d);(e,e);]$%%";
			assertEquals(correct, test);
	}
	
	/** Test case for add and split. */
	@Test
	public void testBasicLeafSplit2() {
		Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g','h'};
		  String alphabetStrings[] = new String[alphabet.length];
		  for (int i = 0; i < alphabet.length; i++) {
		    alphabetStrings[i] = (alphabet[i]).toString();
		  }
			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			Utils.bulkInsert(tree, alphabet, alphabetStrings);

			String test = Utils.outputTree(tree);
			String correct = 
				"@c/e/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);(g,g);(h,h);]$%%";
			assertEquals(correct, test);
	}
	
	/** Test case for add and split. */
	@Test
	public void testBasicIndexSplit() {
		Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g','h','i','j','k','l','m'};
		  String alphabetStrings[] = new String[alphabet.length];
		  for (int i = 0; i < alphabet.length; i++) {
		    alphabetStrings[i] = (alphabet[i]).toString();
		  }
			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			Utils.bulkInsert(tree, alphabet, alphabetStrings);

			String test = Utils.outputTree(tree);
			String correct = 
				"@g/@%%@c/e/@@i/k/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);]$[(g,g);(h,h);]#"
				+ "[(i,i);(j,j);]#[(k,k);(l,l);(m,m);]$%%";
			assertEquals(correct, test);
	}
	
	/** Test case for delete and redistribute for leaf node and the left sibling
	 *  is choseen
	 *  and left sibling has 2*D elements, which means that the left key should
	 *  redistributed to have D elements.. */
	@Test
	public void testLeafDeleteRedistribution() {
		Character alphabet[] = new Character[] { 'a','e','f','g','h','i','j','b','c'};
		  String alphabetStrings[] = new String[alphabet.length];
		  for (int i = 0; i < alphabet.length; i++) {
		    alphabetStrings[i] = (alphabet[i]).toString();
		  }
			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			Utils.bulkInsert(tree, alphabet, alphabetStrings);
			
			tree.delete('f');

			String test = Utils.outputTree(tree);
			String correct = 
				"@c/h/@%%[(a,a);(b,b);]#[(c,c);(e,e);(g,g);]#[(h,h);(i,i);(j,j);]$%%";
			assertEquals(correct, test);
	}
	
	/** Test case for delete and redistribute for index node and the left sibling
	 *  is choseen
	 *  and left sibling has 2*D elements, which means that the left key should
	 *  redistributed to have D elements.. */
	@Test
	public void testIndexDeleteRedistribution() {
		Character alphabet[] = new Character[] {'a','b','c','d','e','f','r','s','t','u','v','w','x','g','h','i','j','k'};
		  String alphabetStrings[] = new String[alphabet.length];
		  for (int i = 0; i < alphabet.length; i++) {
		    alphabetStrings[i] = (alphabet[i]).toString();
		  }
			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			Utils.bulkInsert(tree, alphabet, alphabetStrings);
			
			tree.delete('t');
			

			String test = Utils.outputTree(tree);
			String correct = 
				"@g/@%%@c/e/@@i/r/v/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);]$[(g,g);(h,h);]#[(i,i);(j,j);(k,k);]#[(r,r);(s,s);(u,u);]#[(v,v);(w,w);(x,x);]$%%";
			assertEquals(correct, test);
	}
	
	/** Test case for leaf node as root and delete. */
	@Test
	public void testRootLeafDelete() {
		Character alphabet[] = new Character[] { 'a','b','c','d'};
		  String alphabetStrings[] = new String[alphabet.length];
		  for (int i = 0; i < alphabet.length; i++) {
		    alphabetStrings[i] = (alphabet[i]).toString();
		  }
			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			Utils.bulkInsert(tree, alphabet, alphabetStrings);

			String test = Utils.outputTree(tree);
			String correct = 
			    "[(a,a);(b,b);(c,c);(d,d);]$%%";
			assertEquals(correct, test);
			
	
			tree.delete('a');
			tree.delete('b');
			tree.delete('c');
			
			test = Utils.outputTree(tree);
			correct = 
			    "[(d,d);]$%%";
			assertEquals(correct, test);
			
			tree.delete('d');
			tree.insert('a', "a");
			test = Utils.outputTree(tree);
			correct = 
			    "[(a,a);]$%%";
			assertEquals(correct, test);
	}
	
	
	/** Test case for search. */
	@Test
	public void testBasicLeafSplit3() {
		Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g','h'};
		  String alphabetStrings[] = new String[alphabet.length];
		  for (int i = 0; i < alphabet.length; i++) {
		    alphabetStrings[i] = (alphabet[i]).toString();
		  }
			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			Utils.bulkInsert(tree, alphabet, alphabetStrings);

			String searchresult = tree.search('a');
			System.out.println(searchresult);
			//assertEquals(correct, test);
	}
	
//	/** Test case for traverse */
//	//@Test
//	public void testBasicLeafTraverse() {
//		Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q'};
//		  String alphabetStrings[] = new String[alphabet.length];
//		  for (int i = 0; i < alphabet.length; i++) {
//		    alphabetStrings[i] = (alphabet[i]).toString();
//		  }
//			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
//			Utils.bulkInsert(tree, alphabet, alphabetStrings);
//
//			tree.traverseLeaf('a');
//			tree.reverseTraverseLeaf('q');
//			
//			tree.delete('c');
//			System.out.println("===============");
//			tree.traverseLeaf('a');
//			tree.reverseTraverseLeaf('q');
//			
//			
//			tree.delete('e');
//			System.out.println("===============");
//			tree.traverseLeaf('a');
//			tree.reverseTraverseLeaf('q');
//			
//			tree.delete('o');
//			tree.delete('p');
//			System.out.println("===============");
//			tree.traverseLeaf('a');
//			tree.reverseTraverseLeaf('q');
//			
//			
//			tree.delete('l');
//			System.out.println("===============");
//			tree.traverseLeaf('a');
//			tree.reverseTraverseLeaf('q');
//			//assertEquals(correct, test);
//	}

}
