WORKSPACE=${WORKSPACE:-"$HOME"}

export BINDIR=$WORKSPACE/build
export RELDIR=$WORKSPACE/release/$(date +%s)

[ -d $BINDIR ] || mkdir -p $BINDIR
[ -d $RELDIR ] || mkdir -p $RELDIR
