package dev.fishmodpv.gui;

/**
 * A frame-rate-independent value that eases toward a target using exponential smoothing
 * ({@code current += (target - current) * (1 - e^(-speed*dt))}). Used for every animated bit of
 * chrome in the profile viewer (panel open transition, the sliding active-tab indicator, tab
 * hover glow) so motion stays smooth regardless of FPS, with no keyframes or easing-curve tables.
 */
public class SmoothValue {
	private float current;
	private float target;
	private final float speed;

	public SmoothValue(float initial, float speed) {
		this.current = initial;
		this.target = initial;
		this.speed = speed;
	}

	public void set(float target) {
		this.target = target;
	}

	public void snapTo(float value) {
		this.current = value;
		this.target = value;
	}

	public void update(float dtSeconds) {
		if (dtSeconds <= 0) {
			return;
		}
		float t = 1f - (float) Math.exp(-speed * dtSeconds);
		current += (target - current) * t;
	}

	public float get() {
		return current;
	}
}
