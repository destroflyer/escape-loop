#!/bin/bash

./gradlew lwjgl3:packageWinX64

wsl rsync -avz --delete --exclude assets.txt lwjgl3/build/construo/winX64/roast/ assets/ "root@destrostudios.com:/var/www/destrostudios/apps/Escape Loop/"

curl -X POST https://destrostudios.com:8080/apps/16/updateFiles
