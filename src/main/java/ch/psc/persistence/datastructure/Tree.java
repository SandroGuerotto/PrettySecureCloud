package ch.psc.persistence.datastructure;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Tree<T> {

  private Node<T> root;
  
  public Tree() {
    root = new Node<T>(null, null);
  }
  
  public Node<T> getRoot() {
    return root;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(root);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Tree<?> other = (Tree<?>) obj;
    return Objects.equals(root, other.root);
  }

  public class Node<V> {
    
    private V value;
    private Node<V> parent;
    private List<Node<V>> children;
    
    public Node(Node<V> parent, V value) {
      this.parent = parent;
      this.value = value;
      children = new LinkedList<>();
    }
    
    public V getValue() {
      return value;
    }
    
    public void setValue(V value) {
      this.value = value;
    }
    
    public Node<V> getParent() {
      return parent;
    }
    
    public void setParent(Node<V> parent) {
      this.parent = parent;
    }
    
    public List<Node<V>> getChildren() {
      return children;
    }
    
    public boolean appendChild(Node<V> child) {
      return children.add(child);
    }
    
    public boolean removeChild(Node<V> child) {
      return children.remove(child);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getEnclosingInstance().hashCode();
      result = prime * result + Objects.hash(children, parent, value);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Node<?> other = (Node<?>) obj;
      if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
        return false;
      return Objects.equals(children, other.children) && Objects.equals(parent, other.parent)
          && Objects.equals(value, other.value);
    }

    private Tree<T> getEnclosingInstance() {
      return Tree.this;
    }
    
  }
}
