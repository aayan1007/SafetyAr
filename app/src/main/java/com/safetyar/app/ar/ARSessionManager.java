package com.safetyar.app.ar;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

public class ARSessionManager {
    private static final String TAG = "ARSessionManager";

    private Session session;
    private boolean userRequestedInstall = true;

    public interface SessionCallback {
        void onSessionReady(Session session);
        void onSessionError(String errorMessage);
    }

    public static boolean isArCoreSupported(Context context) {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(context);
        return availability.isSupported();
    }

    public void initializeSession(Activity activity, SessionCallback callback) {
        if (session != null) {
            callback.onSessionReady(session);
            return;
        }

        try {
            switch (ArCoreApk.getInstance().requestInstall(activity, userRequestedInstall)) {
                case INSTALLED:
                    break;
                case INSTALL_REQUESTED:
                    userRequestedInstall = false;
                    return;
            }

            session = new Session(activity);

            Config config = new Config(session);
            config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL);
            config.setFocusMode(Config.FocusMode.AUTO);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            config.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR);

            session.configure(config);
            callback.onSessionReady(session);

        } catch (UnavailableArcoreNotInstalledException | UnavailableApkTooOldException e) {
            callback.onSessionError("Google Play Services for AR is required for AR mode.");
        } catch (UnavailableDeviceNotCompatibleException e) {
            callback.onSessionError("Device does not support Google ARCore hardware features.");
        } catch (UnavailableSdkTooOldException e) {
            callback.onSessionError("ARCore SDK update required.");
        } catch (Exception e) {
            callback.onSessionError("ARCore initialization failure: " + e.getMessage());
        }
    }

    public void resumeSession(Activity activity) throws CameraNotAvailableException {
        if (session != null) {
            session.resume();
        }
    }

    public void pauseSession() {
        if (session != null) {
            session.pause();
        }
    }

    public void destroySession() {
        if (session != null) {
            session.close();
            session = null;
        }
    }

    public Session getSession() {
        return session;
    }
}
