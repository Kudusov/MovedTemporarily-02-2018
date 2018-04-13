package main.views;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public class AboutForm {

    private @NotNull String text;

    public AboutForm(@JsonProperty("text") String text) {
        if (text == null) {
            text = "302 - Moved Temporarily";
        }
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
