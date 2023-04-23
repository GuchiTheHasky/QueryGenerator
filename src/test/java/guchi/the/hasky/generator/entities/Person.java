package guchi.the.hasky.generator.entities;

import guchi.the.hasky.generator.annotations.Column;
import guchi.the.hasky.generator.annotations.Id;
import guchi.the.hasky.generator.annotations.Table;
import lombok.*;


@Table(name = "People")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Person {
    @Id
    @Column(name = "person_id")
    private int id;

    @Column(name = "person_name")
    private String name;

    @Column(name = "person_salary")
    private double salary;
}
