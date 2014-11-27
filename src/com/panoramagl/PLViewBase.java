/*
 * PanoramaGL library
 * Version 0.1
 * Copyright (c) 2010 Javier Baez <javbaezga@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.panoramagl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.SensorEvent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.aaron.library.frame.sensor.HLSensorListener;
import com.aaron.library.frame.sensor.HLSensorManager;
import com.aaron.library.frame.sensor.HLSensorTypeEnum;
import com.panoramagl.computation.PLMath;
import com.panoramagl.enumeration.PLTouchEventType;
import com.panoramagl.enumeration.PLTouchStatus;
import com.panoramagl.enumeration.PLTransitionType;
import com.panoramagl.ios.NSTimer;
import com.panoramagl.ios.UITouch;
import com.panoramagl.ios.enumeration.UIDeviceOrientation;
import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.ios.structs.CGSize;
import com.panoramagl.ios.structs.UIAcceleration;
import com.panoramagl.structs.PLRange;
import com.panoramagl.structs.PLShakeData;
import com.panoramagl.transitions.PLITransition;
import com.panoramagl.transitions.PLTransitionListener;

public abstract class PLViewBase extends Activity implements PLIView,
		 OnDoubleTapListener {
	/** member variables */

	private PLIRenderer renderer;
	private PLIPanorama scene;

	private NSTimer animationTimer;
	private float animationInterval;

	private CGPoint startPoint, endPoint, tempPoint;
	private CGPoint touchDownPoint, touchUpPoint;
	private CGPoint startFovPoint, endFovPoint;
	//用于记录最后一次drawView时的起始点，用于防止move事件停止时，仍然重复drawView.
	private CGPoint lastDrawViewStartPoint, lastDrawViewEndPoint;
	
	private boolean isValidForFov;
	private float fovDistance;
	private int fovCounter;

	/*加速度感应相关，抖动感应数据来源*/
	private boolean isAccelerometerEnabled, isAccelerometerLeftRightEnabled,
			isAccelerometerUpDownEnabled;
	private float accelerometerSensitivity;
//	private float accelerometerInterval;

	/*sensor自动滚动相关*/
	private UIDeviceOrientation currentDeviceOrientation;
	/**Surface.ROTATION_0,Surface.ROTATION_90,Surface.ROTATION_180,Surface.ROTATION_270*/
	private int currentDeviceSurfaceOrientation;
	private float oldPitch, oldYaw;
	private boolean isRunningSensorialRotation;
	private boolean isScrollingEnabled, isValidForScrolling, isScrolling;
	private int minDistanceToEnableScrolling;

	private int minDistanceToEnableDrawing;

	/*惯性滚动相关*/
	private boolean isInertiaEnabled, isValidForInertia;
	private NSTimer inertiaTimer;
	private float inertiaInterval;
	private float inertiaStepValue;

	/*抖动感应相关，目前未应用*/
	private boolean isResetEnabled, isShakeResetEnabled;
	private PLShakeData shakeData;
	private float shakeThreshold;

	private boolean isValidForTouch;

	private PLViewEventListener listener;

	private int animationFrameInterval;
	private boolean isAnimating;

	/*alpha变化相关*/
	private boolean isValidForTransition;
	private PLITransition currentTransition;

	private PLTouchStatus touchStatus;

	private boolean isPointerVisible;

	private boolean isBlocked;

	private boolean isRendererCreated;

	private int numberOfTouchesForReset;
	
	/** init methods */

	protected void initializeValues() {
		animationInterval = PLConstants.kDefaultAnimationTimerInterval;
		animationFrameInterval = PLConstants.kDefaultAnimationFrameInterval;

		isAccelerometerEnabled = false;
		isAccelerometerLeftRightEnabled = true;
		isAccelerometerUpDownEnabled = false;
		accelerometerSensitivity = PLConstants.kDefaultAccelerometerSensitivity;
//		accelerometerInterval = PLConstants.kDefaultAccelerometerInterval;
//
//		sensorialRotationType = PLSensorType.PLSensorTypeUnknow;

		isScrollingEnabled = true;
		minDistanceToEnableScrolling = PLConstants.kDefaultMinDistanceToEnableScrolling;

		minDistanceToEnableDrawing = PLConstants.kDefaultMinDistanceToEnableDrawing;

		isInertiaEnabled = true;
		inertiaInterval = PLConstants.kDefaultInertiaInterval;

		isValidForTouch = false;

		isResetEnabled = isShakeResetEnabled = true;
		numberOfTouchesForReset = PLConstants.kDefaultNumberOfTouchesForReset;

		shakeData = PLShakeData.PLShakeDataMake(0);
		shakeThreshold = PLConstants.kShakeThreshold;

		touchStatus = PLTouchStatus.PLTouchStatusNone;

		isPointerVisible = false;

		isValidForTransition = false;

		isRendererCreated = false;

		startPoint = CGPoint.CGPointMake(0.0f, 0.0f);
		endPoint = CGPoint.CGPointMake(0.0f, 0.0f);
		tempPoint = CGPoint.CGPointMake(0.0f, 0.0f);
		
		lastDrawViewStartPoint = new CGPoint(0.0f,0.0f);
		lastDrawViewEndPoint = new CGPoint(0.0f,0.0f);
		
		touchDownPoint = CGPoint.CGPointMake(0.0f, 0.0f);
		touchUpPoint = CGPoint.CGPointMake(0.0f, 0.0f);

		startFovPoint = CGPoint.CGPointMake(0.0f, 0.0f);
		endFovPoint = CGPoint.CGPointMake(0.0f, 0.0f);

		currentDeviceOrientation = UIDeviceOrientation.UIDeviceOrientationPortrait;

		oldPitch = 0.0f;
		oldYaw = 0.0f;
		
		this.reset();
	}

	/** reset methods */

	@Override
	public void reset() {
		this.resetWithoutAlpha();
		this.resetSceneAlpha();
	}

	@Override
	public void resetWithoutAlpha() {
		this.stopAnimationInternally(false);
		isValidForFov = isValidForScrolling = isScrolling = isValidForInertia = false;
		startPoint.setValues(0.0f, 0.0f);
		endPoint.setValues(0.0f, 0.0f);
		tempPoint.setValues(0.0f, 0.0f);
		lastDrawViewStartPoint.setValues(0.0f,0.0f);
		lastDrawViewEndPoint.setValues(0.0f,0.0f);
		touchDownPoint.setValues(0.0f, 0.0f);
		touchUpPoint.setValues(0.0f, 0.0f);
		startFovPoint.setValues(0.0f, 0.0f);
		endFovPoint.setValues(0.0f, 0.0f);
		fovCounter = 0;
		fovDistance = 0.0f;
		if (scene != null && scene.getCurrentCamera() != null)
			scene.getCurrentCamera().reset();
		if (this.isValidForTransition()) {
			if (currentTransition != null)
				currentTransition.stop();
			else
				this.setValidForTransition(false);
		}
	}

	@Override
	public void resetSceneAlpha() {
		if (scene != null)
			scene.resetAlpha();
	}

	/** property methods */
	
	@Override
	public PLIPanorama getPanorama() {
		return scene;
	}

	@Override
	public void setPanorama(PLIPanorama panorama) {
		if (panorama != null) {
			synchronized (this) {
				if (isRunningSensorialRotation)
					this.stopSensorialRotation();
				this.stopAnimationInternally(true);
				if (renderer != null)
					renderer.stop();
				scene = panorama;
				if (renderer != null) {
					renderer.setScene(scene);
					renderer.resizeFromLayer();
					renderer.start();
					this.startAnimation();
					this.drawViewInternally();
				} else {
					renderer = PLRenderer.rendererWithView(this, scene);
					renderer.setListener(new PLRendererListener() {
						@Override
						public void rendererFirstChanged(GL10 gl,
								PLIRenderer render, int width, int height) {
							currentGL = gl;
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									onGLContextCreated(currentGL);
								}
							});
						}

						@Override
						public void rendererDestroyed(PLIRenderer render) {
						}

						@Override
						public void rendererCreated(PLIRenderer render) {
						}

						@Override
						public void rendererChanged(PLIRenderer render,
								int width, int height) {
							if (!isRendererCreated) {
								isRendererCreated = true;
								startAnimation();
							}
							drawViewInternally();
						}
					});
					surfaceView = new PLSurfaceView(this, renderer);
					this.addContentView(surfaceView, new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT));
				}
			}
		} else {
			synchronized (this) {
				if (renderer != null) {
					renderer.stop();
					renderer.setScene(null);
				}
				scene = null;
			}
		}
	}

	protected PLIScene getScene() {
		return scene;
	}

	protected PLIRenderer getRenderer() {
		return renderer;
	}

	protected NSTimer getAnimationTimer() {
		return animationTimer;
	}

	protected void setAnimationTimer(NSTimer timer) {
		if (animationTimer != null) {
			animationTimer.invalidate();
			animationTimer = null;
		}
		animationTimer = timer;
	}

	@Override
	public float getAnimationInterval() {
		return animationInterval;
	}

	@Override
	public void setAnimationInterval(float interval) {
		animationInterval = interval;
	}

	@Override
	public int getAnimationFrameInterval() {
		return animationFrameInterval;
	}

	@Override
	public void setAnimationFrameInterval(int interval) {
		if (interval >= 1) {
			animationFrameInterval = interval;
			animationInterval = PLConstants.kDefaultAnimationTimerInterval
					* interval;
		}
	}

	@Override
	public boolean isAnimating() {
		return isAnimating;
	}

