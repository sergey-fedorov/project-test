package api.models;

import api.annotations.Random;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
@Accessors(chain = true)
public class BuildType extends BaseModel {
    @Random
    private String id;
    @Random
    private String name;
    private Project project;
    private Steps steps;
}
