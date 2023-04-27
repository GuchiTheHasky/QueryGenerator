package guchi.the.hasky.generator.entities;

import guchi.the.hasky.generator.annotations.Column;
import guchi.the.hasky.generator.annotations.Id;
import guchi.the.hasky.generator.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "Users")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "passport_number")
    private int passportNumber;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;
}
