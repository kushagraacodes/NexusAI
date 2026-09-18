package com.javapowered.nexusai.model;

/** Result returned by the AI layer, including the model actually used. */
public record AIResult(String text, String modelUsed, boolean demoMode, boolean fallbackUsed) {
    public static AIResult demo(String text, String model) {
        return new AIResult(text, model, true, false);
    }
}
