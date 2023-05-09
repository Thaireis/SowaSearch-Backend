package com.sowatec.search.input;

import jakarta.persistence.*;

@Entity
@Table
public class Input {
    @Id
    @SequenceGenerator(
            name = "input_sequence",
            sequenceName = "input_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "student_sequence"
    )
    private long id;
    private String userInput;

    public Input(long id, String userInput) {
        super();
        this.id = id;
        this.userInput = userInput;
    }

    public Input() {    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUserInput() {
        return userInput;
    }
    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}

