package org.spideruci.analysis.statik.flow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.spideruci.analysis.util.caryatid.Helper;

public class Node<T> {
  /**
   * Nodes being pointed to by this node.
   */
  private HashSet<Node<T>> successors;
  /**
   * Nodes that are pointing to this node.
   */
  private HashSet<Node<T>> predecessors;
  /**
   * This is a unique Identifier for the Node
   */
  private String label;
  private final T body;
  /**
   * This is the Graph that contains this node.
   * A given node may be present in only one node.
   */
  private Graph<T> containerGraph;

  public static <N> Node<N> create(String label, Graph<N> containerGraph) {
    Node<N> node = new Node<N>(label);
    node.setContainerGraph(containerGraph);
    return node;
  }

  private Node(String label) {
    this.successors = new LinkedHashSet<Node<T>>();
    this.predecessors = new LinkedHashSet<Node<T>>();
    this.label = new String(label);
    this.body = null;
  }

  public Node(String label, T _body) {
    this.successors = new HashSet<Node<T>>();
    this.predecessors = new HashSet<Node<T>>();
    this.label = new String(label);
    this.body = _body;
  }
  
  public Accumulator<T> and(Node<T> node) {
    return Accumulator.<T>create().and(this).and(node);
  }
  
  public Node<T> clearSuccessors() {
    if(this.successors == null) return this;
    for(Node<T> succ : this.successors) {
      succ.predecessors.remove(succ);
    }
    this.successors.clear();
    return this;
  }
  
  public Node<T> clearPredecessors() {
    this.predecessors.clear();
    return this;
  }
  
  public Node<T> pointsTo(Node<T> node) {
    if(node == null) return this;
    this.successors.add(node);
    node.predecessors.add(this);
    return this;
  }
  
  public Node<T> pointsTo(Accumulator<T> accumulator) {
    ArrayList<Node<T>> nodes = accumulator.period();
    if(nodes == null || nodes.size() == 0) return this;
    for(Node<T> node : nodes) {
      pointsTo(node);
    }
    return this;
  }

  public ArrayList<Node<T>> pointsTo() {
    if(successors == null) return Helper.Collections2.emptyArrayList();
    ArrayList<Node<T>> succs = new ArrayList<Node<T>>(successors);
    return succs;
  }

  public int getSuccessorsSize() {
    if(this.label.equals(Graph.END)) { 
      return 0;
    }
    if(this.successors == null) {
      return 0;
    }
    return this.successors.size();
  }

  public ArrayList<Node<T>> getPredecessors() {
    if(predecessors == null) return Helper.Collections2.emptyArrayList();
    ArrayList<Node<T>> preds = new ArrayList<Node<T>>(predecessors);
    return preds;
  }

  public int getPredecessorsSize() {
    if(this.label.equals(Graph.START)) {
      return 0;
    }
    //FIXME Switch START and END upon reversal.
    if(this.predecessors == null) {
      return 0;
    }
    return this.predecessors.size();
  }
  
  public boolean isPointedByNone() {
    return predecessors.isEmpty();
  }
  
  public boolean pointsToNone() {
    return successors.isEmpty();
  }

  public ArrayList<String> getPredecessorLabels() {
    ArrayList<String> predLabels = new ArrayList<String>();
    for(Node<T> pred : this.predecessors) {
      predLabels.add(pred.getLabel());
    }
    return predLabels;
  }
  
  public ArrayList<String> getSuccessorsLabels() {
    ArrayList<String> succLabels = new ArrayList<String>();
    for(Node<T> pred : this.successors) {
      succLabels.add(pred.getLabel());
    }
    return succLabels;
  }

  public boolean containsSuccessor(String label) {
    if(successors == null) return false;
    for(Node<T> succ : this.successors) {
      if(succ.equals(label)) {
        return true;
      }
    }
    return false;
  }

  public boolean containsPredecessor(String label) {
    if(predecessors == null) return false;
    for(Node<T> pred : this.predecessors) {
      if(pred.equals(label)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return this.getLabel();
  }

  @Override
  public boolean equals(Object node) {
    if(!(node instanceof Node<?>)) {
      return false;
    }
    if(((Node<?>)node).getLabel().equals(label)) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int code = 37;
    code += this.getLabel().hashCode() * 17;
    code += this.containerGraph.uid().hashCode() * 17;
    return code;
  }

  public String getLabel() {
    if(this.label == null && this.body == null) {
      return "null";
    }
    else if(this.label == null && this.body != null) {
      return this.body.toString();
    } else {
      return this.label;
    }
  }
  
  public Node<T> cloneIn(Graph<T> graph) {
    return graph.node(this.getLabel());
  }

  public Graph<T> getContainerGraph() {
    return this.containerGraph;
  }
  
  public void setContainerGraph(Graph<T> graph) {
     containerGraph = graph;
  }

  public T getBody() {
    return this.body;
  }
}