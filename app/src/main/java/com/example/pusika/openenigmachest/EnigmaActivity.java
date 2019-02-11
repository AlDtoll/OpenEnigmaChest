package com.example.pusika.openenigmachest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class EnigmaActivity extends AppCompatActivity {

    ArrayList<Enigma> enigmas = new ArrayList();
    LinearLayout enigmasList;
    TextView timerTextView;
    Timer timer;
    TextView result;

    int numberOfHint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enigma);
        timerTextView = findViewById(R.id.timer);
        Bundle arguments = getIntent().getExtras();
        String enigmasAndParameters = arguments.get("enigma").toString();
        result = findViewById(R.id.textText);
        String[] parts = enigmasAndParameters.split("END");
        String parameters = parts[parts.length - 1];
        String[] modeAndTime = parameters.split("TIME:");
        String time = modeAndTime[modeAndTime.length - 1];
        startTimer(Integer.parseInt(time.replace("ROW", "")));
        enigmasList = findViewById(R.id.enigmasList);
        LayoutInflater ltInflater = getLayoutInflater();
        for (int i = 0; i < parts.length - 1; i++) {
            String[] enigmasPart = parts[i].split("PART");
            enigmas.add(new Enigma(enigmasPart[0], enigmasPart[1]));
            View vi = ltInflater.inflate(R.layout.item, null);
            ((TextView) vi.findViewById(R.id.describe)).setText(enigmasPart[1]);
            enigmasList.addView(vi);
        }
    }

    public void tryToOpenEnigmaChest(View view) {
        View v;
        ArrayList<String> answers = new ArrayList<>();
        EditText editText;
        String answer;
        boolean[] isRight = new boolean[enigmas.size()];
        for (int i = 0; i < isRight.length; i++) {
            isRight[i] = false;
        }
        int counter = 0;
        for (int i = 0; i < enigmas.size(); i++) {
            v = enigmasList.getChildAt(i);
            editText = v.findViewById(R.id.word);
            answer = editText.getText().toString();
            answers.add(answer);
            if (answer.equals(enigmas.get(i).getWord())) {
                editText.setEnabled(false);
                editText.setError(null);
                isRight[i] = true;
                counter++;
            } else {
                editText.setText("");
                editText.setHint(createHint(enigmas.get(i).getWord()));
                editText.setError("Неверный ответ");
            }
        }
        if (counter == isRight.length) {
            timer.cancel();
            timer.purge();
            result.setText("Успех. Подсказок использовано: " + numberOfHint);
        }
    }

    private String createHint(String enigma) {
        numberOfHint++;
        char[] chars = enigma.toCharArray();
        Random random = new Random(new Date().getTime());
        int numberOfLetter = random.nextInt(chars.length);
        for (int i = 0; i < enigma.length(); i++) {
            if (i != numberOfLetter) {
                chars[i] = '*';
            }
        }
        return new String(chars);
    }

    private void startTimer(final int seconds) {
        TimerTask timerTask = new TimerTask() {
            int time = seconds;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time--;
                        timerTextView.setText(String.format("%d:%02d", time / 60, time % 60));
                        if (time == 0) {
                            View v;
                            EditText editText;
                            for (int i = 0; i < enigmas.size(); i++) {
                                v = enigmasList.getChildAt(i);
                                editText = v.findViewById(R.id.word);
                                editText.setEnabled(false);
                                editText.setError(null);
                            }
                            timer.cancel();
                            timer.purge();
                            result.setText("Время вышло. Подсказок " + numberOfHint);
                        }
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }
}
