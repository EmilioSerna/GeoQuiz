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
    private int correctAnswers = 0;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true, true),
            new Question(R.string.question_oceans, true, true),
            new Question(R.string.question_mideast, false, true),
            new Question(R.string.question_africa, false, true),
            new Question(R.string.question_americas, true, true),
            new Question(R.string.question_asia, true, true),
    };

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

        outState.putInt("mCurrentIndex", mCurrentIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCurrentIndex = savedInstanceState.getInt("mCurrentIndex");
        updateQuestion();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        enableButton();

        double percentageAdvance = mCurrentQuestion * 100 / mQuestionBank.length;
        if (percentageAdvance < 100) {
            String advance = Double.valueOf(percentageAdvance).intValue() + "% "+ getResources().getString(R.string.advance);
            Toast.makeText(this, advance, Toast.LENGTH_SHORT).show();
        } else {
            double grade = correctAnswers * 100 / mQuestionBank.length;
            String finalGrade = getResources().getString(R.string.grade) + " " + String.valueOf(grade) + "%";
            Toast.makeText(this, finalGrade, Toast.LENGTH_SHORT).show();
            mNextButton.setEnabled(false);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
            correctAnswers++;
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
}