package com.aaron.library.frame.sensor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;

import com.panoramagl.PLConstants;
import com.panoramagl.computation.PLMath;

public class HLSensorManager implements SensorEventListener {
	private static HLSensorManager instance;
	private SensorManager sensorManager;

	// /////////////////////此处接口供外部调用
	public static HLSensorManager getInstance(Context context) {
		if (instance == null) {
			instance = new HLSensorManager(context);
		}
		return instance;
	}

	public boolean registrySensor(HLSensorTypeEnum type, HLSensorListener listener) {
		if (sensorManager == null) {
			return false;
		}
		if (type == HLSensorTypeEnum.SENSOR_TYPE_SCROLL) {
			return registryScroll(listener);
		} else if (type == HLSensorTypeEnum.SENSOR_TYPE_ACCELEROMETER) {
			return registryAccelerometer(listener);
		} else if (type == HLSensorTypeEnum.SENSOR_TYPE_MAGNETIC) {
			return registryMagnatic(listener);
		} else if (type == HLSensorTypeEnum.SENSOR_TYPE_ORIENTATION) {
			return registryOrienation(listener);
		} else if (type == HLSensorTypeEnum.SENSOR_TYPE_GYROSCOPE) {
			return registryGyroscope(listener);
		}
		return false;
	}

	public void unregistrySensor(HLSensorTypeEnum type, HLSensorListener listener) {
		if (sensorManager == null) {
			return;
		}
		if (type == HLSensorTypeEnum.SENSOR_TYPE_SCROLL) {
			unregistryScroll(listener);
		} else if (type == HLSensorTypeEnum.SENSOR_TYPE_ACCELEROMETER) {
			unregistryAccelerometer(listener);
		} else if (type == HLSensorTypeEnum.SENSOR_TYPE_MAGNETIC) {
			unregistryMagnatic(listener);
		} else if (type == HLSensorTypeEnum.SENSOR_TYPE_ORIENTATION) {
			unregistryOrienation(listener);
		} else if (type == HLSensorTypeEnum.SENSOR_TYPE_GYROSCOPE) {
			unregistryGyroscope(listener);
		}
	}

	// /////////////////////以上接口供外部调用
	private boolean isAccelerometerActed, isMagnaticActivated,
			isOrientationActivated, isGyroscopeActivated, isScrollSensorActivated;
	private List<HLSensorListener> mScrollSensorListenerList = new ArrayList<HLSensorListener>();
	private List<HLSensorListener> mAcceleromerSensorListenerList = new ArrayList<HLSensorListener>();
	private List<HLSensorListener> mMagnaticSensorListenerList = new ArrayList<HLSensorListener>();
	private List<HLSensorListener> mOrientationSensorListenerList = new ArrayList<HLSensorListener>();
	private List<HLSensorListener> mGyroscopeSensorListenerList = new ArrayList<HLSensorListener>();

	private float accelerometerInterval;

	private ScrollSensorType sensorialRotationType;


	private HLSensorManager(Context context) {
		accelerometerInterval = PLConstants.kDefaultAccelerometerInterval;

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
	}

	///////////////////以下为对各sensor的注册管理，激活等
	// 注册滚动Sensor
	private boolean registryScroll(HLSensorListener listener) {
		if (activateScrollSensor() == true) {
			if (mScrollSensorListenerList.contains(listener) == false) {
				mScrollSensorListenerList.add(listener);
			}
			return true;
		}
		return false;
	}

	// 取消注册滚动Sensor
	private void unregistryScroll(HLSensorListener listener) {
		mScrollSensorListenerList.remove(listener);
		if (mScrollSensorListenerList.size() == 0) {
			deactiveScrollSensor();
		}
	}

	//滚动Sensor是否在使用type指定的Sensor
	private boolean isScrollSensorUseThisSensor(HLSensorTypeEnum type) {
		if (isScrollSensorActivated == false) {
			return false;
		}
		if ((type == HLSensorTypeEnum.SENSOR_TYPE_ACCELEROMETER || type == HLSensorTypeEnum.SENSOR_TYPE_MAGNETIC)
				&& ScrollSensorType.DSensorTypeMagnetometer == sensorialRotationType) {
			return mScrollSensorListenerList.size() != 0;
		} else if ((type == HLSensorTypeEnum.SENSOR_TYPE_GYROSCOPE || type == HLSensorTypeEnum.SENSOR_TYPE_ORIENTATION)
				&& ScrollSensorType.DSensorTypeGyroscope == sensorialRotationType) {
			return mScrollSensorListenerList.size() != 0;
		}
		return false;
	}

