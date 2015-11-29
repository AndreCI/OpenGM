package ch.epfl.sweng.opengm.polls.results.HoloGraphLibrary;

import android.animation.Animator;
import android.view.animation.Interpolator;

/**
 * Created by DouglasW on 6/8/2014.
 */
interface HoloGraphAnimate {

    int ANIMATE_NORMAL = 0;
    int ANIMATE_INSERT = 1;
    int ANIMATE_DELETE = 2;

    void setDuration(int duration);

    Interpolator getInterpolator();
    void setInterpolator(Interpolator interpolator);

    boolean isAnimating();
    boolean cancelAnimating();
    void animateToGoalValues();
    void setAnimationListener(Animator.AnimatorListener animationListener);
}