//	@Override
//	public float getAccelerometerInterval() {
//		return accelerometerInterval;
//	}
//
//	@Override
//	public void setAccelerometerInterval(float interval) {
//		accelerometerInterval = interval;
//		this.activateAccelerometer();
//	}
//
	@Override
	public float getAccelerometerSensitivity() {
		return accelerometerSensitivity;
	}
//
	@Override
	public void setAccelerometerSensitivity(float sensitivity) {
		accelerometerSensitivity = PLMath.valueInRange(sensitivity, PLRange
				.PLRangeMake(PLConstants.kAccelerometerSensitivityMinValue,
						PLConstants.kAccelerometerSensitivityMaxValue));
	}

	@Override
	public boolean isBlocked() {
		return isBlocked;
	}

	@Override
	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	protected PLITransition getCurrentTransition() {
		return currentTransition;
	}

	protected void setCurrentTransition(PLITransition transition) {
		this.currentTransition = transition;
	}

	@Override
	public boolean isValidForFov() {
		return isValidForFov;
	}

	protected void setValidForFov(boolean isValidForFov) {
		this.isValidForFov = isValidForFov;
	}

	@Override
	public boolean isValidForInertia() {
		return isValidForInertia;
	}

	protected void setValidForInertia(boolean isValidForInertia) {
		this.isValidForInertia = isValidForInertia;
	}

	@Override
	public float getShakeThreshold() {
		return shakeThreshold;
	}

	@Override
	public void setShakeThreshold(float shakeThreshold) {
		if (shakeThreshold > 0.0f)
			this.shakeThreshold = shakeThreshold;
	}

	@Override
	public boolean isValidForTransition() {
		return isValidForTransition;
	}

	protected synchronized void setValidForTransition(
			boolean isValidForTransition) {
		this.isValidForTransition = isValidForTransition;
	}

	@Override
	public PLTouchStatus getTouchStatus() {
		return touchStatus;
	}

	protected void setTouchStatus(PLTouchStatus touchStatus) {
		this.touchStatus = touchStatus;
	}

	@Override
	public boolean isPointerVisible() {
		return isPointerVisible;
	}

	@Override
	public void setPointerVisible(boolean isPointerVisible) {
		this.isPointerVisible = isPointerVisible;
	}

	@Override
	public boolean isAccelerometerEnabled() {
		return isAccelerometerEnabled;
	}

	@Override
	public void setAccelerometerEnabled(boolean isAccelerometerEnabled) {
		this.isAccelerometerEnabled = isAccelerometerEnabled;
	}

	@Override
	public boolean isAccelerometerLeftRightEnabled() {
		return isAccelerometerLeftRightEnabled;
	}

	@Override
	public void setAccelerometerLeftRightEnabled(
			boolean isAccelerometerLeftRightEnabled) {
		this.isAccelerometerLeftRightEnabled = isAccelerometerLeftRightEnabled;
	}

	@Override
	public boolean isAccelerometerUpDownEnabled() {
		return isAccelerometerUpDownEnabled;
	}

	@Override
	public void setAccelerometerUpDownEnabled(
			boolean isAccelerometerUpDownEnabled) {
		this.isAccelerometerUpDownEnabled = isAccelerometerUpDownEnabled;
	}

	@Override
	public CGPoint getStartPoint() {
		return startPoint;
	}

	@Override
	public void setStartPoint(CGPoint startPoint) {
		if (startPoint != null)
			this.startPoint.setValues(startPoint);
	}

	@Override
	public CGPoint getEndPoint() {
		return endPoint;
	}

	@Override
	public void setEndPoint(CGPoint endPoint) {
		if (endPoint != null)
			this.endPoint.setValues(endPoint);
	}

	@Override
	public boolean isScrollingEnabled() {
		return isScrollingEnabled;
	}

	@Override
	public void setScrollingEnabled(boolean isScrollingEnabled) {
		this.isScrollingEnabled = isScrollingEnabled;
	}

	@Override
	public int getMinDistanceToEnableScrolling() {
		return minDistanceToEnableScrolling;
	}

	@Override
	public void setMinDistanceToEnableScrolling(int minDistanceToEnableScrolling) {
		this.minDistanceToEnableScrolling = minDistanceToEnableScrolling;
	}

	@Override
	public int getMinDistanceToEnableDrawing() {
		return minDistanceToEnableDrawing;
	}

	@Override
	public void setMinDistanceToEnableDrawing(int minDistanceToEnableDrawing) {
		if (minDistanceToEnableDrawing > 0)
			this.minDistanceToEnableDrawing = minDistanceToEnableDrawing;
	}

	@Override
	public boolean isInertiaEnabled() {
		return isInertiaEnabled;
	}

	@Override
	public void setInertiaEnabled(boolean isInertiaEnabled) {
		this.isInertiaEnabled = isInertiaEnabled;
	}

	@Override
	public float getInertiaInterval() {
		return inertiaInterval;
	}

	@Override
	public void setInertiaInterval(float inertiaInterval) {
		this.inertiaInterval = inertiaInterval;
	}

	@Override
	public boolean isResetEnabled() {
		return isResetEnabled;
	}

	@Override
	public void setResetEnabled(boolean isResetEnabled) {
		this.isResetEnabled = isResetEnabled;
	}

	@Override
	public boolean isShakeResetEnabled() {
		return isShakeResetEnabled;
	}

	@Override
	public void setShakeResetEnabled(boolean isShakeResetEnabled) {
		this.isShakeResetEnabled = isShakeResetEnabled;
	}

	@Override
	public boolean isRendererCreated() {
		return isRendererCreated;
	}

	protected void setCameraDelegate() {
		PLCamera camera = scene.getCurrentCamera();
		if (camera.getListener() == null) {
			camera.setListener(new PLCameraListener() {
				@Override
				public void didReset(PLCamera camera) {
					if (listener != null)
						listener.onDidCameraReset(camera);
				}

				@Override
				public void didRotate(PLCamera camera, float pitch, float yaw,
						float roll) {
					if (listener != null)
						listener.onDidCameraRotate(camera, pitch, yaw, roll);
				}

				@Override
				public void didLookAt(PLCamera camera, float pitch, float yaw,
						float realPitch, float realYaw) {
					if (listener != null)
						listener.onDidCameraLookAt(camera, pitch, yaw,
								realPitch, realYaw);
				}

				@Override
				public void didFovDistance(PLCamera camera, float fov) {
					if (listener != null)
						listener.onDidCameraFovDistance(camera, fov);
				}
			});
		}
	}

	@Override
	public PLCamera getCamera() {
		if (scene != null) {
			this.setCameraDelegate();
			return scene.getCurrentCamera();
		}
		return null;
	}

	@Override
	public void setCamera(PLCamera camera) {
		if (scene != null && camera != null) {
			scene.removeCameraAtIndex(0);
			scene.addCamera(camera, 0);
			scene.setCameraIndex(0);
			this.setCameraDelegate();
		}
	}

	@Override
	public float getSceneAlpha() {
		return (scene != null ? scene.getAlpha() : -1.0f);
	}

	@Override
	public void setSceneAlpha(float alpha) {
		if (scene != null && alpha >= 0.0f)
			scene.setAlpha(alpha);
	}

	@Override
	public int getNumberOfTouchesForReset() {
		return numberOfTouchesForReset;
	}

	@Override
	public void setNumberOfTouchesForReset(int value) {
		if (value > 2 && value <= kMaxTouches)
			numberOfTouchesForReset = value;
	}

	@Override
	public boolean isRunningSensorialRotation() {
		return isRunningSensorialRotation;
	}

