/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gruppe22.fjordlinear;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.gruppe22.fjordlinear.helpers.TapHelper;
import com.gruppe22.fjordlinear.rendering.BackgroundRenderer;
import com.gruppe22.fjordlinear.rendering.ObjectRenderer;
import com.gruppe22.fjordlinear.rendering.PlaneRenderer;
import com.gruppe22.fjordlinear.rendering.PointCloudRenderer;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.AnnotationRenderer;
import uk.co.appoly.arcorelocation.rendering.ImageRenderer;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;
import uk.co.appoly.arcorelocation.utils.Utils2D;

/**
 * Eksempelklasse fra Google ARCore som vi har bygget på
 */
public class HelloArActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    private static final String TAG = HelloArActivity.class.getSimpleName();

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView mSurfaceView;

    private Session mSession;
    private Snackbar mMessageSnackbar;
    private DisplayRotationHelper mDisplayRotationHelper;

    private final BackgroundRenderer mBackgroundRenderer = new BackgroundRenderer();

    private final PlaneRenderer mPlaneRenderer = new PlaneRenderer();
    private final PointCloudRenderer mPointCloud = new PointCloudRenderer();
    private TapHelper tapHelper;

    // De neste linjene blir ikke brukt, men vi våger ikke å fjerne dem.
    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] mAnchorMatrix = new float[16];

    // Tap handling and UI.
    //private final ArrayBlockingQueue<MotionEvent> mQueuedSingleTaps = new ArrayBlockingQueue<>(16);
    private final ArrayList<Anchor> mAnchors = new ArrayList<>();

    private LocationScene locationScene;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainar);
        mSurfaceView = findViewById(R.id.surfaceview);
        mDisplayRotationHelper = new DisplayRotationHelper(/*context=*/ this);

        tapHelper = new TapHelper(/*context=*/ this);
        mSurfaceView.setOnTouchListener(tapHelper);

        // Set up renderer.
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        mSurfaceView.setRenderer(this);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        Exception exception = null;
        String message = null;
        try {
            mSession = new Session(/* context= */ this);
        } catch (UnavailableArcoreNotInstalledException e) {
            message = "Please install ARCore";
            exception = e;
        } catch (UnavailableApkTooOldException e) {
            message = "Please update ARCore";
            exception = e;
        } catch (UnavailableSdkTooOldException e) {
            message = "Please update this app";
            exception = e;
        } catch (Exception e) {
            message = "This device does not support AR";
            exception = e;
        }

        if (message != null) {
            showSnackbarMessage(message, true);
            Log.e(TAG, "Exception creating session", exception);
            return;
        }

        // Create default config and check if supported.
        Config config = new Config(mSession);
        if (!mSession.isSupported(config)) {
            showSnackbarMessage("This device may not support AR, please try again", true);
        }
        mSession.configure(config);


        // Set up our location scene
        locationScene = new LocationScene(this, this, mSession);

        // dårlig løsning for å sjekke hvilke AR objekter som skal tegnes
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                oppdaterAr();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException in thread");
                }
            }
        };
        t.start();


        // Correct heading with touching side of screen
        /*mSurfaceView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent e) {


                        if(e.getX() < mSurfaceView.getWidth() / 2) {
                            locationScene.setBearingAdjustment( locationScene.getBearingAdjustment() - 1 );
                        } else {
                            locationScene.setBearingAdjustment( locationScene.getBearingAdjustment() + 1 );
                        }
                        Toast.makeText(HelloArActivity.this.findViewById(android.R.id.content).getContext(),
                                "Bearing adjustment: " + locationScene.getBearingAdjustment(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );
        */

    }

    @Override
    protected void onResume() {
        super.onResume();

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (ARLocationPermissionHelper.hasPermission(this)) {
            if(locationScene != null)
                locationScene.resume();
            if (mSession != null) {
                // Note that order matters - see the note in onPause(), the reverse applies here.
                try {
                    mSession.resume();
                } catch (CameraNotAvailableException e) {
                    e.printStackTrace();
                }
            }
            mSurfaceView.onResume();
            mDisplayRotationHelper.onResume();
        } else {
            ARLocationPermissionHelper.requestPermission(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(locationScene != null)
            locationScene.pause();
        // Note that the order matters - GLSurfaceView is paused first so that it does not try
        // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
        // still call mSession.update() and get a SessionPausedException.
        mDisplayRotationHelper.onPause();
        mSurfaceView.onPause();
        if (mSession != null) {
            mSession.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            Toast.makeText(this,
                "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

   /* private void onSingleTap(MotionEvent e) {
        // Queue tap if there is space. Tap is lost if queue is full.
        mQueuedSingleTaps.offer(e);
    }*/

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Create the texture and pass it to ARCore session to be filled during update().
        mBackgroundRenderer.createOnGlThread(/*context=*/ this);
        if (mSession != null) {
            mSession.setCameraTextureName(mBackgroundRenderer.getTextureId());
        }

        // Prepare the other rendering objects.
        /*try {
            mVirtualObject.createOnGlThread(*//*context=*//*this, "andy.obj", "andy.png");
            mVirtualObject.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            mVirtualObjectShadow.createOnGlThread(*//*context=*//*this,
                "andy_shadow.obj", "andy_shadow.png");
            mVirtualObjectShadow.setBlendMode(BlendMode.Shadow);
            mVirtualObjectShadow.setMaterialProperties(1.0f, 0.0f, 0.0f, 1.0f);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read obj file");
        }*/

        //skal være unødvendig for denne appen, men er igjen redd for å fjerne
        try {
            mPlaneRenderer.createOnGlThread(/*context=*/this, "trigrid.png");
        } catch (IOException e) {
            Log.e(TAG, "Failed to read plane texture");
        }
        mPointCloud.createOnGlThread(/*context=*/this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDisplayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mSession == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        mDisplayRotationHelper.updateSessionIfNeeded(mSession);

        try {
            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = mSession.update();
            Camera camera = frame.getCamera();


            // Handle taps. Handling only one tap per frame, as taps are usually low frequency
            // compared to frame rate.
            MotionEvent tap = tapHelper.poll();
            if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
                Log.i(TAG, "HITTEST: Got a tap and tracking");
                Utils2D.handleTap(this, locationScene, frame, tap);
            }

            // Draw background.
            mBackgroundRenderer.draw(frame);

            // Draw location markers
            locationScene.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    private void showSnackbarMessage(String message, boolean finishOnDismiss) {
        mMessageSnackbar = Snackbar.make(
            HelloArActivity.this.findViewById(android.R.id.content),
            message, Snackbar.LENGTH_INDEFINITE);
        mMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        if (finishOnDismiss) {
            mMessageSnackbar.setAction(
                "Dismiss",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMessageSnackbar.dismiss();
                    }
                });
            mMessageSnackbar.addCallback(
                new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        finish();
                    }
                });
        }
        mMessageSnackbar.show();
    }

    // metode for å endre plasseringen i SharedPreferances
    public void setInformasjon(String text) {
        SharedPreferences sharedPreferences = getSharedPreferences("informasjon", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("plassering", text);
        editor.apply();
    }

    public void oppdaterAr() {

        switch (hentGeofence()) {

            case "Fjord Line":

                // Fjerner alle objektene før nye legges til for å unngå duplisering.
                // Veldig dårlig løsning siden dette skjer hvert 10 sekund (se thread lengre oppe)
                locationScene.mLocationMarkers.clear();

                // Legger til objekter på spesifikke koordinater.
                // I ARCoreLocation finnes det Image-, Annotation- og ObjectRenderer.
                final LocationMarker fisk = new LocationMarker(
                        5.291866,
                        60.387996,
                        new ImageRenderer("fisk300.png"));
                fisk.setOnTouchListener(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HelloArActivity.this,
                                "fisk", Toast.LENGTH_SHORT).show();
                        setInformasjon("fisk");
                        Intent in = new Intent(getApplicationContext(), InfoActivity.class);
                        startActivity(in);

                    }
                });
                fisk.setTouchableSize(400);


                final LocationMarker bygg = new LocationMarker(
                        5.321541,
                        60.393454,
                        new ImageRenderer("bygg300.png"));
                bygg.setOnTouchListener(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HelloArActivity.this,
                                "bygg", Toast.LENGTH_SHORT).show();
                        setInformasjon("bygg");
                        Intent in = new Intent(getApplicationContext(), InfoActivity.class);
                        startActivity(in);

                    }
                });
                bygg.setTouchableSize(400);


                final LocationMarker bad = new LocationMarker(
                        5.308359,
                        60.395708,
                        new ImageRenderer("bad300.png"));
                bad.setOnTouchListener(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HelloArActivity.this,
                                "bad", Toast.LENGTH_SHORT).show();
                        setInformasjon("bad");
                        Intent in = new Intent(getApplicationContext(), InfoActivity.class);
                        startActivity(in);

                    }
                });
                bad.setTouchableSize(400);


                final LocationMarker tur = new LocationMarker(
                        5.387367,
                        60.377745,
                        new AnnotationRenderer("Ulriken"));
                tur.setOnTouchListener(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HelloArActivity.this,
                                "tur", Toast.LENGTH_SHORT).show();
                        setInformasjon("tur");
                        Intent in = new Intent(getApplicationContext(), InfoActivity.class);
                        startActivity(in);

                    }
                });
                tur.setTouchableSize(500);

                // Objektene blir ikke synlige før add() metoden blir kjørt.
                locationScene.mLocationMarkers.add(fisk);
                locationScene.mLocationMarkers.add(bygg);
                locationScene.mLocationMarkers.add(bad);
                locationScene.mLocationMarkers.add(tur);

                break;

            case "kiwi":

                locationScene.mLocationMarkers.clear();

                // Image marker at Eiffel Tower
                final LocationMarker eiffelTower =  new LocationMarker(
                        2.2945,
                        48.858222,
                        new ImageRenderer("eiffel.png")
                );
                eiffelTower.setOnTouchListener(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HelloArActivity.this,
                                "Touched Eiffel Tower", Toast.LENGTH_SHORT).show();
                        setInformasjon("Eiffel Tower");
                        Intent in = new Intent(getApplicationContext(), InfoActivity.class);
                        startActivity(in);
                    }
                });
                eiffelTower.setTouchableSize(1000);
                locationScene.mLocationMarkers.add(
                        eiffelTower
                );

                break;

            case "":

                locationScene.mLocationMarkers.clear();
                break;

        }
    }


    // Henter IDen (navn) til Geofence
    public String hentGeofence()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("informasjon", Context.MODE_PRIVATE);

        return sharedPreferences.getString("geofence", "");

    }

}
