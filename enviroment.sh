STAGE=${STAGE:-"$HOME"}

export BINDIR=$STAGE/build
export RELDIR=$STAGE/release/$(date +%s)

[ -d $BINDIR ] || mkdir -p $BINDIR
[ -d $RELDIR ] || mkdir -p $RELDIR
