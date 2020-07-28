load("@rules_java//java:defs.bzl", "java_plugin")
load("@dagger//:workspace_defs.bzl", "dagger_rules")

dagger_rules()

java_plugin(
    name = "dagger_query_plugin",
    srcs = glob(["src/com/google/daggerquery/**/*.java"]),
    deps = ["//:dagger-spi", "//third_party/java/auto:service"]
)

