#release
## trackactivity
* intent filter for opening CSV from other android app should list app and open TrackActivity

### plot fragment
* toolbar icons for range, slice, discard
* remove app title from toolbar

### track menu fragment
* remove app title from toolbar
* add dirty flag to trackViewModel and show sliced track as modified, click on disk opens save dialog

### stats fragment
* 1:1 altitude distance metric with "i" info icon (on click shows info dialog that slice to exit is required for useful data)

## track picker
* track picker as activity
* show location/foldername in toolbar
* remove app name from toolbar

## main activity
* open trackpicker at import location after successful import

## parsers, data, viewmodels
* parsing status "some errors" not working correctly

# nice to have:
* config editor

# minor:
* prevent markerView from sliding with viewport
* item decorator in files list slips over files when deleting

# next release / longterm
* upload to skyderby
* multi track plot