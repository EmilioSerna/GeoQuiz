package com.emilio.geoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private TextView mQuestionTextView;

    private int mCurrentIndex = 0;
    private int mCurrentQuestion = 0;
    private int mCorrectAnswers = 0;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
            new Question(R.string.question_mexico, true),
            new Question(R.string.question_peru, true),
            new Question(R.string.question_brazil, false),
            new Question(R.string.question_portugal, false),
    };

    private boolean[] mQuestionAnswer = new boolean[mQuestionBank.length];
    private boolean[] mQuestionState = new boolean[mQuestionBank.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);

        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestionBank[mCurrentIndex].setState(false);
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mCurrentQuestion++;
                updateQuestion();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        for (int i=0; i < mQuestionBank.length; i++) {
            mQuestionAnswer[i] = mQuestionBank[i].isAnswerTrue();
            mQuestionState[i] = mQuestionBank[i].isState();
        }

        outState.putInt("mCurrentIndex", mCurrentIndex);
        outState.putInt("mCurrentQuestion", mCurrentQuestion);
        outState.putInt("mCorrectAnswers", mCorrectAnswers);
        outState.putBooleanArray("mQuestionAnswer", mQuestionAnswer);
        outState.putBooleanArray("mQuestionState", mQuestionState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCurrentIndex = savedInstanceState.getInt("mCurrentIndex");
        mCurrentQuestion = savedInstanceState.getInt("mCurrentQuestion");
        mCorrectAnswers = savedInstanceState.getInt("mCorrectAnswers");
        mQuestionAnswer = savedInstanceState.getBooleanArray("mQuestionAnswer");
        mQuestionState = savedInstanceState.getBooleanArray("mQuestionState");

        for (int i=0; i < mQuestionBank.length; i++) {
            mQuestionBank[i].setAnswerTrue(mQuestionAnswer[i]);
            mQuestionBank[i].setState(mQuestionState[i]);
        }

        updateQuestion();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        enableButton();
        percentageCompleted();
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
            mCorrectAnswers++;
        } else {
            messageResId = R.string.incorrect_toast;
        }

        mQuestionBank[mCurrentIndex].setState(false);

        enableButton();

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void enableButton() {
        mTrueButton.setEnabled(mQuestionBank[mCurrentIndex].isState());
        mFalseButton.setEnabled(mQuestionBank[mCurrentIndex].isState());
    }

    private void percentageCompleted() {
        double percentageAdvance = mCurrentQuestion * 100 / mQuestionBank.length;
        if (percentageAdvance < 100) {
            String advance = Double.valueOf(percentageAdvance).intValue() + "% " + getResources().getString(R.string.advance);
            Toast.makeText(this, advance, Toast.LENGTH_SHORT).show();
        } else {
            grade();
            mNextButton.setEnabled(false);
        }
    }

    private void grade() {
        double grade = mCorrectAnswers * 100 / mQuestionBank.length;
        String finalGrade = getResources().getString(R.string.grade) + " " + String.valueOf(grade) + "%";
        Toast.makeText(this, finalGrade, Toast.LENGTH_SHORT).show();
    }
}