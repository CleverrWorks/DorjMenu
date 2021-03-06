package mohammed.taosif7.dorjmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import mohammed.taosif7.dorjmenu.helpers.ContentDragTouchListener;
import mohammed.taosif7.dorjmenu.helpers.DrawerCallbacks;
import mohammed.taosif7.dorjmenu.helpers.HelperMethods;
import mohammed.taosif7.dorjmenu.helpers.MenuDragTouchListener;
import mohammed.taosif7.dorjmenu.models.menuItem;

public class DorjMenu extends LinearLayout {

    // constants
    public enum direction {LEFT, RIGHT}

    public enum MenuType {MENU_SUBLIST, MENU_PAGES}

    // Properties
    public int menu_width = 360;
    public FrameLayout user_content;
    boolean initialised = false;
    boolean menu_open = false;
    boolean showHeaderShadow = true;
    Boolean isRTL;
    private int screen_width = 600;
    long anim_duration = 400;
    double menu_open_factor = 0.7;
    public direction menu_direction = direction.RIGHT;
    public MenuType menuType = MenuType.MENU_PAGES;

    // Views
    public RelativeLayout menu;
    public ImageView IV_header_bg;

    // Data
    List<menuItem> items = new ArrayList<>();
    Activity bindedActivity;
    View.OnClickListener persistentButtonListener, ctaButtonListener, headerButtonListener;
    int highlightColor = -1, menuAccentColor = -1, ctaButtonColor = -1;
    Drawable profileImage, persistentButtonIcon, headerButtonIcon, headerBG;
    String displayName, email, persistentButtonLabel, ctaButtonLabel;

    // Other
    DrawerCallbacks listener;
    DorjMenuModule dorjMenuModule;

    public DorjMenu(Context context, DrawerCallbacks listener) {
        super(context);
        this.listener = listener;
    }

    private void setContent() {

        if (bindedActivity == null) {
            throw new IllegalStateException("No Activity attached");
        }

        ViewGroup mainContainer = (ViewGroup) bindedActivity.findViewById(android.R.id.content).getRootView();
        View content = mainContainer.getChildAt(0);
        ViewGroup user_container = bindedActivity.findViewById(android.R.id.content);
        mainContainer.removeView(content);

        // Add user content in out layout
        ((ViewGroup) findViewById(R.id.user_content)).addView(content);
        mainContainer.addView(this);

        // Touch Listeners
        ContentDragTouchListener contentDrag = new ContentDragTouchListener(this);
        MenuDragTouchListener menuDrag = new MenuDragTouchListener(this);

        menu.setOnTouchListener(menuDrag);
        user_content.setOnTouchListener(contentDrag);
        ((ViewGroup) user_container.getChildAt(0)).setOnTouchListener(contentDrag);
        ((ViewGroup) user_container.getChildAt(0)).setFitsSystemWindows(true);

        // Initialise properties
        menuAccentColor = ContextCompat.getColor(getContext(), R.color.menu_accent);
        highlightColor = ContextCompat.getColor(getContext(), R.color.menu_accent);
        if (ctaButtonColor == -1)
            ctaButtonColor = ContextCompat.getColor(getContext(), R.color.menu_accent);

        // set header props
        if (headerBG == null) headerBG = getResources().getDrawable(R.drawable.drawer_default_bg);
        setHeaderBackground(headerBG);
        showHeaderShadow(showHeaderShadow);

        // Set User details
        setUserDetails(profileImage, displayName, email, true);

        // Set bottom navigation bar spacer
        findViewById(R.id.bottom_spacer).getLayoutParams().height = getNavigationBarHeight();

        // set persistent button & cta button props
        setPersistentButton(persistentButtonIcon, persistentButtonLabel, persistentButtonListener);
        setCTAButton(ctaButtonLabel, ctaButtonColor, ctaButtonListener);
        setHeaderButton(headerButtonIcon, headerButtonListener);

        // Build menu
        setMenuType(this.menuType);
    }

