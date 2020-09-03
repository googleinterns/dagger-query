/** @fileoverview @typeSummary */
goog.module("com.google.daggerquery.executor.models.Graph$impl");
const $Util = goog.require("nativebootstrap.Util$impl");
let ImmutableSet = goog.forwardDeclare("com.google.common.collect.ImmutableSet$impl");
/** @interface */ class Graph {
  /**
   * @abstract
   * @return {ImmutableSet<?string>}
   */
  m_getDependencies__java_lang_String(/** ?string */ node) {
  }
  /**
   * @abstract
   * @return {boolean}
   */
  m_containsNode__java_lang_String(/** ?string */ node) {
  }
  /**
   * @abstract
   * @return {ImmutableSet<?string>}
   */
  m_getAllNodes__() {
  }
  static $clinit() {
  }
  static $markImplementor(/** Function */ ctor) {
  }
  /**
   * @return {boolean}
   */
  static $isInstance(/** ? */ instance) {
  }
  static $loadModules() {
  }
}
exports = Graph;
/** @fileoverview @typeSummary */
goog.module("com.google.daggerquery.executor.models.Graph");
goog.require("com.google.common.collect.ImmutableSet");
goog.require("nativebootstrap.Util");
const Graph = goog.require("com.google.daggerquery.executor.models.Graph$impl");
exports = Graph;
/** @fileoverview @typeSummary */
goog.module("com.google.daggerquery.executor.models.MisspelledNodeNameException$impl");
const RuntimeException = goog.require("java.lang.RuntimeException$impl");
const $Util = goog.require("nativebootstrap.Util$impl");
let j_l_String = goog.forwardDeclare("java.lang.String$impl");
let List = goog.forwardDeclare("java.util.List$impl");
class MisspelledNodeNameException extends RuntimeException {
  constructor(/** ?string */ nodeNameWithTypo, /** List<?string> */ correctNodesName) {
  }
  $ctor__com_google_daggerquery_executor_models_MisspelledNodeNameException__java_lang_String__java_util_List(/** ?string */ nodeNameWithTypo, /** List<?string> */ correctNodesName) {
  }
  static $clinit() {
  }
  /**
   * @return {boolean}
   */
  static $isInstance(/** ? */ instance) {
  }
  static $loadModules() {
  }
}
exports = MisspelledNodeNameException;
/** @fileoverview @typeSummary */
goog.module("com.google.daggerquery.executor.models.MisspelledNodeNameException");
goog.require("java.lang.RuntimeException");
goog.require("java.lang.String");
goog.require("java.util.List");
goog.require("nativebootstrap.Util");
const MisspelledNodeNameException = goog.require("com.google.daggerquery.executor.models.MisspelledNodeNameException$impl");
exports = MisspelledNodeNameException;
/** @fileoverview @typeSummary */
goog.module("com.google.daggerquery.executor.models.Query$impl");
const j_l_Object = goog.require("java.lang.Object$impl");
const $Util = goog.require("nativebootstrap.Util$impl");
let ImmutableList = goog.forwardDeclare("com.google.common.collect.ImmutableList$impl");
let Builder = goog.forwardDeclare("com.google.common.collect.ImmutableList.Builder$impl");
let ImmutableMap = goog.forwardDeclare("com.google.common.collect.ImmutableMap$impl");
let ImmutableMap_Builder = goog.forwardDeclare("com.google.common.collect.ImmutableMap.Builder$impl");
let Graph = goog.forwardDeclare("com.google.daggerquery.executor.models.Graph$impl");
let MisspelledNodeNameException = goog.forwardDeclare("com.google.daggerquery.executor.models.MisspelledNodeNameException$impl");
let Path = goog.forwardDeclare("com.google.daggerquery.executor.models.Query.Path$impl");
let IllegalArgumentException = goog.forwardDeclare("java.lang.IllegalArgumentException$impl");
let Integer = goog.forwardDeclare("java.lang.Integer$impl");
let NullPointerException = goog.forwardDeclare("java.lang.NullPointerException$impl");
let j_l_String = goog.forwardDeclare("java.lang.String$impl");
let UnsupportedOperationException = goog.forwardDeclare("java.lang.UnsupportedOperationException$impl");
let ArrayList = goog.forwardDeclare("java.util.ArrayList$impl");
let Arrays = goog.forwardDeclare("java.util.Arrays$impl");
let Collections = goog.forwardDeclare("java.util.Collections$impl");
let HashSet = goog.forwardDeclare("java.util.HashSet$impl");
let List = goog.forwardDeclare("java.util.List$impl");
let Set = goog.forwardDeclare("java.util.Set$impl");
let j_u_function_Function = goog.forwardDeclare("java.util.function.Function$impl");
let Collector = goog.forwardDeclare("java.util.stream.Collector$impl");
let Collectors = goog.forwardDeclare("java.util.stream.Collectors$impl");
let Stream = goog.forwardDeclare("java.util.stream.Stream$impl");
let InternalPreconditions = goog.forwardDeclare("javaemul.internal.InternalPreconditions$impl");
let $Equality = goog.forwardDeclare("nativebootstrap.Equality$impl");
let $Arrays = goog.forwardDeclare("vmbootstrap.Arrays$impl");
let $Casts = goog.forwardDeclare("vmbootstrap.Casts$impl");
let $Exceptions = goog.forwardDeclare("vmbootstrap.Exceptions$impl");
let $Objects = goog.forwardDeclare("vmbootstrap.Objects$impl");
let $int = goog.forwardDeclare("vmbootstrap.primitives.$int$impl");
class Query extends j_l_Object {
  constructor(/** ?string */ typeName, .../** ...?string */ parameters) {
  }
  $ctor__com_google_daggerquery_executor_models_Query__java_lang_String__arrayOf_java_lang_String(/** ?string */ typeName, /** Array<?string> */ parameters) {
  }
  /**
   * @return {ImmutableList<?string>}
   */
  execute(/** Graph */ bindingGraph) {
  }
  m_findAllPaths__java_lang_String__java_lang_String__com_google_daggerquery_executor_models_Query_Path__com_google_daggerquery_executor_models_Graph__java_util_Set__com_google_common_collect_ImmutableList_Builder_$p_com_google_daggerquery_executor_models_Query(/** ?string */ source, /** ?string */ target, /** Path */ path, /** Graph */ bindingGraph, /** Set<?string> */ visitedNodes, /** Builder<Path> */ allPaths) {
  }
  /**
   * @return {boolean}
   */
  m_findSomePath__java_lang_String__java_lang_String__com_google_daggerquery_executor_models_Query_Path__com_google_daggerquery_executor_models_Graph__java_util_Set_$p_com_google_daggerquery_executor_models_Query(/** ?string */ source, /** ?string */ target, /** Path */ path, /** Graph */ bindingGraph, /** Set<?string> */ visitedNodes) {
  }
  m_checkNodeForCorrectness__java_lang_String__com_google_daggerquery_executor_models_Graph_$p_com_google_daggerquery_executor_models_Query(/** ?string */ node, /** Graph */ bindingGraph) {
  }
  /**
   * @return {List<?string>}
   */
  m_findNodesWithClosestName__java_lang_String__java_util_Set_$p_com_google_daggerquery_executor_models_Query(/** ?string */ originalNode, /** Set<?string> */ allNodes) {
  }
  /**
   * @return {number}
   */
  m_calculateDistance__java_lang_String__java_lang_String_$p_com_google_daggerquery_executor_models_Query(/** ?string */ firstString, /** ?string */ secondString) {
  }
  static $clinit() {
  }
  /**
   * @return {boolean}
   */
  static $isInstance(/** ? */ instance) {
  }
  static $loadModules() {
  }
}
/** @type {Array<?string>} */ Query.prototype.f_parameters__com_google_daggerquery_executor_models_Query_;
/** @type {?string} */ Query.prototype.f_name__com_google_daggerquery_executor_models_Query_;
/** @const @type {?string} */ Query.f_DEPS_QUERY_NAME__com_google_daggerquery_executor_models_Query_;
/** @const @type {?string} */ Query.f_ALLPATHS_QUERY_NAME__com_google_daggerquery_executor_models_Query_;
/** @const @type {?string} */ Query.f_SOMEPATH_QUERY_NAME__com_google_daggerquery_executor_models_Query_;
/** @const @type {number} */ Query.f_MAX_NUMBER_OF_MISPLACED_LETTERS__com_google_daggerquery_executor_models_Query_;
/** @type {ImmutableMap<?string,Integer>} */ Query.f_supportedQueries__com_google_daggerquery_executor_models_Query_;
exports = Query;
/** @fileoverview @typeSummary */
goog.module("com.google.daggerquery.executor.models.Query");
goog.require("com.google.common.collect.ImmutableList");
goog.require("com.google.common.collect.ImmutableList.Builder");
goog.require("com.google.common.collect.ImmutableMap");
goog.require("com.google.common.collect.ImmutableMap.Builder");
goog.require("com.google.daggerquery.executor.models.Graph");
goog.require("com.google.daggerquery.executor.models.MisspelledNodeNameException");
goog.require("com.google.daggerquery.executor.models.Query.Path");
goog.require("java.lang.IllegalArgumentException");
goog.require("java.lang.Integer");
goog.require("java.lang.NullPointerException");
goog.require("java.lang.Object");
goog.require("java.lang.String");
goog.require("java.lang.UnsupportedOperationException");
goog.require("java.util.ArrayList");
goog.require("java.util.Arrays");
goog.require("java.util.Collections");
goog.require("java.util.HashSet");
goog.require("java.util.List");
goog.require("java.util.Set");
goog.require("java.util.function.Function");
goog.require("java.util.stream.Collector");
goog.require("java.util.stream.Collectors");
goog.require("java.util.stream.Stream");
goog.require("javaemul.internal.InternalPreconditions");
goog.require("nativebootstrap.Equality");
goog.require("nativebootstrap.Util");
goog.require("vmbootstrap.Arrays");
goog.require("vmbootstrap.Casts");
goog.require("vmbootstrap.Exceptions");
goog.require("vmbootstrap.Objects");
goog.require("vmbootstrap.primitives.$int");
const Query = goog.require("com.google.daggerquery.executor.models.Query$impl");
exports = Query;
/** @fileoverview @typeSummary */
goog.module("com.google.daggerquery.executor.models.Query.Path$impl");
const j_l_Object = goog.require("java.lang.Object$impl");
const $Util = goog.require("nativebootstrap.Util$impl");
let j_l_String = goog.forwardDeclare("java.lang.String$impl");
let ArrayDeque = goog.forwardDeclare("java.util.ArrayDeque$impl");
let Deque = goog.forwardDeclare("java.util.Deque$impl");
let List = goog.forwardDeclare("java.util.List$impl");
let j_u_function_Function = goog.forwardDeclare("java.util.function.Function$impl");
let Collector = goog.forwardDeclare("java.util.stream.Collector$impl");
let Collectors = goog.forwardDeclare("java.util.stream.Collectors$impl");
let Stream = goog.forwardDeclare("java.util.stream.Stream$impl");
let $Casts = goog.forwardDeclare("vmbootstrap.Casts$impl");
let $Objects = goog.forwardDeclare("vmbootstrap.Objects$impl");
/**
 * @template NodeT
 */
