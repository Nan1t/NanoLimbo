#!/bin/sh
set -eu

if [ ! -e /data/settings.yml ]; then
    cp /opt/nanolimbo/settings.yml /data/settings.yml
fi

exec "$@"
