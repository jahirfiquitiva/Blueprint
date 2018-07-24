#!/usr/bin/env bash
tagged="$(curl https://api.github.com/repos/${TRAVIS_REPO_SLUG}/releases/latest)"
relname="$(echo "$tagged" | jq --raw-output ".tag_name")"
repoName=$(echo $TRAVIS_REPO_SLUG | cut -f2 -d/)
teleMess="Latest version for *$repoName* is ${relname//\"}"
printf $teleMess
curl "https://api.telegram.org/bot${TEL_BOT_KEY}/sendMessage?chat_id=@JFsDashSupport&text=${teleMess}&parse_mode=Markdown"