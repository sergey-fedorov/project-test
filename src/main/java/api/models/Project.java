package api.models;

import api.annotations.Optional;
import api.annotations.Parameterizable;
import api.annotations.Random;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project extends BaseModel {
    @Random
    private String id;
    @Random
    private String name;
    private Boolean copyAllAssociatedSettings = null;
    @Optional
    private SourceProject sourceProject;
    private ParentProject parentProject;
    private BuildTypes buildTypes;

}