//	protected PLSensorType getSensorialRotationType() {
//		return sensorialRotationType;
//	}

	@Override
	public UIDeviceOrientation getCurrentDeviceOrientation() {
		return currentDeviceOrientation;
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public PLViewEventListener getListener() {
		return listener;
	}

	@Override
	public void setListener(PLViewEventListener listener) {
		this.listener = listener;
	}

	/** draw methods */

	@Override
	public void drawView() {
		if (isScrolling && listener != null
				&& !listener.onShouldScroll(this, startPoint, endPoint)){
			return;
		}
		this.drawViewInternally();
		if (isScrolling && listener != null)
			listener.onDidScroll(this, startPoint, endPoint);
	}

	@Override
	public void drawViewNTimes(int times) {
		for (int i = 0; i < times; i++)
			this.drawView();
	}

	protected void drawViewInternally() {
		if (isRendererCreated) {
			if (scene != null) {
				if (!isValidForFov
						&& !isRunningSensorialRotation
						&& PLMath.distanceBetweenPoints(startPoint, endPoint) > PLConstants.kDefaultMinDistanceToEnable){
					//如果在进行惯性动画，或者起始点有变化，则变换camera。此条件用于防止停止move但未up时，重复移动camera的问题
					if(inertiaTimer != null || !startPoint.equals(lastDrawViewStartPoint) || !endPoint.equals(lastDrawViewEndPoint)){
						scene.getCurrentCamera().rotateWith(startPoint, endPoint);
						lastDrawViewStartPoint.setValues(startPoint);
						lastDrawViewEndPoint.setValues(endPoint);
					}
				}
				if (renderer != null && surfaceView != null)
					surfaceView.requestRender();
			}
		}
	}

	protected void drawViewInternallyNTimes(int times) {
		for (int i = 0; i < times; i++)
			this.drawViewInternally();
	}

	/** animation methods */

	@Override
	public void startAnimation() {
		if (!isAnimating) {
			if (animationTimer == null) {
				this.setAnimationTimer(NSTimer.scheduledTimerWithTimeInterval(
						animationInterval, new NSTimer.Runnable() {
							@Override
							public void run(NSTimer target, Object[] userInfo) {
								drawView();
							}
						}, null, true));
			}
			if (isScrollingEnabled){
				isValidForScrolling = true;
			}
			this.stopInertia();

			isAnimating = true;
		}
	}

	@Override
	public void stopAnimation() {
		if (isAnimating)
			this.stopAnimationInternally(true);
		else if (animationTimer != null)
			this.setAnimationTimer(null);
	}

	protected void stopAnimationInternally(boolean stopAnimationTimer) {
		if (isAnimating) {
			isAnimating = false;
			if (stopAnimationTimer)
				this.setAnimationTimer(null);
			this.stopAnimationInternals();
		}
		this.stopInertia();
	}

	protected void stopAnimationInternals() {
		if (isScrollingEnabled) {
			isValidForScrolling = false;
			if (!isInertiaEnabled)
				isValidForTouch = false;
		} else
			isValidForTouch = false;
	}

	/** fov methods */

	protected boolean calculateFov(List<UITouch> touches) {
		if (touches.size() == 2) {
			startFovPoint.setValues(touches.get(0).locationInView(this));
			endFovPoint.setValues(touches.get(1).locationInView(this));

			fovCounter++;
			if (fovCounter < PLConstants.kDefaultFovMinCounter) {
				fovDistance = PLMath.distanceBetweenPoints(startFovPoint,
						endFovPoint);
				return false;
			}

			float distance = PLMath.distanceBetweenPoints(startFovPoint,
					endFovPoint);

			if (Math.abs(distance - fovDistance) < scene.getCurrentCamera()
					.getMinDistanceToEnableFov())
				return false;

			distance = (Math.abs(fovDistance) <= distance ? distance
					: -distance);
			boolean isZoomIn = (distance >= 0);
			boolean isNotCancelable = true;

			if (listener != null)
				isNotCancelable = listener.onShouldRunZooming(this, distance,
						isZoomIn, !isZoomIn);

//			isNotCancelable = false; // forbid zoom
			if (isNotCancelable) {
				fovDistance = distance;
				scene.getCurrentCamera().addFovWithDistance(fovDistance);
				if (listener != null)
					listener.onDidRunZooming(this, fovDistance, isZoomIn,
							!isZoomIn);
				return true;
			}
		}
		return false;
	}

	/** action methods */

	protected boolean executeDefaultAction(List<UITouch> touches,
			PLTouchEventType eventType) {
		int touchCount = touches.size();
		if (touchCount == numberOfTouchesForReset) {
			if (!isRunningSensorialRotation)
				this.executeResetAction(touches);
		} else if (touchCount == 2) {
			boolean isNotCancelable = true;
			if (listener != null)
				isNotCancelable = listener.onShouldBeginZooming(this);
			if (isNotCancelable) {
				if (!isValidForFov) {
					this.startAnimation();
					fovCounter = 0;
					isValidForFov = true;
				}
				if (eventType == PLTouchEventType.PLTouchEventTypeMoved)
					this.calculateFov(touches);
				else if (eventType == PLTouchEventType.PLTouchEventTypeBegan) {
					startFovPoint
							.setValues(touches.get(0).locationInView(this));
					endFovPoint.setValues(touches.get(1).locationInView(this));
					if (listener != null)
						listener.onDidBeginZooming(this, startFovPoint,
								endFovPoint);
				}
			}
		} else if (touchCount == 1 && !isValidForFov) {
			if (eventType == PLTouchEventType.PLTouchEventTypeMoved) {
				// if(isValidForFov || (startPoint.x == 0.0f && endPoint.y ==
				// 0.0f))
				//startPoint.setValues(tempPoint);
				//将startPoint修改为取倒数第三个move点，tempPoint为倒数第二个点，endPoint为最后一个点
				if(tempPoint != startPoint && tempPoint != endPoint){
					startPoint.setValues(tempPoint);
				}
				if (animationTimer == null){
					this.startAnimation();
				}
			} else if (eventType == PLTouchEventType.PLTouchEventTypeEnded
					&& startPoint.x == 0.0f && startPoint.y == 0.0f){
				startPoint.setValues(tempPoint);
				return true;
			}
			isValidForFov = false;
			return false;
		}
		return true;
	}
	
	protected boolean executeResetAction(List<UITouch> touches) {
		if (isResetEnabled && touches.size() >= numberOfTouchesForReset) {
			boolean isNotCancelable = true;
			if (listener != null)
				isNotCancelable = listener.onShouldReset(this);
			if (isNotCancelable) {
				this.stopAnimationInternally(false);
				this.reset();
				this.drawViewInternally();
				if (listener != null)
					listener.onDidReset(this);
				return true;
			}
		}
		return false;
	}

	/** touch methods */

	protected boolean isTouchInView(List<UITouch> touches) {
		int touchesLength = touches.size();
		for (int i = 0; i < touchesLength; i++)
			if (touches.get(i).getView() != this)
				return false;
		return true;
	}

	protected CGPoint getLocationOfFirstTouch(List<UITouch> touches) {
		return touches.get(0).locationInView(this);
	}

	protected void touchesBegan(List<UITouch> touches, MotionEvent event) {
		boolean listenerExists = (listener != null);

		if (listenerExists)
			listener.onTouchesBegan(this, touches, event);

		if (isBlocked
				|| scene == null
				|| this.isValidForTransition()
				|| !this.isTouchInView(touches)
				|| (listenerExists && !listener.onShouldBeginTouching(this,
						touches, event)))
			return;

		isRunningSensorialRotation = false;
		switch (touches.get(0).getTapCount()) {
		case 1:
			touchStatus = PLTouchStatus.PLTouchStatusSingleTapCount;
			if (isValidForScrolling) {
				this.stopAnimationInternally(false);
				startPoint.setValues(endPoint);

				isScrolling = false;
				if (listenerExists)
					listener.onDidEndScrolling(this, startPoint, endPoint);
			} else if (inertiaTimer != null) {
				this.stopAnimationInternally(false);
				startPoint.setValues(endPoint);
				if (listenerExists)
					listener.onDidEndInertia(this, startPoint, endPoint);
			}
			break;
		case 2:
			touchStatus = PLTouchStatus.PLTouchStatusDoubleTapCount;
			break;
		}

		isValidForTouch = true;
		touchStatus = PLTouchStatus.PLTouchStatusBegan;

		if (!this.executeDefaultAction(touches,
				PLTouchEventType.PLTouchEventTypeBegan)) {
			startPoint.setValues(this.getLocationOfFirstTouch(touches));
			endPoint.setValues(startPoint);
			tempPoint.setValues(startPoint);
			touchDownPoint.setValues(startPoint);
			if (touches.get(0).getTapCount() == 1) {
				touchStatus = PLTouchStatus.PLTouchStatusFirstSingleTapCount;
				if (renderer != null && surfaceView != null) {
					renderer.setWaitingForClick(true);
					surfaceView.requestRender();
				}
				touchStatus = PLTouchStatus.PLTouchStatusSingleTapCount;
			}
			this.startAnimation();
		} else if (isRunningSensorialRotation)
			this.startAnimation();

		if (listenerExists)
			listener.onDidBeginTouching(this, touches, event);
	}

	protected void touchesMoved(List<UITouch> touches, MotionEvent event) {
		boolean listenerExists = (listener != null);

		if (listenerExists)
			listener.onTouchesMoved(this, touches, event);

		if (isBlocked
				|| scene == null
				|| this.isValidForTransition()
				|| !this.isTouchInView(touches)
				|| (listenerExists && !listener.onShouldTouch(this, touches,
						event)))
			return;

		touchStatus = PLTouchStatus.PLTouchStatusMoved;

		if (!this.executeDefaultAction(touches,
				PLTouchEventType.PLTouchEventTypeMoved)) {
			//endPoint.setValues(this.getLocationOfFirstTouch(touches));
			//tempPoint.setValues(endPoint);
			//修改为tempPoint为倒数第二个点，endPoint为最后一个点
			tempPoint.setValues(endPoint);
			endPoint.setValues(this.getLocationOfFirstTouch(touches));
		}

		if (listenerExists)
			listener.onDidTouch(this, touches, event);
	}

	protected void touchesEnded(List<UITouch> touches, MotionEvent event) {
		boolean listenerExists = (listener != null);
		if (listenerExists)
			listener.onTouchesEnded(this, touches, event);

		if (isBlocked
				|| scene == null
				|| this.isValidForTransition()
				|| !this.isTouchInView(touches)
				|| (listenerExists && !listener.onShouldEndTouching(this,
						touches, event)))
			return;

		touchStatus = PLTouchStatus.PLTouchStatusEnded;

		if (isValidForFov) {
			startPoint.setValues(0.0f, 0.0f);
			endPoint.setValues(0.0f, 0.0f);
			isValidForFov = isValidForTouch = false;
			if (!isRunningSensorialRotation){
				this.stopAnimationInternally(false);
			}
		} else if (!startPoint.equals(endPoint)) {
			if (!this.executeDefaultAction(touches,
					PLTouchEventType.PLTouchEventTypeEnded)) {
//				CGPoint tempPoint = this.getLocationOfFirstTouch(touches);
//				if (PLMath.distanceBetweenPoints(startPoint, tempPoint) >= minDistanceToEnableDrawing)
//					endPoint.setValues(tempPoint);
//				else
//					endPoint.setValues(startPoint);
				//修改为取最后两段距离中最长的，因为最后一段距离可能未真实反映滑动的速度
				CGPoint lastPoint = this.getLocationOfFirstTouch(touches);
				float distance1 = PLMath.distanceBetweenPoints(startPoint, tempPoint);
				float distance2 = PLMath.distanceBetweenPoints(tempPoint, lastPoint);
				if (Math.max(distance1, distance2) >= minDistanceToEnableScrolling){
					if(distance1 > distance2){
						endPoint.setValues(tempPoint);
					}else{
						startPoint.setValues(tempPoint);
						endPoint.setValues(lastPoint);
					}
				}
				else{
					endPoint.setValues(startPoint);
				}

				boolean isNotCancelable = true;

				if (isScrollingEnabled && listenerExists){
					isNotCancelable = listener.onShouldBeingScrolling(this,
							startPoint, endPoint);
				}
				if (isScrollingEnabled && isNotCancelable) {
					boolean isValidForMove = (PLMath.distanceBetweenPoints(
							startPoint, endPoint) <= minDistanceToEnableScrolling);
					if (isInertiaEnabled) {
						this.stopAnimationInternals();
						if (isValidForMove){
							isValidForTouch = false;
						}else {
							isNotCancelable = true;
							if (listenerExists){
								isNotCancelable = listener
										.onShouldBeginInertia(this, startPoint,
												endPoint);
							}
							if (isNotCancelable){
								this.startInertia();
							}
						}
					} else {
						if (isValidForMove){
							this.stopAnimationInternals();
						}else {
							isScrolling = true;
							if (listenerExists){
								listener.onDidBeginScrolling(this, startPoint,
										endPoint);
							}
						}
					}
				} else {
					startPoint.setValues(endPoint);
					if (!isRunningSensorialRotation)
						this.stopAnimationInternally(false);
//					this.startInertia();
					if (listenerExists){
						listener.onDidEndMoving(this, startPoint, endPoint);
					}
				}
			}
		}

		if (inertiaTimer == null) {
			setCanSensorialRotation();
		}

		touchUpPoint.setValues(endPoint);
		if (PLMath.distanceBetweenPoints(touchDownPoint, touchUpPoint) < PLConstants.kDefaultMinDistanceToEnable){
			listener.onDidClick(this, startPoint, startPoint);
		}

		if (listenerExists){
			listener.onDidEndTouching(this, touches, event);
		}
	}

	private void setCanSensorialRotation() {
		isRunningSensorialRotation = true;
	}

	/** inertia methods */

	protected void startInertia() {
		if (isBlocked
				|| scene == null
				|| isRunningSensorialRotation
				|| this.isValidForTransition()
				|| (listener != null && !listener.onShouldRunInertia(this,
						startPoint, endPoint)) || startPoint.equals(endPoint)){
			return;
		}
		this.stopAnimationInternally(false);
		float interval = inertiaInterval
				/ PLMath.distanceBetweenPoints(startPoint, endPoint);
		if (interval < 0.01f) {
			inertiaStepValue = 0.01f / interval;
			interval = 0.01f;
		} else
			inertiaStepValue = 10.0f;
		inertiaTimer = NSTimer.scheduledTimerWithTimeInterval(interval,
				new NSTimer.Runnable() {
					@Override
					public void run(NSTimer target, Object[] userInfo) {
						inertia();
					}
				}, null, true);

		if (listener != null)
			listener.onDidBeginInertia(this, startPoint, endPoint);
	}

	protected void stopInertia() {
		if (inertiaTimer != null) {
			inertiaTimer.invalidate();
			inertiaTimer = null;
			touchStatus = PLTouchStatus.PLTouchStatusNone;
		}
	}

	protected void inertia() {
		if (isBlocked || scene == null || this.isValidForTransition())
			return;

		float m = (endPoint.y - startPoint.y) / (endPoint.x - startPoint.x);
		float b = (startPoint.y * endPoint.x - endPoint.y * startPoint.x)
				/ (endPoint.x - startPoint.x);
		float x, y, add;

		if (Math.abs(endPoint.x - startPoint.x) >= Math.abs(endPoint.y
				- startPoint.y)) {
			add = (endPoint.x > startPoint.x ? -inertiaStepValue
					: inertiaStepValue);
			x = endPoint.x + add;
			if ((add > 0.0f && x > startPoint.x)
					|| (add <= 0.0f && x < startPoint.x)) {
				this.stopInertia();
				startPoint.setValues(endPoint);
				isValidForTouch = false;

				if (listener != null)
					listener.onDidEndInertia(this, startPoint, endPoint);
				this.startAnimation();

				setCanSensorialRotation();

				return;
			}
			y = m * x + b;
		} else {
			add = (endPoint.y > startPoint.y ? -inertiaStepValue
					: inertiaStepValue);
			y = endPoint.y + add;
			if ((add > 0.0f && y > startPoint.y)
					|| (add <= 0.0f && y < startPoint.y)) {
				this.stopInertia();
				startPoint.setValues(endPoint);
				isValidForTouch = false;

				if (listener != null)
					listener.onDidEndInertia(this, startPoint, endPoint);
				this.startAnimation();

				setCanSensorialRotation();
				return;
			}
			x = (y - b) / m;
		}
		endPoint.setValues(x, y);
		this.drawView();

		if (listener != null)
			listener.onDidRunInertia(this, startPoint, endPoint);
	}

	/** accelerometer methods */

//	protected void activateAccelerometer() {
//		if (sensorManager != null
//				&& !sensorManager.registerListener(this, sensorManager
//						.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//						(int) (accelerometerInterval * 1000.0f)))
//			PLLog.debug("PLViewBase::activateAccelerometer",
//					"Accelerometer sensor, not available on the device!");
//	}

//	protected void deactiveAccelerometer() {
//		if (sensorManager != null)
//			sensorManager.unregisterListener(this,
//					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
//	}

	protected void accelerometer(SensorEvent event, UIAcceleration acceleration) {
		if (isBlocked || isRunningSensorialRotation || scene == null
				|| !isRendererCreated || this.isValidForTransition()
				|| this.resetWithShake(acceleration) || isValidForTouch)
			return;

		if (isAccelerometerEnabled) {
			if (listener != null
					&& !listener.onShouldAccelerate(this, acceleration, event))
				return;

			float x = 0, y = 0;
			float factor = PLConstants.kAccelerometerMultiplyFactor
					* accelerometerSensitivity;
			CGSize size = this.getSize();

			UIDeviceOrientation currentOrientation = this
					.getCurrentDeviceOrientation();
			switch (currentOrientation) {
			case UIDeviceOrientationUnknown:
			case UIDeviceOrientationPortrait:
			case UIDeviceOrientationPortraitUpsideDown:
				x = (isAccelerometerLeftRightEnabled ? acceleration.x : 0.0f);
				y = (isAccelerometerUpDownEnabled ? acceleration.z : 0.0f);
				startPoint.setValues(size.width / 2.0f, size.height / 2.0f);
				if (currentOrientation == UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown) {
					x = -x;
					y = -y;
				}
				break;
			case UIDeviceOrientationLandscapeLeft:
			case UIDeviceOrientationLandscapeRight:
				x = (isAccelerometerLeftRightEnabled ? acceleration.y : 0.0f);
				y = (isAccelerometerUpDownEnabled ? acceleration.z : 0.0f);
				startPoint.setValues(size.height / 2.0f, size.width / 2.0f);
				if (currentOrientation == UIDeviceOrientation.UIDeviceOrientationLandscapeRight) {
					x = -x;
					y = -y;
				}
				break;
			default:
				startPoint.setValues(size.width / 2.0f, size.height / 2.0f);
				break;
			}

			endPoint.setValues(startPoint.x + (x * factor), startPoint.y
					+ (y * factor));

			if (listener != null)
				listener.onDidAccelerate(this, acceleration, event);
		}
	}

	/** sensorial rotation methods */

	@Override
	public void startSensorialRotation() {
		if (!isRunningSensorialRotation) {
			boolean startSensorialRotation = false;
			startSensorialRotation = HLSensorManager.getInstance(this).registrySensor(HLSensorTypeEnum.SENSOR_TYPE_SCROLL, mScrollSensorListener);
			if (startSensorialRotation) {
				isRunningSensorialRotation = true;
				if (inertiaTimer != null)
					this.stopAnimationInternally(false);
				else
					this.stopAnimationInternals();
				startPoint.setValues(0.0f, 0.0f);
				endPoint.setValues(0.0f, 0.0f);
				this.startAnimation();
			}
		}
	}

	@Override
	public void stopSensorialRotation() {
		if (isRunningSensorialRotation) {
			isRunningSensorialRotation = false;
			HLSensorManager.getInstance(this).unregistrySensor(HLSensorTypeEnum.SENSOR_TYPE_SCROLL, mScrollSensorListener);
			this.stopAnimationInternally(false);
			startPoint.setValues(endPoint.setValues(0.0f, 0.0f));
		}
	}
	
	private HLSensorListener mScrollSensorListener = new HLSensorListener() {
		
		@Override
		public void onSensorRawDataListener(SensorEvent event) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void OnScrollSensorListener(float pitch, float yaw,
				float timeDiffPitch, float timeDiffYaw) {
			if (!isBlocked && scene != null && !isValidForTransition()) {
				//本地转化
				float diffPitch = pitch - oldPitch;
				float diffYaw = yaw - oldYaw;
				//记录
				oldPitch = pitch;
				oldYaw = yaw;
				if (isRunningSensorialRotation) {
					scene.getCurrentCamera().lookAt(
							-scene.getCurrentCamera().getPitch() + diffPitch
									- timeDiffPitch,
							-scene.getCurrentCamera().getYaw() + diffYaw
									- timeDiffYaw);
				}
			}
		}

		@Override
		public int providerSurfaceOrientation() {
			return currentDeviceSurfaceOrientation;
		}
	};

	/** shake methods */

	protected boolean resetWithShake(UIAcceleration acceleration) {
		if (!isShakeResetEnabled || !isResetEnabled || isBlocked
				|| scene == null)
			return false;

		boolean result = false;
		long currentTime = System.currentTimeMillis();

		if ((currentTime - shakeData.lastTime) > PLConstants.kShakeDiffTime) {
			long diffTime = (currentTime - shakeData.lastTime);
			shakeData.lastTime = currentTime;

			shakeData.shakePosition.setValues(acceleration);

			float speed = Math.abs(shakeData.shakePosition.x
					+ shakeData.shakePosition.y + shakeData.shakePosition.z
					- shakeData.shakeLastPosition.x
					- shakeData.shakeLastPosition.y
					- shakeData.shakeLastPosition.z)
					/ diffTime * 10000;
			if (speed > shakeThreshold) {
				this.reset();
				this.drawViewInternally();
				result = true;
			}

			shakeData.shakeLastPosition.setValues(shakeData.shakePosition);
		}
		return result;
	}

	/** transition methods */

	@Override
	public boolean executeTransition(PLITransition transition) {
		if (scene == null || renderer == null || transition == null
				|| this.isValidForTransition())
			return false;

		this.setValidForTransition(true);
		isValidForTouch = false;

		startPoint.setValues(0.0f, 0.0f);
		endPoint.setValues(0.0f, 0.0f);

		currentTransition = transition;
		currentTransition.setListener(new PLTransitionListener() {
			@Override
			public void didBeginTransition(PLITransition transition,
					PLTransitionType type) {
				if (listener != null)
					listener.onDidBeginTransition(PLViewBase.this, transition);
			}

			@Override
			public void didProcessTransition(PLITransition transition,
					PLTransitionType type, int progressPercentage) {

				if (listener != null)
					listener.onDidProcessTransition(PLViewBase.this,
							transition, progressPercentage);
			}

			@Override
			public void didEndTransition(PLITransition transition,
					PLTransitionType type) {
				PLViewBase.this.setValidForTransition(false);
				if (currentTransition != null) {
					currentTransition.releaseView();
					currentTransition = null;
				}
				if (listener != null)
					listener.onDidEndTransition(PLViewBase.this, transition);
			}
		});
		currentTransition.start(this, scene);

		return true;
	}

	/** dealloc methods */

	@Override
	protected void onDestroy() {
		HLSensorManager.getInstance(this).unregistrySensor(HLSensorTypeEnum.SENSOR_TYPE_SCROLL, mScrollSensorListener);
		
		this.stopAnimation();
		this.reset();

		if (renderer != null)
			renderer.stop();

		List<PLIReleaseView> releaseViewObjects = new ArrayList<PLIReleaseView>();
		releaseViewObjects.add(scene);
		releaseViewObjects.add(renderer);
		releaseViewObjects.add(currentTransition);
		releaseViewObjects.addAll(internalTouches);
		releaseViewObjects.addAll(currentTouches);

		for (PLIReleaseView releaseViewObject : releaseViewObjects)
			if (releaseViewObject != null)
				releaseViewObject.releaseView();

		releaseViewObjects.clear();

//		this.sensorManager = null;
		this.touchStatus = null;
		this.startPoint = this.endPoint = null;
		this.startFovPoint = this.endFovPoint = null;
		this.currentTransition = null;
		this.scene = null;
		this.surfaceView = null;
		this.renderer = null;
		this.shakeData = null;
		this.listener = null;
		this.currentGL = null;
		super.onDestroy();
	}

	public interface ICallBck{
		void callBack();
	}
	
	/** internal classes declaration */

	public class PLSurfaceView extends GLSurfaceView {
		/** init methods */

		public PLSurfaceView(Context context, GLSurfaceView.Renderer renderer) {
			super(context);
			this.setRenderer(renderer);
			this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			queueEvent(new Runnable() {
				@Override
				public void run() {
					if (scene != null)
						scene.clearPanorama(currentGL);
				}
			});
			super.surfaceDestroyed(holder);
		}

		public void clearPanorama() {
			queueEvent(new Runnable() {
				@Override
				public void run() {
					if (scene != null)
						scene.clearPanorama(currentGL);
				}
			});
		}
		
		public void clearPanorama(final ICallBck callBack) {
			queueEvent(new Runnable() {
				@Override
				public void run() {
					if (scene != null)
						scene.clearPanorama(currentGL);
					if(callBack!=null)
						callBack.callBack();
				}
			});
		}
	}

	/** android code */

	private static final int kMaxTouches = 10;

	private GL10 currentGL;
//	private SensorManager sensorManager;
	private PLSurfaceView surfaceView;
	private UIAcceleration tempAcceleration;
	private CGSize tempSize;
	private List<UITouch> internalTouches, currentTouches;
	private int[] location;

	@Override
	public GL10 getCurrentGL() {
		return currentGL;
	}

//	protected SensorManager getSensorManager() {
//		return sensorManager;
//	}

	public PLSurfaceView getSurfaceView() {
		return surfaceView;
	}

	@Override
	public CGSize getSize() {
		if (renderer != null && !renderer.getSize().isResetted())
			return tempSize.setValues(renderer.getSize());
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return tempSize.setValues(displayMetrics.widthPixels,
				displayMetrics.heightPixels);
	}

	protected List<UITouch> getTouches(MotionEvent event) {
		return this.getTouches(event, 1);
	}

	protected List<UITouch> getTouches(MotionEvent event, int tapCount) {
		this.getSurfaceView().getLocationOnScreen(location);
		int top = location[1], left = location[0];
		int count = Math.min(event.getPointerCount(), kMaxTouches);
		currentTouches.clear();
		for (int i = 0; i < count; i++) {
			UITouch touch = internalTouches.get(i);
			touch.locationInView(this).setValues(event.getX(i) - left,
					event.getY(i) - top);
			touch.setTapCount(tapCount);
			currentTouches.add(i, touch);
		}
		return currentTouches;
	}

	protected void onGLContextCreated(GL10 gl) {
	}

	/** android activity events methods */

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			location = new int[2];
			internalTouches = new ArrayList<UITouch>();
			currentTouches = new ArrayList<UITouch>();
			tempAcceleration = new UIAcceleration();
			tempSize = new CGSize();
			for (int i = 0; i < kMaxTouches; i++)
				internalTouches.add(new UITouch(this, new CGPoint(0.0f, 0.0f)));
//			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			this.initializeValues();
		} catch (Exception e) {
			PLLog.error("PLViewBase::onCreate", "Error: %s", e.getMessage());
			e.printStackTrace();
		}
		//get orientation
		WindowManager wm = PLViewBase.this.getWindowManager();
		currentDeviceSurfaceOrientation = wm.getDefaultDisplay().getRotation();
		switch (currentDeviceSurfaceOrientation) {
		case Surface.ROTATION_0:
			currentDeviceOrientation = UIDeviceOrientation.UIDeviceOrientationPortrait;
			break;
		case Surface.ROTATION_90:
			currentDeviceOrientation = UIDeviceOrientation.UIDeviceOrientationLandscapeLeft;
			break;
		case Surface.ROTATION_180:
			currentDeviceOrientation = UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown;
			break;
		case Surface.ROTATION_270:
			currentDeviceOrientation = UIDeviceOrientation.UIDeviceOrientationLandscapeRight;
			break;
		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isRunningSensorialRotation) {
			HLSensorManager.getInstance(this).registrySensor(HLSensorTypeEnum.SENSOR_TYPE_SCROLL, mScrollSensorListener);
		}
		if (isRendererCreated){
			this.startAnimation();
		}
	}

	@Override
	protected void onPause() {
		if (isRunningSensorialRotation) {
			HLSensorManager.getInstance(this).unregistrySensor(HLSensorTypeEnum.SENSOR_TYPE_SCROLL, mScrollSensorListener);
		}
		this.stopAnimation();
		super.onPause();
	}

	/** android touch event methods */

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRendererCreated) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				this.touchesBegan(this.getTouches(event), event);
				break;
			case MotionEvent.ACTION_MOVE:
				this.touchesMoved(this.getTouches(event), event);
				break;
			case MotionEvent.ACTION_UP:
				this.touchesEnded(this.getTouches(event), event);
				break;
			}
		}
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		if (isRendererCreated)
			this.touchesBegan(this.getTouches(event, 2), event);
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		if (isRendererCreated)
			this.touchesBegan(this.getTouches(event, 1), event);
		return false;
	}
}