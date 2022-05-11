package com.ct.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(generator = "strategy",strategy = GenerationType.AUTO)
    @GenericGenerator(name = "strategy",strategy = "com.ct.condition.ManulInsertGenerator")
    private Long id;
    private String username;
    private String realname;
}