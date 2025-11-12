package com.example.vocabularybuilder.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

/**
 * This is a "Relation" class, NOT an @Entity.
 * It's used by Room to fetch a "parent" object (QuizResult) and all its
 * "child" objects (List<QuizQuestion>) in a single, efficient query.
 */
public class QuizResultWithQuestions {

    // @Embedded tells Room to treat the fields of QuizResult as if
    // they were part of this class.
    @Embedded
    public QuizResult quizResult;

    // @Relation links the parent and child tables.
    @Relation(
            parentColumn = "id", // The PrimaryKey of the parent (QuizResult)
            entityColumn = "quizResultId" // The foreign key in the child (QuizQuestion)
    )
    public List<QuizQuestion> questions;

    // --- Constructor and Getters ---

    public QuizResultWithQuestions(QuizResult quizResult, List<QuizQuestion> questions) {
        this.quizResult = quizResult;
        this.questions = questions;
    }

    public QuizResult getQuizResult() {
        return quizResult;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }
}