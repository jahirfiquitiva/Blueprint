#!/usr/bin/env bash
if [ "$TRAVIS_PULL_REQUEST" = false ]; then
	if [ "$TRAVIS_TAG" ]; then
		cd $TRAVIS_BUILD_DIR/app/build/outputs/apk/release/
		
		printf "\n\nGetting tag information\n"
		tagInfo="$(curl https://api.github.com/repos/${TRAVIS_REPO_SLUG}/releases/tags/${TRAVIS_TAG})"
		releaseId="$(echo "$tagInfo" | jq --compact-output ".id")"

		releaseNameOrg="$(echo "$tagInfo" | jq --compact-output ".tag_name")"
		releaseName=$(echo ${releaseNameOrg} | cut -d "\"" -f 2)

		ln=$"%0D%0A"
        tab=$"%09"

		changes="$(echo "$tagInfo" | jq --compact-output ".body")"
        changes=$(echo ${changes} | cut -d "\"" -f 2)
        changes=$(echo "${changes//\"\r\n\"/$ln}")
        changes=$(echo "${changes//'\r\n'/$ln}")
        changes=$(echo "${changes//\\r\\n/$ln}")

		repoName=$(echo ${TRAVIS_REPO_SLUG} | cut -d / -f 2)

		printf "\n"

		for apk in $(find *.apk -type f); do
			apkName="${apk::-4}"

			printf "\n\nUploading: $apkName.apk ...\n"
			upload=`curl "https://uploads.github.com/repos/${TRAVIS_REPO_SLUG}/releases/${releaseId}/assets?access_token=${GITHUB_API_KEY}&name=${apkName}.apk" --header 'Content-Type: application/zip' --upload-file ${apkName}.apk  -X POST`

			printf "\n\nUpload Result: $upload\n"

			url="$(echo "$upload" | jq --compact-output ".browser_download_url")"
            url=$(echo ${url} | cut -d "\"" -f 2)
            url=$(echo "${url//\"\r\n\"/$ln}")
            url=$(echo "${url//'\r\n'/$ln}")
            url=$(echo "${url//\\r\\n/$ln}")

			if [ ! -z "$url" -a "$url" != " " -a "$url" != "null" ]; then
				printf "\nAPK url: $url"
				message=$"*New ${repoName} update available now!*${ln}*Version:*${ln}${tab}${releaseName}${ln}*Changes:*${ln}${changes}"
				btns=$"{\"inline_keyboard\":[[{\"text\":\"How To Update\",\"url\":\"https://github.com/${TRAVIS_REPO_SLUG}/wiki/How-to-update\"}],[{\"text\":\"Download sample\",\"url\":\"${url}\"}]]}"

				printf "\n\nSending message to Telegram channel\n"
				echo "Message: ${message}"
                printf "\n\n"
                echo "Buttons: ${btns}"
                printf "\n\n"

                telegramUrl="https://api.telegram.org/bot${TEL_BOT_KEY}/sendMessage?chat_id=@JFsDashSupport&text=${message}&parse_mode=Markdown&reply_markup=${btns}"
                echo "Telegram url: ${telegramUrl}"
                printf "\n\n"
                curl -g "${telegramUrl}"
			else
				printf "\n\nSkipping Telegram report because no file was uploaded\n"
			fi
		done

		printf "\n\nFinished uploading APK(s)\n"
	else
		printf "\n\nSkipping APK(s) upload because this commit does not have a tag\n"
	fi
else
	printf "\n\nSkipping APK(s) upload because this is just a pull request\n"
fi