#!/usr/bin/env bash
printf "\n\nGetting tag information...\n"
tagInfo=$(
	curl \
		-H "Authorization: token $GITHUB_API_KEY" \
		"${GITHUB_API_URL}/repos/${GITHUB_REPOSITORY}/releases/latest"
)
# printf "\n\nRelease data: $tagInfo\n"
releaseId="$(echo "$tagInfo" | jq --compact-output ".id")"

releaseNameOrg="$(echo "$tagInfo" | jq --compact-output ".tag_name")"
releaseName=$(echo ${releaseNameOrg} | cut -d "\"" -f 2)

ln=$"%0D%0A"
tab=$"%09"

changes="$(echo "$tagInfo" | jq --compact-output ".body")"
changes=$(echo ${changes} | cut -d "\"" -f 2)
defaultChanges="$changes"
changes=$(echo "${changes//\"\r\n\"/$ln}")
changes=$(echo "${changes//'\r\n'/$ln}")
changes=$(echo "${changes//\\r\\n/$ln}")

repoName=$(echo ${GITHUB_REPOSITORY} | cut -d / -f 2)

printf "\n"

cd ./app/build/outputs/apk/release/
printf "\n\nGetting APK file name...\n"
for apk in $(find *.apk -type f); do
	FILE="$apk"

	echo "release_tag=$releaseName" >> $GITHUB_ENV
	echo "apk_file=$FILE" >> $GITHUB_ENV
	echo "release_changes=$defaultChanges" >> $GITHUB_ENV

	break
done
