import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Looper;
import java.io.FileInputStream;

public class Torch {
  public static void main(String[] a) throws Exception {
    Looper.prepareMainLooper();
    Class<?> at = Class.forName("android.app.ActivityThread");
    Object t = at.getMethod("systemMain").invoke(null);
    Context c = (Context) at.getMethod("getSystemContext").invoke(t);
    CameraManager cm = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);
    String id = cm.getCameraIdList()[0];
    for (String s : cm.getCameraIdList())
      if (Boolean.TRUE.equals(cm.getCameraCharacteristics(s)
          .get(CameraCharacteristics.FLASH_INFO_AVAILABLE))) { id = s; break; }
    String fifo = a.length > 0 ? a[0] : "/data/local/tmp/torch.fifo";
    boolean on = false;
    while (true) {
      FileInputStream in = new FileInputStream(fifo);
      int ch;
      while ((ch = in.read()) != -1) {
        boolean want = on;
        if (ch == 't') want = !on;
        else if (ch == '1') want = true;
        else if (ch == '0') want = false;
        else continue;
        try { cm.setTorchMode(id, want); on = want; } catch (Exception e) { }
      }
      in.close();
    }
  }
}
