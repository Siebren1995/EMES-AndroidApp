package www.wemaketotem.org.totemopenhealth;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

public class Metronome implements Runnable
{
    private static int sleepTime = 2000;
    private static boolean keepPlaying = false;
    private static boolean threadRunning = false;
    private static Metronome instance = null;
    private static Context myContext;
    private static Vibrator vib;
    private static MediaPlayer mediaPlayer;
    private static Camera camera;
    private static Camera.Parameters cameraParams;
    private static boolean modeVisual;
    private static boolean modeAudible;
    private static boolean modeHaptic;

    protected Metronome() {}

    public static Metronome getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new Metronome();
            myContext = context;
            vib = (Vibrator) myContext.getSystemService(myContext.VIBRATOR_SERVICE);
            getBackCamera();
            mediaPlayer = MediaPlayer.create(myContext, R.raw.drum2_amp);
            mediaPlayer.setVolume(1.0f, 1.0f);
            //mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
        return instance;
    }

    public void run()
    {
        threadRunning = true;
        while(keepPlaying)
        {
            try
            {
                long startTime = System.currentTimeMillis();
                if(modeVisual)
                    toggleTorch(50);
                if(modeAudible)
                    playSound();
                if(modeHaptic)
                    vib.vibrate(150);

                long endTime = System.currentTimeMillis();
                long intermediaryTime = endTime-startTime;
                if(intermediaryTime < sleepTime)
                    Thread.sleep(sleepTime-intermediaryTime);
            }
            catch(InterruptedException e)
            {
                Log.d("Runnable", e.getMessage());
            }
        }
        threadRunning = false;
    }

    public void setModeVisual(boolean state)
    {
        modeVisual = state;
    }

    public void setModeAudible(boolean state)
    {
        modeAudible = state;
    }

    public void setModeHaptic(boolean state)
    {
        modeHaptic = state;
    }

    public void startPlayback()
    {
        keepPlaying = true;
    }

    public void stopPlayback()
    {
        keepPlaying = false;
    }

    public void setBPM(int bpm)
    {
//        sleepTime = 15*(100-bpm)+500;
        sleepTime = (175*(100-bpm))/10+250;
    }

    private void playSound()
    {
        mediaPlayer.start();
    }

    private void toggleTorch(int time)
    {
        try
        {
            cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(cameraParams);
            camera.startPreview();
            Thread.sleep(time);
            cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(cameraParams);
            camera.startPreview();
        }
        catch(InterruptedException e)
        {
            Log.d("toggleTorch", e.getMessage());
        }
    }

    static private void getBackCamera()
    {
        if(myContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
        {
            camera = Camera.open();
            cameraParams = camera.getParameters();
        }
    }

    public static boolean isThreadRunning()
    {
        return threadRunning;
    }
}
