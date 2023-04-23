package guchi.the.hasky.generator.entities;

import guchi.the.hasky.generator.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnotherEntity {
    @Id
    private String id;
}
