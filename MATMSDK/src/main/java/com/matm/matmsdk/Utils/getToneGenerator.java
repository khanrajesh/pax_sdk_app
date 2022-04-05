package com.matm.matmsdk.Utils;

import android.media.AudioManager;
import android.media.ToneGenerator;

public class getToneGenerator {

    public getToneGenerator() {

        final ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150);
        toneGen1.release();

    }

}
