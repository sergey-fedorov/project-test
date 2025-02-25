package api.models;

import api.annotations.Random;
import api.models.BaseModel;
import api.models.Project;
import api.models.Steps;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildType extends BaseModel {
    @Random
    private String id;
    @Random
    private String name;
    private Project project;
    private Steps steps;
}