	// 注册加速度Sensor
	private boolean registryAccelerometer(HLSensorListener listener) {
		if (activateAccelerometer() == true) {
			if (mAcceleromerSensorListenerList.contains(listener) == false) {
				mAcceleromerSensorListenerList.add(listener);
			}
			return true;
		}
		return false;
	}

	// 取消注册加速度Sensor
	private void unregistryAccelerometer(HLSensorListener listener) {
		mAcceleromerSensorListenerList.remove(listener);
		if (mAcceleromerSensorListenerList.size() == 0
				&& isScrollSensorUseThisSensor(HLSensorTypeEnum.SENSOR_TYPE_ACCELEROMETER) == false) {
			deactiveAccelerometer();
		}
	}

	// 注册磁感应Sensor
	private boolean registryMagnatic(HLSensorListener listener) {
		if (activateMagnetometer() == true) {
			if (mMagnaticSensorListenerList.contains(listener) == false) {
				mMagnaticSensorListenerList.add(listener);
			}
			return true;
		}
		return false;
	}

	// 取消注册磁感应Sensor
	private void unregistryMagnatic(HLSensorListener listener) {
		mMagnaticSensorListenerList.remove(listener);
		if (mMagnaticSensorListenerList.size() == 0
				&& isScrollSensorUseThisSensor(HLSensorTypeEnum.SENSOR_TYPE_MAGNETIC) == false) {
			deactivateMagnetometer();
		}
	}

	// 注册方向Sensor
	private boolean registryOrienation(HLSensorListener listener) {
		if (activateOrientation() == true) {
			if (mOrientationSensorListenerList.contains(listener) == false) {
				mOrientationSensorListenerList.add(listener);
			}
			return true;
		}
		return false;
	}

	// 取消注册方向Sensor
	private void unregistryOrienation(HLSensorListener listener) {
		mOrientationSensorListenerList.remove(listener);
		if (mOrientationSensorListenerList.size() == 0
				&& isScrollSensorUseThisSensor(HLSensorTypeEnum.SENSOR_TYPE_ORIENTATION) == false) {
			deactiveOrientation();
		}
	}

	// 注册陀螺仪
	private boolean registryGyroscope(HLSensorListener listener) {
		if (activateGyroscope() == true) {
			if (mGyroscopeSensorListenerList.contains(listener) == false) {
				mGyroscopeSensorListenerList.add(listener);
			}
			return true;
		}
		return false;
	}

	// 取消注册陀螺仪
	private void unregistryGyroscope(HLSensorListener listener) {
		mGyroscopeSensorListenerList.remove(listener);
		if (mGyroscopeSensorListenerList.size() == 0
				&& isScrollSensorUseThisSensor(HLSensorTypeEnum.SENSOR_TYPE_GYROSCOPE) == false) {
			deactivateGyroscope();
		}
	}

