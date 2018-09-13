package org.floatcast.floatsight.csvparser;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class GPSCoordinate {

	@Retention(SOURCE)
	@IntDef({INVALID, VALID})
	@interface ValidGPSLocation {}
	public static final int INVALID = 0;
	public static final int VALID = 1;

	final double lat;
	final double lon;
	final int valid;
	GPSCoordinate(double lat, double lon, @ValidGPSLocation int valid ) {
		this.lat = lat;
		this.lon = lon;
		this.valid = valid;
	}

	@NonNull
	public Float calculateHaversinDistanceMeters(GPSCoordinate gpsCoordinate) {
		if(this.valid == GPSCoordinate.INVALID || gpsCoordinate.valid == GPSCoordinate.INVALID) {
			return 0f;
		}
		double EARTH_RADIUS = 6371000;
		double dLat = Math.toRadians(gpsCoordinate.lat - this.lat);
		double dLon = Math.toRadians(gpsCoordinate.lon - this.lon);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLon / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(gpsCoordinate.lat));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		Double distance = EARTH_RADIUS * c;
		return distance.floatValue();
	}
}
