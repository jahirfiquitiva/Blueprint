url="$(echo "$APK_URL")"
url=$(echo ${url} | cut -d "\"" -f 2)
url=$(echo "${url//\"\r\n\"/$ln}")
url=$(echo "${url//'\r\n'/$ln}")
url=$(echo "${url//\\r\\n/$ln}")

if [[ ! -z "$url" && "$url" != " " && "$url" != "null" ]]; then
	printf "\nAPK url: $url"
	printf "\n\nSending message to Discord channel…\n"
	message="**Changes:**\n$CHANGELOG"
	message+="\n\n**Useful links:**"
	message+="\n* [How to update?](https://github.com/${GITHUB_REPOSITORY}/wiki/How-to-update)"
	message+="\n* [Download sample APK]("
	message+="$url"
	message+=")\n* [Donate & support future development](https://jahir.dev/donate)"
	curl -X POST -H 'Content-Type: application/json' -d '{ "embeds": [{ "title": "**New update available! ('"$RELEASE_TAG"')** 🚀", "description": "'"$message"'", "color": 15844367 }] }' $UPDATE_DISCORD_WEBHOOK

	printf "\n\nFinished sending notifications\n"
else
	printf "\n\nSkipping notifications because no file was uploaded\n"
fi
