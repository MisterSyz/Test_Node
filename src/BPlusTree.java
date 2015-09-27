import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.List;


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
        return searchNode(root, key);
    }
    public T searchNode(Node<K,T> temp_root, K key) {
        if (temp_root.isLeafNode) {
            int i = 0;
            LeafNode<K, T> ln = (LeafNode<K, T>) temp_root;
            for (i = 0; i < ln.keys.size(); i++ ) {
                if (key.compareTo(ln.keys.get(i)) == 0) {
                    return ln.values.get(i);
                }
            }
            return null;
        } else {
            int i = 0;
            IndexNode<K, T> in = (IndexNode<K, T>)temp_root;
            for (i = 0; i < in.keys.size(); i++) {
                if (key.compareTo(in.keys.get(i)) < 0) {
                    break;
                }
            }
            return searchNode(in.children.get(i),key);
        }
    }


	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
    public void insert(K key, T value) {
        if (root == null) {
            LeafNode<K,T> ln = new LeafNode<K,T>(key, value);
            root = ln;
            return;
        }
        Node<K,T> temp_node = root;
        ArrayList<IndexNode<K,T>> parents_node = new ArrayList<IndexNode<K,T>>();
        while(!temp_node.isLeafNode) {
            parents_node.add((IndexNode<K,T>)temp_node);
            IndexNode<K,T> in = (IndexNode<K,T>) temp_node;
            int i;
            for (i = 0; i < in.keys.size(); i++) {
                if (key.compareTo(in.keys.get(i)) < 0) {
                    break;
                }
            }
            temp_node = in.children.get(i);
        }
        assert (temp_node.isLeafNode);
        if(temp_node.isLeafNode) {
            LeafNode<K, T> ln = (LeafNode<K,T>) temp_node;
            int i = 0;
            for (i = 0; i < ln.keys.size(); i++) {
                if (key.compareTo(ln.keys.get(i)) < 0) {
                    break;
                }
            }
            ln.keys.add(i, key);
            ln.values.add(i, value);
        }
        if (temp_node.isOverflowed()) {
            int j = parents_node.size() - 1;
            Entry<K, Node<K,T>> temp_entry = splitLeafNode((LeafNode<K,T>)temp_node);
            if (j > -1) {
                int i;
                for (i = 0; i < parents_node.get(j).keys.size(); i++) {
                    if (temp_entry.getKey().compareTo(parents_node.get(j).keys.get(i)) < 0) {
                        break;
                    }
                }
                parents_node.get(j).insertSorted(temp_entry, i);
                while (parents_node.get(j).isOverflowed()) {
                    temp_entry = splitIndexNode((parents_node.get(j)));
                    j = j - 1;
                    if (j == -1) {
                        break;
                    }
                    for (i = 0; i < parents_node.get(j).keys.size(); i++) {
                        if (temp_entry.getKey().compareTo(parents_node.get(j).keys.get(i)) < 0) {
                            break;
                        }
                    }
                    parents_node.get(j).insertSorted(temp_entry, i);
                }
                if (j == -1) {
                    root = new IndexNode<K,T>(temp_entry.getKey(), parents_node.get(0), temp_entry.getValue());
                }
            } else {
                if (j == -1) {
                    root = new IndexNode<K,T>(temp_entry.getKey(), ((LeafNode<K,T>)temp_entry.getValue()).previousLeaf, temp_entry.getValue());
                }
            }
        }
        
    }

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf, any other relevant data
	 * @return the key/node pair as an Entry
	 */
    public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
        assert(leaf.keys.size() == 2 * D + 1);
        List<T> temp_values = new ArrayList<T>(leaf.values);
        List<K> temp_keys = new ArrayList<K>(leaf.keys);
        for (int i = 0; i < BPlusTree.D; i++) {
            temp_values.remove(0);
            temp_keys.remove(0);
        }
        K splitkey = temp_keys.get(0);
        for (int i = 0; i <= BPlusTree.D; i++) {
            leaf.values.remove(BPlusTree.D);
            leaf.keys.remove(BPlusTree.D);
        }
        LeafNode<K,T> new_ln = new LeafNode<K,T>((List<K>)temp_keys, (List<T>)temp_values);
        new_ln.isLeafNode = true;
        new_ln.nextLeaf = leaf.nextLeaf;
        leaf.nextLeaf = new_ln;
        new_ln.previousLeaf = leaf;
        Entry<K, Node<K,T>> new_entry = new AbstractMap.SimpleEntry<K, Node<K,T>>((K)splitkey, new_ln);
        return new_entry;
    }


	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
    public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {
        assert (index.keys.size() == 2 * D + 1);
        List<K> temp_keys = new ArrayList<K>(index.keys);
        List<Node<K,T>> temp_children = new ArrayList<Node<K,T>>(index.children);
        for (int i = 0; i < BPlusTree.D; i++) {
            temp_keys.remove(0);
            temp_children.remove(0);
        }
        K splitkey = (K)temp_keys.get(0);
        temp_keys.remove(0);
        temp_children.remove(0);
        int size = index.keys.size();
        for (int i = D; i < size; i++) {
            index.keys.remove(D);
            index.children.remove(D + 1);
        }
        assert (temp_keys.size() + 1 == temp_children.size());
        assert (index.keys.size() + 1 == index.children.size());
        IndexNode<K, T> in = new IndexNode<K, T> (temp_keys, temp_children);
        in.isLeafNode = false;
        Entry<K, Node<K,T>> temp_entry = new AbstractMap.SimpleEntry<K, Node<K, T>>(splitkey, in);
        return temp_entry;
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
		
		int indexInParent = -1;
		
		if(parent != null){
			indexInParent = parent.children.indexOf(leftIndex);
		}
		
		// Merge operation
		if(leftIndex.keys.size() + rightIndex.keys.size() < 2 * D){
			leftIndex.keys.add(parent.keys.get(indexInParent));
			leftIndex.keys.addAll(rightIndex.keys);
			leftIndex.children.addAll(rightIndex.children);
			parent.children.remove(rightIndex);
			return indexInParent;
		}else{
			// Redistribute
			if(leftIndex.isUnderflowed()){
				leftIndex.keys.add(parent.keys.get(indexInParent));
				parent.keys.set(indexInParent, rightIndex.keys.remove(0));
				leftIndex.children.add(rightIndex.children.remove(0));
			}else{
				rightIndex.keys.add(0, parent.keys.get(indexInParent));
				Node<K, T> temp = leftIndex.children.remove(leftIndex.children.size() - 1);
				rightIndex.children.add(temp);
				parent.keys.set(parent.keys.size() - 1, leftIndex.keys.remove(leftIndex.keys.size() - 1));
			}
		}
		 
		return -1;
	}

}