class Path extends j_l_Object {
  /** @protected */ constructor() {
  }
  /**
   * @return {!Path<NodeT>}
   * @template NodeT
   */
  static $create__() {
  }
  $ctor__com_google_daggerquery_executor_models_Query_Path__() {
  }
  /**
   * @return {!Path<NodeT>}
   * @template NodeT
   */
  static $create__com_google_daggerquery_executor_models_Query_Path(/** Path<NodeT> */ path) {
  }
  $ctor__com_google_daggerquery_executor_models_Query_Path__com_google_daggerquery_executor_models_Query_Path(/** Path<NodeT> */ path) {
  }
  m_addLast__java_lang_Object_$pp_com_google_daggerquery_executor_models(/** NodeT */ element) {
  }
  /**
   * @return {NodeT}
   */
  m_removeLast___$pp_com_google_daggerquery_executor_models() {
  }
  /**
   * @return {NodeT}
   */
  m_getLast___$pp_com_google_daggerquery_executor_models() {
  }
  /**
   * @return {boolean}
   */
  m_isEmpty___$pp_com_google_daggerquery_executor_models() {
  }
  /**
   * @return {?string}
   * @override
   */
  toString() {
  }
  /** @private */ $init___$p_com_google_daggerquery_executor_models_Query_Path() {
  }
  static $clinit() {
  }
  /**
   * @return {boolean}
   */
  static $isInstance(/** ? */ instance) {
  }
  static $loadModules() {
  }
}
/** @type {Deque<NodeT>} */ Path.prototype.f_nodesDeque__com_google_daggerquery_executor_models_Query_Path_;
exports = Path;
/** @fileoverview @typeSummary */
goog.module("com.google.daggerquery.executor.models.Query.Path");
goog.require("java.lang.Object");
goog.require("java.lang.String");
goog.require("java.util.ArrayDeque");
goog.require("java.util.Deque");
goog.require("java.util.List");
goog.require("java.util.function.Function");
goog.require("java.util.stream.Collector");
goog.require("java.util.stream.Collectors");
goog.require("java.util.stream.Stream");
goog.require("nativebootstrap.Util");
goog.require("vmbootstrap.Casts");
goog.require("vmbootstrap.Objects");
const Path = goog.require("com.google.daggerquery.executor.models.Query.Path$impl");
exports = Path;
