/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.jack.util;

import com.android.jack.IllegalOptionsException;
import com.android.jack.Options;
import com.android.jack.util.graph.Graph;
import com.android.jack.util.graph.GraphNode;
import com.android.jack.util.graph.GraphUtils;
import com.android.sched.util.RunnableHooks;
import com.android.sched.util.config.ConfigurationException;
import com.android.sched.util.config.ThreadConfig;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class GraphUtilsTests {

  @BeforeClass
  public static void setUpClass() throws ConfigurationException, IllegalOptionsException {
    Options options = new Options();
    RunnableHooks hooks = new RunnableHooks();
    options.checkValidity(hooks);
    options.getConfigBuilder(hooks).getCodecContext().setDebug();
    ThreadConfig.setConfig(options.getConfig());
  }

  private static final String ENTRY_NAME = "ENTRY";
  private static final String EXIT_NAME = "EXIT";

  @Test
  public void test001() {
    //   ENTRY
    //     |
    //     A <-+
    //     |\  |
    //     | \-+
    //    EXIT
    Graph<TestNode> graph = buildGraph(
        Edge.makeEdge(ENTRY_NAME, "A"),
        Edge.makeEdge("A", EXIT_NAME)
        );
    List<TestNode> orderNodes = GraphUtils.getNodesInPostOrder(graph);
    Assert.assertEquals(graph.getNodes().size(), orderNodes.size());
    Assert.assertEquals("A", getNodesAsString(orderNodes));
  }

  @Test
  public void test001Bis() {
    // Same than test001 with A being its own predecessor.
    Graph<TestNode> graph = buildGraph(
        Edge.makeEdge(ENTRY_NAME, "A"),
        Edge.makeEdge("A", "A"),
        Edge.makeEdge("A", EXIT_NAME)
        );
    List<TestNode> orderNodes = GraphUtils.getNodesInPostOrder(graph);
    Assert.assertEquals(graph.getNodes().size(), orderNodes.size());
    Assert.assertEquals("A", getNodesAsString(orderNodes));
  }

  @Test
  public void test002() {
    //   ENTRY
    //     |
    //     A
    //     |
    //     B
    //     |
    //    EXIT
    Graph<TestNode> graph = buildGraph(
        Edge.makeEdge(ENTRY_NAME, "A"),
        Edge.makeEdge("A", "B"),
        Edge.makeEdge("B", EXIT_NAME)
        );
    List<TestNode> orderNodes = GraphUtils.getNodesInPostOrder(graph);
    Assert.assertEquals(graph.getNodes().size(), orderNodes.size());
    Assert.assertEquals("BA", getNodesAsString(orderNodes));
  }

  @Test
  public void test003() {
    //   ENTRY
    //     |
    //     A
    //    / \
    //   B   C
    //    \ /
    //    EXIT
    Graph<TestNode> graph = buildGraph(
        Edge.makeEdge(ENTRY_NAME, "A"),
        Edge.makeEdge("A", "B"),
        Edge.makeEdge("A", "C"),
        Edge.makeEdge("B", EXIT_NAME),
        Edge.makeEdge("C", EXIT_NAME)
        );
    List<TestNode> orderNodes = GraphUtils.getNodesInPostOrder(graph);
    Assert.assertEquals(graph.getNodes().size(), orderNodes.size());
    Assert.assertEquals("CBA", getNodesAsString(orderNodes));
  }

  @Test
  public void test003Bis() {
    // Variant of test003 where B and C are at different positions.
    //
    //   ENTRY
    //     |
    //     A
    //    / \
    //   C   B
    //    \ /
    //    EXIT
    Graph<TestNode> graph = buildGraph(
        Edge.makeEdge(ENTRY_NAME, "A"),
        Edge.makeEdge("A", "C"),
        Edge.makeEdge("A", "B"),
        Edge.makeEdge("B", EXIT_NAME),
        Edge.makeEdge("C", EXIT_NAME)
        );
    List<TestNode> orderNodes = GraphUtils.getNodesInPostOrder(graph);
    Assert.assertEquals(graph.getNodes().size(), orderNodes.size());
    Assert.assertEquals("BCA", getNodesAsString(orderNodes));
  }

  @Test
  public void test004() {
    //   ENTRY
    //     |
    //     A
    //    / \
    //   B   C
    //    \ /
    //     D
    //     |
    //    EXIT
    Graph<TestNode> graph = buildGraph(
        Edge.makeEdge(ENTRY_NAME, "A"),
        Edge.makeEdge("A", "B"),
        Edge.makeEdge("A", "C"),
        Edge.makeEdge("B", "D"),
        Edge.makeEdge("C", "D"),
        Edge.makeEdge("D", EXIT_NAME)
        );
    List<TestNode> orderNodes = GraphUtils.getNodesInPostOrder(graph);
    Assert.assertEquals(graph.getNodes().size(), orderNodes.size());
    Assert.assertEquals("DCBA", getNodesAsString(orderNodes));
  }

  @Test
  public void test004Bis() {
    // Variant of test004 where B and C are at different positions.
    //
    //   ENTRY
    //     |
    //     A
    //    / \
    //   C   B
    //    \ /
    //     D
    //     |
    //    EXIT
    Graph<TestNode> graph = buildGraph(
        Edge.makeEdge(ENTRY_NAME, "A"),
        Edge.makeEdge("A", "C"),
        Edge.makeEdge("A", "B"),
        Edge.makeEdge("B", "D"),
        Edge.makeEdge("C", "D"),
        Edge.makeEdge("D", EXIT_NAME)
        );
    List<TestNode> orderNodes = GraphUtils.getNodesInPostOrder(graph);
    Assert.assertEquals(graph.getNodes().size(), orderNodes.size());
    Assert.assertEquals("DBCA", getNodesAsString(orderNodes));
  }

  @Test
  public void test005() {
    //   ENTRY
    //     |
    //     A <----
    //    / \
    //   B   C
    //   |   |
    //   D   |
    //   |\  |
    //   | \ |
    //   |  \|
    //   |   E
    //   |   |
    //   F   G ---
    //    \ /
    //     H
    //     |
    //    EXIT
    Graph<TestNode> graph = buildGraph(
        Edge.makeEdge(ENTRY_NAME, "A"),
        Edge.makeEdge("A", "B"),
        Edge.makeEdge("A", "C"),
        Edge.makeEdge("B", "D"),
        Edge.makeEdge("C", "E"),
        Edge.makeEdge("D", "E"),
        Edge.makeEdge("D", "F"),
        Edge.makeEdge("E", "G"),
        Edge.makeEdge("G", "A"),
        Edge.makeEdge("F", "H"),
        Edge.makeEdge("G", "H"),
        Edge.makeEdge("H", EXIT_NAME)
        );
    List<TestNode> orderNodes = GraphUtils.getNodesInPostOrder(graph);
    Assert.assertEquals(graph.getNodes().size(), orderNodes.size());
    Assert.assertEquals("HGECFDBA", getNodesAsString(orderNodes));
  }

  @Test
  public void test006() {
    // A CFG where the node B has D as successor twice (possible for empty if/else statement).
    //
    //   ENTRY
    //     |
    //     A
    //    / \
    //   B   C.
    //   /\  |
    //  D  D |
    //   \ |/
    //   EXIT
    Graph<TestNode> graph = buildGraph(
        Edge.makeEdge(ENTRY_NAME, "A"),
        Edge.makeEdge("A", "B"),
        Edge.makeEdge("A", "C"),
        Edge.makeEdge("B", "D"),
        Edge.makeEdge("B", "D"),
        Edge.makeEdge("C", EXIT_NAME),
        Edge.makeEdge("D", EXIT_NAME)
        );
    List<TestNode> orderNodes = GraphUtils.getNodesInPostOrder(graph);
    Assert.assertEquals(graph.getNodes().size(), orderNodes.size());
    Assert.assertEquals("CDBA", getNodesAsString(orderNodes));
  }

  private static String getNodesAsString(List<TestNode> nodes) {
    StringBuilder sb = new StringBuilder();
    for (TestNode n : nodes) {
      sb.append(n.toString());
    }
    return sb.toString();
  }

  private static Graph<TestNode> buildGraph(Edge... edges) {
    // Build nodes
    Map<String, TestNode> idToNode = new HashMap<String, GraphUtilsTests.TestNode>();
    for (Edge e : edges) {
      TestNode fromNode = idToNode.get(e.from);
      TestNode toNode = idToNode.get(e.to);
      if (fromNode == null) {
        fromNode = new TestNode(e.from);
        idToNode.put(e.from, fromNode);
      }
      if (toNode == null) {
        toNode = new TestNode(e.to);
        idToNode.put(e.to, toNode);
      }
      assert fromNode != null;
      assert toNode != null;

      fromNode.addSuccessor(fromNode.getSuccessors().size(), toNode);
    }

    // Remove entry and exit from the map since they're special nodes.
    TestNode entryNode = idToNode.remove(ENTRY_NAME);
    TestNode exitNode = idToNode.remove(EXIT_NAME);
    assert entryNode != null;
    assert exitNode != null;

    return new Graph<GraphUtilsTests.TestNode>(entryNode, exitNode,
        new ArrayList<>(idToNode.values()));
  }

  private static class TestNode extends GraphNode<TestNode> {
    private final String name;

    public TestNode(String name) {
      super(0);
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }

    @Override
    @Nonnull
    public List<TestNode> getSuccessors() {
      return successors;
    }

    @Override
    public void addSuccessor(int index, TestNode successor) {
      super.addSuccessor(index, successor);
    }
  }

  private static class Edge {
    String from;
    String to;

    private Edge(String from, String to) {
      this.from = from;
      this.to = to;
    }

    public static Edge makeEdge(String from, String to) {
      return new Edge(from, to);
    }
  }

}
