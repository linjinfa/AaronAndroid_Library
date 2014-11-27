package com.aaron.library.frame.sensor;

import android.hardware.SensorEvent;

public interface HLSensorListener {

	public abstract void OnScrollSensorListener(float pitch, float yaw, float timeDiffPitch, float timeDiffYaw);
	public abstract void onSensorRawDataListener(SensorEvent event);
	/**如果需要，需提供正确的view方向，Surface.ROTATION_0,Surface.ROTATION_90,Surface.ROTATION_180,Surface.ROTATION_270*/
	public abstract int providerSurfaceOrientation();
}
