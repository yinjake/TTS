package com.freelycar.voice.audiorecorder.text2speech;

public interface FocusTts {
     void onStart(String utteranceId);
     void onDone(String utteranceId);
     void onError(String utteranceId);
}