    private void init() {
        if (initialised) return;

        // Inflate menu
        inflate(getContext(), R.layout.main_layout, this);

        // set properties to meu
        menu = findViewById(R.id.menu_layout);
        user_content = findViewById(R.id.user_content);
        IV_header_bg = findViewById(R.id.drawer_header_bg);
        View menuEndBorder = findViewById(R.id.menuEndBorder);

        // Set RTL
        forceRTLLayout(this.isRTL);


        // set properties
        CoordinatorLayout.LayoutParams params_menu = (CoordinatorLayout.LayoutParams) menu.getLayoutParams();
        params_menu.insetEdge = (menu_direction == direction.RIGHT) ? Gravity.END : Gravity.START;
        params_menu.gravity = (menu_direction == direction.RIGHT) ? Gravity.END : Gravity.START;
        menu.setLayoutParams(params_menu);

        CoordinatorLayout.LayoutParams params_content = (CoordinatorLayout.LayoutParams) user_content.getLayoutParams();
        params_content.dodgeInsetEdges = (menu_direction == direction.RIGHT) ? Gravity.END : Gravity.START;
        user_content.setLayoutParams(params_content);

        menu.getLayoutParams().width = menu_width;
        menu.setTranslationX((menu_direction == direction.RIGHT) ? menu_width : -menu_width);
        findViewById(R.id.profileImage).setClipToOutline(true);

        RelativeLayout.LayoutParams params_headerImage = (RelativeLayout.LayoutParams) IV_header_bg.getLayoutParams();
        params_headerImage.addRule((menu_direction == direction.RIGHT) ? RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_RIGHT);
        IV_header_bg.setLayoutParams(params_headerImage);

        RelativeLayout.LayoutParams params_border = (RelativeLayout.LayoutParams) menuEndBorder.getLayoutParams();
        params_border.addRule((menu_direction == direction.RIGHT) ? RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_RIGHT);
        menuEndBorder.setLayoutParams(params_border);

        // Set the content to activity
        setContent();

        initialised = true;
    }

