package ProcessingDemo.PS3Eye_GUI;

import ProcessingDemo.PS3Eye_GUI.PS3Eye_GUI.MiniGUI;
import ProcessingDemo.PS3Eye_GUI.PS3Eye_GUI.MiniGUIEvent;
import ProcessingDemo.PS3Eye_GUI.PS3Eye_GUI.MiniSlider;
import ProcessingDemo.PS3Eye_GUI.PS3Eye_GUI.MiniSwitch;
import com.thomasdiewald.ps3eye.PS3Eye;
import com.thomasdiewald.ps3eye.PS3EyeP5;

public class GUIEventListener implements MiniGUIEvent {

    public GUIEventListener(PS3EyeP5 ps3eye, MiniGUI gui) {
        this.ps3eye = ps3eye;
        this.gui = gui;
        init();
    }

    PS3EyeP5 ps3eye;
    MiniGUI gui;

    MiniSlider slider_gain;
    MiniSlider slider_exposure;
    MiniSlider slider_sharpness;
    MiniSlider slider_hue;
    MiniSlider slider_brightness;
    MiniSlider slider_contrast;
    MiniSlider slider_blueblc;
    MiniSlider slider_redblc;
    MiniSlider slider_greenblc;
    MiniSwitch switch_autogain;
    MiniSwitch switch_awb;
    MiniSwitch switch_flip_h;
    MiniSwitch switch_flip_v;
    MiniSwitch switch_io;
    MiniSwitch switch_vga75;
    MiniSwitch switch_vga60;
    MiniSwitch switch_vga30;
    MiniSwitch switch_qvga187;
    MiniSwitch switch_qvga60;
    MiniSwitch switch_qvga30;
    MiniSwitch[] configs;

    private void init() {
        int px = 10, py = 10, sx = 120, sy = 15, gap = 3, dy = sy + gap;
        slider_gain = (new MiniSlider(this.gui, "gain", px, py, sx, sy)).setRange(0, 63).setValue(this.ps3eye.getGain());
        slider_exposure = (new MiniSlider(this.gui, "exposure", px, py += dy, sx, sy)).setRange(0, 255).setValue(this.ps3eye.getExposure());
        slider_sharpness = (new MiniSlider(this.gui, "sharpness", px, py += dy, sx, sy)).setRange(0, 63).setValue(this.ps3eye.getSharpness());
        slider_hue = (new MiniSlider(this.gui, "hue", px, py += dy, sx, sy)).setRange(0, 255).setValue(this.ps3eye.getHue());
        slider_brightness = (new MiniSlider(this.gui, "brightness", px, py += dy, sx, sy)).setRange(0, 255).setValue(this.ps3eye.getBrightness());
        slider_contrast = (new MiniSlider(this.gui, "contrast", px, py += dy, sx, sy)).setRange(0, 255).setValue(this.ps3eye.getContrast());
        slider_blueblc = (new MiniSlider(this.gui, "blueblc", px, py += dy, sx, sy)).setRange(0, 255).setValue(this.ps3eye.getBlueBalance());
        slider_redblc = (new MiniSlider(this.gui, "redblc", px, py += dy, sx, sy)).setRange(0, 255).setValue(this.ps3eye.getRedBalance());
        slider_greenblc = (new MiniSlider(this.gui, "greenblc", px, py += dy, sx, sy)).setRange(0, 255).setValue(this.ps3eye.getGreenBalance());
        sx = sy = 20;
        dy = sy + gap;
        switch_autogain = (new MiniSwitch(this.gui, "autogain", px, py += dy, sx, sy)).toggle(this.ps3eye.getAutogain());
        switch_awb = (new MiniSwitch(this.gui, "autoWhiteBlance", px, py += dy, sx, sy)).toggle(this.ps3eye.getAutoWhiteBalance());
        switch_flip_h = (new MiniSwitch(this.gui, "flip_h", px, py += dy, sx, sy)).toggle(this.ps3eye.getFlipH());
        switch_flip_v = (new MiniSwitch(this.gui, "flip_v", px, py += dy, sx, sy)).toggle(this.ps3eye.getFlipV());
        switch_io = (new MiniSwitch(this.gui, "ON", px, py += dy * 2, sx, sy)).toggle(true);
        py += dy;
        switch_vga75 = new MiniSwitch(this.gui, "VGA.75", px, py += dy, sx, sy);
        switch_vga60 = (new MiniSwitch(this.gui, "VGA.60", px, py += dy, sx, sy)).toggle(true);
        switch_vga30 = new MiniSwitch(this.gui, "VGA.30", px, py += dy, sx, sy);
        switch_qvga187 = new MiniSwitch(this.gui, "QVGA.187", px, py += dy, sx, sy);
        switch_qvga60 = new MiniSwitch(this.gui, "QVGA.60", px, py += dy, sx, sy);
        switch_qvga30 = new MiniSwitch(this.gui, "QVGA.30", px, py += dy, sx, sy);
        configs = new MiniSwitch[]{switch_vga75,
            switch_vga60,
            switch_vga30,
            switch_qvga187,
            switch_qvga60,
            switch_qvga30};
    }

