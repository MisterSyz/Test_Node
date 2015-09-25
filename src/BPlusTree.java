import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 * TODO: Rename to BPlusTree
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
		Node n = root;
		while(!n.isLeafNode){
			IndexNode<K, T> in = (IndexNode<K, T>) n;
			for(Node tempNode: in.children){
				
			}
//			for(K temp: in.keys){
//				if(key.compareTo(temp) >= 0){
//					in.
//					break;
//				}
//			}
		}
		return null;
	}

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {

	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf, any other relevant data
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf, ...) {

		return null;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index, ...) {

		return null;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {
		delete(null, root, key, -1);
	}
	
	private void delete(IndexNode <K, T> parent, Node<K, T> current, K key, int indexInParent){
		// LeafNode case
		if(!current.isLeafNode){
			IndexNode<K, T> indexNode = (IndexNode<K, T>) current;
			// Choose subtree
			for(int i = 0; i < indexNode.keys.size(); i++){
				if(i == 0 && indexNode.keys.get(0).compareTo(key) >= 0){
					delete(indexNode, indexNode.children.get(0), key, i);
				}else if(i == indexNode.keys.size() - 1 && indexNode.keys.get(indexNode.keys.size() - 1).compareTo(key) <= 0){
					delete(indexNode, indexNode.children.get(indexNode.children.size() - 1), key, i);
				}else if(indexNode.keys.get(i).compareTo(key) <= 0 && indexNode.keys.get(i + 1).compareTo(key) >= 0){
					delete(indexNode, indexNode.children.get(i + 1), key, i);
				}
			}
			// Handle leafNode underflow case
			if(indexNode.isUnderflowed()){
				int splitPos;
				if(indexInParent > 0){
					splitPos = handleIndexNodeUnderflow((IndexNode<K, T>)parent.children.get(indexInParent - 1), indexNode, parent);
				}else{
					splitPos = handleIndexNodeUnderflow(indexNode, (IndexNode<K, T>)parent.children.get(indexInParent + 1), parent);
				}
				
			}
		}else{  // IndexNode case
			LeafNode<K, T> leafNode = (LeafNode<K, T>) current;
			// Locate position to delete leafNode
			for(int i = 0; i < leafNode.keys.size(); i++){
				if(leafNode.keys.get(i).compareTo(key) == 0){
					leafNode.keys.remove(i);
					leafNode.values.remove(i);
					break;
				}
			}
			// Handle leafNode underflow case;
			if(leafNode.isUnderflowed()){
				int splitPos;
				if(indexInParent > 0){
					splitPos = handleLeafNodeUnderflow(leafNode.previousLeaf, leafNode, parent);
				}else{
					splitPos = handleLeafNodeUnderflow(leafNode,leafNode.nextLeaf, parent);
				}
			}
		}
	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		// Merge if left node has enough space to merge, redistribute otherwise
		if(left.keys.size() + right.keys.size() < 2 * D){
			left.keys.addAll(right.keys);
			left.values.addAll(right.values);
			parent.children.remove(right);
			return parent.children.indexOf(left);
		}else{
			if(left.isUnderflowed()){
				left.insertSorted(right.keys.remove(0), right.values.remove(0));
			}else{
				right.insertSorted(left.keys.remove(left.keys.size() - 1), left.values.remove(left.values.size() - 1));
				parent.keys.set(parent.children.indexOf(left), right.keys.get(0));
			}
		}
		return -1;
	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		if(leftIndex.keys.size() + rightIndex.keys.size() < 2 * D){
			leftIndex.children.add(rightIndex.children.remove(0));
			rightIndex.keys.remove(0);
		}
		
		return -1;
	}

}
