package com.panoramagl.transitions;

import java.util.List;

import com.panoramagl.PLConstants;
import com.panoramagl.PLIScene;
import com.panoramagl.PLSceneElement;
import com.panoramagl.enumeration.PLSceneElementType;
import com.panoramagl.enumeration.PLTransitionType;
import com.panoramagl.hotspots.PLHotspot;
import com.panoramagl.structs.PLRange;

public class PLTransitionZoomBase extends PLTransition {
	/** member variables */

	private float zoomStep;

	/** init methods */

	protected PLTransitionZoomBase(float interval, PLTransitionType type) {
		super(interval, type);
	}

	@Override
	protected void initializeValues() {
		super.initializeValues();
		zoomStep = PLConstants.kDefaultStepZoomBlend;
	}
	
	/**property methods*/

	public float getZoomStep()
	{
		return zoomStep;
	}
	
	public void setZoomStep(float value)
	{
		if(value > 0.0f)
			zoomStep = value;
	}
	
	/**internal methods*/

	@Override
	protected void beginExecute()
	{
		PLIScene scene = this.getScene();
		float zoom = 0.0f;
		
		switch(this.getType())
		{
			case PLTransitionTypeZoomIn:
				zoom = 1.0f;
				break;
			case PLTransitionTypeZoomOut:
				zoom = -1.0f;
				break;
		}
		scene.getCurrentCamera().setFov(zoom);
		this.getView().drawView();
	}

	@Override
	protected boolean processInternally()
	{
		boolean isEnd = false;
		PLIScene scene = this.getScene();
		PLRange fovRange = scene.getCamera().getFovRange();
		float denominator = fovRange.max - fovRange.min;
		switch(this.getType())
		{
			case PLTransitionTypeZoomIn:
				scene.getCamera().setFov(Math.min(scene.getCamera().getFov() - zoomStep, scene.getCamera().getFovRange().min));
				this.setProgressPercentage((int)Math.min(((scene.getCamera().getFov() - fovRange.min)/denominator) * 100.0f, 100.0f));
				isEnd = scene.getCamera().getFov() <= scene.getCamera().getFovRange().min;
				break;
			case PLTransitionTypeZoomOut:
				scene.getCamera().setFov(scene.getCamera().getFov() + zoomStep);
				this.setProgressPercentage((int)Math.max(((fovRange.max - scene.getCamera().getFov())/denominator) * 100.0f, 0.0f));
				isEnd = (scene.getCamera().getFov() >= scene.getCamera().getFovRange().max);
				break;
		}
		if(isEnd)
		{
			List<PLSceneElement> elements = scene.getElements();
			int elementsLength = elements.size();
			for(int i = 0; i < elementsLength; i++)
			{
				PLSceneElement element = elements.get(i);
				if(element.getType() == PLSceneElementType.PLSceneElementTypeHotspot)
					((PLHotspot)element).touchUnblock();
			}
		}
		return isEnd;
	}
}
