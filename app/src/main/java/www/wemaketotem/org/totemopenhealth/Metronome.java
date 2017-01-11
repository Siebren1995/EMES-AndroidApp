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
    private static int mode;

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
            mediaPlayer.setVolume(0.0f, 0.0f);
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
                switch(mode)
                {
                    case 0: // Sound
                        playSound();
                        break;
                    case 1: // Light
                        toggleTorch(50);
                        break;
                    case 2: // Vibrate
                        vib.vibrate(150);
                        break;
                    default:
                        Log.e("mode", "Invalid cue mode");
                        break;
                }
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

    public void startPlayback(int m)
    {
        if (keepPlaying == true)
            return;
        keepPlaying = true;
        mode = m;
    }

    public void stopPlayback()
    {
        if (keepPlaying == false)
            return;
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
