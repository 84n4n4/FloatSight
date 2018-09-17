# FloatSight
An Android app for visualizing [**FlySight**](http://flysight.ca/) GPS tracker data.

## Permissions
FloatSight need read and write storage permissions. (Saved tracks can be found on your mobiles storage in the FloatSight folder)

## Open files directly from a FlySight -
* Your Android device has to support USB-Host / USB On-The-Go.
* You need an USB-OTG cable.

## Other sources
Alternatively you can of course open and view Flysight tracks from any other storage location on your phone, attachments from messenger apps, downloads, etc.

## Connecting to the FlySight
To connect your FlySight -
* Connect your Android device with your FlySight using an OTG cable.
* Change USB option (accessible via notifications bar) to "File transfer". (Give it a few seconds.)
* Use the import function to open the track in FloatSight, a file picker is displayed and you will have to navigate to the USB mounted storage that is your FlySight and pick a track.
* Use the load function to open imported tracks that are already on you mobile devices storage.
* Don't Forget to properly eject your FlySight before disonnecting the cable. This is also done via the notification bar. (If no eject option is visible there, swipe down on the Android System notification for USB drive.) 

Alternatively you can copy tracks from FlySight with any another file manager app if you prefer.

## Plot usage
* Short tap on a graph shows brings up an yellow marker with data on this position.
* Either short tap on a yellow marker or tap on menu icon "x" dismisses the marker.
* Either long tap on this yellow marker or tap on menu icon "[]" sets this as a range start/end.
* The blue range marker averages and differentials of data within this range.
* The scissors menu icon will crop the track to the current range.
* Either short tap on a blue range marker or tap on menu icon "x" dismisses the range.
* Options menu lets you toggle visibility of lines, switch units, toggle time or distance for x-axis.

## Licence
FloatSight is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
FloatSight is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
Read [**full licence**](https://github.com/84n4n4/FloatSight/blob/master/COPYING).

# Contributing

This project utilizes git flow workflow.
- head of master is the current play store version.
- master is only updated for releases and hotfixes.
- develop is where development happens.

## How to contribute

* Fork the project.
* Branch off of develop.
* Implement your stuff.
* Push your branch.
* Create pull request.

# Privacy policy

This app doesn't collect, transfer, share any information. There's no analytics tools or trackers built in. All FlySight track processing happens offline on your mobile device.