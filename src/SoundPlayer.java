import java.io.File;
import javax.sound.sampled.*;

public class SoundPlayer {
    private Clip clip;
    private FloatControl volumeControl;

    public SoundPlayer(String musicFilePath) {
        try {
            // Load the audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(musicFilePath));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // Get the volume control for the clip
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        clip.stop();
        clip.setMicrosecondPosition(0);
        clip.start();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    public void setVolume(float percentage) {
        // Ensure that the percentage is within the valid range [0, 100]
        percentage = Math.max(0, Math.min(percentage, 100));

        // Convert the percentage to a range suitable for FloatControl
        float minVolume = volumeControl.getMinimum();
        float maxVolume = volumeControl.getMaximum();
        float newVolume = minVolume + (maxVolume - minVolume) * (percentage / 100.0f);
        volumeControl.setValue(newVolume);
    }
}
