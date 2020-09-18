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
   * Specific options of the binding graph.
   */
  const options = {
    physics: false,
    layout: {
      randomSeed: 42,
    }
  };

  /**
   * Determines if graph needs to be redrawn or not.
   * @type {boolean}
   */
  let needsToRedraw = true;

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
      const simpleName = node.replace(/(?:\w+\.)+(\w+)/g, '$1');
      nodes.add({id: id, label: simpleName, title: node});
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
   * Retrieves all edges where the given node equals to the source or target nodes.
   *
   * @param {number} nodeId an identifier of the given node
   */
  function getAllEdges(nodeId) {
    return edges.get({
      filter: edge => edge.from === nodeId || edge.to === nodeId
    });
  }

  /**
   * Retrieves all edges where the given node equals to the source node.
   *
   * @param {number} nodeId an identifier of the given node
   */
  function getAllEdgesToDeps(nodeId) {
    return edges.get({
      filter: edge => edge.from === nodeId
    });
  }

  /**
   * Retrieves all edges where the given node equals to the target node.
   *
   * @param {number} nodeId an identifier of the given node
   */
  function getAllEdgesFromAncestors(nodeId) {
    return edges.get({
      filter: edge => edge.to === nodeId
    });
  }

  /**
   * Removes an edge from a subgraph, and if the destination node
   * becomes isolated, then removes it as well.
   *
   * @param {vis.Edge} edge an edge which will be removed
   * @param {boolean} shouldRemoveSource
   * @param {boolean} shouldRemoveTarget
   */
  function removeEdge(edge, shouldRemoveSource, shouldRemoveTarget) {
    edges.remove(edge);

    if (shouldRemoveSource) {
      tryToRemoveNode(edge.from);
    }
    if (shouldRemoveTarget) {
      tryToRemoveNode(edge.to);
    }
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

    // Checks if an edge already exists.
    if (hasEdge(sourceId, targetId)) {
      return;
    }

    edges.add({from: sourceId, to: targetId});
  }

  /**
   * Checks if an edge exists in the graph or not.
   *
   * @param {number} sourceId
   * @param {number} targetId
   * @return {boolean}
   */
  function hasEdge(sourceId, targetId) {
    return edges.get({
      filter: edge => edge.from === sourceId && edge.to === targetId
    }).length !== 0;
  }

  /**
   * If the dependencies are not currently shown, we prefer to draw them all.
   * Otherwise, we hide all children, even if they are not all were presented.
   *
   * @param {number} nodeId
   */
  function showOrHideDeps(nodeId) {
    const nodeTitle = nodes.get(nodeId).title;

    if (getAllEdgesToDeps(nodeId).length === 0) {
      queryExecutor.processQuery(['deps', nodeTitle], false);
    } else {
      bindingGraph.deleteDeps(nodeTitle);
    }
  }

  /**
   * If the ancestors are not currently shown, we prefer to draw them all.
   * Otherwise, we hide all parents, even if they are not all were presented.
   *
   * @param {number} nodeId
   */
  function showOrHideAncestors(nodeId) {
    const nodeTitle = nodes.get(nodeId).title;

    if (getAllEdgesFromAncestors(nodeId).length === 0) {
      queryExecutor.processQuery(['rdeps', nodeTitle], false);
    } else {
      bindingGraph.deleteAncestors(nodeTitle);
    }
  }

  /**
   * Supports right-click and left-click events in the network object.
   * @param {vis.Network} network
   */
  function supportEventsRecognition(network) {
    // An event for managing children nodes.
    network.on("click", function (params) {
      // Checks if any node was selected.
      if (params.nodes.length === 0) {
        return;
      }

      const nodeId = params.nodes[0];
      showOrHideDeps(nodeId);
    });

    // An event for managing parent nodes.
    network.on("oncontext", function (params) {
      // Checks if any node was selected.
      if (params.nodes.length === 0) {
        return;
      }

      const nodeId = params.nodes[0];
      showOrHideAncestors(nodeId);
    });
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
     * Removes all edges with the given source node from the subgraph.
     *
     * @param {string} node a source node which dependencies will be removed
     */
    deleteDeps: function (node) {
      // Retrieves all edges having a start node with value `node`.
      const edgesToBeRemoved = getAllEdgesToDeps(nodesToNumbers.get(node));

      for (const edge of edgesToBeRemoved) {
        removeEdge(edge, /*shouldRemoveSource =*/ false, /*shouldRemoveTarget =*/ true);
      }
    },

    /**
     * Removes all edges with the given target node from the subgraph.
     *
     * @param {string} node a target node which parents will be removed
     */
    deleteAncestors: function (node) {
      const edgesToBeRemoved = getAllEdgesFromAncestors(nodesToNumbers.get(node));

      for (const edge of edgesToBeRemoved) {
        removeEdge(edge, /*shouldRemoveSource =*/ true, /*shouldRemoveTarget =*/ false);
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
     * Toggles physics switcher.
     *
     * <p>Disables physics if enabled and vice versa.
     *
     * @return {boolean} a flag that indicates if physics is enabled or not
     */
    togglePhysics: function() {
      options.physics = !options.physics;
      needsToRedraw = true;
      return options.physics;
    },

    /**
     * Draws the entire graph and sets events listeners.
     */
    draw: function() {
      if (!needsToRedraw) {
        return;
      }

      const container = document.getElementById('binding-graph');
      const data = {
        nodes: nodes,
        edges: edges
      };

      network = new vis.Network(container, data, options);
      supportEventsRecognition(network);

      // With physics enabled, we don't need to draw the graph again, because the nodes are located by gravity.
      // Otherwise, all the nodes will appear in the initial position and overlap, so we need to redraw the graph.
      if (options.physics) {
        needsToRedraw = false;
      }
    },
  };
})();