    public DorjMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DorjMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DorjMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void attachToActivity(Activity activity, direction direction) {
        bindedActivity = activity;
        menu_direction = direction;

        // Get screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_width = displayMetrics.widthPixels;
        menu_width = (int) (screen_width * menu_open_factor);

        // Attach init method to activity's onResume
        activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                init();
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                closeMenu();
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && menu_open) {
            closeMenu();
            return true;
        } else return false;
    }

    int getNavigationBarHeight() {
        boolean hasMenuKey = ViewConfiguration.get(getContext()).hasPermanentMenuKey();
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /*
     *
     *
     * Exposed Methods
     *
     *
     */

    /**
     * Method to set User info in the header
     * <br>
     * <br>
     * If this method is not called, The header will only contain Header Image
     * with background with no shade
     *
     * @param profileImage         Drawable Image that will be shown as profile pic (can be null)
     * @param displayName          Display name of the user (can be null)
     * @param email                Email of the user (can be null)
     * @param showImagePlaceholder If this is set to true, and profileImage is null, there will be text placeholder
     */
    public void setUserDetails(@Nullable Drawable profileImage, @Nullable String displayName, @Nullable String email, boolean showImagePlaceholder) throws AssertionError {
        TextView TV_displayName = ((TextView) findViewById(R.id.displayName));
        TextView TV_email = ((TextView) findViewById(R.id.email));
        TextView TV_profileImageInitials = ((TextView) findViewById(R.id.profileImageInitials));
        ImageView IV_profile = ((ImageView) findViewById(R.id.profileImage));

        // Store data
        this.profileImage = profileImage;
        this.displayName = displayName;
        this.email = email;

        // Return if views are not inflated yet
        if (TV_displayName == null || TV_email == null || IV_profile == null || TV_profileImageInitials == null)
            return;
        else {
            TV_displayName.setVisibility(VISIBLE);
            TV_email.setVisibility(VISIBLE);
            TV_profileImageInitials.setVisibility(VISIBLE);
            IV_profile.setVisibility(VISIBLE);
        }

        if (displayName == null && email == null) {
            TV_displayName.setVisibility(INVISIBLE);
            TV_email.setVisibility(INVISIBLE);
            TV_profileImageInitials.setVisibility(INVISIBLE);
            IV_profile.setVisibility(INVISIBLE);
            showHeaderShadow(false);
            return;
        } else if (displayName == null) {
            TV_displayName.setText(email);
            TV_email.setVisibility(INVISIBLE);
        } else if (email == null) {
            TV_email.setVisibility(GONE);
            TV_displayName.setText(displayName);
        } else {
            TV_displayName.setText(displayName);
            TV_email.setText(email);
        }


        if (profileImage == null) {
            IV_profile.setVisibility(INVISIBLE);
            if (showImagePlaceholder) {
                TV_profileImageInitials.setBackgroundTintList(ColorStateList.valueOf(menuAccentColor));

                StringBuilder initials = new StringBuilder();
                if (displayName != null) {
                    String[] nameSplit = displayName.split(" ");
                    for (String s : nameSplit) initials.append(s.charAt(0));
                } else {
                    initials.append(email.substring(0, 2));
                }
                TV_profileImageInitials.setText(initials);
            } else TV_profileImageInitials.setVisibility(GONE);
        } else {
            TV_profileImageInitials.setVisibility(INVISIBLE);
            IV_profile.setImageDrawable(profileImage);
        }

    }

    /**
     * This method forces to show/hide the shadow in header background image
     *
     * @param show provide true to show
     */
    public void showHeaderShadow(boolean show) {
        this.showHeaderShadow = show;
        findViewById(R.id.drawer_header_shadow).setVisibility(show ? VISIBLE : GONE);
    }

    /**
     * Method to set Header background Image
     * TIP: use horizontal image for better parallax while gliding
     *
     * @param backgroundImage Drawable Image to set
     */
    public void setHeaderBackground(Drawable backgroundImage) {
        ImageView headerBG = findViewById(R.id.drawer_header_bg);
        if (backgroundImage != null) this.headerBG = backgroundImage;

        if (headerBG == null) return;
        if (backgroundImage != null) headerBG.setImageDrawable(this.headerBG);
        else headerBG.setImageResource(R.drawable.drawer_default_bg);
    }

    /**
     * Method to set the button that will be permanently visible at the bottom of menu
     *
     * @param icon    Drawable icon for the button (can be null)
     * @param label   Label for the button
     * @param onClick action to perform on click
     */
    public void setPersistentButton(@Nullable Drawable icon, @Nullable String label, View.OnClickListener onClick) {
        this.persistentButtonIcon = icon;
        this.persistentButtonLabel = label;
        this.persistentButtonListener = onClick;

        RelativeLayout buttonBody = findViewById(R.id.persistentButton);
        TextView buttonLabel = findViewById(R.id.persistentButtonLabel);
        ImageView buttonIcon = findViewById(R.id.persistentButtonIcon);

        if (buttonBody != null) {
            if (onClick == null) {
                buttonBody.setVisibility(GONE);
                return;
            }

            if (buttonLabel != null && label != null) buttonLabel.setText(label);
            if (buttonIcon != null && icon != null) buttonIcon.setImageDrawable(icon);
            buttonBody.setOnClickListener(onClick);
        }
    }

    /**
     * method to set CTA button that will be visible above persistent Button (or at bottom of drawer if persistent button not provided)
     *
     * @param label       Label for the button
     * @param buttonColor int resolved Color of the button background
     * @param onClick     listener for click action
     */
    public void setCTAButton(String label, @Nullable Integer buttonColor, @Nullable View.OnClickListener onClick) {
        this.ctaButtonLabel = label;
        if (buttonColor != null) this.ctaButtonColor = buttonColor;
        this.ctaButtonListener = onClick;

        Button ctaButton = findViewById(R.id.ctaButton);
        if (ctaButton == null) return;
        if (label == null || onClick == null) {
            ctaButton.setVisibility(GONE);
            return;
        }

        ctaButton.setText(ctaButtonLabel);
        ctaButton.setTextColor(HelperMethods.isColorDark(ctaButtonColor) ? Color.WHITE : Color.BLACK);
        ctaButton.setOnClickListener(ctaButtonListener);
        ctaButton.setBackgroundTintList(ColorStateList.valueOf(ctaButtonColor));
    }

    /**
     * Method to set A small button that'll be present in header, to the opposite side of profile picture
     *
     * @param icon    Drawable Icon of button
     * @param onClick Listener for click action
     */
    public void setHeaderButton(Drawable icon, View.OnClickListener onClick) {
        this.headerButtonIcon = icon;
        this.headerButtonListener = onClick;

        ImageView IV_headerBtn = findViewById(R.id.drawer_header_button);
        if (IV_headerBtn == null) return;

        if (icon == null || onClick == null) {
            IV_headerBtn.setVisibility(GONE);
            return;
        }

        IV_headerBtn.setImageDrawable(icon);
        IV_headerBtn.setOnClickListener(onClick);
    }

    /**
     * Method to set list of menu items
     * <br>
     * <br>
     * A {@link menuItem} is a recursive data type, that means you can add child to it,
     * and hence construct a levelled/paged menu depending on MenuType ({@link MenuType})
     *
     * @param items List of {@link menuItem}
     */
    public void setItems(List<menuItem> items) {
        this.items.clear();
        this.items.addAll(items);
        if (dorjMenuModule != null)
            dorjMenuModule.setItems(items);
    }

    /**
     * Method to set Selected Item highlight colour
     * <br>
     * This method can be called anytime, on each item selection
     * so that every item can have its own color for highlight
     *
     * @param color resolved int color
     */
    public void setItemHighlightColor(int color) {
        this.highlightColor = color;
        if (dorjMenuModule != null)
            dorjMenuModule.setItemHighlightColor(color);
    }

    /**
     * Method to set drawer accent color
     * <br>
     * This color is applied to various things like
     * <br>
     * default CTA button color
     * <br>
     * default item highlight color
     *
     * @param color int resolved color
     */
    public void setMenuAccentColor(int color) {
        this.menuAccentColor = color;

        // Set background tint of profileImage initials
        TextView TV_profileImageInitials = ((TextView) findViewById(R.id.profileImageInitials));
        if (TV_profileImageInitials != null)
            TV_profileImageInitials.setBackgroundTintList(ColorStateList.valueOf(menuAccentColor));
    }

    /**
     * Method to set menu type
     * <p>
     * <p>
     * This method must be called before attachToRoot method
     *
     * @param type one of values from {@link MenuType}
     */
    public void setMenuType(MenuType type) {
        this.menuType = type;

        dorjMenuModule = (type == MenuType.MENU_SUBLIST) ? findViewById(R.id.menu_list) : findViewById(R.id.menu_page);

        if (dorjMenuModule == null) return;

        dorjMenuModule.setVisibility(VISIBLE);
        dorjMenuModule.setItems(items);
        if (highlightColor != -1) dorjMenuModule.setItemHighlightColor(highlightColor);
        if (listener != null) dorjMenuModule.setListener(listener);

    }

    /**
     * Method to set duration of menu opening/closing animation
     *
     * @param duration animation duration in long
     */
    public void setGlideDuration(long duration) {
        this.anim_duration = duration;
    }

    /**
     * Method to set Amount of screen that'll be covered by menu
     *
     * @param factor a value between 0.6 to 1.0
     */
    public void setMenuOpenFactor(@FloatRange(from = 0.6, to = 1.0) double factor) {
        this.menu_open_factor = factor;
    }

    /**
     * Method to force whole the drawer contents to layout in Right to Left direction
     *
     * @param isRTL if true, drawer contents will be laid out in right to left mode, If null, it'll inherit
     */
    public void forceRTLLayout(Boolean isRTL) {
        this.isRTL = isRTL;

        RelativeLayout menu = findViewById(R.id.menu_layout);
        if (menu == null) return;

        if (isRTL == null) {
            findViewById(R.id.menu_layout).setLayoutDirection(LAYOUT_DIRECTION_LOCALE);
        } else {
            findViewById(R.id.menu_layout).setLayoutDirection(isRTL ? View.LAYOUT_DIRECTION_RTL : LAYOUT_DIRECTION_LTR);
        }
    }

    /**
     * Method to close menu
     */
    public void closeMenu() {

        // Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", (menu_direction == direction.RIGHT) ? menu_width : -menu_width);
        animation.setDuration(anim_duration);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                menu_open = false;
                super.onAnimationEnd(animation);
            }
        });

        // Animate drawer Image bg
        ValueAnimator anim = ValueAnimator.ofInt(IV_header_bg.getMeasuredWidth(), 0);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams drawerImageParams = IV_header_bg.getLayoutParams();
            drawerImageParams.width = val;
            IV_header_bg.setLayoutParams(drawerImageParams);
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(anim_duration + 100); // close 100ms slower than menu closes
        anim.start();
    }

    /**
     * Method to open menu
     */
    public void openMenu() {

        // Get focus so that we can get key-press
        requestFocus();

        // Animate menu X coordinate
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu, "translationX", 0f);
        animation.setDuration(anim_duration);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                menu_open = true;
                requestFocus();
                super.onAnimationEnd(animation);
            }
        });

        // Animate drawer Image bg
        ValueAnimator anim = ValueAnimator.ofInt(IV_header_bg.getMeasuredWidth(), menu_width);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams drawerImageParams = IV_header_bg.getLayoutParams();
            drawerImageParams.width = val;
            IV_header_bg.setLayoutParams(drawerImageParams);
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(Math.max(anim_duration - 100, 0)); // open 100ms faster than menu opens
        anim.start();
    }

    /**
     * Method to toggle menu
     */
    public void toggleMenu() {
        if (menu_open) closeMenu();
        else openMenu();
    }
}
