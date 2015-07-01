package su.whs.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * 
 * @author igor n. boulliev
 * 
 * read metadata from manifest 
 *
 */

public class ManifestMetadata {
	/**
	 * 
	 * @param context
	 * @param key - meta-data name
	 * @return string value
	 */
	public static String getMetadata(Context context, String key) {
		Bundle bundle = all(context);
	    if (bundle.containsKey(key))
	    	return bundle.getString(key);
	    return null;
	}
	
	/**
	 * 
	 * @param context
	 * @return Bundle with all application's meta-data key-value pairs
	 *  
	 */
	
	public static Bundle all(Context context) {
		ApplicationInfo ai;
		try {
			ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			return null;
		}
	    Bundle bundle = ai.metaData;
	    return bundle;
	}
}
