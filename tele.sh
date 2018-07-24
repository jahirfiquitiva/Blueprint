#!/usr/bin/env bash
tagged="$(curl https://api.github.com/repos/${TRAVIS_REPO_SLUG}/releases/latest)"
relname="$(echo "$tagged" | jq ".tag_name")"
teleMess="Latest version is: $relname"
curl "https://api.telegram.org/bot${TEL_BOT_KEY}/sendMessage?chat_id=@JFsDashSupport&text=${teleMess}"