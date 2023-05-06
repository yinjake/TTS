package com.freelycar.demo.asrt;

public class Sdk {
    public static BaseSpeechRecognizer GetSpeechRecognizer(String host, String port, String protocol) {
        if("http".equals(protocol) || "https".equals(protocol)){
            return HttpSpeechRecognizer.newHttpSpeechRecognizer(host, port, protocol, "");
        }
        return null;
    }
}
