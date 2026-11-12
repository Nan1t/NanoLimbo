#!/bin/sh
set -eu
umask 077

if [ ! -e /data/settings.yml ]; then
    cp /opt/nanolimbo/settings.yml /data/settings.yml
fi

exec "$@"
