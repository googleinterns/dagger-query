// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const bindingGraph = (function() {
  /**
   * A dictionary that maps a node's name to its identifier.
   * @type {Map<string, number>}
   */
  const nodesToNumbers = new Map();

  /**
   * A data structure <a href="https://ww3.arb.ca.gov/ei/tools/lib/vis/docs/dataset.html">DataSet</a>
   * that contains all the nodes from the shown subgraph.
   */
  const nodes = new vis.DataSet();

  /**
   * A data structure <a href="https://ww3.arb.ca.gov/ei/tools/lib/vis/docs/dataset.html">DataSet</a>
   * that contains all the edges from the shown subgraph.
   */
  const edges = new vis.DataSet();

  /**
   * Associates given node with an identifier, if the node was not represented in the graph.
   *
   * @param {string} node a node to be added if it doesn't exist
   * @return {number} an identifier of a given node
   */
  function addNode(node) {
    if (!nodesToNumbers.has(node)) {
      nodesToNumbers.set(node, nodesToNumbers.size);
    }

    const id = nodesToNumbers.get(node);
    if (nodes.get(id) === null) {
      // Since angle brackets have a special meaning in html, we replace them with square brackets.
      const fullName = node.replace(/</g, "[").replace(/>/g, "]");
      const simpleName = fullName.replace(/(?:\w+\.)+(\w+)/g, '$1');
      nodes.add({id: id, label: simpleName, title: fullName});
    }
    return id;
  }

  /**
   * Tries to remove the given node from a subgraph.
   *
   * <p>The operation is considered successful only if the given node does not connect
   * to any other node in the graph.
   *
   * @param {number} nodeId an identifier of a node to be removed
   * @return {boolean} a boolean flag which indicates if a node was removed or not
   */
  function tryToRemoveNode(nodeId) {
    if (getAllEdges(nodeId).length === 0) {
      nodes.remove(nodeId);
      return true;
    }

    return false;
  }

  /**
   * Retrieves all edges where the start or destination nodes are equal to the given one.
   *
   * @param {number} nodeId an identifier of the given node
   */
  function getAllEdges(nodeId) {
    return edges.get({
      filter: function (edge) {
        return (edge.from === nodeId || edge.to === nodeId);
      }
    });
  }

  /**
   * Retrieves all edges where the start node is the specified one.
   *
   * @param {number} nodeId an identifier of the given node
   */
  function getAllDeps(nodeId) {
    return edges.get({
      filter: function (edge) {
        return (edge.from === nodeId);
      }
    });
  }

  /**
   * Removes an edge from a subgraph, and if the destination node
   * becomes isolated, then removes it as well.
   *
   * @param edge an edge which will be removed
   */
  function removeEdge(edge) {
    edges.remove(edge);

    tryToRemoveNode(edge.to);
  }

  /**
   * Adds an edge from the <b>source</b> node to the <b>target</b>.
   *
   * @param {string} source the first node of the given edge
   * @param {string} target the second node of the given edge
   */
  function addEdge(source, target, style) {
    const sourceId = addNode(source);
    const targetId = addNode(target);

    edges.add({from: sourceId, to: targetId});
  }

  return {
    /**
     * Takes a string representing a path where two adjacent nodes are connected by an edge,
     * and adds all these edges to the subgraph.
     *
     * <p>Given path is separated with ' -> ' arrow.
     *
     * @param {string} path a valid string representing a path with at least two nodes
     */
    addPath: function (path) {
      const nodesInPath = path.split( ' -> ');
      for (let index = 0; index + 1 < nodesInPath.length; ++index) {
        addEdge(nodesInPath[index], nodesInPath[index + 1]);
      }
    },

    /**
     * Adds all source node's dependencies to the subgraph.
     * @param {string} source
     * @param {string[]} deps
     */
    addDeps: function (source, deps) {
      for (const childNode of deps) {
        addEdge(source, childNode);
      }
    },

    /**
     * Clears all data from the graph.
     */
    clear: function () {
      nodesToNumbers.clear();
      nodes.clear();
      edges.clear();
    },

    /**
     * Returns a <a href="https://ww3.arb.ca.gov/ei/tools/lib/vis/docs/dataset.html">DataSet</a>
     * with all edges from the subgraph.
     *
     * @return {vis.DataSet}
     */
    getEdges: function () {
      return edges;
    },

    /**
     * Returns a <a href="https://ww3.arb.ca.gov/ei/tools/lib/vis/docs/dataset.html">DataSet</a>
     * with all nodes from the subgraph.
     *
     * @return {vis.DataSet}
     */
    getNodes: function () {
      return nodes;
    },

    /**
     * Draws the entire graph and sets events listeners.
     */
    draw: function() {
      const container = document.getElementById('binding-graph');
      const data = {
        nodes: nodes,
        edges: edges
      };

      var options = {
        physics: false
      };
      const network = new vis.Network(container, data, options);
    }
  };
})();
