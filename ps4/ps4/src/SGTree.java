/**
 * Scapegoat Tree class
 *
 * This class contains an implementation of a Scapegoat tree.
 */

public class SGTree {
    /**
     * TreeNode class.
     *
     * This class holds the data for a node in a binary tree.
     *
     * Note: we have made things public here to facilitate problem set grading/testing.
     * In general, making everything public like this is a bad idea!
     *
     */
    public static class TreeNode {
        int key;
        public TreeNode left = null;
        public TreeNode right = null;
        public TreeNode parent = null;
        public int weight = 1;

        TreeNode(int k) {
            key = k;
        }
    }

    // Root of the binary tree
    public TreeNode root = null;

    /**
     * Counts the number of nodes in the subtree rooted at node
     *
     * @param node the root of the subtree
     * @return number of nodes
     */
    public int countNodes(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int leftCount = countNodes(node.left);
        int rightCount = countNodes(node.right);
        node.weight = leftCount + rightCount + 1;
        return node.weight;
    }

    /**
     * Builds an array of nodes in the subtree rooted at node
     *
     * @param node the root of the subtree
     * @return array of nodes
     */
    public TreeNode[] enumerateNodes(TreeNode node) {
        // TODO: Implement this
        if (node == null) {
            return new TreeNode[0];
        }
        TreeNode[] array = new TreeNode[countNodes(node)];
        //Inorder = Left, Entry, Right
        TreeNode[] left = enumerateNodes(node.left);
        TreeNode[] right = enumerateNodes(node.right);
        for (int i = 0; i < left.length; i ++) {
            array[i] = left[i];
        }
        array[left.length] = node;
        for (int i = left.length + 1; i < array.length; i++) {
            array[i] = right[i - left.length - 1];
        }
        return array;
    }

    /**
     * Builds a tree from the list of nodes
     * Returns the node that is the new root of the subtree
     *
     * @param nodeList ordered array of nodes
     * @return the new root node
     */
    public TreeNode buildTree(TreeNode[] nodeList) {
        // TODO: Implement this
        if (nodeList == null) {
            return null;
        }
        return helper(nodeList, 0, nodeList.length - 1);
    }
    public TreeNode helper(TreeNode[] nodeList, int left, int right) {
        if (left > right) {
            return null;
        }
        int mid = (left + right) / 2;
        TreeNode root = nodeList[mid];
        root.left = helper(nodeList, left, mid - 1);
        root.right = helper(nodeList, mid + 1,  right);
        if (root.left != null) {
            root.left.parent = root;
        }
        if (root.right != null) {
            root.right.parent = root;
        }
        root.weight = 1 + (root.left == null ? 0 : root.left.weight)
                + (root.right == null ? 0 : root.right.weight);
        return root;
    }

    /**
     * Determines if a node is balanced. If the node is balanced, this should return true. Otherwise, it should return
     * false. A node is unbalanced if either of its children has weight greater than 2/3 of its weight.
     *
     * @param node a node to check balance on
     * @return true if the node is balanced, false otherwise
     */
    public boolean checkBalance(TreeNode node) {
        // TODO: Implement this
        if (node == null) {
            return true;
        }
        int leftWeight = (node.left == null ? 0 : node.left.weight);
        int rightWeight = (node.right == null ? 0 : node.right.weight);
        double threshold = (double) (2 * node.weight) / 3;

        return !(leftWeight > threshold || rightWeight > threshold);
    }

    /**
    * Rebuilds the subtree rooted at node
    * 
    * @param node the root of the subtree to rebuild
    */
    public void rebuild(TreeNode node) {
        // Error checking: cannot rebuild null tree
        if (node == null) {
            return;
        }

        TreeNode p = node.parent;
        TreeNode[] nodeList = enumerateNodes(node);
        TreeNode newRoot = buildTree(nodeList);

        if (p == null) {
            root = newRoot;
        } else if (node == p.left) {
            p.left = newRoot;
        } else {
            p.right = newRoot;
        }
        newRoot.parent = p;
    }

    /**
    * Inserts a key into the tree
    *
    * @param key the key to insert
    */
    public void insert(int key) {
        if (root == null) {
            root = new TreeNode(key);
            return;
        }

        insert(key, root);
    }

    // Helper method to insert a key into the tree
    private void insert(int key, TreeNode node) {
        if (key <= node.key) {
            if (node.left == null) {
                node.left = new TreeNode(key);
                node.left.parent = node;
                update(node);
            } else {
                insert(key, node.left);
            }
        } else {
            if (node.right == null) {
                node.right = new TreeNode(key);
                node.right.parent = node;
                update(node);
            } else {
                insert(key, node.right);
            }
        }
    }
    //Helper method to update weights
    public void update(TreeNode node) {
        TreeNode highest = null;
        while (node != null) {
            node.weight += 1;
            if (!checkBalance(node)) {
                highest = node;
            }
            node = node.parent;
        }
        rebuild(highest);
    }

    // Simple main function for debugging purposes
    public static void main(String[] args) {
        SGTree tree = new SGTree();
        for (int i = 0; i < 100; i++) {
            tree.insert(i);
        }
        tree.rebuild(tree.root);
    }
}
