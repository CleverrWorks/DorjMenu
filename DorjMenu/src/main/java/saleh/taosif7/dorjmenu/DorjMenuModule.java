package saleh.taosif7.dorjmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import saleh.taosif7.dorjmenu.helpers.DrawerCallbacks;
import saleh.taosif7.dorjmenu.models.menuItem;

public abstract class DorjMenuModule extends RelativeLayout {

    List<menuItem> items = new ArrayList<>();
    DrawerCallbacks listener;
    int highlightColor = -1;

    public DorjMenuModule(Context context) {
        super(context);
        init();
    }

    public DorjMenuModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DorjMenuModule(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DorjMenuModule(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setItems(List<menuItem> items) {
        this.items.clear();
        this.items.addAll(items);
        buildMenu();
    }

    public void setListener(DrawerCallbacks callbacks) {
        this.listener = callbacks;
    }

    public void setItemHighlightColor(int color) {
        this.highlightColor = color;
    }

    boolean hasSelectedSubItem(List<menuItem> items) {
        boolean hasSelected = false;
        for (menuItem item : items) {
            hasSelected |= item.isSelected();
            if (item.hasSubItems()) hasSelected |= hasSelectedSubItem(item.getSubItems());
        }
        return hasSelected;
    }

    void setSelected(String id, List<menuItem> items) {
        for (menuItem item : items) {
            item.setSelected(item.getId().equals(id));
            if (item.hasSubItems()) setSelected(id, item.getSubItems());
        }
    }

    abstract void init();

    abstract void buildMenu();

}
