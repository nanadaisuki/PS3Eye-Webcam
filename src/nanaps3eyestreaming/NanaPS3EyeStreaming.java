package nanaps3eyestreaming;

import nana.NanaWebcamService;
import processing.core.PApplet;

public class NanaPS3EyeStreaming {

   public static NanaWebcamService pushService;

    public void run(String[] args) {
        PApplet.main(ExtendedGUI.class.getName());
        pushService = new NanaWebcamService();
        pushService.startService();
    }

    public static void main(String[] args) {
        new NanaPS3EyeStreaming().run(args);
    }

}
