package net.ccbluex.liquidbounce.utils.render.animations.impl;

import net.ccbluex.liquidbounce.utils.render.animations.Animation;
import net.ccbluex.liquidbounce.utils.render.animations.Direction;

public class SmoothStepAnimation extends Animation {

    public SmoothStepAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public SmoothStepAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        return -2 * Math.pow(x, 3) + (3 * Math.pow(x, 2));
    }

}
