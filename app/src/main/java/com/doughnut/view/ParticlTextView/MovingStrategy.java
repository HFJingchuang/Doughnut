package com.doughnut.view.ParticlTextView;

public abstract class MovingStrategy {
    public abstract void setMovingPath(Particle particle, int rangeWidth, int rangeHeight, double[] targetPosition);
}
