package com.thedeveloperworldisyours.attachoverkeyboard;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by javierg on 31/10/2017.
 */

public class MessageActivity extends AppCompatActivity {

    private PopupWindow mPopupWindow;
    private ConstraintLayout mParentLayout;
    private boolean mIsKeyBoardVisible;
    private boolean mIsPopupVisible;
    ScrollView mScrollView;
    Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        mParentLayout = (ConstraintLayout) findViewById(R.id.root_view);
        mButton = (Button) findViewById(R.id.activity_messenger_button);


        mParentLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                Rect r = new Rect();

                mParentLayout.getWindowVisibleDisplayFrame(r);

                int heightDiff = mParentLayout.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    //enter your code here
                    if (mIsPopupVisible) {
                        keepKeyboard();
                        mIsPopupVisible = false;
                        mPopupWindow.dismiss();
                    }
                } else {
                    //enter code for hid
                }
        });

        checkKeyboardIsOpen(mParentLayout);

        mButton.setOnClickListener((View view) -> {
            if (mIsKeyBoardVisible) {
                showPopUpKeyboard();
            } else {
                showPopup();
            }
        });
    }

    private void keepKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    private void checkKeyboardIsOpen(final View contentView) {

        mParentLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            Rect r = new Rect();
            contentView.getWindowVisibleDisplayFrame(r);
            int screenHeight = contentView.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                mIsKeyBoardVisible = true;
            } else {
                // keyboard is closed
                mIsKeyBoardVisible = false;
            }

        });
    }

    public void showPopUpKeyboard() {
        mIsPopupVisible = true;
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popup_in_keyboard, null);


        mScrollView = (ScrollView) customView.findViewById(R.id.keyboard_layout_view);
        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );


        setSizeForSoftKeyboard();

        // Get a reference for the custom view close button
        Button closeButton = (Button) customView.findViewById(R.id.ib_close);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener((View view) -> {
                // Dismiss the popup window
                mIsPopupVisible = false;
                mPopupWindow.dismiss();
        });
        mPopupWindow.showAtLocation(mParentLayout, Gravity.CENTER, 0, 0);

    }

    public void showPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_new, null);

        PopupWindow popup = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        int[] location = new int[2];
        mButton.getLocationInWindow(location);


        ScrollView scrollView = (ScrollView) popupView.findViewById(R.id.keyboard_scroll_layout_view);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = metrics.heightPixels - location[1];  // left, top, right, bottom
        scrollView.setLayoutParams(params);

        // Get a reference for the custom view close button
        Button belowCloseButton = (Button) popupView.findViewById(R.id.popup_new_below_close_button);
        // Get a reference for the custom view close button
        Button closeButton = (Button) popupView.findViewById(R.id.popup_new_close_button);

        closeButton.setOnClickListener((View view) -> {
                // Dismiss the popup window
                mIsPopupVisible = false;
                popup.dismiss();
        });

        belowCloseButton.setOnClickListener((View view) -> {
                // Dismiss the popup window
                mIsPopupVisible = false;
                popup.dismiss();
        });

        popup.showAtLocation(mParentLayout, Gravity.NO_GRAVITY, 0, 0);//location[1]-popupHeight);
    }

    public void setSizeForSoftKeyboard() {
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mParentLayout.getWindowVisibleDisplayFrame(r);

                int screenHeight = getUsableScreenHeight();
                int heightDifference = screenHeight
                        - (r.bottom - r.top);
                int resourceId = getResources()
                        .getIdentifier("status_bar_height",
                                "dimen", "android");
                if (resourceId > 0) {
                    heightDifference -= getResources()
                            .getDimensionPixelSize(resourceId);
                }
                if (heightDifference > 100) {
                    int keyBoardHeight = heightDifference;

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mScrollView.getLayoutParams();
                    params.height = keyBoardHeight;
                    mScrollView.setLayoutParams(params);
                }
            }
        });
    }

    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return mParentLayout.getRootView().getHeight();
        }
    }


    public void startCircularReveal(View view) {
        int cx = (mButton.getLeft() + mButton.getRight()) / 2;
        int cy = (mButton.getTop() + mButton.getBottom()) / 2;
//        view.setBackgroundColor(Color.parseColor("#6FA6FF"));
        int finalRadius = Math.max(cy, view.getHeight() - cy);
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        }
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(200);
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

}
