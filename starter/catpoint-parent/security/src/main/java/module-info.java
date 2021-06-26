module com.udacity.catpoint.security {
	opens com.udacity.catpoint.security.data to com.google.gson;
	requires com.udacity.catpoint.image;
	requires miglayout;
	requires java.desktop;
	requires java.prefs;
	requires com.google.common;
	requires com.google.gson;
	requires java.sql;
}