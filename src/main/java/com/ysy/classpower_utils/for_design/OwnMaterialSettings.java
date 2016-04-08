package com.ysy.classpower_utils.for_design;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.kenumir.materialsettings.MaterialSettingsItem;
import com.kenumir.materialsettings.R.id;
import com.kenumir.materialsettings.R.layout;
import com.kenumir.materialsettings.storage.SimpleStorageInterface;
import com.kenumir.materialsettings.storage.StorageInterface;
import com.ysy.classpower_utils.swipe_back.SwipeBackActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class OwnMaterialSettings extends SwipeBackActivity {
    private static String SAVE_PREFIX = "SSI_";
    private LinearLayout material_settings_content;
    private Toolbar toolbar;
    private StorageInterface mStorageInterface;
    private HashMap<String, MaterialSettingsItem> items;
    private FrameLayout[] frames;

    public OwnMaterialSettings() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_material_settings);
        this.items = new HashMap();
        this.toolbar = (Toolbar)this.findViewById(id.toolbar);
        this.setSupportActionBar(this.toolbar);
        this.material_settings_content = (LinearLayout)this.findViewById(id.material_settings_content);
        this.frames = new FrameLayout[4];
        this.frames[0] = (FrameLayout)this.findViewById(id.material_settings_top_frame);
        this.frames[1] = (FrameLayout)this.findViewById(id.material_settings_top_frame_inside);
        this.frames[2] = (FrameLayout)this.findViewById(id.material_settings_bottom_frame_inside);
        this.frames[3] = (FrameLayout)this.findViewById(id.material_settings_bottom_frame);
        this.mStorageInterface = this.initStorageInterface();
        if(savedInstanceState != null) {
            Iterator i$ = savedInstanceState.keySet().iterator();

            while(i$.hasNext()) {
                String key = (String)i$.next();
                if(key.startsWith(SAVE_PREFIX)) {
                    String keyName = key.substring(SAVE_PREFIX.length());
                    Object value = savedInstanceState.get(key);
                    if(value instanceof String) {
                        this.mStorageInterface.save(keyName, (String)value);
                    } else if(value instanceof Integer) {
                        this.mStorageInterface.save(keyName, (Integer)value);
                    } else if(value instanceof Float) {
                        this.mStorageInterface.save(keyName, (Float)value);
                    } else if(value instanceof Long) {
                        this.mStorageInterface.save(keyName, (Long)value);
                    } else if(value instanceof Boolean) {
                        this.mStorageInterface.save(keyName, (Boolean)value);
                    } else {
                        this.mStorageInterface.save(keyName, value.toString());
                    }
                }
            }
        }

    }

    protected void onSaveInstanceState(Bundle outState) {
        StorageInterface si = this.getStorageInterface();
        if(si instanceof SimpleStorageInterface) {
            this.saveAll();
            Map all = ((SimpleStorageInterface)si).getAll();
            if(all.size() > 0) {
                Iterator i$ = all.keySet().iterator();

                while(i$.hasNext()) {
                    String key = (String)i$.next();
                    Object value = all.get(key);
                    if(value instanceof String) {
                        outState.putString(SAVE_PREFIX + key, (String)value);
                    } else if(value instanceof Integer) {
                        outState.putInt(SAVE_PREFIX + key, ((Integer)value).intValue());
                    } else if(value instanceof Float) {
                        outState.putFloat(SAVE_PREFIX + key, ((Float)value).floatValue());
                    } else if(value instanceof Long) {
                        outState.putString(SAVE_PREFIX + key, (String)value);
                    } else if(value instanceof Boolean) {
                        outState.putBoolean(SAVE_PREFIX + key, ((Boolean)value).booleanValue());
                    } else {
                        outState.putString(SAVE_PREFIX + key, value.toString());
                    }
                }
            }
        }

        super.onSaveInstanceState(outState);
    }

    public FrameLayout getContentFrame(OwnMaterialSettings.ContentFrames frame) {
        return this.frames[frame.getValue()];
    }

    public void saveAll() {
        Iterator i$ = this.items.keySet().iterator();

        while(i$.hasNext()) {
            String key = (String)i$.next();
            ((MaterialSettingsItem)this.items.get(key)).save();
        }

    }

    public void addItem(MaterialSettingsItem item) {
        View newView = item.getView(this.material_settings_content);
        if(newView != null) {
            this.material_settings_content.addView(newView);
            this.items.put(item.getName(), item);
        }

    }

    public MaterialSettingsItem getItem(String keyName) {
        return (MaterialSettingsItem)this.items.get(keyName);
    }

    public StorageInterface getStorageInterface() {
        return this.mStorageInterface;
    }

    public abstract StorageInterface initStorageInterface();

    public static enum ContentFrames {
        FRAME_TOP(0),
        FRAME_TOP_INSIDE(1),
        FRAME_BOTTOM(2),
        FRAME_BOTTOM_INSIDE(3);

        private int id;

        private ContentFrames(int idx) {
            this.id = idx;
        }

        public int getValue() {
            return this.id;
        }
    }
}
