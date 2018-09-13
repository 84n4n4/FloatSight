package org.floatcast.floatsight;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.*;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

public final class Actions {
    private Actions() {
        throw new AssertionError();
    }

    public static ViewAction wait(final int mSeconds) {
        return new ViewAction() {
            @Override
            public String getDescription() {
                return "wait for " + mSeconds + " milliseconds";
            }

            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();
                uiController.loopMainThreadForAtLeast(mSeconds);
            }
        };
    }

    public static ViewAction clickOnPosition(final int x, final int y){
        return new GeneralClickAction(Tap.SINGLE, getCoordinatesProvider(x, y), Press.FINGER, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY);
    }

    public static ViewAction longClickOnPosition(final int x, final int y){
        return new GeneralClickAction(Tap.LONG, getCoordinatesProvider(x, y), Press.FINGER, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY);
    }

    private static CoordinatesProvider getCoordinatesProvider(final int x, final int y) {
        return new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {

                final int[] screenPos = new int[2];
                view.getLocationOnScreen(screenPos);

                final float screenX = screenPos[0] + x;
                final float screenY = screenPos[1] + y;
                float[] coordinates = {screenX, screenY};

                return coordinates;
            }
        };
    }
}