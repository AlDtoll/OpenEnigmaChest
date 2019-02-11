package com.example.pusika.openenigmachest;

import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView infoText;
    Button puzzle;
    String enigmasAndParameters;
    Intent intent;
    String textFromBuf = "";

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getPreferences(MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        infoText = findViewById(R.id.infoText);
        puzzle = findViewById(R.id.puzzle);
    }

    public void showStatus(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        textFromBuf = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
        String message = encrypt(textFromBuf);
        String error = "Сообщение повреждено";
        if (!message.contains("KEY:")) {
            infoText.setText(error);
        } else {
            String[] enigmasAndParametersAndKey = message.split("KEY:");
            enigmasAndParameters = enigmasAndParametersAndKey[0];
            String key = enigmasAndParametersAndKey[enigmasAndParametersAndKey.length - 1];
            if (!key.substring(key.length() - 3).equals("ROW")) {
                infoText.setText(error);
            } else {
                if (prefs.getBoolean(key, true)) {
                    prefs.edit().putBoolean(key, false).commit();
                    puzzle.setEnabled(true);
                    puzzle.setVisibility(View.VISIBLE);
                    showInfo();
                } else {
                    String warning = "Такая загадка уже была";
                    infoText.setText(warning);
                }
            }
        }
    }

    public void tryGuess(View view) {
        intent.putExtra("enigma", enigmasAndParameters);
        puzzle.setEnabled(false);
        startActivity(intent);
    }

    private String encrypt(String enigma) {
        char[] chars = enigma.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) ((int) chars[i] + 13);
        }
        return new String(chars);
    }

    private void showInfo() {
        String[] parts = enigmasAndParameters.split("END");
        String parameters = parts[parts.length - 1];
        String[] modeAndTime = parameters.split("TIME:");
        String time = modeAndTime[modeAndTime.length - 1];
        String mode = modeAndTime[0].replace("MODE:", "");
        String info;
        switch (mode) {
            case "oneWord":
                info = "Нужно отгадать только главное слово. ";
                break;
            default:
                info = "Нужно отгадать все слова. ";
                intent = new Intent(this, EnigmaActivity.class);
        }
        int seconds = Integer.parseInt(time.replace("ROW", ""));
        info = info + "Время на отгадывание " + String.format("%d:%02d", seconds / 60, seconds % 60);
        infoText.setText(info);
    }
}
