export PATH="$PATH:$HOME/bin"

bazel run --sandbox_debug --sandbox_block_path=/usr/local $@
