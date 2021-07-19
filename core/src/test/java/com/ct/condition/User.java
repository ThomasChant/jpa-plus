package com.ct.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator = "strategy",strategy = GenerationType.AUTO)
    @GenericGenerator(name = "strategy",strategy = "com.ct.wrapper.ManulInsertGenerator")
    private Long id;
    private String username;
    private String realname;
}