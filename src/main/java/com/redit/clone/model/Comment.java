package com.redit.clone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.FetchType.LAZY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    @NotEmpty //must not be null or empty
    private String text;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "postId",referencedColumnName = "postId")
    private Post post;
    private Instant createdDate;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="userId",referencedColumnName = "userID")
    private User user;

}