    @Override
    public void guiEvent(PS3Eye_GUI.MiniControl control) {
        if (control instanceof PS3Eye_GUI.MiniSlider) {
            PS3Eye_GUI.MiniSlider slider = (PS3Eye_GUI.MiniSlider) control;
            if (slider == slider_gain) {
                ps3eye.setGain(slider.val);
            }
            if (slider == slider_exposure) {
                ps3eye.setExposure(slider.val);
            }
            if (slider == slider_sharpness) {
                ps3eye.setSharpness(slider.val);
            }
            if (slider == slider_hue) {
                ps3eye.setHue(slider.val);
            }
            if (slider == slider_brightness) {
                ps3eye.setBrightness(slider.val);
            }
            if (slider == slider_contrast) {
                ps3eye.setContrast(slider.val);
            }
            if (slider == slider_blueblc) {
                ps3eye.setBlueBalance(slider.val);
            }
            if (slider == slider_redblc) {
                ps3eye.setRedBalance(slider.val);
            }
            if (slider == slider_greenblc) {
                ps3eye.setGreenBalance(slider.val);
            }
        }
        if (control instanceof PS3Eye_GUI.MiniSwitch) {
            PS3Eye_GUI.MiniSwitch sw = (PS3Eye_GUI.MiniSwitch) control;
            boolean enabled = sw.value;
            if (sw == switch_autogain) {
                ps3eye.setAutogain(enabled);
            }
            if (sw == switch_awb) {
                ps3eye.setAutoWhiteBalance(enabled);
            }
            if (sw == switch_flip_h) {
                ps3eye.setFlip(switch_flip_h.value, switch_flip_v.value);
            }
            if (sw == switch_flip_v) {
                ps3eye.setFlip(switch_flip_h.value, switch_flip_v.value);
            }
            if (sw == switch_io) {
                if (enabled) {
                    ps3eye.start();
                    switch_io.name = "ON";
                } else {
                    ps3eye.stop();
                    switch_io.name = "OFF";
                }
            }
            if (enabled) {
                int idx = -1;
                int i;
                for (i = 0; i < configs.length; i++) {
                    if (sw == configs[i]) {
                        idx = i;
                        break;
                    }
                }
                if (idx != -1) {
                    for (i = 0; i < configs.length; i++) {
                        if (sw == configs[i]) {
                            String[] token = sw.name.split("[.]");
                            PS3Eye.Resolution resolution = PS3Eye.Resolution.valueOf(token[0]);
                            int fps = Integer.parseInt(token[1]);
                            ps3eye.init(fps, resolution);
                        } else {
                            configs[i].toggle(false);
                        }
                    }
                }
            }
        }
    }
}
