package guchi.the.hasky.generator.entities;

import guchi.the.hasky.generator.annotations.Column;
import guchi.the.hasky.generator.annotations.Id;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class SomeEntity {
    @Id
    @Column(name = "entity_id")
    private Serializable id;
    @Column(name = "entity_name")
    private String name;
    @Column(name = "entity_age")
    private int age;
}
