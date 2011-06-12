package audioreactive;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;

/**
 * Created by IntelliJ IDEA.
 * User: akoeberle
 * Date: 21.05.11
 * Time: 22:02
 * To change this template use File | Settings | File Templates.
 */
public class Audio {

    private GLSLNoiseInput p;

    private AudioInput in;

    private FFT fft;

    private static final int BUFFER_SIZE = 1024;

    private static final float SAMPLE_RATE = 44100;

    private static final int SPECTRUM_WIDTH = BUFFER_SIZE / 2; // determines how much of

    private float[] peaks;

    private int[] peak_age;

    private int BINS_PER_BAND = 1;

    private int peakSize; // how many individual peak bands we have (dep. BINS_PER_BAND)

    private static final float GAIN = 40; // in dB

    private static final float DB_SCALE = 2.0f; // pixels per dB

    public Audio(GLSLNoiseInput p) {
        this.p = p;
        Minim  minim = new Minim(p);
        in = minim.getLineIn(Minim.MONO, BUFFER_SIZE, SAMPLE_RATE);
        fft = new FFT(in.bufferSize(), in.sampleRate());
        fft.window(FFT.HAMMING);
        peakSize = 1 + Math.round(fft.specSize() / BINS_PER_BAND);
        peaks = new float[peakSize];
        peak_age = new int[peakSize];
    }

    void updatePeaks() {

        fft.forward(in.mix);

        for (int i = 0; i < peakSize; ++i) {

            if (peak_age[i] < p.peak_hold_time) {
                ++peak_age[i];
            } else {
                peaks[i] -= p.damp;
                if (peaks[i] < 0) {
                    peaks[i] = 0;
                }
            }
        }
        for (int i = 0; i < SPECTRUM_WIDTH; i++) {
            float val = DB_SCALE * (20 * ((float) Math.log10(fft.getBand(i))) + GAIN);
            if (fft.getBand(i) == 0) {
                val = -200;
            }

            // update the peak record
            // which peak bin are we in?
            int peaksi = i / BINS_PER_BAND;
            if (val > peaks[peaksi]) {
                peaks[peaksi] = val;
                // reset peak age counter
                peak_age[peaksi] = 0;
            }
        }

    }


    public float[] getPeaks() {
        return peaks;
    }

    public int getPeakSize() {
        return peakSize;
    }
}
