package com.panoramagl.transitions;

import com.panoramagl.enumeration.PLTransitionType;

public class PLTransitionZoomOut extends PLTransitionZoomBase{

	/** init methods */
	
	protected PLTransitionZoomOut(float interval, PLTransitionType type) {
		super(interval, type);
	}

	public PLTransitionZoomOut(float interval) {
		super(interval, PLTransitionType.PLTransitionTypeZoomOut);
	}

	public static PLTransitionZoomOut transition(float interval) {
		return new PLTransitionZoomOut(interval);
	}
}
