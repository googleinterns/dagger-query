export PATH="$PATH:$HOME/bin"

if [[ -z $3 ]]; then
  bazel run --sandbox_debug --sandbox_block_path=/usr/local //src/com/google/daggerquery/executor:query_executor $1 $2
else
  bazel run --sandbox_debug --sandbox_block_path=/usr/local //src/com/google/daggerquery/executor:query_executor $1 $2 $3
fi
