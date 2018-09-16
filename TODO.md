# release

## trackactivity
* intent filter for opening CSV from other android app should list app and open TrackActivity

### plot fragment

### track menu fragment

### stats fragment

## track picker

## main activity

## parsers, data, viewmodels
* fix parser for old style flysight track data:

    time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,heading,cAcc,gpsFix,numSV
    ,(deg),(deg),(m),(m/s),(m/s),(m/s),(m),(m),(m/s),(deg),(deg),,
    2018-08-12T14:25:43.07Z,47.1111668,15.4640163,3474.052,24.19,-45.37,-0.87,243.700,215.045,5.41,298.06437,6.45464,3,6
    2018-08-12T14:25:43.20Z,47.1111651,15.4640340,3471.412,24.13,-46.03,-1.74,134.650,109.656,2.37,297.65783,3.26640,3,6
    
    time,lat,lon,hMSL,velN,velE,velD,hAcc,vAcc,sAcc,gpsFix,numSV
    ,(deg),(deg),(m),(m/s),(m/s),(m/s),(m),(m),(m/s),,,
    2010-09-11T21:23:08.00Z,53.6322164,-114.1941555,4443.290,-1.38,-33.82,9.96,14.015,17.902,0.90,3,4
    2010-09-11T21:23:08.20Z,53.6322474,-114.1942516,4440.997,2.42,-32.79,10.55,8.128,10.257,0.89,3,8

# nice to have:
* config editor

# minor:
* prevent markerView from sliding with viewport

# next release / longterm
* upload to skyderby
* multi track plot