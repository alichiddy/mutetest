import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import java.io.File;

public class Torch {
  static volatile boolean on = false;

  public static void main(String[] a) throws Exception {
    File f = new File(a.length > 0 ? a[0] : "/data/local/tmp/torch.cmd");
    long last = f.lastModified();
    Looper.prepareMainLooper();
    Class<?> at = Class.forName("android.app.ActivityThread");
    Object t = at.getMethod("systemMain").invoke(null);
    Context c = (Context) at.getMethod("getSystemContext").invoke(t);
    CameraManager cm = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);
    String id = cm.getCameraIdList()[0];
    for (String s : cm.getCameraIdList())
      if (Boolean.TRUE.equals(cm.getCameraCharacteristics(s)
          .get(CameraCharacteristics.FLASH_INFO_AVAILABLE))) { id = s; break; }
    final String fid = id;
    HandlerThread ht = new HandlerThread("torch-cb");
    ht.start();
    cm.registerTorchCallback(new CameraManager.TorchCallback() {
      @Override public void onTorchModeChanged(String cameraId, boolean enabled) {
        if (cameraId.equals(fid)) on = enabled;
      }
    }, new Handler(ht.getLooper()));
    try { cm.setTorchMode(id, true); } catch (Exception e) { }
    while (true) {
      Thread.sleep(50);
      long m = f.lastModified();
      if (m != last) {
        last = m;
        try { cm.setTorchMode(id, !on); } catch (Exception e) { }
      }
    }
  }
}
