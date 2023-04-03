package model;

import java.util.List;

public class Ticket {
    public Ticket(List<String> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        StringBuilder showInfo = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            showInfo.append(i + 1).append(") ").append(questions.get(i)).append('\n');
        }
        return String.valueOf(showInfo);
    }

    private List<String> questions;
}