	// 启动加速度感应器
	private boolean activateAccelerometer() {
		if (isAccelerometerActed == false) {
			if (false == sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					(int) (accelerometerInterval * 1000.0f))) {
				return false;
			}
			isAccelerometerActed = true;
		}
		return true;
	}

	// 关闭加速度感应器
	private void deactiveAccelerometer() {
		if (isAccelerometerActed == true) {
			sensorManager.unregisterListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
			isAccelerometerActed = false;
		}
	}

	// 启动陀螺仪
	private boolean activateGyroscope() {
		if (isGyroscopeActivated == false) {
			if (false == sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
					SensorManager.SENSOR_DELAY_GAME)) {
				return false;
			}
			isGyroscopeActivated = true;
		}
		return true;
	}

	// 关闭陀螺仪
	private void deactivateGyroscope() {
		if (isGyroscopeActivated == true) {
			sensorManager.unregisterListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
			isGyroscopeActivated = false;
		}
	}

	// 启动磁感应
	private boolean activateMagnetometer() {
		if (isMagnaticActivated == false) {
			if (false == sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
					SensorManager.SENSOR_DELAY_GAME)) {
				return false;
			}
			isMagnaticActivated = true;
		}
		return true;
	}

	// 关闭磁感应
	private void deactivateMagnetometer() {
		if (isMagnaticActivated == true) {
			sensorManager.unregisterListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
			isMagnaticActivated = false;
		}
	}

	// 启动方向感应器
	private boolean activateOrientation() {
		if (isOrientationActivated == false) {
			if (false == sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					SensorManager.SENSOR_DELAY_GAME)) {
				return false;
			}
			isOrientationActivated = true;
		}
		return true;
	}

	// 关闭方向感应器
	private void deactiveOrientation() {
		if (isOrientationActivated == true) {
			sensorManager.unregisterListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
			isOrientationActivated = false;
		}
	}

	//启动滚动sensor
	public boolean activateScrollSensor() {
		if (isScrollSensorActivated == false) {
			// boolean startSensorialRotation = false;

			// FIXME gyroscope rotation
			if (activateGyroscope()) {
				activateOrientation();
//				hasFirstGyroscopePitch = false;
				gyroscopeLastTime = 0;
				gyroscopeRotationX = gyroscopeRotationY = 0.0f;
				sensorialRotationType = ScrollSensorType.DSensorTypeGyroscope;
				isScrollSensorActivated = true;
				// startSensorialRotation = true;
			} else {
				if (sensorManager != null
						&& sensorManager.getSensorList(
								Sensor.TYPE_ACCELEROMETER).size() > 0
						&& sensorManager.getSensorList(
								Sensor.TYPE_MAGNETIC_FIELD).size() > 0) {
					sensorialRotationThresholdTimestamp = 0;
					sensorialRotationThresholdFlag = false;
					sensorialRotationRotationMatrix = new float[16];
					sensorialRotationOrientationData = new float[3];
					hasFirstAccelerometerPitch = hasFirstMagneticHeading = false;
					lastAccelerometerPitch = accelerometerPitch = 0.0f;
					firstMagneticHeading = lastMagneticHeading = magneticHeading = 0.0f;
					sensorialRotationType = ScrollSensorType.DSensorTypeMagnetometer;
					isScrollSensorActivated = true;
					this.activateMagnetometer();
					activateAccelerometer();
					// startSensorialRotation = true;
				} else{
					return false;
				}
			}
		}
		return true;
	}

	//关闭滚动sensor
	public void deactiveScrollSensor() {
		if (isScrollSensorActivated) {
			isScrollSensorActivated = false;
			if (sensorialRotationType == ScrollSensorType.DSensorTypeUnknow) {
				return;
			}

			if (sensorialRotationType == ScrollSensorType.DSensorTypeGyroscope){
				if(mOrientationSensorListenerList.size() == 0){
					deactiveOrientation();
				}
				if(mGyroscopeSensorListenerList.size() == 0){
					deactivateGyroscope();
				}
			}else if (sensorialRotationType == ScrollSensorType.DSensorTypeMagnetometer) {
				if(mAcceleromerSensorListenerList.size() == 0){
					deactiveAccelerometer();
				}
				if(mMagnaticSensorListenerList.size() == 0){
					deactivateMagnetometer();
				}
				sensorialRotationRotationMatrix = sensorialRotationOrientationData = sensorialRotationMagnetometerData = null;
			}
			sensorialRotationType = ScrollSensorType.DSensorTypeUnknow;
			// this.stopAnimationInternally(false);
			// startPoint.setValues(endPoint.setValues(0.0f, 0.0f));
		}
	}
	///////////////////以上为对各sensor的注册管理，激活等

	private void doGyroUpdate(float originPitch, float originYaw, float timeDiffPitch,
			float timeDiffYaw) {
		for (HLSensorListener listener : mScrollSensorListenerList) {
			
			int currentDeviceSurfaceOrientation = listener
					.providerSurfaceOrientation();
			float localPitch = originPitch;
			float localYaw = originYaw;

			if (currentDeviceSurfaceOrientation == Surface.ROTATION_0) {
			} else if (currentDeviceSurfaceOrientation == Surface.ROTATION_180) {
				localPitch = -localPitch;
				localYaw = -localYaw;
			} else if (currentDeviceSurfaceOrientation == Surface.ROTATION_90) {
				float tmp = localPitch;
				localPitch = -localYaw;
				localYaw = tmp;
			} else if (currentDeviceSurfaceOrientation == Surface.ROTATION_270) {
				float tmp = localPitch;
				localPitch = localYaw;
				localYaw = -tmp;
			}
			// 转化为度数
			localPitch = PLMath.normalizeAngle(localPitch
					* PLConstants.kToDegrees, -180.0f, 180.0f);
			localYaw = PLMath.normalizeAngle(localYaw * PLConstants.kToDegrees,
					-180.0f, 180.0f);
			// 本地转化
			localPitch = -localPitch;

			listener.OnScrollSensorListener(localPitch, localYaw, timeDiffPitch,
					timeDiffYaw);
		}
	}

	protected void doSimulatedGyroUpdate() {
		 if (hasFirstAccelerometerPitch && hasFirstMagneticHeading) {
			float step, offset = Math.abs(lastAccelerometerPitch
					- accelerometerPitch);
			if (offset < 0.5f) {
				lastAccelerometerPitch = accelerometerPitch = Math
						.round(lastAccelerometerPitch);
			} else {
				step = (offset <= 5.0f ? 0.25f : 1.0f);
				if (lastAccelerometerPitch > accelerometerPitch)
					accelerometerPitch += step;
				else if (lastAccelerometerPitch < accelerometerPitch)
					accelerometerPitch -= step;
			}
			offset = Math.abs(lastMagneticHeading - magneticHeading);
			if (offset < 0.5f)
				lastMagneticHeading = magneticHeading = Math
						.round(lastMagneticHeading);
			else {
				step = (offset <= 5.0f ? 0.25f : 1.0f);
				if (lastMagneticHeading > magneticHeading)
					magneticHeading += step;
				else if (lastMagneticHeading < magneticHeading)
					magneticHeading -= step;
			}

			float pitch = accelerometerPitch;
			float yaw = -magneticHeading;
			for (HLSensorListener listener : mScrollSensorListenerList) {
				listener.OnScrollSensorListener(pitch, yaw, 0, 0);
			}
		 // scene.getCurrentCamera().lookAt(accelerometerPitch,
//		 //-magneticHeading);
		 }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	private float[] sensorialRotationMagnetometerData;

	private long sensorialRotationThresholdTimestamp;
	private boolean sensorialRotationThresholdFlag;

	private float[] sensorialRotationRotationMatrix;
	private float[] sensorialRotationOrientationData;
	private float lastAccelerometerPitch, accelerometerPitch;
	private boolean hasFirstAccelerometerPitch, hasFirstMagneticHeading;
	private float firstMagneticHeading, lastMagneticHeading, magneticHeading;
//	private UIAcceleration tempAcceleration = new UIAcceleration();

//	private boolean hasFirstGyroscopePitch;
//	private UIDeviceOrientation currentDeviceOrientation;
	private long gyroscopeLastTime;
	private float gyroscopeRotationX, gyroscopeRotationY;

	@Override
	public void onSensorChanged(SensorEvent event) {

		float[] values = event.values;
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			if (sensorialRotationType == ScrollSensorType.DSensorTypeMagnetometer
					&& sensorialRotationMagnetometerData != null) {
				if (!sensorialRotationThresholdFlag) {
					if (sensorialRotationThresholdTimestamp == 0){
						sensorialRotationThresholdTimestamp = System
								.currentTimeMillis();
					}else if ((System.currentTimeMillis() - sensorialRotationThresholdTimestamp) >= PLConstants.kSensorialRotationThreshold){
						sensorialRotationThresholdFlag = true;
					}
				}
				if (sensorialRotationThresholdFlag) {
					SensorManager.getRotationMatrix(
							sensorialRotationRotationMatrix, null, values,
							sensorialRotationMagnetometerData);
					SensorManager.remapCoordinateSystem(
							sensorialRotationRotationMatrix,
							SensorManager.AXIS_X, SensorManager.AXIS_Z,
							sensorialRotationRotationMatrix);
					SensorManager.getOrientation(
							sensorialRotationRotationMatrix,
							sensorialRotationOrientationData);
					float pitch = sensorialRotationOrientationData[1]
							* PLConstants.kToDegrees;
					float yaw = sensorialRotationOrientationData[0]
							* PLConstants.kToDegrees - 180.0f;
					if (hasFirstAccelerometerPitch) {
						if ((pitch > lastAccelerometerPitch && pitch
								- PLConstants.kSensorialRotationPitchErrorMargin > lastAccelerometerPitch)
								|| (pitch < lastAccelerometerPitch && pitch
										+ PLConstants.kSensorialRotationPitchErrorMargin < lastAccelerometerPitch))
							lastAccelerometerPitch = pitch;
					} else {
						lastAccelerometerPitch = accelerometerPitch = pitch;
						hasFirstAccelerometerPitch = true;
					}
					if (hasFirstMagneticHeading) {
						if ((lastAccelerometerPitch >= 0.0f && lastAccelerometerPitch < 50.0f)
								|| (lastAccelerometerPitch < 0.0f && lastAccelerometerPitch > -50.0f)) {
							yaw -= firstMagneticHeading;
							float diff = yaw - lastMagneticHeading;
							if (Math.abs(diff) > 100.0f) {
								lastMagneticHeading = yaw;
								magneticHeading += (diff >= 0.0f ? 360.0f
										: -360.0f);
							} else if ((yaw > lastMagneticHeading && yaw
									- PLConstants.kSensorialRotationYawErrorMargin > lastMagneticHeading)
									|| (yaw < lastMagneticHeading && yaw
											+ PLConstants.kSensorialRotationYawErrorMargin < lastMagneticHeading))
								lastMagneticHeading = yaw;
						}
					} else {
						firstMagneticHeading = yaw;
						lastMagneticHeading = magneticHeading = 0.0f;
						hasFirstMagneticHeading = true;
					}
					doSimulatedGyroUpdate();
				}
			}
			//accelerometer(event, tempAcceleration.setValues(values));
			for(HLSensorListener listener : mAcceleromerSensorListenerList){
				listener.onSensorRawDataListener(event);
			}
			break;
		case Sensor.TYPE_ORIENTATION:
//			if (isScrollSensorActivated
//					&& sensorialRotationType == ScrollSensorType.DSensorTypeGyroscope
//					&& !hasFirstGyroscopePitch)
//				hasFirstGyroscopePitch = true;
			for(HLSensorListener listener : mOrientationSensorListenerList){
				listener.onSensorRawDataListener(event);
			}
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			if (sensorialRotationMagnetometerData == null){
				sensorialRotationMagnetometerData = new float[3];
			}
			sensorialRotationMagnetometerData[0] = values[0];
			sensorialRotationMagnetometerData[1] = values[1];
			sensorialRotationMagnetometerData[2] = values[2];
			for(HLSensorListener listener : mMagnaticSensorListenerList){
				listener.onSensorRawDataListener(event);
			}
			break;
		case Sensor.TYPE_GYROSCOPE:
			// FIXME gyroscope rotation
			if (gyroscopeLastTime != 0 /*&& hasFirstGyroscopePitch*/) {
				float timeDiff = (event.timestamp - gyroscopeLastTime)
						* PLConstants.kGyroscopeTimeConversion;
				if (timeDiff > 1.0)
					timeDiff = PLConstants.kGyroscopeMinTimeStep;
				gyroscopeRotationX += values[0] * timeDiff;
				gyroscopeRotationY += values[1] * timeDiff;
				doGyroUpdate(
						gyroscopeRotationX,
						gyroscopeRotationY,
						values[1] * timeDiff, values[0] * timeDiff);
//				if (currentDeviceOrientation == UIDeviceOrientation.UIDeviceOrientationPortrait) {
//					doGyroUpdate(
//							PLMath.normalizeAngle(gyroscopeRotationX
//									* PLConstants.kToDegrees, -180.0f, 180.0f),
//							PLMath.normalizeAngle(gyroscopeRotationY
//									* PLConstants.kToDegrees, -180.0f, 180.0f),
//							values[1] * timeDiff, values[0] * timeDiff);
//				} else if (currentDeviceOrientation == UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown) {
//					doGyroUpdate(
//							PLMath.normalizeAngle(-gyroscopeRotationX
//									* PLConstants.kToDegrees, -180.0f, 180.0f),
//							PLMath.normalizeAngle(-gyroscopeRotationY
//									* PLConstants.kToDegrees, -180.0f, 180.0f),
//							values[1] * timeDiff, values[0] * timeDiff);
//				} else if (currentDeviceOrientation == UIDeviceOrientation.UIDeviceOrientationLandscapeLeft) {
//					doGyroUpdate(
//							PLMath.normalizeAngle(gyroscopeRotationY
//									* PLConstants.kToDegrees, -180.0f, 180.0f),
//							PLMath.normalizeAngle(-gyroscopeRotationX
//									* PLConstants.kToDegrees, -180.0f, 180.0f),
//							values[1] * timeDiff, values[0] * timeDiff);
//				} else if (currentDeviceOrientation == UIDeviceOrientation.UIDeviceOrientationLandscapeRight) {
//					doGyroUpdate(
//							PLMath.normalizeAngle(-gyroscopeRotationY
//									* PLConstants.kToDegrees, -180.0f, 180.0f),
//							PLMath.normalizeAngle(gyroscopeRotationX
//									* PLConstants.kToDegrees, -180.0f, 180.0f),
//							values[1] * timeDiff, values[0] * timeDiff);
//				}
			}
			gyroscopeLastTime = event.timestamp;
			for(HLSensorListener listener : mGyroscopeSensorListenerList){
				listener.onSensorRawDataListener(event);
			}
			break;
		}
	}

	private enum ScrollSensorType {
		DSensorTypeUnknow, DSensorTypeGyroscope, DSensorTypeMagnetometer,
	}
}
