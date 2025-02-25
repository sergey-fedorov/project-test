package api.models;

import lombok.Data;

import java.util.List;

@Data
public class BuildTypes {
    private int count;
    private List<BuildType> buildType;

}
