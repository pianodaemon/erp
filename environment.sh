STAGE=${STAGE:-"$HOME"}

TIME_STAMP_BUILD=$(date +%s)

export BINDIR="$STAGE/$TIME_STAMP_BUILD/out"
export RELDIR="$STAGE/$TIME_STAMP_BUILD/release"

[ -d $BINDIR ] || mkdir -p $BINDIR
[ -d $RELDIR ] || mkdir -p $RELDIR
