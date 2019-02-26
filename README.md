# mobile-android-blackbird
The Blackbird mobile app for Android

<h4><u>Working with git submodule</u></h4>

Step 1) Import the git submodule to use from <b>mobile-android-common</b>:

`git submodule add git@github.com:Crestron/mobile-android-common.git mobile-android-common`

Step 2) Pull latest incremental changes from <b>mobile-android-common</b> git submodule:

`git submodule update --recursive --remote`

Step 3) Any changes you make inside git submodule should be committed and pushed like any git repo.