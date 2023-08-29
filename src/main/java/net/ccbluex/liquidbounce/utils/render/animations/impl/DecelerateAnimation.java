package net.ccbluex.liquidbounce.utils.render.animations.impl;


import net.ccbluex.liquidbounce.utils.render.animations.Animation;
import net.ccbluex.liquidbounce.utils.render.animations.Direction;

public class DecelerateAnimation extends Animation {

    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }


    protected double getEquation(double x) {
        return 1 - ((x - 1) * (x - 1));
    }
}
