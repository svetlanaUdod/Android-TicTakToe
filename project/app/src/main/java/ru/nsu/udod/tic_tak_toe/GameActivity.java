package ru.nsu.udod.tic_tak_toe;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class GameActivity extends AppCompatActivity {
    private final int N = 3;
    private int current_turn = 0;
    private final String GAMEFIELD = "gamefield";
    private final String SCOREF = "scorefirst";
    private final String SCORES = "scoresecond";
    private final String TURN = "turn";
    private String[] field;
    private Button[][] buttons;
    private final String[] players = new String[2];
    private final Integer[] score = new Integer[2];
    private final TextView[] vscore = new TextView[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        players[0] = getIntent().getStringExtra("player1");
        players[1] = getIntent().getStringExtra("player2");
        score[0] = 0;
        score[1] = 0;
        field = new String[N * N];
        for (int i = 0; i < N * N; i++) {
            field[i] = null;
        }
        initView();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(GAMEFIELD, field);
        outState.putInt(SCOREF, score[0]);
        outState.putInt(SCORES, score[1]);
        outState.putInt(TURN, current_turn % 2);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        score[0] = savedInstanceState.getInt(SCOREF, 0);
        score[1] = savedInstanceState.getInt(SCORES, 0);
        field = savedInstanceState.getStringArray(GAMEFIELD);
        current_turn = savedInstanceState.getInt(TURN);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (field[i * N + j] != null) {
                    buttons[i][j].setText(field[i * N + j]);
                    buttons[i][j].setEnabled(false);
                }
            }
        }
        changeScore();

    }

    private void initView() {
        TextView name1 = findViewById(R.id.name1);
        TextView name2 = findViewById(R.id.name2);
        name1.setText(players[0]);
        name2.setText(players[1]);

        vscore[0] = findViewById(R.id.score1);
        vscore[1] = findViewById(R.id.score2);
        changeScore();

        String extraimg1 = getIntent().getStringExtra("img1");
        String extraimg2 = getIntent().getStringExtra("img2");
        ImageView img1 = findViewById(R.id.img1);
        ImageView img2 = findViewById(R.id.img2);
        try {
            if (extraimg1 != null)
                img1.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(extraimg1)));
            if (extraimg2 != null)
                img2.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(extraimg2)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        initGameField();
        setTurn();
    }

    private void initGameField() {
        GridLayout gridLayout = findViewById(R.id.game_field);
        gridLayout.setColumnCount(N);
        gridLayout.setRowCount(N);
        buttons = new Button[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                buttons[i][j] = (Button) LayoutInflater.from(this).inflate(R.layout.my_button, null);
                buttons[i][j].setId(i * N + j);
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = 0;
                lp.height = 0;
                lp.columnSpec = GridLayout.spec(j, 1f); // вес и позиция кнопки по горизонтали
                lp.rowSpec = GridLayout.spec(i, 1f); // и по вертикали
                gridLayout.addView(buttons[i][j], lp);
            }
        }
    }


    public void onClick(View view) {
        Button current_button = (Button) view;
        String current_player;

        if (current_turn % 2 == 0)
            current_player = "X";
        else
            current_player = "O";

        current_button.setText(current_player);
        current_button.setEnabled(false);

        field[current_button.getId()] = current_player;
        boolean winner = checkWinner();
        if (winner || full()) {
            Toast toast;
            if (winner) {
                toast = Toast.makeText(this, (players[current_turn % 2] + " " + this.getString(R.string.won) + "!"), Toast.LENGTH_SHORT);
                score[current_turn % 2]++;
                changeScore();
            } else {
                toast = Toast.makeText(this, this.getString(R.string.draw), Toast.LENGTH_SHORT);
            }
            toast.show();
            clearButtons();
            clearField();
        }
        current_turn++;
        setTurn();
    }

    private boolean checkWinner() {
        boolean diag = true;
        boolean revdiag = true;

        for (int i = 0; i < N; i++) {
            boolean horizontal = true;
            boolean vertical = true;
            for (int j = 0; j < N - 1; j++) {
                if (field[i * N + j] != null && field[i * N + j + 1] != null) {
                    if (field[i * N + j] != field[i * N + j + 1]) {
                        horizontal = false;
                    }
                } else horizontal = false;

                if (field[j * N + i] != null && field[(j + 1) * N + i] != null) {
                    if (field[j * N + i] != field[(j + 1) * N + i]) {
                        vertical = false;
                    }
                } else vertical = false;
            }
            if (i != N - 1) {
                if (field[i * N + i] != null && field[(i + 1) * N + i + 1] != null) {
                    if (!field[i * N + i].equals(field[(i + 1) * N + i + 1])) {
                        diag = false;
                    }
                } else diag = false;
                if (field[(i + 1) * N - i - 1] != null && field[(i + 2) * N - i - 2] != null) {
                    if (!field[(i + 1) * N - i - 1].equals(field[(i + 2) * N - i - 2])) {
                        revdiag = false;
                    }
                } else revdiag = false;
            }
            if (horizontal || vertical) {
                System.out.println("horizont vert");
                return true;
            }
        }
        if (diag || revdiag) {
            System.out.println("diag");
            return true;
        }
        return false;
    }

    private void clearButtons() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (buttons[i][j] != null) {
                    buttons[i][j].setText("");
                    buttons[i][j].setEnabled(true);
                }
            }
        }
    }

    private void clearField() {
        for (int i = 0; i < N * N; i++) {
            field[i] = null;
        }
    }

    private void changeScore() {
        if (vscore[0] != null && vscore[1] != null && score[0] != null && score[1] != null) {
            vscore[0].setText(score[0].toString());
            vscore[1].setText(score[1].toString());
        }
    }

    private boolean full() {
        for (int i = 0; i < N * N; i++) {
            if (field[i] == null)
                return false;
        }
        return true;
    }

    private void setTurn() {
        TextView turn = findViewById(R.id.current_player);
        turn.setText(this.getString(R.string.turn) + ": " + players[current_turn % 2]);
    }
}