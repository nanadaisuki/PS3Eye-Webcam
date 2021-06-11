package nanaps3eyestreaming;

import ProcessingDemo.PS3Eye_GUI.GUIEventListener;
import ProcessingDemo.PS3Eye_GUI.PS3Eye_GUI;
import com.thomasdiewald.ps3eye.PS3EyeP5;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import static nana.NanaWebcamClient.saveBitmap;
import static nana.NanaWebcamClient.saveTIFF;
import static nanaps3eyestreaming.NanaPS3EyeStreaming.pushService;
import processing.core.PImage;

public class ExtendedGUI extends PS3Eye_GUI {

    public ExtendedGUI() {
        System.out.println("created");
    }

    public static Object getDeclaredFieldObject(Object target, Class targetClass, String name) {
        try {
            System.out.println("--------> " + targetClass.getName());
            Field result = null;
            for (Field f : targetClass.getDeclaredFields()) {
                System.out.println(f);
                if (f.getName().equals(name)) {
                    System.out.println(f.getName());
                    result = f;
                }
            }
            if (result == null) {
                return null;
            }
            result.setAccessible(true);
            return result.get(target);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.out.println(":(");
            Logger.getLogger(ExtendedGUI.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
            return null;
        }
    }

    class nanaEvent extends GUIEventListener {

        public nanaEvent(PS3EyeP5 ps3eye, MiniGUI gui) {
            super(ps3eye, gui);
        }

        @Override
        public void guiEvent(MiniControl mc) {
            super.guiEvent(mc);
            if (skipEvent) {
                skipEvent = false;
                System.out.println("skiped");
                return;
            }
            if (mc instanceof MiniSwitch) {
                MiniSwitch sw = (MiniSwitch) mc;
                boolean enabled = (boolean) getDeclaredFieldObject(sw, sw.getClass(), "value");
                if (sw == test_save && !needSave) {
                    needSave = true;
                }
                System.out.println(enabled);
            }
        }
    }

    @Override
    public void createGUI() {
        super.createGUI();
        System.out.println("nana!");
        Class superClass = this.getClass().getSuperclass();
        MiniGUI miniGui = (MiniGUI) getDeclaredFieldObject(this, superClass, "gui");
        ps3eye = (PS3EyeP5) getDeclaredFieldObject(this, superClass, "ps3eye");
        System.out.println(miniGui);
        int px = 100, py = 200, sx = 120, sy = 15, gap = 3, dy = sy + gap;
        MiniSwitch switch_server = (new MiniSwitch(miniGui, "Enable NanaWebcam", 480, 430, 20, 20)).toggle(true);
        test_save = (new MiniSwitch(miniGui, "Save Image", 480, 455, 20, 20));
        miniGui.addEventListener(new nanaEvent(ps3eye, miniGui));
        System.out.println(":)");
    }

    PS3EyeP5 ps3eye;
    MiniSwitch test_save;
    boolean skipEvent = false;
    boolean needSave = false;

    @Override
    public void draw() {
        if (ps3eye == null) {
            return;
        }
        PImage pImage = ps3eye.getFrame();
        if (needSave) {
            needSave = false;
            pImage.save("out.jpg");
            File f = new File("out.tif");
            try {
                FileOutputStream fos = new FileOutputStream(f);
                saveTIFF(pImage, fos);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExtendedGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            skipEvent = true;
            test_save.toggle(false);
        }
        if (pushService != null) {
            pushService.handleImage(pImage);
        }
        image(pImage, 0.0F, 0.0F, super.width, super.height);
        updateGUI();
        super.surface.setTitle(String.format(String.valueOf(getClass().getName()) + "  [fps %6.2f] (NanaWebcam)", new Object[]{Float.valueOf(super.frameRate)}));
    }

}
