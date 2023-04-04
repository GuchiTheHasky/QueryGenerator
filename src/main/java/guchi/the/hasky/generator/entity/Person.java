package guchi.the.hasky.generator.entity;

import guchi.the.hasky.generator.annotations.Column;
import guchi.the.hasky.generator.annotations.Id;
import guchi.the.hasky.generator.annotations.Table;
import lombok.*;

import java.io.Serializable;

@Table
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Person {
    @Id
    @Column(name = "person_id")
    private Serializable id;

    @NonNull
    @Column(name = "person_name")
    private String name;
    @NonNull
    @Column(name = "person_salary")
    private double salary;
}
