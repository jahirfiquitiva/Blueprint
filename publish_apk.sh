#!/usr/bin/env bash
if [ "$TRAVIS_PULL_REQUEST" = false ]; then
	if [ "$TRAVIS_TAG" ]; then
		cd $TRAVIS_BUILD_DIR/app/build/outputs/apk/release/
		
		printf "\nGetting tag information\n"
		tagInfo="$(curl https://api.github.com/repos/${TRAVIS_REPO_SLUG}/releases/tags/${TRAVIS_TAG})"
		releaseId="$(echo "$tagInfo" | jq ".id")"

		printf "\n\n"
		for apk in $(find *.apk -type f); do
		  apkName="${apk::-4}"
		  printf "Uploading: $apkName.apk ...\n"
		  curl "https://uploads.github.com/repos/${TRAVIS_REPO_SLUG}/releases/${releaseId}/assets?access_token=${GITHUB_API_KEY}&name=${apkName}.apk" --header 'Content-Type: application/zip' --upload-file $apkName.apk -X POST
		done

		printf "\n\nFinished uploading APK(s)\n"
	else
		printf "\nSkipping APK(s) upload because this commit does not have a tag\n"
	fi
else
	printf "\nSkipping APK(s) upload  because this is just a pull request\n"
fi
