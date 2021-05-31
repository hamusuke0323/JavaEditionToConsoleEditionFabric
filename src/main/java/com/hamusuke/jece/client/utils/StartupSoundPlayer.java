package com.hamusuke.jece.client.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.OggAudioStream;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Environment(EnvType.CLIENT)
public class StartupSoundPlayer {
    private final OggAudioStream oggAudioStream;
    private FloatControl volume;
    private Clip clip;

    public StartupSoundPlayer(InputStream inputStream) throws IOException {
        this.oggAudioStream = new OggAudioStream(inputStream);
    }

    public void play() {
        try {
            this.clip = AudioSystem.getClip();
            ByteBuffer byteBuffer = this.oggAudioStream.getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            this.clip.open(this.oggAudioStream.getFormat(), bytes, 0, bytes.length);
            this.volume = (FloatControl) this.clip.getControl(FloatControl.Type.MASTER_GAIN);
            this.clip.start();
            this.oggAudioStream.close();
        } catch (Exception e) {
            LogManager.getLogger().warn("Failed play startup sound: {}", e.getMessage());
        }
    }

    public void stop() {
        this.clip.stop();
        this.clip.close();
    }

    public void setVolume(float vol) {
        this.volume.setValue(MathHelper.clamp(vol, this.volume.getMinimum(), this.volume.getMaximum()));
    }
}
