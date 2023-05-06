package com.freelycar.demo.text2speech;

public interface FreeTts {
     void speak(final String content);
     void pause();
     void resume();
     void setSpeechRate(float newRate);
     void setSpeechPitch(float newPitch);
     void release();
     //WhyTTS getInstance();
}
