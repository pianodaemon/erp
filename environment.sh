STAGE=${STAGE:-"$HOME"}

export GRPC_HOST="127.0.0.1"
export GRPC_PORT="10090"

TIME_STAMP_BUILD=$(date +%s)

export BINDIR="$STAGE/$TIME_STAMP_BUILD/out"
export RELDIR="$STAGE/$TIME_STAMP_BUILD/release"

[ -d $BINDIR ] || mkdir -p $BINDIR
[ -d $RELDIR ] || mkdir -p $RELDIR
