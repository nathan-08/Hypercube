// Binary Tree

// Construct tree given dimension
//    leaves represent vertices of hypercube
// Generate edges
interface Lambda {
  void call(int[]p1, int[]p2);
}

public class Tree {
  private Node root;
  private final int DIMENSION = 4;

  public Tree() {
    root = new Node(new int[DIMENSION]);
    for (int d = 0; d < DIMENSION; ++d) {
      // duplicate tree, setting left half point[d] to +1,
      //                         right half point[d] to -1
      Node new_root = new Node(root.copy(), root);
      root = new_root;
      update_dimension(root.left, d, -1);
      update_dimension(root.right, d, 1);
    }
  }

  public void print(Node node) {
    if (node == null) node = root;
    if (node.is_leaf()) {
      for (int i: node.point) {
        System.out.printf("%d, ", i);
      }
      System.out.println();
    }
    else {
      print(node.left);
      print(node.right);
    }
  }

  public void for_each_edge(Lambda lambda) {
    traverse_edges(root, lambda);
  }

  private void traverse_edges(Node node, Lambda lambda) {
    if (node.is_leaf()) return;

    if (node.left != null)
      traverse_edges(node.left, lambda);
    if (node.right != null)
      traverse_edges(node.right, lambda);

    traverse_two_lambda(node.left, node.right, lambda);
  }

  private void traverse_two_lambda
  (Node left, Node right, Lambda lambda) {
    if (left.is_leaf()) {
      assert right.is_leaf() : "tree structure err";
      lambda.call(left.point, right.point);
    }
    else {
      traverse_two_lambda(left.left, right.left, lambda);
      traverse_two_lambda(left.right, right.right, lambda);
    }
  }

  public void print_edges(Node node) {
    if (node == null) node = root;
    if (node.is_leaf()) return;

    if (node.left != null) print_edges(node.left);
    if (node.right != null) print_edges(node.right);

    traverse_two(node.left, node.right);
  }

  private void traverse_two(Node left, Node right) {
    if (left.is_leaf()) {
      assert right.is_leaf() : "tree structure err";
      print_edge(left.point, right.point);
    }
    else {
      traverse_two(left.left, right.left);
      traverse_two(left.right, right.right);
    }
  }

  private void print_edge(int[]e1, int[]e2) {
    System.out.print("(");
    for (int i: e1)
      System.out.printf("%d,", i);
    System.out.print(") -> (");
    for (int i: e2)
      System.out.printf("%d,", i);
    System.out.print(")\n");
  }

  private void update_dimension(Node node, int dim, int val) {
    if (node.is_leaf()) node.point[dim] = val;
    else {
      update_dimension(node.left, dim, val);
      update_dimension(node.right, dim, val);
    }
  }

  class Node {
    public int[] point;
    public Node left;
    public Node right;

    public Node() { point = new int[DIMENSION]; }
    public Node(int p[]) { point = p.clone(); }
    public Node(Node l, Node r) { left = l; right = r; }

    public boolean is_leaf() { return left == null && right == null; }
    public Node copy() {
      if (is_leaf())
        return new Node(point);
      else
        return new Node(left.copy(), right.copy());
    }
  }
}
