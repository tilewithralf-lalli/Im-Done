# I'M DONE! Android App

This folder contains the full Android project. The signed installable file is delivered as `IM-DONE-HOME-FAMILY-v1.0.1.apk` alongside the source download.

## Install on an Android phone or tablet

1. Copy `IM-DONE-HOME-FAMILY-v1.0.1.apk` to the device.
2. Tap the APK and allow installation from that source when Android asks.
3. Open I'M DONE!

The starting parent PIN is 1234. You can change it in Parent Settings.

On the parent's device, tap PARENT and create or sign in to the family account. In Parent Settings, the app shows a private 6-number family code.

On each child's device, install the same APK, tap CHILD, enter the family code, and tap that child's name. Children do not need email addresses and their device is locked to their own child screen.

## Open the project on a PC

Open this entire folder in Android Studio. The main kid and parent interface is app/src/main/assets/index.html.

## Included in this build

- Colourful kid-friendly chore screen
- Multiple children and child switching
- Add, rename, and delete children
- Add, edit, and delete chores
- Change chore title, icon, time, and stars
- Change rewards and required star amount
- ALL DONE and 5 MORE MINUTES buttons
- Parent message, bonus stars, pause, reset, and undo
- Parent PIN
- Editable parent/family name, login email and password
- Password-reset email from Parent Settings
- Built-in How to Use guide
- Backup and Restore controls
- Feedback, Privacy, App Version and Delete All Data controls
- Official Team LALLI61 Settings footer
- Local saved data on the installed device
- One parent family account
- Private 6-number family code for child devices
- Child device linking without a child email
- Child device locked to the selected child profile
- Live Firebase syncing for children, chores, stars, DONE status and rewards
- Android chore-time notifications and working 5-minute snooze
- Offline on-device saved copy with automatic cloud retry when the connection returns

The app keeps a local copy on each device and securely syncs the family data through Firebase when connected.
