package com.panoramagl.transitions;

import com.panoramagl.enumeration.PLTransitionType;

public class PLTransitionZoomIn extends PLTransitionZoomBase {

	/** init methods */
	
	protected PLTransitionZoomIn(float interval, PLTransitionType type) {
		super(interval, type);
	}

	public PLTransitionZoomIn(float interval) {
		super(interval, PLTransitionType.PLTransitionTypeZoomIn);
	}

	public static PLTransitionZoomIn transition(float interval) {
		return new PLTransitionZoomIn(interval);
	}

}
