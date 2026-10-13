import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Looper;

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
    cm.setTorchMode(id, true);
    Looper.loop(); // stay alive so the torch stays on
  }
}
